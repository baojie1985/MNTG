package util;

import java.io.*;

/**
 * Klasse zum strukturierten Einlesen von Streams.
 *
 * @version	1.10	30.11.98
 * @version	1.20	07.03.99	setTerminatingChar
 * @version	1.30	10.10.99	readWord
 * @author Thomas Brinkhoff
 */
public class EntryReader extends InputStreamReader implements EntryInput {

	private char buffer[] = null;					// Zeichenpuffer
	private final int maxBufferSize = 8192;		// dessen max. Größe
	private boolean eol = false;					// End of Line?
	private boolean eof = false;					// End of Stream?
	private boolean numErr = false;				// Numerischer Fehler?
	private int bi = 0;								// Index auf buffer
	private int nextEntryLength = 0;				//	Länge des nächsten Eintrags, 0 = beliebig
	private char terminatingChar = '\t';			// Abschluß-Zeichen


/**
 * Konstruktor für einen neuen EntryReader.
 * @param in InputStream
 */
public EntryReader (InputStream in) {
	super(in);
	buffer = new char[maxBufferSize];
}
/**
 * Gibt zurück, ob das Ende des Streams / der Datei erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eof () {
	return eof;
}
/**
 * Gibt zurück, ob das Ende der Zeile erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eol () {
	return eol;
}
/**
 * Gibt zurück, ob bei der letzten Lese-Operation ein numerischer Fehler erfolgt ist.
 * @return numerischer Fehler?
 */
public boolean numErr () {
	return numErr;
}
/**
 * Liest einen Boolean. Dabei entspricht 1 true; ansonsten wird false geliefert.
 * @return der gelesene Wert
 */
public boolean readBoolean () {
	char c = readChar();
	return c == '1';
}
/**
 * Liest ein Zeichen.
 * @return das gelesene Zeichen
 */
public char readChar () {
	int c = 0;
	try {
		c = super.read();
		// eof?
		if (c < 0) {
			c = 0;
			eof = true;
		}	
		eol = (c == '\n') || (c == '\r');
		return (char)c;
	}	
	catch (IOException io)
	{
		eof = true;
		return (char)c;
	}			
}
/**
 * Liest eine Double-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public double readDouble () {
	String s = readString();
	numErr = false;
	if (s != null)
		try {
			return new Double(s.replace('+',' ').trim()).doubleValue();
		}	
		catch (NumberFormatException nfe) {
			numErr = true;
			return 0;
		}	
	else {
		numErr = true;
		return 0;
	}	
}
/**
 * Liest eine Integer-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public int readInt () {
	String s = readString();
	numErr = false;
	if (s != null)
		try {
			return new Integer(s.replace('+',' ').trim()).intValue();
		}	
		catch (NumberFormatException nfe) {
			numErr = true;
			return 0;
		}	
	else {
		numErr = true;
		return 0;
	}	
}
/**
 * Liest eine Long-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public long readLong () {
	String s = readString();
	numErr = false;
	if (s != null)
		try {
			return new Long(s.replace('+',' ').trim()).longValue();
		}	
		catch (NumberFormatException nfe) {
			numErr = true;
			return 0;
		}	
	else {
		numErr = true;
		return 0;
	}	
}
/**
 * Liest eine Short-Zahl. Tritt ein Fehler auf, wird 0 zurückgegeben
 * und errNum() gibt true zurück.
 * @return die gelesene Zahl
 */
public short readShort () {
	String s = readString();
	numErr = false;
	if (s != null)
		try {
			return new Short(s.replace('+',' ').trim()).shortValue();
		}	
		catch (NumberFormatException nfe) {
			numErr = true;
			return 0;
		}	
	else {
		numErr = true;
		return 0;
	}	
}
/**
 * Liest eine Zeichenkette ein.
 * @return eingelesene Zeichenkette; ggf. null
 */
public String readString () {
	int c;
	try {
		// Eintrag einlesen
		do {
			// Eintragsende?
			if ((bi >= maxBufferSize) || ((bi > 0) && (bi == nextEntryLength)))
				break;
			c = super.read();
			// eof?
			if (c < 0) {
				buffer[bi] = ' ';
				eof = true;
				break;
			}	
			buffer[bi] = (char)c;
			// Eintragsende?
			if ((buffer[bi] == '\n') || (buffer[bi] == '\r') || (buffer[bi] == terminatingChar))
				break;
			bi++;
		} while(true);
		eol = (buffer[bi] == '\n') || (buffer[bi] == '\r');
		// ggf. noch fehlendes \n einlesen und ggf. String zurückgeben
		int bi1 = bi;
		bi = 0;
		if ((!eof) && (bi1 < maxBufferSize) && (buffer[bi1] == '\r')) {
			c = super.read();
			if ((char)c != '\n') {
				String s = new String(buffer,0,bi1);
				// Puffer mit eingelesenen Zeichen belegen
				buffer[0] = (char)c;
				bi = 1;
				return s;
			}	
		}
		// String zurückgeben
		if ((!eof) || (bi1 > 0)) {
			return new String(buffer,0,bi1);
		}	
		else
			return null;
	}
	catch (IOException io)
	{
		eof = true;
		if (bi > 0)
			return new String(buffer,0,bi);
		else
			return null;
	}			
}
/**
 * Liest ein Wort, das aus 2 Byte besteht zurück.
 * Wird das Dateiende überschritten, wird 0 zurückgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public short readWord2 () {
	int ch1 = readChar();
	int ch2 = readChar();
	return (short)((ch1 << 8) + (ch2 << 0));
}
/**
 * Liest ein Wort, das aus 4 Byte besteht zurück.
 * Wird das Dateiende überschritten, wird 0 zurückgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public int readWord4 () {
	int ch1 = readChar();
	int ch2 = readChar();
	int ch3 = readChar();
	int ch4 = readChar();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
}
/**
 * Setzt die Länge des nächsten einzulesenden Eintrags.
 * @param length Eintragslänge, 0 = beliebig
 */
public void setNextEntryLength (int length) {
	nextEntryLength = length;
}
/**
 * Setzt das Abschlußzeichen (Default = '\t').
 * @param t Abschlußzeichen
 */
public void setTerminatingChar (char t) {
	terminatingChar = t;
}
}
