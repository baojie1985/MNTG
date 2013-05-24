package util;

import java.util.*;

/**
 * A DupHashtable enumerator class.  This class should remain opaque 
 * to the client. It will use the Enumeration interface.
 *
 * @author  Arthur van Hoff / Thomas Brinkhoff
 * @version	1.00	01.03.00	first version
 */
class DupHashtableEnumerator implements Enumeration {
	boolean keys;
	int index;
	DupHashtableEntry table[];
	DupHashtableEntry entry;
	DupHashtableEnumerator(DupHashtableEntry table[], boolean keys) {
	this.table = table;
	this.keys = keys;
	this.index = table.length;
	}
	public boolean hasMoreElements() {
	if (entry != null) {
	    return true;
	}
	while (index-- > 0) {
	    if ((entry = table[index]) != null) {
		return true;
	    }
	}
	return false;
	}
	public Object nextElement() {
	if (entry == null) {
	    while ((index-- > 0) && ((entry = table[index]) == null));
	}
	if (entry != null) {
	    DupHashtableEntry e = entry;
	    entry = e.next;
	    return keys ? e.key : e.value;
	}
	throw new NoSuchElementException("DupHashtableEnumerator");
	}
}
