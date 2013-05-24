package util;

/**
 * DupHashtable collision list.
 *
 * @author  Arthur van Hoff / Thomas Brinkhoff
 * @version	1.00	01.03.00	first version
 */
class DupHashtableEntry {
	int hash;
	Object key;
	Object value;
	DupHashtableEntry next;
	protected Object clone() {
	DupHashtableEntry entry = new DupHashtableEntry();
	entry.hash = hash;
	entry.key = key;
	entry.value = value;
	entry.next = (next != null) ? (DupHashtableEntry)next.clone() : null;
	return entry;
	}
}
