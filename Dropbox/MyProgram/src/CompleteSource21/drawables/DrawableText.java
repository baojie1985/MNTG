package drawables;

import java.awt.*;
import util.*;

/**
 * Class for drawable texts.
 *
 * @version	2.33	02.10.2001	support of a^ (130)
 * @version	2.32	29.10.2000	correctString public & supporting null
 * @version	2.31	01.09.2000	no use of 'Latin Extended Additional'
 * @version	2.30	03.06.2000	support of presentation modes, getText with new functionality
 * @version 2.20	23.01.2000	setText added
 * @version	2.10	09.10.1999	write, getHor/VertAlignment, origText added
 * @version	2.00	05.04.1999	adapted the to superclass Drawable v2.0
 * @author Thomas Brinkhoff
 */

public class DrawableText extends Drawable {

 	/**
 	 * normaler Text-Stil.
 	 */
 	public static final int NORMAL = 0;
 	/**
 	 * fetter Text-Stil.
 	 */
 	public static final int BOLD = 1;
 	/**
 	 * kursiver Text-Stil.
 	 */
 	public static final int ITALIC = 2;
 	/**
 	 * fett kursiver Text-Stil.
 	 */
 	public static final int BOLDITALIC = 3;
 	/**
 	 * linksbündige horizontale Ausrichtung.
 	 */
	public static final int LEFT = 0;
 	/**
 	 * mittige horizontale Ausrichtung.
 	 */
  	public static final int CENTER = 1;
 	/**
 	 * rechtsbündige horizontale Ausrichtung.
 	 */
  	public static final int RIGHT = 2;
 	/**
 	 * vertikale Ausrichtung am Oberrand.
 	 */
  	public static final int TOPLINE = 0;
 	/**
 	 * mittige vertikale Ausrichtung.
 	 */
  	public static final int CENTERLINE = 1;
 	/**
 	 * vertikale Ausrichtung an der Basislinie.
 	 */
  	public static final int BASELINE = 2;
 	/**
 	 * minimale Textgröße in Punkt.
 	 */
  	public static final int MINSIZE = 5;
 	/**
 	 * maximale Textgröße in Punkt.
 	 */
	public static final int MAXSIZE = 24;

