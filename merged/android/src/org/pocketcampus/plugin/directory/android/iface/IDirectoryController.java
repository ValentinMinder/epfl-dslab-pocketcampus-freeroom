package org.pocketcampus.plugin.directory.android.iface;

import org.pocketcampus.plugin.directory.shared.Person;
import java.util.List;

public interface IDirectoryController {
	public void setResults(List<Person> res);
	public void search(String name);
}
