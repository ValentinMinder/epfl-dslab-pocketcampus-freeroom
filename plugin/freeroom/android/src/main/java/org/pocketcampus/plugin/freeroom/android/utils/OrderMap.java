package org.pocketcampus.plugin.freeroom.android.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <code>OrderMap</code> is a standard <code>Map</code> where the ORDER of the
 * <code>key</code>s is guaranteed. The order is maintained by a
 * <code>List</code> of <code>key</code>s <code>K</code>, and the actual data
 * are stored in a standard <code>Map</code> of parameters <code>key</code>s
 * <code>K</code> mapping to <code>value</code>s <code>V</code>.
 * 
 * <p>
 * Getting an <code>value</code> is done by default by the actual
 * <code>key</code> (the behavior is just like an usual map). You can also
 * choose the position <code>index</code> where you want the <code>value</code>
 * mapped by the <code>key</code> at this position (in such case, it will
 * retrieve the actual <code>key</code> in the list of <code>key</code>s).
 * 
 * <p>
 * Insertion of a pair (<code>key</code>, <code>value</code>) is done at the end
 * by default (the behavior is just like an usual map). You can also choose the
 * position <code>index</code> where you want the <code>key</code> to be
 * inserted in the map. Note that if the <code>key</code> already existed, it's
 * new position will be modified! (at the end in default case, at the
 * <code>index</code> for the specific case).
 * 
 * <p>
 * Removing of a pair (<code>key</code>, <code>value</code>) is done by default
 * by the (the behavior is just like an usual map). You can also choose the
 * position <code>index</code> where you want the <code>key</code> to be deleted
 * from the map. Note that there may still be an element at <code>index</code>
 * after removal, as the elements in the list of <code>key</code>s are shifted
 * and the next element will takes the place.
 * 
 * <p>
 * As the list of <code>key</code>s is simply a list, this class offers a method
 * to get the ordered list of <code>key</code>s, or the element at a specific
 * <code>index</code>.
 * 
 * <p>
 * It's also possible to update a <code>key</code> without changing the
 * corresponding <code>value</code>. They <code>key</code> to be changed can be
 * either directly given or specified either by its <code>index</code>.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <K>
 *            the type of the <code>key</code>s
 * @param <V>
 *            the type of the <code>value</code>s
 */
public class OrderMap<K, V> implements Map<K, V>, Serializable {
	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = 8898116355542735442L;
	public static int defaultSize = 30;
	private List<K> listKey = new ArrayList<K>();
	private Map<K, V> data = new HashMap<K, V>();

	/**
	 * Default constructor.
	 */
	public OrderMap() {
		listKey = new ArrayList<K>(defaultSize);
		data = new HashMap<K, V>(defaultSize);
	}

	/**
	 * Constructor with optimization to specify an expected size.
	 * 
	 * @param size
	 *            the expected size
	 */
	public OrderMap(int size) {
		listKey = new ArrayList<K>(size);
		data = new HashMap<K, V>(size);
	}

	@Override
	public void clear() {
		listKey.clear();
		data.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return data.entrySet();
	}

	@Override
	public V get(Object key) {
		return data.get(key);
	}

