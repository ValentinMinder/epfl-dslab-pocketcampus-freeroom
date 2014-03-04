package org.pocketcampus.plugin.edx.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pocketcampus.platform.sdk.shared.utils.Callback;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Base64;
import android.util.Log;

public class Utils {

	/*
	 * Thread to manage live recording/playback of voice input from the device's microphone.
	 */
	public static class Audio extends Thread
	{ 
	    private boolean stopped = false;

	    /**
	     * Give the thread high priority so that it's not canceled unexpectedly, and start it
	     */
	    public Audio()
	    { 
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	        start();
	    }

	    @Override
	    public void run()
	    { 
	        AudioRecord recorder = null;
	        AudioTrack track = null;
	        
	        Log.i("Audio", "Running Audio Thread");
	        short[][]   buffers  = new short[256][160];
	        int ix = 0;

	        /*
	         * Initialize buffer to hold continuously recorded audio data, start recording, and start
	         * playback.
	         */
	        try
	        {
	            int N = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
                //Log.i("Init", "Min " + N); // 1024
                //N *= 10;
                N = 16000; // 1 sec
	            recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N);
	            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
	                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N, AudioTrack.MODE_STREAM);
	            recorder.startRecording();
	            track.play();
	            /*
	             * Loops until something outside of this thread stops it.
	             * Reads the data from the recorder and writes it to the audio track for playback.
	             */
                short[] buffer = new short [8000];
                byte [] bbuffer = new byte[16000];
	            while(!stopped)
	            { 
	                Log.i("Map", "Writing new data to buffer");
	                //short[] buffer = buffers[ix++ % buffers.length];
	                N = recorder.read(buffer,0,buffer.length);
	                Log.i("Map", "Read " + N + " samples");
	                ByteBuffer.wrap(bbuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buffer);
	                
	                track.write(buffer, 0, N);
	                
	                String encoded = Base64.encodeToString(bbuffer, 0);
	                Log.i("Map", encoded);
	            }
	        }
	        catch(Throwable x)
	        { 
	        	x.printStackTrace();
	            Log.w("Audio", "Error reading voice audio", x);
	        }
	        /*
	         * Frees the thread's resources after the loop completes so that it can be run again
	         */
	        finally
	        { 
	            recorder.stop();
	            recorder.release();
	            track.stop();
	            track.release();
	        }
	    }

	    /**
	     * Called from outside of the thread in order to stop the recording/playback loop
	     */
	    public void close()
	    { 
	         stopped = true;
	    }
	    

	}
	
	public static class ByteArrayWrapper {
		public byte[] bytes;
		public ByteArrayWrapper(byte[] bytes) {
			this.bytes = bytes;
		}
	}
	

	/*
	 * Thread to manage live recording/playback of voice input from the device's microphone.
	 */
	public static class AudioIn extends Thread
	{ 
	    private boolean stopped = false;
	    
	    Callback<String> callback1;
	    Callback<ByteArrayWrapper> callback2;

	    /**
	     * Give the thread high priority so that it's not canceled unexpectedly, and start it
	     */
	    public AudioIn(Callback<String> cb1, Callback<ByteArrayWrapper> cb2)
	    { 
	    	this.callback1 = cb1;
	    	this.callback2 = cb2;
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	        start();
	    }

        AudioRecord recorder = null;
        //int  = 4000;
        int SMPLING_RATE = 8000;
        //int BUFFER_SIZE = 16000;
        
	    @SuppressLint("NewApi")
		@Override
	    public void run()
	    { 
	        Log.i("Audio", "Running Audio Thread");

	        /*
	         * Initialize buffer to hold continuously recorded audio data, start recording, and start
	         * playback.
	         */
	        try
	        {
	        	int BUFFER_SIZE = AudioRecord.getMinBufferSize(SMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
	            recorder = new AudioRecord(AudioSource.VOICE_COMMUNICATION, SMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
	            recorder.startRecording();
	            
	            if(AcousticEchoCanceler.isAvailable()) {
		            System.out.println("              ECHO CANCELER: yoohoo it is available!!");
		            AcousticEchoCanceler aec = AcousticEchoCanceler.create(recorder.getAudioSessionId());
		            aec.setEnabled(true);
	            } else {
		            System.out.println("              ECHO CANCELER: noooo it is not implemented!!");
	            }
	            
	            
	            /*
	             * Loops until something outside of this thread stops it.
	             * Reads the data from the recorder and writes it to the audio track for playback.
	             */
                short[] buffer = new short [BUFFER_SIZE / 2];
                byte [] bbuffer = new byte[BUFFER_SIZE];
	            while(!stopped)
	            { 
	                //Log.i("Map", "Writing new data to buffer");
	                //short[] buffer = buffers[ix++ % buffers.length];
	                recorder.read(buffer,0,buffer.length);
	                //Log.i("Map", "Read " + N + " samples");
	                ByteBuffer.wrap(bbuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buffer);
	                
	                
	                String encoded = Base64.encodeToString(bbuffer, 0);
	                if(callback1 != null) callback1.callback(encoded);
	                if(callback2 != null) callback2.callback(new ByteArrayWrapper(bbuffer));
	                //Log.i("Map", encoded);
	            }
	        }
	        catch(Throwable x)
	        { 
	        	x.printStackTrace();
	            //Log.w("Audio", "Error reading voice audio", x);
	        }
	        /*
	         * Frees the thread's resources after the loop completes so that it can be run again
	         */
	        finally
	        { 
	            recorder.stop();
	            recorder.release();
	        }
	    }

	    /**
	     * Called from outside of the thread in order to stop the recording/playback loop
	     */
	    public void close()
	    { 
	         stopped = true;
	    }
	    
	}
	
	

	/*
	 * Thread to manage live recording/playback of voice input from the device's microphone.
	 */
	public static class AudioOut1 extends Thread
	{ 
	    
	    String buf = null;
	    byte[] bbuf;

	    /**
	     * Give the thread high priority so that it's not canceled unexpectedly, and start it
	     */
	    public AudioOut1(String encoded)
	    { 
	    	buf = encoded;
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	        start();
	    }
	    public AudioOut1(byte[] bbuf)
	    { 
	    	this.bbuf = bbuf;
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	        start();
	    }

        static AudioTrack track = null;
        
	    @Override
	    public void run()
	    { 

	        /*
	         * Initialize buffer to hold continuously recorded audio data, start recording, and start
	         * playback.
	         */
	        try
	        {
	        	if(track == null) {
	        		System.out.println("CREATING AUDIOTRACK");
		            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
		                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 16000, AudioTrack.MODE_STREAM);
		            track.play();
	        	}
	            	
	            	
                byte[] decoded ;
                if(buf != null)
                	decoded = Base64.decode(buf, 0);
                else
                	decoded= bbuf;
                short[] buffer = new short [decoded.length / 2];
                ByteBuffer.wrap(decoded).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
    	    	
                track.write(buffer, 0, buffer.length);

	                
	                
	        }
	        catch(Throwable x)
	        { 
	        	x.printStackTrace();
	            //Log.w("Audio", "Error reading voice audio", x);
	        }
	    }

	    public static void close()
	    { 
	        if(track != null) {
	            track.stop();
	            track.release();
	            track = null;
	        }
	    }


	    

	}
	
	
	


	/*
	 * Thread to manage live recording/playback of voice input from the device's microphone.
	 */
	public static class AudioOut2 extends Thread
	{ 

		class Frame {
		    byte[] bbuf;
		    Frame(byte[] bbuf) {
		    	this.bbuf = bbuf;
		    }
		}
		
		ConcurrentLinkedQueue<Frame> queue = new ConcurrentLinkedQueue<Frame>();
	    private boolean stopped = false;

	    /**
	     * Give the thread high priority so that it's not canceled unexpectedly, and start it
	     */
	    public AudioOut2()
	    { 
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	        start();
	    }

	    public void queue(String buf) {
	    	if(!stopped)
	    		queue.add(new Frame(Base64.decode(buf, 0)));
	    }
	    public void queue(byte[] buf) {
	    	if(!stopped)
	    		queue.add(new Frame(buf));
	    }
	    
	    @Override
	    public void run()
	    { 

	    	AudioTrack track = null;
	        /*
	         * Initialize buffer to hold continuously recorded audio data, start recording, and start
	         * playback.
	         */
	        try
	        {
	        	track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
	                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 16000, AudioTrack.MODE_STREAM);
	            track.play();
	            	
	            while(!stopped) {
	            	Frame fr;
	            	while((fr = queue.poll()) == null) {
	            		Thread.sleep(100);
	            	}
	            	
	            	byte[] bbuf = fr.bbuf;
	            	
	            	if(queue.peek() != null) {
	            		bbuf = underSample(fr.bbuf);
	            	}

	                short[] buffer = new short [bbuf.length / 2];
	                ByteBuffer.wrap(bbuf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
	    	    	
	                track.write(buffer, 0, buffer.length);

	            	
	            }
	            	

	                
	        }
	        catch(Throwable x)
	        { 
	        	x.printStackTrace();
	            //Log.w("Audio", "Error reading voice audio", x);
	        }finally{
	        	if(track != null) {
		            track.stop();
		            track.release();
	        	}

	        }
	    }

	    public void close()
	    { 
	        stopped = true;
	    }


	    

	}
	
	public static byte [] underSample(byte [] buffer) {
		byte [] buffer2 = new byte [buffer.length / 4 * 3];
		int index = 0;
		int count = 0;
		for(int i = 0; i < buffer.length; i++) {
			if(++count % 4 == 0)
				continue;
			buffer2[index++] = buffer[i];
			if(index >= buffer2.length)
				return buffer2;
		}
		throw new RuntimeException("not enough audio data!");
	}
	
}
