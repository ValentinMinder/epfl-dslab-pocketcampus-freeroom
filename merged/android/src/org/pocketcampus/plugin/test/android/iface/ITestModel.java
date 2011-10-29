package org.pocketcampus.plugin.test.android.iface;

/**
 * Interface for the getters of the model.
 *
 * The idea is to only have the methods to *access* the model in there, this way we can use it in the views
 * and we're sure we'll never accidentally modify the model from there.
 */
public interface ITestModel {
	public int getFoo();
	public int getBar();
}
