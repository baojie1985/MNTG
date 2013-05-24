package util;

import java.awt.*;

/**
 * Klasse zum Definieren von Farben.
 *
 * @version	1.00	27.02.99	Erstfassung
 * @author Thomas Brinkhoff
 */
public class ColorDefiner {

	private static Color defaultColor = new Color(121,198,208);
	

/**
 * Gibt die textuell angebene Farbe als "java.awt.Color" zürück.
 * @return ausgewählte Farbe
 * @param name Name der Farbe (black, blue, cyan, darkGray, gray, green, lightGray, magenta, orange, pink, red, white, yellow)
 */
public static Color getColor (String name) {
	if (name.compareTo("black")==0)
		return Color.black;
	else if (name.compareTo("blue")==0)
		return Color.blue;
	else if (name.compareTo("cyan")==0)
		return Color.cyan;
	else if (name.compareTo("darkGray")==0)
		return Color.darkGray;
	else if (name.compareTo("gray")==0)
		return Color.gray;
	else if (name.compareTo("green")==0)
		return Color.green;
	else if (name.compareTo("lightGray")==0)
		return Color.lightGray;
	else if (name.compareTo("magenta")==0)
		return Color.magenta;
	else if (name.compareTo("orange")==0)
		return Color.orange;
	else if (name.compareTo("pink")==0)
		return Color.pink;
	else if (name.compareTo("red")==0)
		return Color.red;
	else if (name.compareTo("white")==0)
		return Color.white;
	else if (name.compareTo("yellow")==0)
		return Color.yellow;
	return getDefaultColor();
}
/**
 * Gibt die Standardfarbe zurück.
 * @return Standardfarbe
 */
public static Color getDefaultColor () {
	return defaultColor;
}
}
