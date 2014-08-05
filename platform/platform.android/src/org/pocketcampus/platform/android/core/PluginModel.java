package org.pocketcampus.platform.android.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;


/**
 * Base Model class.
 * 
 * TODO multiplexing! reuse single service for all plugins, use common port, encrypt.
 * TODO caching, use https://github.com/kaeppler/droid-fu/blob/master/src/main/java/com/github/droidfu/cachefu/AbstractCache.java
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public abstract class PluginModel {

	private IView mPluginViewComposite;

	/**
	 * Called from the model to get access to all the listeners at once.
	 * @return
	 */
	protected IView getListeners() {
		return mPluginViewComposite;
	}
	
	abstract protected Class<? extends IView> getViewInterface();
	
	public void notifyNetworkError() {
		getListeners().networkErrorHappened();
	}
	
	/**
	 * @param viewClass Class of the views that will be listening to this model.
	 */
	public PluginModel() {
		mPluginViewComposite = makePluginViewComposite();
		mListeners = new ArrayList<PluginView>();
	}

	private IView makePluginViewComposite() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		final Class<?>[] interfaces = {getViewInterface()};
		
		InvocationHandler invocationHandler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				for(PluginView pluginView : mListeners) {
					try {
						method.invoke(pluginView, args);
						
					} catch (IllegalArgumentException e) {
						String msg = "View " + pluginView.getClass().getName();
						msg += " doesn't implement " + interfaces[0].getName() + "!";
						System.out.println(msg);
					}
				}
				
				return null;
			}
		};
		
		return (IView) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
	}

	
	
	
	

	/** Interface for the <code>ViewMessage</code>s the Model will send to the plugin Controller. */
//	public interface ViewMessage {}

	/** List of Views listening to this Model. */
	private List<PluginView> mListeners;

	public void addListener(PluginView listener) {
		mListeners.add(listener);
	}

	public void removeListener(PluginView listener) {
		mListeners.remove(listener);
	}

	public int getNbListeners() {
		return mListeners.size();
	}
}
