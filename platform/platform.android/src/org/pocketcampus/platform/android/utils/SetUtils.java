package org.pocketcampus.platform.android.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/****
 * SetUtils
 * @author Amer Chamseddine <amer@accandme.com>
 *
 */
public class SetUtils {

	public static <T> Set<T> intersect(Collection<T> l1, Collection<T> l2) {
		Set<T> nl = new HashSet<T>(l1);
		nl.retainAll(l2);
		return nl;
	}
	
	public static <T> Set<T> difference(Collection<T> s1, Collection<T> s2) {
		Set<T> ns = new HashSet<T>(s1);
		ns.removeAll(s2);
		return ns;
	}	
	
}