	/**
	 * Returns the <code>value</code> for which the corresponding
	 * <code>key</code> is at position <code>index</code> in the list of
	 * <code>key</code>s, or <code>null</code> if this map contains no mapping
	 * for the <code>key</code> or the <code>index</code> is out of range.
	 * <p>
	 * If this map permits <code>null</code> <code>value</code>s, then a return
	 * <code>value</code> of <code>null</code> does not necessarily indicate
	 * that the map contains no mapping for the <code>key</code>; it's also
	 * possible that the map explicitly maps the <code>key</code> to
	 * <code>null</code>. The containsKey operation may be used to distinguish
	 * these two cases.
	 * 
	 * 
	 * @param index
	 *            the position in the list of <code>key</code>s wanted
	 * @return the <code>value</code> for which the corresponding
	 *         <code>key</code> is at position <code>index</code> in the list of
	 *         <code>key</code>s, or <code>null</code> if this map contains no
	 *         mapping for the <code>key</code> or the <code>index</code> is out
	 *         of range.
	 */
	public V get(int index) {
		if (index >= 0 && index < listKey.size()) {
			return data.get(listKey.get(index));
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return data.keySet();
	}

	/**
	 * Returns a List view of the <code>key</code>s contained in this map, as
	 * the list of <code>key</code>s.
	 * <p>
	 * Modification of the list outside of the map could lead to unwanted
	 * behavior.
	 * 
	 * @return an ordered <code>List</code> of the <code>key</code>s contained
	 *         in this map.
	 */
	public List<K> keySetOrdered() {
		return listKey;
	}

	@Override
	public V put(K key, V value) {
		if (containsKey(key)) {
			listKey.remove(key);
		}
		V hold = data.put(key, value);
		listKey.add(key);
		return hold;
	}

	/**
	 * Associates the specified <code>value</code> with the specified
	 * <code>key</code> in this map (optional operation), and the
	 * <code>key</code> is guaranteed to be at the desired <code>index</code>.
	 * If <code>index</code> is out of the bounds, it will do nothing nor crash.
	 * <p>
	 * If the map previously contained a mapping for the <code>key</code>, the
	 * old <code>value</code> is replaced by the specified <code>value</code>.
	 * (A map m is said to contain a mapping for a <code>key</code> k if and
	 * only if m.containsKey(k) would return true.)
	 * 
	 * @param key
	 *            <code>key</code> with which the specified <code>value</code>
	 *            is to be associated
	 * @param value
	 *            <code>value</code> to be associated with the specified
	 *            <code>key</code>
	 * @param index
	 *            desired <code>index</code> for the <code>key</code> position
	 *            in the list of <code>key</code>s (discarded if out of bounds)
	 * @return the previous <code>value</code> associated with <code>key</code>,
	 *         or <code>null</code> if there was no mapping for <code>key</code>
	 *         . (A <code>null</code> return can also indicate that the map
	 *         previously associated <code>null</code> with <code>key</code>, if
	 *         the implementation supports <code>null</code> <code>value</code>
	 *         s.)
	 */
	public V put(K key, V value, int index) {
		if (index >= 0 && index < data.size()) {
			if (containsKey(key)) {
				listKey.remove(key);
			}
			V hold = data.put(key, value);
			listKey.add(index, key);
			return hold;
		} else {
			return null;
		}
	}

	/**
	 * Same as <code>put(K key, V value, int index)</code>. If
	 * <code>index</code> is out of bounds, the <code>value</code> will be
	 * inserted in all case. Specifically, if <code>index</code> is negative, it
	 * will be used at <code>index</code> 0, if <code>index</code> is greater
	 * than the size, it will be used as the end <code>index</code>.
	 * 
	 * @param key
	 *            <code>key</code> with which the specified <code>value</code>
	 *            is to be associated
	 * @param value
	 *            <code>value</code> to be associated with the specified
	 *            <code>key</code>
	 * @param index
	 *            desired <code>index</code> for the <code>key</code> position
	 *            in the list of <code>key</code>s (accepted even if out of
	 *            bounds)
	 * @return the previous <code>value</code> associated with <code>key</code>,
	 *         or <code>null</code> if there was no mapping for <code>key</code>
	 *         . (A <code>null</code> return can also indicate that the map
	 *         previously associated <code>null</code> with <code>key</code>, if
	 *         the implementation supports <code>null</code> <code>value</code>
	 *         s.)
	 */
	public V putGently(K key, V value, int index) {
		if (index < 0) {
			index = 0;
		}
		if (index > data.size()) {
			index = data.size();
		}
		return put(key, value, index);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		listKey.removeAll(arg0.keySet());
		listKey.addAll(arg0.keySet());
		data.putAll(arg0);
	}

	@Override
	public V remove(Object key) {
		listKey.remove(key);
		return data.remove(key);
	}

	/**
	 * Removes the mapping for a <code>key</code>, specified by its
	 * <code>index</code> position in the list of <code>key</code>s, from this
	 * map if it is present (optional operation).
	 * <p>
	 * Returns the <code>value</code> to which this map previously associated
	 * the <code>key</code>, or <code>null</code> if the map contained no
	 * mapping for the <code>key</code>.
	 * <p>
	 * If this map permits <code>null</code> <code>value</code>s, then a return
	 * <code>value</code> of <code>null</code> does not necessarily indicate
	 * that the map contained no mapping for the <code>key</code>; it's also
	 * possible that the map explicitly mapped the <code>key</code> to
	 * <code>null</code>.
	 * <p>
	 * The map will not contain a mapping for the specified <code>key</code>
	 * once the call returns. The <code>index</code> may still exists, as the
	 * elements are shifted in the list of <code>key</code>s (<code>index</code>
	 * + 1 will now take place of <code>index</code>).
	 * 
	 * @param index
	 *            position of the <code>key</code> whose mapping is to be
	 *            removed from the map
	 * @return the previous <code>value</code> associated with <code>key</code>
	 *         at the position <code>index</code>, or <code>null</code> if there
	 *         was no mapping for <code>key</code> or <code>index</code> is out
	 *         of bounds.
	 */
	public V remove(int index) {
		if (index >= 0 && index < data.size()) {
			K hold = listKey.remove(index);
			return data.remove(hold);
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Collection<V> values() {
		return data.values();
	}

	/**
	 * Update the <code>key</code> which is at position <code>index</code> by
	 * the new <code>key</code> given. The <code>value</code> mapped by the old
	 * <code>key</code> at position <code>index</code> is now mapped by the new
	 * <code>key</code>. The <code>index</code> of the <code>key</code>s is
	 * guaranteed to remain at the same place.
	 * 
	 * @param index
	 *            <code>index</code> of the old <code>key</code> to be replaced
	 *            by the new <code>key</code>
	 * @param newKey
	 *            new <code>key</code> that will replace the old
	 *            <code>key</code>
	 */
	public void updateKey(int index, K newKey) {
		if (index >= 0 && index < data.size()) {
			K key = listKey.get(index);
			V value = data.remove(key);
			listKey.remove(index);
			data.put(newKey, value);
			listKey.add(index, newKey);
		}
	}

	/**
	 * Update the old <code>key</code> by the new <code>key</code> given. The
	 * <code>value</code> mapped by the old <code>key</code> is now mapped by
	 * the new <code>key</code>. The <code>index</code> of the <code>key</code>s
	 * is guaranteed to remain at the same place.
	 * 
	 * @param oldKey
	 *            old <code>key</code> that will be replaced the new
	 *            <code>key</code>.
	 * @param newKey
	 *            new <code>key</code> that will replace the old
	 *            <code>key</code>
	 */
	public void updateKey(K oldKey, K newKey) {
		int index = listKey.indexOf(oldKey);
		if (index != -1) {
			V value = data.remove(oldKey);
			listKey.remove(index);
			data.put(newKey, value);
			listKey.add(index, newKey);
		}
	}

	/**
	 * Returns the <code>key</code> at position <code>index</code>.
	 * 
	 * @param index
	 *            <code>index</code> of the <code>key</code> wanted
	 * @return the <code>key</code> at position <code>index</code>.
	 */
	public K getKey(int index) {
		if (index >= 0 && index < data.size()) {
			return listKey.get(index);
		}
		return null;
	}

	/**
	 * Returns the <code>index</code> of the given <code>key</code>.
	 * 
	 * @param key
	 *            the given <code>key</code>.
	 * @return the <code>index</code> of the given <code>key</code>.
	 */
	public int indexOf(Object key) {
		return listKey.indexOf(key);
	}

	/**
	 * Counts the number of children for the whole dataset (including all
	 * collection elements if values are collections).
	 * 
	 * @return the number of children for the whole dataset
	 */
	public int totalChild() {
		int total = 0;
		Iterator<V> i = values().iterator();
		while (i.hasNext()) {
			V v = i.next();
			if (v instanceof Collection<?>) {
				Collection<?> col = (Collection<?>) v;
				total += col.size();
			} else {
				total += 1;
			}
		}
		return total;
	}
}