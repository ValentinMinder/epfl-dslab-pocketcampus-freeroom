package org.pocketcampus.plugin.edx.android;

import java.io.IOException;
import java.util.List;

import org.pocketcampus.plugin.edx.R;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.edx.android.EdXController.Stopper;
import org.pocketcampus.plugin.edx.android.Utils.AudioIn;
import org.pocketcampus.plugin.edx.android.Utils.AudioOut2;
import org.pocketcampus.plugin.edx.android.Utils.ByteArrayWrapper;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessage;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessageType;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * EdXModuleView - Module view that shows EdX module.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
@SuppressLint("NewApi")
public class EdXStudyRoomView extends PluginView implements IEdXView, OnInitializedListener {

	private EdXController mController;
	private EdXModel mModel;
	
	public static final String EXTRAS_KEY_YOUTUBE_VIDID = "EDX_YOUTUBE_VIDID";
	public static final String EXTRAS_KEY_ROOM_NBR = "EDX_ROOM_NBR";
	
	//String roomName = "yGOAtugqvBQ!8065"; // TODO remove hard coding
	String videoID = "yGOAtugqvBQ";
	//Integer roomNbr = 8065;
	Integer roomNbr = null;
	String videoPath = "http://pocketcampus.epfl.ch/backend/videos/The_Role_of_Software.mp4"; // TODO remove hard coding 
	Stopper poller;

	
	
	PowerManager.WakeLock wl;
	MediaPlayer mediaPlayer;
	UIUpdater progBarUpdater;

	AudioIn rec = null;
	AudioOut2 pb = null;
	
