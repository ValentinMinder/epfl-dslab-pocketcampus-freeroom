package org.pocketcampus.plugin.freeroom.android.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * <code>SetArrayList<E></code> is a class that maintains an ordered List that
 * contains only unique elements. It can be seen an an ordered set or a
 * unique-element List. It uses the advantages of List for ordering, lookup at
 * indexes, and the advantages of sets for contains.
 * <p>
 * The inconvenient is that the objects are stored twices, both in the internal
 * list and the internal set.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * 
 * @param <E>
 *            the type of object to use
 */
public class SetArrayList<E> implements List<E>, Set<E>, Serializable {

	/**
	 * Auto generated serialVersionUID
	 */
	private static final long serialVersionUID = -5637544010891474998L;
	private List<E> internalList;
	private Set<E> internalSet;

	public SetArrayList(int i) {
		internalList = new ArrayList<E>(i);
		internalSet = new HashSet<E>(i);
	}

	public SetArrayList(int i, boolean linkedList) {
		if (linkedList) {
			internalList = new LinkedList<E>();
		} else {
			internalList = new ArrayList<E>(i);
		}
		internalSet = new HashSet<E>(i);
	}

	public SetArrayList() {
		internalList = new ArrayList<E>();
		internalSet = new HashSet<E>();
	}

	@Override
	public boolean add(E arg0) {
		return addBool(size(), arg0);
	}

	@Override
	public void add(int arg0, E arg1) {
		addBool(arg0, arg1);
	}

	/**
	 * Add an element at the first place. Optimized if you have a linkedlist as
	 * an internal structure.
	 */
	public void addFirst(E arg1) {
		if (internalList instanceof LinkedList<?>) {
			((LinkedList<E>) internalList).addFirst(arg1);
			internalSet.add(arg1);
		} else {
			add(0, arg1);
		}
	}

	/**
	 * Removes the last element. Optimized if you have a linkedlist as an
	 * internal structure.
	 */
	public void removeLast() {
		if (internalList instanceof LinkedList<?>) {
			E e = ((LinkedList<E>) internalList).removeLast();
			internalSet.remove(e);
		} else {
			remove(internalList.size() - 1);
		}
	}

	private boolean addBool(int arg0, E arg1) {
		boolean flag = internalSet.add(arg1);
		if (flag) {
			internalList.add(arg0, arg1);
		}
		return flag;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		return addAll(size(), arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		boolean ret = false;
		boolean flag = false;
		for (E e : arg1) {
			flag = addBool(arg0, e);
			if (flag) {
				arg0++;
			}
			ret = flag || ret;
		}
		return ret;
	}

	@Override
	public void clear() {
		internalList.clear();
		internalSet.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return internalSet.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return internalSet.containsAll(arg0);
	}

	@Override
	public E get(int arg0) {
		return internalList.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return internalList.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return internalSet.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return internalList.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return internalList.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internalList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		return internalList.listIterator(arg0);
	}

	@Override
	public E remove(int arg0) {
		E hold = internalList.remove(arg0);
		internalSet.remove(hold);
		return hold;
	}

	@Override
	public boolean remove(Object arg0) {
		boolean first = internalList.remove(arg0);
		boolean second = internalSet.remove(arg0);
		return first || second;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		boolean first = internalList.removeAll(arg0);
		boolean second = internalSet.removeAll(arg0);
		return first || second;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		boolean flag = internalSet.retainAll(arg0);
		if (flag) {
			for (int i = size() - 1; i >= 0; i--) {
				if (!internalSet.contains(internalList.get(i))) {
					internalList.remove(i);
				}
			}
		}
		return flag;
	}

	@Override
	public E set(int arg0, E arg1) {
		return internalList.set(arg0, arg1);
	}

	@Override
	public int size() {
		return internalList.size();
	}

	@Override
	public List<E> subList(int arg0, int arg1) {
		return internalList.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return internalList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return internalList.toArray(arg0);
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(size() * 50);
		Iterator<E> iter = iterator();
		while (iter.hasNext()) {
			E e = iter.next();
			build.append(e);
		}
		return build.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// removed from generated equals
		// we dont care about the order, only the content!
		// content of set and list must be the same all the time
		// result = prime * result
		// + ((internalList == null) ? 0 : internalList.hashCode());
		result = prime * result
				+ ((internalSet == null) ? 0 : internalSet.hashCode());
		return result;
	}

	/**
	 * Checks if two sets contains the same object. Reuse the equals method from
	 * legacy java Set.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SetArrayList other = (SetArrayList) obj;
		if (internalList == null) {
			if (other.internalList != null) {
				return false;
			}
			// removed from generated equals
			// we dont care about the order, only the content!
			// content of set and list must be the same all the time

			// } else if (!internalList.equals(other.internalList)) {
			// return false;
		}
		if (internalSet == null) {
			if (other.internalSet != null) {
				return false;
			}
		} else if (!internalSet.equals(other.internalSet)) {
			return false;
		}
		return true;
	}
}
