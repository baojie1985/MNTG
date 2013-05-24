package util;

import java.io.*;

/**
 * Interface einer Reader-Klasse zum strukturierten Einlesen von Streams.
 * 
 * @version	1.10	29.11.98
 * @version	1.20	07.03.99	setTerminatingChar
 * @version 1.30	10.10.99	readWord
 * @author Thomas Brinkhoff
 */
public interface EntryInput {
	

/**
 * Gibt zurück, ob das Ende des Streams / der Datei erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eof ();
/**
 * Gibt zurück, ob das Ende der Zeile erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eol ();
/**
 * Gibt zurück, ob bei der letzten Lese-Operation ein numerischer Fehler erfolgt ist.
 * @return numerischer Fehler?
 */
public boolean numErr ();
/**
 * Liest einen Boolean. Dabei entspricht 1 true; ansonsten wird false geliefert.
 * @return der gelesene Wert
 */
public boolean readBoolean ();
/**
 * Liest ein Zeichen.
 * @return das gelesene Zeichen
 */
public char readChar ();
/**
 * Liest eine Double-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public double readDouble ();
/**
 * Liest eine Integer-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public int readInt ();
/**
 * Liest eine Long-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public long readLong ();
/**
 * Liest eine Long-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public short readShort ();
/**
 * Liest eine Zeichenkette ein.
 * @return eingelesene Zeichenkette; ggf. null
 */
public String readString ();
/**
 * Liest ein Wort, das aus 2 Byte besteht zurück.
 * Wird das Dateiende überschritte, wird 0 zurückgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public short readWord2 ();
/**
 * Liest ein Wort, das aus 4 Byte besteht zurück.
 * Wird das Dateiende überschritten, wird 0 zurückgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public int readWord4 ();
/**
 * Setzt die Länge des nächsten einzulesenden Eintrags.
 * @param length Eintragslänge, 0 = beliebig
 */
public void setNextEntryLength (int length);
/**
 * Setzt das Abschlußzeichen (Default = '\t').
 * @param t Abschlußzeichen
 */
public void setTerminatingChar (char t);
/**
 * Überspringt n Zeichen.
 * @return Anzahl der tatsächlich übersprungenen Zeichen
 * @param n Anzahl der Zeichen
 */
public long skip (long n) throws IOException;
}