	YouTubePlayer ytPlayer = null;


	
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return EdXController.class;
	}


	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult errorReason) {
	    if (errorReason.isUserRecoverableError()) {
	        //errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
	        Toast.makeText(this, "user recoverable youtube ERROR", Toast.LENGTH_LONG).show();
	      } else {
	        Toast.makeText(this, "youtube ERROR", Toast.LENGTH_LONG).show();
	      }
		
	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
		
		if(mModel.getVideoDesc(videoID) != null) {
    		TextView discussionView = (TextView) findViewById(R.id.edx_sr_discussion);
//    		discussionView.append(Html.fromHtml(msg.getTextBody()));
//			TextView vidDesc = (TextView) findViewById(R.id.edx_sr_video_desc);
    		discussionView.append(Html.fromHtml("<p>" + mModel.getVideoDesc(videoID) + "</p>"));
			
		}

		
		player.setPlaybackEventListener(new PlaybackEventListener() {
			public void onStopped() {
			}
			public void onSeekTo(int arg0) {
				if(roomNbr != null)
					mController.pushMessage2(EdXStudyRoomView.this, videoID + roomNbr, "SEEK", "" + arg0);
			}
			public void onPlaying() {
				if(roomNbr != null)
					mController.pushMessage2(EdXStudyRoomView.this, videoID + roomNbr, "PLAY", "");
			}
			public void onPaused() {
				if(roomNbr != null)
					mController.pushMessage2(EdXStudyRoomView.this, videoID + roomNbr, "PAUSE", "");
			}
			public void onBuffering(boolean arg0) {
			}
		});
		player.setPlayerStateChangeListener(new PlayerStateChangeListener() {
			public void onVideoStarted() {
			}
			public void onVideoEnded() {
			}
			public void onLoading() {
			}
			public void onLoaded(String arg0) {
			}
			public void onError(ErrorReason arg0) {
			}
			public void onAdStarted() {
			}
		});
		
		ytPlayer = player;
		//player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		//player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		if (!wasRestored) {
			// player.cueVideo("nCgQDjiotG0");
			// System.out.println("roomName=" + roomName);
			/*String vidId = roomName.split("[!]", 2)[0];
			if (vidId.startsWith("+"))
				vidId = vidId.substring(1);*/
			player.cueVideo(videoID);

		}

		
		

		if(roomNbr != null) {
			poller = mController.startPolling2(this, videoID + roomNbr, new Callback<List<MsgPsgMessage>>() {
				public void callback(List<MsgPsgMessage> s) {
					String selfRef = mModel.getSession();
					for(MsgPsgMessage msg : s){
						if(msg.getMessageType() == MsgPsgMessageType.MESSAGE) {
							if("PLAY".equals(msg.getMessageHeader())) {
								if(selfRef.equals(msg.getSenderRef()))
									continue;
								//playClicked();
								mController.setSkipMessage(msg.getMessageHeader(), msg.getTextBody());
								ytPlayer.play();
							} else if("PAUSE".equals(msg.getMessageHeader())) {
								if(selfRef.equals(msg.getSenderRef()))
									continue;
								//pauseClicked();
								mController.setSkipMessage(msg.getMessageHeader(), msg.getTextBody());
								ytPlayer.pause();
							} else if("SEEK".equals(msg.getMessageHeader())) {
								if(selfRef.equals(msg.getSenderRef()))
									continue;
				        		//mediaPlayer.seekTo(Integer.parseInt(msg.getTextBody()));
								mController.setSkipMessage(msg.getMessageHeader(), msg.getTextBody());
								ytPlayer.seekToMillis(Integer.parseInt(msg.getTextBody()));
							} else if("TEXT".equals(msg.getMessageHeader())) {
//								String sender = msg.getSenderRef();
//								if(EdXController.ANDROID_ID.equals(msg.getSenderRef()))
//									sender = "me";
				        		TextView discussionView = (TextView) findViewById(R.id.edx_sr_discussion);
				        		discussionView.append(Html.fromHtml(msg.getTextBody()));
				        		//discussionView.scrollTo(0, discussionView.getHeight());
				        		Layout layout = discussionView.getLayout();
				        		int scrollDelta = layout.getLineBottom(discussionView.getLineCount() - 1) 
				        				- discussionView.getScrollY() - discussionView.getHeight();
				        		if(scrollDelta > 0)
				        			discussionView.scrollBy(0, scrollDelta);
//				        		discussionView.setText(discussionView.getText().toString() + "\n" + );
							}
							
						} else if(msg.getMessageType() == MsgPsgMessageType.AUDIO) {
							
			        		//new Utils.AudioOut(msg.getBinaryBody());
							if(pb != null)
								pb.queue(msg.getBinaryBody());
			        		
						}
					}
				}
			});
		}
		
		
	}
	
	
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (EdXController) controller;
		mModel = (EdXModel) controller.getModel();

		setContentView(R.layout.edx_study_room);
		
		
		
	    YouTubePlayerFragment youTubePlayerFragment =
	            (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.edx_youtube_fragment);
	        youTubePlayerFragment.initialize("AIzaSyAcYTsAuAhbb9zcd9z9u7mS8Z0_wrNoTUs", this);

	        
		

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "DoNotDimScreen");
        
		
        TextView discussionView = (TextView) findViewById(R.id.edx_sr_discussion);
        //discussionView.setMovementMethod(new ScrollingMovementMethod());
        discussionView.setMovementMethod(LinkMovementMethod.getInstance());
        
        //TextView vidDescView = (TextView) findViewById(R.id.edx_sr_video_desc);
        //vidDescView.setMovementMethod(LinkMovementMethod.getInstance());
        
        
        /*
    	
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.edx_sr_surfaceview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		        

		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
			}
			public void surfaceCreated(SurfaceHolder holder) {
			}
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
		// surfaceHolder.setFixedSize(176, 144);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mediaPlayer = new MediaPlayer();
		
		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				System.out.println("percent=" + percent);
			}
		});

		
		
		mediaPlayer.setOnSeekCompleteListener(new  OnSeekCompleteListener() {
			public void onSeekComplete(MediaPlayer mp) {
		        SeekBar seekBar = (SeekBar) findViewById(R.id.edx_sr_seekbar);
				seekBar.setMax(mediaPlayer.getDuration());
				seekBar.setProgress(mediaPlayer.getCurrentPosition());
			}
		});

		
		

		progBarUpdater = new UIUpdater(new Runnable() {
			public void run() {
				if(mediaPlayer.isPlaying()) {
			        SeekBar seekBar = (SeekBar) findViewById(R.id.edx_sr_seekbar);
			        seekBar.setMax(mediaPlayer.getDuration());
			        seekBar.setProgress(mediaPlayer.getCurrentPosition());
				}
			}
		}, 1000);
        
        
        
		
	
		//Log.i("ON DISPLAY", "" + surfaceView);
		
		// make it 16:9 aspect ratio
		//Log.i("BALLOUTA", "" + surfaceView);
		//Log.i("BALLOUTA", "" + surfaceView.getViewTreeObserver());
		surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				SurfaceView surfaceView = (SurfaceView) findViewById(R.id.edx_sr_surfaceview);
				int width = Math.min(surfaceView.getWidth(), surfaceView.getHeight() * 16 / 9);
				int height = Math.min(surfaceView.getHeight(), surfaceView.getWidth() * 9 / 16);
				surfaceView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
				surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});

		*/
		
		attachButtonActions();
		attachSeekBarActions();

	}


	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
				
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			//Uri aData = aIntent.getData();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_YOUTUBE_VIDID)) {
				videoID = aExtras.getString(EXTRAS_KEY_YOUTUBE_VIDID);
			}
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_ROOM_NBR)) {
				roomNbr = Integer.parseInt(aExtras.getString(EXTRAS_KEY_ROOM_NBR));
			}
		}
		
		
		if(roomNbr != null) {
			/*poller = mController.startPolling(roomName, new Callback<String>() {
				public void callback(String msg) {
		        	if(msg.startsWith("PLAY")) {
		        		playClicked();
		        	} else if(msg.startsWith("PAUSE")) {
		        		pauseClicked();
		        	} else if(msg.startsWith("SEEK ")) {
		        		mediaPlayer.seekTo(Integer.parseInt(msg.split(" ")[1]));
		        	} else if(msg.startsWith("AUDIO\n")) {
		        		//Log.i("AUDIO", msg.substring(6));
		        		new Utils.AudioOut(msg.substring(6));
		        	} else if(msg.startsWith("TEXT ")) {
		        		String m = msg.split(" ", 2)[1];
		        		TextView discussionView = (TextView) findViewById(R.id.edx_sr_discussion);
		        		discussionView.setText(discussionView.getText().toString() + "\n" + m);
		        		System.out.println("Received: " + m);
		        	}
				}
			});*/

			
			
			
			//TextView titleView = (TextView) findViewById(R.id.edx_sr_title);
			//titleView.setText("Room #" + roomName.split("[!]")[1]);
			//titleView.setText("Room #" + roomNbr);
    		TextView discussionView = (TextView) findViewById(R.id.edx_sr_discussion);
    		discussionView.append(Html.fromHtml("<h2>Room #" + roomNbr + "</h2>"));

			
			//System.out.println("vid id: " + roomName.replaceAll("[+]", "").split("[!]")[0]);
			
		}
		
		
		wl.acquire();
		//progBarUpdater.startUpdates();

		
		
		
		//Tracker
		//if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("edx");
		//else Tracker.getInstance().trackPageView("edx/" + eventPoolId + "/subevents");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(poller != null)
			poller.stop();
		if(rec != null) {
			rec.close();
			rec = null;
		}
		if(pb != null) {
			pb.close();
			pb = null;
		}

		//Utils.AudioOut.close();
		
		wl.release();
		//progBarUpdater.stopUpdates();

	}

	
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	
	
	
	


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add("enable audio").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			/*public boolean onMenuItemClick(MenuItem item) {
				rec = new Utils.AudioIn(new Callback<String>() {
					public void callback(String s) {
						mController.pushMessage(roomName, "AUDIO\n" + s);
					}
				}, null);
				return true;
			}*/
			public boolean onMenuItemClick(MenuItem item) {
				if(roomNbr != null) {
					rec = new AudioIn(null, new Callback<ByteArrayWrapper>() {
						public void callback(ByteArrayWrapper s) {
								mController.pushMessage2(EdXStudyRoomView.this, videoID + roomNbr, s.bytes);
						}
					});
					pb = new AudioOut2();
				}

				return true;
			}
		});
		menu.add("disable audio").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if(rec != null) {
					rec.close();
					rec = null;
				}
				if(pb != null) {
					pb.close();
					pb = null;
				}
				return true;
			}
		});
		return true;
	}

	
	
	
	
	
	
	
	void attachButtonActions() {
		/*
		Button playButton = (Button) findViewById(R.id.edx_sr_play);
		Button pauseButton = (Button) findViewById(R.id.edx_sr_pause);
		

		
		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mController.pushMessage(roomName, "PLAY");
				mController.pushMessage2(EdXStudyRoomView.this, roomName, "PLAY", "");
				playClicked();
			}
		});
		pauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mController.pushMessage(roomName, "PAUSE");
				mController.pushMessage2(EdXStudyRoomView.this, roomName, "PAUSE", "");
				pauseClicked();
			}
		});
		*/
		
		Button sendButton = (Button) findViewById(R.id.edx_sr_sendmsg);

		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MultiAutoCompleteTextView composeView = (MultiAutoCompleteTextView) findViewById(R.id.edx_sr_composemsg);
				String txtMsg = composeView.getText().toString().trim();
				if("".equals(txtMsg))
					return;

				//mController.pushMessage(roomName, "TEXT " + composeView.getText().toString());				
				if(roomNbr != null)
					mController.pushMessage2(EdXStudyRoomView.this, videoID + roomNbr, "TEXT", "<p><b>" + mModel.getUserName() + ":</b> " + txtMsg + "</p>");
				composeView.setText("");
			}
		});
		
	}
	
	
	void attachSeekBarActions() {
		/*
        SeekBar seekBar = (SeekBar) findViewById(R.id.edx_sr_seekbar);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					//mController.pushMessage(roomName, "SEEK " + progress);
					mController.pushMessage2(EdXStudyRoomView.this, roomName, "SEEK", "" + progress);
					mediaPlayer.seekTo(progress);
				}
			}
		});
*/
	}

	

	void playClicked() {

		/*if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();
		}*/
	
		/*if(mediaPlayer == null)
			return;
		
		mediaPlayer.reset();
		
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.edx_sr_surfaceview);
        SeekBar seekBar = (SeekBar) findViewById(R.id.edx_sr_seekbar);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDisplay(surfaceHolder);

		try {
			mediaPlayer.setDataSource(videoPath);
			mediaPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mediaPlayer.start();
		
		seekBar.setMax(mediaPlayer.getDuration());
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		*/
	}
	
	void pauseClicked() {
		//mediaPlayer.pause();

	}
	
	
	
	
	

	
	
	@Override
	public void userCoursesUpdated() {
	}
	@Override
	public void courseSectionsUpdated() {
	}
	@Override
	public void moduleDetailsUpdated() {
	}
	@Override
	public void activeRoomsUpdated() {
	}

	
	
	

	
	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	@Override
	public void networkErrorCacheExists() {
	}
	@Override
	public void upstreamServerFailure() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
	}
	@Override
	public void serverFailure() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_server_failed), Toast.LENGTH_SHORT).show();
	}

	
	
	
	@Override
	public void userCredentialsUpdated() { 
		// this is called on all listeners, so no need to do anything
	}
	@Override
	public void loginSucceeded() {
		//mController.refreshModuleDetails(this, courseId, moduleId, false);
	}
	@Override
	public void loginFailed() {
		mController.openLoginDialog();
	}
	@Override
	public void sessionTimedOut() {
		mController.performLogin(this);
	}


	@Override
	protected String screenName() {
		// TODO Auto-generated method stub
		return null;
	}



	
	
	
	
}
