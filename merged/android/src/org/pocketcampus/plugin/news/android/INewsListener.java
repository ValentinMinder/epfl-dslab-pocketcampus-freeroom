package org.pocketcampus.plugin.news.android;

/**
 * A class that want to be called when the news change has to implement thi interface. 
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public interface INewsListener {
	public void newsRefreshing();
	public void newsRefreshed();
}
