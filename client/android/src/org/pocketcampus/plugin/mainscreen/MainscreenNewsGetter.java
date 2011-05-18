//package org.pocketcampus.plugin.mainscreen;
//
//import java.util.Collections;
//import java.util.List;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//public class MainscreenNewsGetter extends AsyncTask<Void, Void, List<MainscreenNews>> {
//
//	private static final long SLEEP_TIME = 1000;
//	
//	private IMainscreenNewsProvider plugin_;
//	private Context ctx_;
//	private MainscreenPlugin main_;
//	
//	public MainscreenNewsGetter(IMainscreenNewsProvider plugin, Context ctx, MainscreenPlugin main) {
//		this.plugin_ = plugin;
//		this.ctx_ = ctx;
//		this.main_ = main;
//	}
//	
//	@Override
//	protected List<MainscreenNews> doInBackground(Void... params) {
//				
//		List<MainscreenNews> tmp = plugin_.getNews(ctx_);
//		
//		Log.d("MainscreenNewsGetter", "Tmp size: " + tmp.size());
//		
//		if(tmp.size() > 0) {
//			Collections.sort(tmp);
//			return tmp;
//		}
//		
//		while(!MainscreenPlugin.hasNotification(plugin_.getClass())) {
//			
//			Log.d("MainscreenNewsGetter","No notification found");
//			
//			try {
//				Thread.sleep(SLEEP_TIME);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//				
//		tmp = plugin_.getNews(ctx_);
//		
//		Collections.sort(tmp);
//		return tmp;
//		
//	}
//	
//	@Override
//	protected void onPostExecute(List<MainscreenNews> result) {
//
//		MainscreenPlugin.addAll(result);
//		MainscreenPlugin.sort();		
//		
//		main_.displayNews();
//		
//		MainscreenPlugin.refreshed();
//	}
//	
//	
//
//}
