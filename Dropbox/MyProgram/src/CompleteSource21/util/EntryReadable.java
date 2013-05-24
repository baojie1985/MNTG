package util;

/**
 * Interface einer Klasse, die Objekte vom Entry-Input einlesen kann.
 * 
 * @version	1.00	26.02.99	Erstfassung
 * @author Thomas Brinkhoff
 */
 
public interface EntryReadable {

/**
 * Liest das jeweilige Objekt vom Entry-Input ein.
 * @return eingelesenes Objekt bzw. null
 * @param r Entry-Input
 */
public EntryReadable read (EntryInput r);
}