 	/**
 	 * Ausgabe als Unicode?
 	 */
	private static boolean unicode = false;	// Font-Ausgabe
 	/**
 	 * Zeichensatz [NORMAL,BOLD,ITALIC,BOLDITALIC][MINSIZE..MAXSIZE pt]
 	 */
	private static Font font[][] = {{null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
										  {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
										  {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
										  {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null}};
 	/**
 	 * Umsetzungstabelle der Zeichen 128-191 nach ANSI.
 	 */
	private static char[] ansiChar = {'\u0080','\u0081','a','e','r','n','z','c','g','u','S','C','\u008c','\u008d','\u008e','\u008f',
											 '\u0090','Z','U','r','n','z','c','s','S','Z','s','\u009b','\u009c','\u009d','\u009e','a',
											 'e','s','t','S','l','L','z','\u00a7','e','i','I','Z','i','a','u','o',
											 '\u00b0','e','A','O','ö','a','i','u','o','h','s','t','H','T','S','z'};
 	/**
 	 * Umsetzungstabelle der Zeichen 128-191 nach Unicode.
 	 */
	private static char[] unicodeChar = {'\u0080','\u0081','a'/*'\u01ce'*/,'\u0115','\u0159','\u0148','\u017e','\u010d','\u011f','\u01d4','\u0160','\u010c','\u0152','\u008d','\u008e','\u008f',
											   '\u0090','\u017d','\u016c','\u0155','\u0144','\u017a','\u0107','\u015b','\u015a','\u0179','\u0161','\u009b','\u0153','\u009d','\u009e','\u0105',
											   '\u0119','\u015f','\u0163','\u015e','\u0142','\u0141','\u017c','\u00a7','\u0117','\u0131','\u0130','\u017b','\u012b','\u0101','\u016b','\u014d',
											   '\u00b0','\u0113','\u0100','\u014c','\u0151','\u0103','\u012d','\u016d','\u014f','h'/*'\u1e25'*/,'s'/*'\u1e63'*/,'t'/*'\u1e6d'*/,'H'/*'\u1e24'*/,'T'/*'\u1e6c'*/,'S'/*'\u1e62'*/,'z'/*'\u1e95'*/};

 	/**
 	 * Original-Text.
 	 */
	private String origText = null;
 	/**
 	 * Text in ANSI.
 	 */
	private String ansiText = "";
 	/**
 	 * Text in Unicode.
 	 */
	private String unicodeText = "";
 	/**
 	 * x-Koordinate.
 	 */
	private int x = 0;
 	/**
 	 * y-Koordinate.
 	 */
	private int y = 0;
 	/**
 	 * Länge des Textes.
 	 */
	private int len = 0;
 	/**
 	 * horizontale Ausrichtung.
 	 */
	private int horAlignment = LEFT;
 	/**
 	 * vertikale Ausrichtung.
 	 */
	private int vertAlignment = BASELINE;

/**
 * Konstruktor.
 */
protected DrawableText ( ) {
	layer = TEXTLAYER;
}
/**
 * Konstruktor.
 * @param px x-Position
 * @param py y-Position
 * @param s Text
 */
public DrawableText (int px, int py, String s) {
	this();
	x = px;
	y = py;
	len = s.length();
	origText = s;
	if (!s.equals("=")) {
		ansiText = toAnsi(s);
		unicodeText = toUnicode(s);
	}
	else {
		ansiText = s;
		unicodeText = s;
	}
	mbr = new Rectangle (x-len,y-1,2*len,3);
	pres = DrawablePresentation.get("default");
}
/**
 * Konstruktor.
 * @param px x-Position
 * @param py y-Position
 * @param s Text
 * @param presName Name des Darstellungsobjekts
 * @param h horizontale Ausrichtung
 * @param v vertikale Ausrichtung
 */
public DrawableText (int x, int y, String s, String presName, int h, int v) {
	this (x,y,s);
	pres = DrawablePresentation.get(presName);
	horAlignment = h;
	vertAlignment = v;
}
/**
 * Konstruktor.
 * @param px x-Position
 * @param py y-Position
 * @param s Text
 * @param presName Name des Darstellungsobjekts
 * @param h horizontale Ausrichtung
 * @param v vertikale Ausrichtung
 * @param pMinScale Mindest-Maßstab zum Zeichnen
 * @param pMaxScale Maximal-Maßstab zum Zeichnen
 */
public DrawableText (int x, int y, String s, String presName, int h, int v, int pMinScale, int pMaxScale) {
	this (x,y,s,presName,h,v);
	minScale = pMinScale;
	maxScale = pMaxScale;
}
/**
 * Korrigiert eingelesenen String.
 * Dies ist für den MS Internet Explorer notwendig, der einige Zeichen falsch übernimmt.
 * @return korrigierter String
 * @param input eingelesener String
 */
public static String correctString (String input) {
	if (input == null)
		return null;
	while (input.indexOf ((char)352) >= 0)		// Sv
		input = input.replace ((char)352,(char)138);
	while (input.indexOf ((char)353) >= 0)		// sv
		input = input.replace ((char)353,(char)154);
	while (input.indexOf ((char)376) >= 0)		// a,
		input = input.replace ((char)376,(char)159);
	while (input.indexOf ((char)402) >= 0)		// ev
		input = input.replace ((char)402,(char)131);
	while (input.indexOf ((char)710) >= 0)		// gv
		input = input.replace ((char)710,(char)136);
	while (input.indexOf ((char)732) >= 0)		// S´
		input = input.replace ((char)732,(char)152);
	while (input.indexOf ((char)8211) >= 0)		// c´
		input = input.replace ((char)8211,(char)150);
	while (input.indexOf ((char)8212) >= 0)		// s´
		input = input.replace ((char)8212,(char)151);
	while (input.indexOf ((char)8216) >= 0)		// Zv
		input = input.replace ((char)8216,(char)145);
	while (input.indexOf ((char)8217) >= 0)		// Uv
		input = input.replace ((char)8217,(char)146);
	while (input.indexOf ((char)8218) >= 0)		// av
		input = input.replace ((char)8218,(char)130);
	while (input.indexOf ((char)8221) >= 0)		// n´
		input = input.replace ((char)8221,(char)148);
	while (input.indexOf ((char)8222) >= 0)		// rv
		input = input.replace ((char)8222,(char)132);
	while (input.indexOf ((char)8224) >= 0)		// zv
		input = input.replace ((char)8224,(char)134);
	while (input.indexOf ((char)8225) >= 0)		// cv
		input = input.replace ((char)8225,(char)135);
	while (input.indexOf ((char)8226) >= 0)		// z´
		input = input.replace ((char)8226,(char)149);
	while (input.indexOf ((char)8230) >= 0)		// nv
		input = input.replace ((char)8230,(char)133);
	while (input.indexOf ((char)8240) >= 0)		// uv
		input = input.replace ((char)8240,(char)137);
	while (input.indexOf ((char)8249) >= 0)		// Cv
		input = input.replace ((char)8249,(char)139);
	return input;
}
/**
 * Zeichnet den Text im Graphic Context g,
 * vorausgesetzt der aktuelle Maßstab wird eingehalten.
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	DrawablePresentation ap = pres.get(scale,mode,value);
	if (ap.size < MINSIZE)
		ap.size = MINSIZE;
	if (ap.size > MAXSIZE)
		ap.size = MAXSIZE;
	// Farbe setzen
	if (selected) {
		if (ap.selectionColor == null)
			return;
		g.setColor (ap.selectionColor);
	}
	else {
		if (ap.color == null)
			return;
		g.setColor (ap.color);
	}
	// Font setzen
	Font f = setFont (g,ap.type,ap.size);
	// Alignment setzen, dazu ggf. FontMetrics bestimmen
	FontMetrics fm = g.getFontMetrics (f);
	int width = fm.stringWidth (ansiText);
	int height = fm.getLeading() + fm.getAscent();
	// Ausgabeposition bestimmen
	int px,py;
	switch (horAlignment) {
		case RIGHT:		px = x/scale-width;
						if (vertAlignment == DrawableText.CENTERLINE)
							px = px-ap.offset;
						break;
		case CENTER:	px = x/scale-width/2; break;
		default:		px = x/scale;
						if (vertAlignment == DrawableText.CENTERLINE)
							px = px+ap.offset;
	}
	switch (vertAlignment) {
		case TOPLINE:		py = y/scale+height-ap.size/2+ap.offset; break;
		case CENTERLINE:	py = y/scale+height/2-1; break;
		default:			py = y/scale+ap.size/5-ap.offset;
	}
	// Text ausgeben
 	if (unicode)
		g.drawString (unicodeText, px,py);
	else
		g.drawString (ansiText, px,py);
}
/**
 * Gibt originalen Text zurück.
 * @return orig. Text
 */
public String getOrigString () {
	return origText;
}
/**
 * Gibt den Text in ANSI- bzw. Unicode-Darstellung zurück.
 * @return Text
 */
public String getString () {
 	if (unicode)
		return unicodeText;
	else
		return ansiText;
}
/*
 * Testet, ob der Text durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param px x-Koordinate des zu testenden Punktes
 * @param py y-Koordinate des zu testenden Punktes
 * @param scale aktuelle Maßstab (nicht relevant)
 */
public boolean interacts (int px, int py, int scale) {
	int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
	int value = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
	DrawablePresentation ap = pres.get(scale,actMode,value);
	return (px >= x-(ap.size*len/3)*scale) && (px <= x+(ap.size*len/3)*scale) &&
			(py >= y-ap.size/2*scale) && (py <= y+ap.size/2*scale);
}
/**
 * Gibt zurück, ob die Ausgabe in Unicode oder ANSI erfolgen soll.
 * @return Unicode?
 */
public static boolean isUnicodeUsed () {
	return unicode;
}
/**
 * Liest den Text vom Entry-Input ein. Erster Eintrag "type". <br>
 * type == 0: x,y,text <br>
 * type == 1: presName,hor,vert <br>
 * type == 2: minScale,maxScale
 * @return eingelesener Text bzw. null
 * @param r Entry-Input
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	x = r.readInt();
	y = r.readInt();
	origText = correctString(r.readString());
	mbr = new Rectangle (x-1,y-1,3,3);
	len = origText.length();
	ansiText = toAnsi(origText);
	unicodeText = toUnicode(origText);
	if (type == 0) {
		pres = DrawablePresentation.get("default");
		return (EntryReadable) this;
	}
	pres = DrawablePresentation.get(r.readString());
	horAlignment = r.readInt();
	vertAlignment = r.readInt();
	if (type == 1)
		return (EntryReadable) this;
	minScale = r.readInt();
	maxScale = r.readInt();
	return (EntryReadable) this;
}
/**
 * Setzt den gewünschten Font im Graphik-Kontext.
 * @param g Graphik-Kontext
 * @param style Stil
 * @param size Größe in Punkt
 */
public static Font setFont (Graphics g, int style, int size) {
	if (font[style][size-MINSIZE] == null) {
		switch (style) {
			case NORMAL:		font[style][size-MINSIZE] = new Font ("SansSerif",Font.PLAIN,size+2); break;
			case BOLD:		font[style][size-MINSIZE] = new Font ("SansSerif",Font.BOLD,size+2); break;
			case ITALIC:		font[style][size-MINSIZE] = new Font ("SansSerif",Font.ITALIC,size+2); break;
			case BOLDITALIC:	font[style][size-MINSIZE] = new Font ("SansSerif",Font.BOLD+Font.ITALIC,size+2); break;
		}
	}
	g.setFont (font[style][size-MINSIZE]);
	return font[style][size-MINSIZE];
}
/**
 * Sets the text attribute of the drawable text.
 * @param text new text
 */
public void setText (String text) {
	len = text.length();
	origText = text;
	ansiText = toAnsi(text);
	unicodeText = toUnicode(text);
	mbr.setBounds(x-len,y-1,2*len,3);
}
/**
 * Setzt, ob die Ausgabe in Unicode oder ANSI erfolgen soll.
 * @param u Unicode?
 */
public static void setUnicode (boolean u) {
	unicode = u;
}
/**
 * Wandelt Länder-Text in ANSI-Text.
 * @return ANSI-Text
 * @param s umzuwandelnder Text
 */
private static String toAnsi (String s) {
	StringBuffer r = new StringBuffer(s);
	for (int i=0; i<r.length(); i++)
		if ((int)r.charAt(i) >= 128)
			// Zwischen 128 und 191 über ansiChar-Tabelle
			if ((int)r.charAt(i) < 192)
					r.setCharAt(i,ansiChar[(int)r.charAt(i)-128]);
			// ansonsten einzeln
			else {
				if (r.charAt(i) == '\u00d7')	// Z_
					r.setCharAt(i,'Z');
				if (r.charAt(i) == '\u00de')	// I-
					r.setCharAt(i,'I');
				if (r.charAt(i) == '\u00f7')	// z.
					r.setCharAt(i,'z');
				if (r.charAt(i) == '\u00fe')	// Z.
					r.setCharAt(i,'Z');
				if (r.charAt(i) == '\u00ff')	// d.
					r.setCharAt(i,'d');
				// Unsaubere Zeichen abfangen
				if ((int)r.charAt(i) >= 256) {
					System.out.println("s: "+s+" i: "+i+" r.charAt(i): "+(int)r.charAt(i));
					r.setCharAt(i,'*');
				}
			}
	return r.toString();
}
/**
 * Konvertiert den Eingabe-String in Abhängigkeit vom akt. Modus
 * in eine ANSI- bzw. Unicode-Darstellung
 * @return konvertierter String
 * @param in Eingabe-String
 */
protected static String toOutputString (String in) {
	if (unicode)
		return toUnicode(in);
	else
		return toAnsi(in);
}
/**
 * Wandelt Länder-Text in Unicode-Text.
 * @return Unicode-Text
 * @param s umzuwandelnder Text
 */
private static String toUnicode (String s) {
	StringBuffer r = new StringBuffer(s);
	for (int i=0; i<r.length(); i++)
		if ((int)r.charAt(i) >= 128)
			// Zwischen 128 und 191 über unicodeChar-Tabelle
			if ((int)r.charAt(i) < 192)
					r.setCharAt(i,unicodeChar[(int)r.charAt(i)-128]);
			// ansonsten einzeln
			else {
				if (r.charAt(i) == '\u00d7')	// Z_
					r.setCharAt(i,'Z'/*'\u1e94'*/);
				if (r.charAt(i) == '\u00de')	// I-
					r.setCharAt(i,'\u012a');
				if (r.charAt(i) == '\u00f7')	// z.
					r.setCharAt(i,'z'/*'\u1e93'*/);
				if (r.charAt(i) == '\u00fe')	// Z.
					r.setCharAt(i,'Z'/*'\u1e92'*/);
				if (r.charAt(i) == '\u00ff')	// d.
					r.setCharAt(i,'d'/*'\u1e0d'*/);
				// Unsaubere Zeichen abfangen
				if ((int)r.charAt(i) >= 256) {
					System.out.println("s: "+s+" i: "+i+" r.charAt(i): "+(int)r.charAt(i));
					r.setCharAt(i,'*');
				}
			}
	return r.toString();
}
/**
 * Writes the text.
 * @param out entry writer
 * @param type type of the output; meaning see read
 */
protected void writeProtected (EntryWriter out, int type) {
	out.print("T\t"+type+'\t'+x+'\t'+y+'\t');
	if ((obj != null) && (obj.getId()>0))
		out.print('=');
	else
		out.print(origText);
	if (type == 0)
		return;
	// ggf. weitere Attribute ausgeben
	out.print('\t'+pres.getName()+'\t'+horAlignment+'\t'+vertAlignment);
	if (type == 1)
		return;
	out.print("\t"+minScale+'\t'+maxScale);
}
}
