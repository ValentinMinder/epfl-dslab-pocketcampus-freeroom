package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.List;

/**
 * <code>OrderMapList</code> is an <code>OrderMap</code>, therefore a standard
 * <code>Map</code> where the ORDER of the <code>key</code>s is guaranteed.
 * Moreover, the <code>value</code>s MUST be of type <code>List<?></code>, and
 * the type of the <code>value</code>s elements must also be specified
 * separately as a third parameter.
 * <p>
 * This implementation lets define method to count the number of elements or
 * retrieve a specific element inside these lists. Return types are safe because
 * they are parameterized at the start.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <K>
 *            the type of the <code>key</code>s
 * @param <V>
 *            the type of the <code>value</code>s, MUST be a
 *            <code>List<?></code>
 * @param <T>
 *            the type of the <code>value</code>s elements
 */
public class OrderMapList<K, V, T> extends OrderMap<K, V> {
	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = -2302200486834357420L;

	/**
	 * Default constructor.
	 */
	public OrderMapList() {
		super();
	}

	/**
	 * Constructor with a specific start <code>size</code> for optimization.
	 * 
	 * @param size
	 */
	public OrderMapList(int size) {
		super(size);
	}

	/**
	 * Retrieves the <code>child</code> Object for the specified
	 * <code>group</code> and <code>child</code> positions.
	 * 
	 * @param group
	 *            specific <code>group</code> number for which to check the the
	 *            <code>child</code>ren.
	 * 
	 * @param child
	 *            specific <code>child</code> number for which to get the
	 *            <code>child</code>s object.
	 * @return the <code>child</code> Object for the specified
	 *         <code>group</code> and <code>child</code> positions.
	 */
	// this is secured, as the type of the list elements are specified in the
	// parameters.
	@SuppressWarnings("unchecked")
	public T getChild(int group, int child) {
		V v = this.get(group);
		if (v == null) {
			return null;
		} else if (v instanceof List<?>) {
			if (child >= 0 && child < ((List<?>) v).size()) {
				List<?> list = (List<?>) v;
				return (T) list.get(child);
			}
			return null;
		} else {
			throw new IllegalAccessError(
					"Class doesn't contains lists as values!");
		}
	}

	/**
	 * Retrieves the number of <code>child</code>ren for the specified
	 * <code>group</code>.
	 * 
	 * @param group
	 *            specific <code>group</code> number for which to check the
	 *            number of <code>child</code>ren.
	 * @return the number of <code>child</code>ren for the specified
	 *         <code>group</code>.
	 */
	public int getChildCount(int group) {
		V v = this.get(group);
		if (v == null) {
			return 0;
		} else if (v instanceof List<?>) {
			List<?> list = (List<?>) v;
			return list.size();
		} else {
			return 0;
		}
	}
}