package util;

import java.util.*;

/**
 * This class implements a Hashtable, which allows to store the same key several times.
 * More see: java.util.Hashtable
 *
 * @author  Arthur van Hoff / Thomas Brinkhoff
 * @version	1.00	01.03.00	first version
 */
public class DupHashtable extends Dictionary implements Cloneable, java.io.Serializable {
	/**
	 * The hash table data.
	 */
	private transient DupHashtableEntry table[];
	/**
	 * The total number of entries in the hash table.
	 */
	private transient int count;
	/**
	 * Rehashes the table when count exceeds this threshold.
	 */
	private int threshold;
	/**
	 * The load factor for the DupHashtable.
	 */
	private float loadFactor;
	/**
	 * The last entry found.
	 */
	private DupHashtableEntry lastFoundEntry;

	/**
	 * Constructs a new, empty DupHashtable with a default capacity and load
	 * factor. 
	 */
	public DupHashtable() {
	this(101, 0.75f);
	}
	/**
	 * Constructs a new, empty DupHashtable with the specified initial capacity
	 * and default load factor.
	 *
	 * @param   initialCapacity   the initial capacity of the DupHashtable.
	 */
	public DupHashtable(int initialCapacity) {
	this(initialCapacity, 0.75f);
	}
	/**
	 * Constructs a new, empty DupHashtable with the specified initial 
	 * capacity and the specified load factor. 
	 *
	 * @param      initialCapacity   the initial capacity of the DupHashtable.
	 * @param      loadFactor        a number between 0.0 and 1.0.
	 * @exception  IllegalArgumentException  if the initial capacity is less
	 *               than or equal to zero, or if the load factor is less than
	 *               or equal to zero.
	 */
	public DupHashtable(int initialCapacity, float loadFactor) {
	if ((initialCapacity <= 0) || (loadFactor <= 0.0)) {
	    throw new IllegalArgumentException();
	}
	this.loadFactor = loadFactor;
	table = new DupHashtableEntry[initialCapacity];
	threshold = (int)(initialCapacity * loadFactor);
	}
	/**
	 * Clears this DupHashtable so that it contains no keys. 
	 */
	public synchronized void clear() {
	DupHashtableEntry tab[] = table;
	for (int index = tab.length; --index >= 0; )
	    tab[index] = null;
	count = 0;
	}
	/**
	 * Creates a shallow copy of this DupHashtable. The keys and values 
	 * themselves are not cloned. 
	 * This is a relatively expensive operation.
	 *
	 * @return  a clone of the DupHashtable.
	 */
	public synchronized Object clone() {
	try { 
	    DupHashtable t = (DupHashtable)super.clone();
	    t.table = new DupHashtableEntry[table.length];
	    for (int i = table.length ; i-- > 0 ; ) {
		t.table[i] = (table[i] != null) 
		    ? (DupHashtableEntry)table[i].clone() : null;
	    }
	    return t;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
	}
	/**
	 * Tests if some key maps into the specified value in this DupHashtable.
	 * This operation is more expensive than the <code>containsKey</code>
	 * method.
	 *
	 * @param      value   a value to search for.
	 * @return     <code>true</code> if some key maps to the
	 *             <code>value</code> argument in this DupHashtable;
	 *             <code>false</code> otherwise.
	 * @exception  NullPointerException  if the value is <code>null</code>.
	 * @see        util.DupHashtable#containsKey(java.lang.Object)
	 */
	public synchronized boolean contains(Object value) {
	if (value == null) {
	    throw new NullPointerException();
	}

	DupHashtableEntry tab[] = table;
	for (int i = tab.length ; i-- > 0 ;) {
	    for (DupHashtableEntry e = tab[i] ; e != null ; e = e.next) {
		if (e.value.equals(value)) {
		    return true;
		}
	    }
	}
	return false;
	}
	/**
	 * Tests if the specified object is a key in this DupHashtable.
	 * 
	 * @param   key   possible key.
	 * @return  <code>true</code> if the specified object is a key in this
	 *          DupHashtable; <code>false</code> otherwise.
	 * @see     util.DupHashtable#contains(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key) {
	DupHashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (DupHashtableEntry e = tab[index] ; e != null ; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		return true;
	    }
	}
	return false;
	}
	/**
	 * Returns an enumeration of the values in this DupHashtable.
	 * Use the Enumeration methods on the returned object to fetch the elements
	 * sequentially.
	 *
	 * @return  an enumeration of the values in this DupHashtable.
	 * @see     java.util.Enumeration
	 * @see     util.DupHashtable#keys()
	 */
	public synchronized Enumeration elements() {
	return new DupHashtableEnumerator(table, false);
	}
/**
 * Returns the value to which the specified key is mapped in this DupHashtable.
 *
 * @param   key   a key in the DupHashtable.
 * @return  the value to which the key is mapped in this DupHashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this DupHashtable.
 */
public synchronized Object get (Object key) {
	DupHashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (DupHashtableEntry e = tab[index] ; e != null ; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		    lastFoundEntry = e;
			return e.value;
	    }
	}
	lastFoundEntry = null;
	return null;
}
/**
 * Returns the value to which the specified key is mapped in this DupHashtable.
 *
 * @param   key   a key in the DupHashtable.
 * @return  the value to which the key is mapped in this DupHashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this DupHashtable.
 */
public synchronized Object getNext (Object key) {
	if (lastFoundEntry == null)
		return null;
	int hash = key.hashCode();
	for (DupHashtableEntry e = lastFoundEntry.next ; e != null ; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		    lastFoundEntry = e;
			return e.value;
	    }
	}
	lastFoundEntry = null;
	return null;
}
	/**
	 * Tests if this DupHashtable maps no keys to values.
	 *
	 * @return  <code>true</code> if this DupHashtable maps no keys to values;
	 *          <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
	return count == 0;
	}
	/**
	 * Returns an enumeration of the keys in this DupHashtable.
	 *
	 * @return  an enumeration of the keys in this DupHashtable.
	 * @see     java.util.Enumeration
	 * @see     util.DupHashtable#elements()
	 */
	public synchronized Enumeration keys() {
	return new DupHashtableEnumerator(table, true);
	}
