package util;

import java.io.*;

/**
 * Klasse zum gemischten Ausgeben von Texten und Daten.
 *
 * @version	1.00	10.10.99	Erfassung
 * @author Thomas Brinkhoff
 */
public class EntryWriter {

	/**
	 * OutputStream
	 */
	private OutputStream out = null;
	/**
	 * Zeilen-Separator string.
	 */
	private String lineSeparator = null;
	/**
	 * Ausgabe-Fehler?
	 */
	private boolean err = false;
	
/**
 * Konstruktor.
 */
public EntryWriter (OutputStream out) {
	this.out = out;
	lineSeparator = System.getProperty("line.separator");
}
/**
 * Liegt Fehler vor?
 */
public boolean error () {
	return err;
}
/**
 * Flushing.
 */
public void flush () {
	try {
		out.flush();
	}
	catch (IOException e) {
		err = true;
	}
}
/**
 * Text-Ausgabe eines Zeichens.
 * @param c Zeichen
 */
public void print (char c) {
	try {
		err = false;
		out.write(c);
	}
	catch (IOException e) {
		err = true;
	}	
}
/**
 * Text-Ausgabe einer Double-Zahl.
 * @param d Double-Zahl
 */
public void print (double d) {
	print (String.valueOf(d));
}
/**
 * Text-Ausgabe einer Float-Zahl.
 * @param f Float-Zahl
 */
public void print (float f) {
	print (String.valueOf(f));
}
/**
 * Text-Ausgabe einer Integer-Zahl.
 * @param i Integer-Zahl
 */
public void print (int i) {
	print (String.valueOf(i));
}
/**
 * Text-Ausgabe einer Long-Zahl.
 * @param l Long-Zahl
 */
public void print (long l) {
	print (String.valueOf(l));
}
/**
 * Text-Ausgabe eines Strings.
 * @param s String
 */
public void print (String s) {
	try {
		err = false;
		if (s != null)
			for (int i=0; i<s.length(); i++)
				out.write(s.charAt(i));
	}
	catch (IOException e) {
		err = true;
	}	
}
/**
 * Text-Ausgabe einer Short-Zahl.
 * @param s Short-Zahl
 */
public void print (short s) {
	print (String.valueOf(s));
}
/**
 * Zeilenumbruch.
 */
public void println() {
	print(lineSeparator);
	try {
		out.flush();
	}
	catch (IOException e) {
		err = true;
	}
}
/**
 * Text-Ausgabe eines Zeichens mit Zeilenumbruch.
 * @param c Zeichen
 */
public void println (char c) {
	print(c); println();
}
/**
 * Text-Ausgabe einer Double-Zahl mit Zeilenumbruch.
 * @param d Double-Zahl
 */
public void println (double d) {
	print(d); println();
}
/**
 * Text-Ausgabe einer Float-Zahl mit Zeilenumbruch.
 * @param f Float-Zahl
 */
public void println (float f) {
	print(f); println();
}
/**
 * Text-Ausgabe einer Integer-Zahl mit Zeilenumbruch.
 * @param i Integer-Zahl
 */
public void println (int i) {
	print(i); println();
}
/**
 * Text-Ausgabe einer Long-Zahl mit Zeilenumbruch.
 * @param l Long-Zahl
 */
public void println (long l) {
	print(l); println();
}
/**
 * Text-Ausgabe eines Strings mit Zeilenumbruch.
 * @param s String
 */
public void println (String s) {
	print(s); println();
}
/**
 * Text-Ausgabe einer Short-Zahl mit Zeilenumbruch.
 * @param s Short-Zahl
 */
public void println (short s) {
	print(s); println();
}
/**
 * Daten-Ausgabe eines Bytes.
 * @param b Byte
 */
public void writeByte (int b) {
	char c = (char)(b & 255);
	print(c);
}
/**
 * Daten-Ausgabe von 2-Byte-Wort.
 * @param w 2-Byte-Wort
 */
public void writeWord2 (int w) {
	char c = (char)((w>>8) & 255);
	print(c);
	c = (char)(w & 255);
	print(c);
}
/**
 * Daten-Ausgabe von 4-Byte-Wort.
 * @param w 4-Byte-Wort
 */
public void writeWord4 (int w) {
	char c = (char)((w>>24) & 255);
	print(c);
	c = (char)((w>>16) & 255);
	print(c);
	c = (char)((w>>8) & 255);
	print(c);
	c = (char)(w & 255);
	print(c);
}
}