/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> in this DupHashtable. Neither the key nor the 
 * value can be <code>null</code>. 
 *
 * @param      key     the key.
 * @param      value   the value.
 * @return     the previous value of the specified key in this DupHashtable,
 *             or <code>null</code> if it did not have one.
 * @exception  NullPointerException  if the key or value is <code>null</code>.
 */
public synchronized Object put(Object key, Object value) {
	// Make sure the value is not null
	if (value == null) {
	    throw new NullPointerException();
	}
	// Rehash the table if the threshold is exceeded
	if (count >= threshold) {
	    rehash();
	    return put(key, value);
	} 
	// Creates the new entry.
	DupHashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	DupHashtableEntry e = new DupHashtableEntry();
	e.hash = hash;
	e.key = key;
	e.value = value;
	e.next = null;
	// Stores the entry
	if (tab[index] == null)
		tab[index] = e;
	else {
		DupHashtableEntry actEntry = tab[index];
		while (actEntry.next != null)
			actEntry = actEntry.next;
		actEntry.next = e;
	}
	count++;
	return value;
}
	/**
	 * Rehashes the contents of the DupHashtable into a DupHashtable with a 
	 * larger capacity. This method is called automatically when the 
	 * number of keys in the DupHashtable exceeds this DupHashtable's capacity 
	 * and load factor. 
	 */
	protected void rehash() {
	int oldCapacity = table.length;
	DupHashtableEntry oldTable[] = table;

	int newCapacity = oldCapacity * 2 + 1;
	DupHashtableEntry newTable[] = new DupHashtableEntry[newCapacity];

	threshold = (int)(newCapacity * loadFactor);
	table = newTable;

	//System.out.println("rehash old=" + oldCapacity + ", new=" + newCapacity + ", thresh=" + threshold + ", count=" + count);

	for (int i = oldCapacity ; i-- > 0 ;) {
	    for (DupHashtableEntry old = oldTable[i] ; old != null ; ) {
		DupHashtableEntry e = old;
		old = old.next;

		int index = (e.hash & 0x7FFFFFFF) % newCapacity;
		e.next = newTable[index];
		newTable[index] = e;
	    }
	}
	}
	/**
	 * Removes the key (and its corresponding value) from this 
	 * DupHashtable. This method does nothing if the key is not in the DupHashtable.
	 *
	 * @param   key   the key that needs to be removed.
	 * @return  the value to which the key had been mapped in this DupHashtable,
	 *          or <code>null</code> if the key did not have a mapping.
	 */
	public synchronized Object remove(Object key) {
	DupHashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (DupHashtableEntry e = tab[index], prev = null ; e != null ; prev = e, e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		if (prev != null) {
		    prev.next = e.next;
		} else {
		    tab[index] = e.next;
		}
		count--;
		return e.value;
	    }
	}
	return null;
	}
	/**
	 * Returns the number of keys in this DupHashtable.
	 *
	 * @return  the number of keys in this DupHashtable.
	 */
	public int size() {
	return count;
	}
	/**
	 * Returns a rather long string representation of this DupHashtable.
	 *
	 * @return  a string representation of this DupHashtable.
	 */
	public synchronized String toString() {
	int max = size() - 1;
	StringBuffer buf = new StringBuffer();
	Enumeration k = keys();
	Enumeration e = elements();
	buf.append("{");

	for (int i = 0; i <= max; i++) {
	    String s1 = k.nextElement().toString();
	    String s2 = e.nextElement().toString();
	    buf.append(s1 + "=" + s2);
	    if (i < max) {
		buf.append(", ");
	    }
	}
	buf.append("}");
	return buf.toString();
	}
}
