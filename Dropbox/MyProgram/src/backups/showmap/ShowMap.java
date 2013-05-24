package showmap;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipInputStream;

import util.CPUTimer;
import util.ColorDefiner;
import util.DataReader;
import util.EntryInput;
import drawables.Drawable;
import drawables.DrawableObject;
import drawables.DrawableObjectType;
import drawables.DrawableObjects;
import drawables.DrawableText;

/**
 * Abstract applet for showing an interactive map. The applet allows navigating,
 * zooming and retrieval of object information.
 * 
 * @version 2.42 02.07.01 computeURL modified
 * @version 2.41 01.09.00 unicode selected at start
 * @version 2.40 28.06.00 support of DO210-files and presentation modes,
 *          CPUTimer instead of Timer
 * @version 2.30 01.03.00 x/yIntoCoord added, index added to readDrawables
 * @version 2.20 07.12.99 support of two languages
 * @version 2.10 19.08.99 support of DrawableObjects
 * @version 2.00 19.03.99 adapted to Drawables v2.0, changed to an abstract
 *          class
 * @author Thomas Brinkhoff
 */

public abstract class ShowMap implements ItemListener {

	/**
	 * Status "startend".
	 */
	protected static final int STARTING = 0;
	/**
	 * Status "gestarted".
	 */
	protected static final int STARTED = 2;
	/**
	 * Status "aktiv: gestarted und bereit für Interaktion".
	 */
	protected static final int ACTIVE = 3;
	/**
	 * Status "vollständig geladen".
	 */
	protected static final int COMPLETE = 4;

	/**
	 * Sprache Englisch.
	 */
	protected static final int ENGLISH = 0;
	/**
	 * Sprache Deutsch.
	 */
	protected static final int GERMAN = 1;

	/**
	 * ID der Update-Timers.
	 */
	protected CPUTimer updateTimer = new CPUTimer();
	/**
	 * Wartezeit für nächsten Update in Millisekunden.
	 */
	protected static final long UPDATETIME = 3000;

	/**
	 * Anzahl der Layer.
	 */
	protected int numOfLayers = 5;
	/**
	 * Container über Drawable-Objekte.
	 */
	protected DrawableObjects drawableObjects = null;

	/**
	 * Faktor des Basis-Maßstabs (gibt an, mit welchen Faktor der Maßstab zu
	 * multiplizieren ist, um die eigentliche Maßstabszahl zu erhalten).
	 */
	protected int baseScaleFactor = 1000000;
	/**
	 * Breite der Gesamtkarte im Basis-Maßstab 1 in Pixel.
	 */
	protected int mapWidth = 300;
	/**
	 * Höhe der Gesamtkarte im Basis-Maßstab 1 in Pixel.
	 */
	protected int mapHeight = 300;
	/**
	 * x-Koordinate des Mittelpunkts des akt. angezeigten Kartenausschnitts im
	 * akt. Maßstab.
	 */
	protected int viewMapX = 150;
	/**
	 * y-Koordinate des Mittelpunkts des akt. angezeigten Kartenausschnitts im
	 * akt. Maßstab.
	 */
	protected int viewMapY = 150;
	/**
	 * detailiertester Maßstab (in Maßstabseinheiten).
	 */
	protected int maxScale = 1;
	/**
	 * gröbster Maßstab (in Maßstabseinheiten).
	 */
	protected int minScale = 320;
	/**
	 * aktueller Maßstab (in Maßstabseinheiten).
	 */
	protected int scale = 160;
	/**
	 * Breite der Kartenanzeige (in Pixel).
	 */
	protected int viewWidth = 300;
	/**
	 * Höhe der Kartenanzeige (in Pixel).
	 */
	protected int viewHeight = 300;
	/**
	 * Linke Position der Kartenanzeige (in Pixel).
	 */
	protected int viewX = 10;
	/**
	 * Obere Position der Kartenanzeige (in Pixel).
	 */
	protected int viewY = 10;
	/**
	 * Obere Position des Ein-/Ausgabebereichs (in Pixel). Belegung mit
	 * negativen Wert bewirkt, daß der Ausgabebereich oberhalb der Karte
	 * dargestellt wird. Ansonsten wird er unterhalb der Karte dargestellt.
	 */
	protected int panelY = 1;
	/**
	 * Höhe des Ein-/Ausgabebereichs (in Pixel).
	 */
	protected int panelHeight = 163;
	/**
	 * Hintergrundfarbe.
	 */
	protected Color backgroundColor = ColorDefiner.getDefaultColor();
	/**
	 * Basis-Kartenfarbe.
	 */
	protected Color mapColor = new Color(120, 255, 255);
	/**
	 * Wird der Wechsel auf Unicode-Darstellung unterstützt?
	 */
	protected boolean unicodeSupported = false;
	/**
	 * gewählte Sprache
	 */
	protected int language = ENGLISH;

	/**
	 * Applet-Status
	 */
	protected int state = STARTING;
	/**
	 * Image für Double-Buffering.
	 */
	private Image doubleBuffer = null;
	/**
	 * letzte x-Position der Maus beim MousePressed-Event.
	 */
	private int lastMouseXPos = 0;
	/**
	 * letzte y-Position der Maus beim MousePressed-Event.
	 */
	private int lastMouseYPos = 0;
	/**
	 * letzte x-Position der Maus beim MouseDrag-Event.
	 */
	private int lastDragXPos = 0;
	/**
	 * letzte y-Position der Maus beim MouseDrag-Event.
	 */
	private int lastDragYPos = 0;
	/**
	 * Wird aktuell eine Info-Box angezeigt?
	 */
	protected boolean infoIsShown = false;

	/**
	 * Labels
	 */
	private Label ivjClickInfoLabel = null;
	private Label ivjCopyrightLabel = null;
	private Label ivjNameLabel = null;
	private Label ivjPressInfoLabel = null;
	private Label ivjScaleLabel = null;
	private Label ivjShiftClickInfoLabel = null;
	private Label ivjTagLabel = null;
	private Label ivjValueLabel = null;
	/**
	 * Buttons
	 */
	private Button ivjEastButton = null;
	private Button ivjNorthButton = null;
	private Button ivjSouthButton = null;
	private Button ivjWestButton = null;
	private Button ivjZoomInButton = null;
	private Button ivjZoomOutButton = null;
	/**
	 * Checkbox
	 */
	private Checkbox ivjUnicodeCheckbox = null;

	/**
	 * Computes the URL from the specified name. Relative name are expanded by
	 * the code base. If an error occurs, null will be returned.
	 * 
	 * @return the URL
	 * @param name
	 *            absolute orL
	 */
	protected URL computeURL(String name) {
		try {
			if (name == null)
				return null;
			if ((name.startsWith("http:")) || (name.startsWith("file:")))
				return new URL(name);
			if (name.indexOf(':') > 0)
				return new URL("file:/" + name.replace('\\', '/'));
			String docBase = System.getProperty("user.dir").toString();
			return new URL(docBase + name.replace('\\', '/'));
		} catch (MalformedURLException me) {
			System.err.println("MalformedURLException: " + me);
			return null;
		}
	}

	/**
	 * Deselektiert das bisher darstellte Objekt und selektiert das übergebene
	 * Objekt. Falls null übergeben wird, wird die Anzeige zurückgesetzt. Die
	 * abgeleiteten Klassen müssen dafür sorgen, daß die Anzeige der Attribute
	 * des übergebenen Objekts im Applet angezeigt.
	 * 
	 * @param obj
	 *            Objekt
	 */
	protected void depictObjectAttributes(DrawableObject obj) {
		// Bisheriges Objekt deselektieren
		// DrawableObject oldSelectedObject =
		// drawableObjects.getSelectedObject();
		drawableObjects.deselect();
		// Falls obj == null, Anzeige löschen & fertig
		// Neues Objekt hervorheben und zeichnen
		drawableObjects.select(obj);
	}

	/**
	 * Draws the map.
	 * 
	 * @param g
	 *            graphical context
	 * @param r
	 *            clipping rectangle in world coordinates
	 * @param scale
	 *            current scale
	 */
	protected void drawMap(Graphics g, Rectangle r, int scale) {
		drawableObjects.drawAllObjects(g, r, scale);
		g.setColor(Color.black);
		g.drawRect(0, 0, mapWidth / scale + 1, mapHeight / scale + 1);
	}

	/**
	 * Sucht, ob sich ein sichtbares Objekt an der angegebenen Position
	 * befindet.
	 * 
	 * @param mx
	 *            x-Position in Applet-Pixel-Koordinaten
	 * @param my
	 *            y-Position in Applet-Pixel-Koordinaten
	 * @param selectable
	 *            nur selektierbare Objekte suchen?
	 */
	protected DrawableObject findObject(int mx, int my, boolean selectable) {
		// Fall: Maus außerhalb der Karte -> Resultat: null
		if ((mx < viewX) || (mx > viewX + viewWidth) || (my < viewY)
				|| (my > viewY + viewHeight))
			return null;
		// Symbol suchen
		DrawableObject obj = null;
		int px = xIntoCoord(mx);
		int py = yIntoCoord(my);
		obj = drawableObjects.getNextVisibleIntersectingObject(px, py, scale,
				selectable);
		return obj;
	}

	/**
	 * Information about the applet.
	 * 
	 * @return the information.
	 */
	public String getAppletInfo() {
		return "Applet ShowMap, Version 2.40\n" + getCopyrightLabel().getText();
	}

	/**
	 * Anlegen / Zurückgeben des ClickInfo-Labels.
	 * 
	 * @return ClickInfo-Label
	 */
	protected Label getClickInfoLabel() {
		if (ivjClickInfoLabel == null) {
			ivjClickInfoLabel = new Label();
			ivjClickInfoLabel.setName("ClickInfo");
			ivjClickInfoLabel.setFont(new Font("dialog", 0, 10));
			if (language == GERMAN)
				ivjClickInfoLabel.setText("Klicken: Objekt-Info");
			else
				ivjClickInfoLabel.setText("click: show info");
		}
		;
		return ivjClickInfoLabel;
	}

	/**
	 * Anlegen / Zurückgeben des Copyright-Labels.
	 * 
	 * @return Copyright-Label
	 */
	protected Label getCopyrightLabel() {
		if (ivjCopyrightLabel == null) {
			ivjCopyrightLabel = new Label();
			ivjCopyrightLabel.setName("CopyrightLabel");
			ivjCopyrightLabel.setFont(new Font("dialog", 0, 10));
			ivjCopyrightLabel
					.setText("(c) Th. Brinkhoff, 1999-2001, tbrinkhoff@acm.org");
		}
		;
		return ivjCopyrightLabel;
	}

	/**
	 * Anlegen / Zurückgeben des East-Buttons.
	 * 
	 * @return East-Button
	 */
	protected Button getEastButton() {
		if (ivjEastButton == null) {
			ivjEastButton = new Button();
			ivjEastButton.setName("EastButton");
			ivjEastButton.setFont(new Font("dialog", 0, 12));
			if (language == GERMAN)
				ivjEastButton.setLabel("O");
			else
				ivjEastButton.setLabel("E");
		}
		;
		return ivjEastButton;
	}

	/**
	 * Gibt die ID des selektierten Objekts zurück. Falls kein Objekt selektiert
	 * ist, wird -1 zurückgegeben.
	 * 
	 * @return ID des selektierten Objekts
	 */
	public long getIdOfSelectedObject() {
		DrawableObject obj = drawableObjects.getSelectedObject();
		if (obj != null)
			return obj.getId();
		else
			return -1;
	}

	/**
	 * Gibt die ID des selektierten Objekts als String zurück. Falls kein Objekt
	 * selektiert ist, wird einee leere Zeichenkette zurückgegeben.
	 * 
	 * @return ID des selektierten Objekz
	 */
	public String getIdOfSelectedObjectAsString() {
		long id = getIdOfSelectedObject();
		if (id != -1)
			return String.valueOf(id);
		else
			return "";
	}

	/**
	 * Gibt den Info-Text zu einem Objekt zurück. Die Rückgabe von null ist
	 * zulässig.
	 * 
	 * @return Info-Text
	 * @param obj
	 *            Objekt
	 */
	protected abstract String getInfoText(DrawableObject obj);

	/**
	 * Returns the name of the selected object.
	 * 
	 * @return Symbolname
	 */
	public String getNameOfSelectedObject() {
		DrawableObject obj = drawableObjects.getSelectedObject();
		if (obj != null)
			return obj.getName();
		else
			return "";
	}

	/**
	 * Anlegen / Zurückgeben des North-Buttons.
	 * 
	 * @return East-Button
	 */
	protected Button getNorthButton() {
		if (ivjNorthButton == null) {
			ivjNorthButton = new Button();
			ivjNorthButton.setName("NorthButton");
			ivjNorthButton.setFont(new Font("Dialog", 0, 12));
			ivjNorthButton.setLabel("N");
		}
		;
		return ivjNorthButton;
	}

	/**
	 * Gibt Informationen über die unterstützten Parameter zurück.
	 * 
	 * @return Parameter-Info in String-Array.
	 */
	public java.lang.String[][] getParameterInfo() {
		String[][] info = { { "name of parameter", "type of parameter",
				"see: ShowMapParameters.html" }, };
		return info;
	}

	/**
	 * Gibt Applet-Status zurück.
	 * 
	 * @return Status
	 */
	protected int getState() {
		return state;
	}

	/**
	 * Anlegen / Zurückgeben des West-Buttons.
	 * 
	 * @return West-Button
	 */
	protected Button getWestButton() {
		if (ivjWestButton == null) {
			ivjWestButton = new Button();
			ivjWestButton.setName("WestButton");
			ivjWestButton.setFont(new Font("dialog", 0, 12));
			ivjWestButton.setLabel("W");
		}
		;
		return ivjWestButton;
	}

	/**
	 * Initialisieren des Applets.
	 */
	public void init() {
		// übergebene Parameter auswerten
		// Oberflächenelemente hinzufügen
		if (panelY >= 0)
			panelY = viewY + viewHeight;
		else {
			panelY = 0;
			viewY = panelHeight;
		}

		// String p = getParameter("layers");
		// if (p != null)
		// numOfLayers = new Integer(p).intValue();
		if (drawableObjects == null)
			drawableObjects = new DrawableObjects(numOfLayers);
		initDrawablePresentation();
		// Anzeige vorbereiten
		depictObjectAttributes(null);
		startLoadingThread();
	}

	/**
	 * Legt die notwendigen Darstellungsobjekte an.
	 */
	protected abstract void initDrawablePresentation();

	/**
	 * Methode zur Behandlung von StateChanged-Events für das
	 * ItemListener-Interface.
	 * 
	 * @param e
	 *            akt. Event
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e) {

	}

	/**
	 * Die Methode wird aufgerufen, falls evtl. Daten nach Änderung des
	 * Kartenausschnitts oder ähnlichen Ereignissen geladen werden müssen.
	 * Standardmäßig passiert nichts.
	 */
	protected void loadDrawables() {
	}

	/**
	 * Methode zur Behandlung vom mouseEntered-Event für das
	 * MouseListener-Interface.
	 * 
	 * @param e
	 *            akt. Event
	 */
	public void mouseEntered(java.awt.event.MouseEvent e) {
	}

	/**
	 * Methode zur Behandlung vom mouseExited-Event für das
	 * MouseListener-Interface.
	 * 
	 * @param e
	 *            akt. Event
	 */
	public void mouseExited(java.awt.event.MouseEvent e) {
	}

	/**
	 * Wertet URL aus; weiteres siehe readDrawables (int,EntryInput,String).
	 * 
	 * @return Anzahl eingelesener Objekte
	 * @param objNum
	 *            Anzahl bisher eingelesener Objekte
	 * @param url
	 *            URL, wo die zu lesenden Daten liegen
	 * @param index
	 *            Index der URL
	 */
	protected int readDrawables(int objNum, URL url, int index) {
		try {

			// Stream öffnen
			InputStream is = null;
			EntryInput ber;
			if (url.toString().endsWith(".zip")) {
				ZipInputStream zis = new ZipInputStream(url.openStream());
				zis.getNextEntry();
				is = zis;
				ber = new DataReader(zis);
			} else {
				is = url.openStream();
				ber = new DataReader(is); // hier besser puffern!!!
			}
			// alle Objekte einlesen
			objNum = readDrawables(objNum, ber);
			is.close();
			return objNum;
		} catch (IOException ioe) {
			System.err.println("ShowMap.readDrawables.IOException: " + ioe);
			return objNum;
		}
	}

	/**
	 * Liest Drawable-Datei vom Enry-Input und erzeugt entsprechende
	 * Drawable-Objekte.
	 * 
	 * @return Anzahl eingelesener Objekte
	 * @param objNum
	 *            Anzahl bisher eingelesener Objekte
	 * @param ber
	 *            EntryInput
	 */
	protected int readDrawables(int objNum, EntryInput ber) {
		// read and test file type
		char t1 = ber.readChar();
		char t2 = ber.readChar();
		if ((t1 == 'D') && (t2 == 'O')) {
			// read and test version
			int version = ber.readInt();
			if (version < 200) {
				System.err.println("Wrong version!");
				return objNum;
			}
			// read map properties
			if (version >= 210) {
				mapWidth = ber.readInt();
				mapHeight = ber.readInt();
				baseScaleFactor = ber.readInt();
			}
			// read objects
			String objType = null;
			while ((objType = ber.readString()) != null) {
				// commands
				if (objType.startsWith("END"))
					break;
				else if (objType.startsWith("STARTED"))
					state = STARTED;
				else if (objType.startsWith("ACTIVE")) {
					state = ACTIVE;
				}
				// object type
				else if (objType.compareTo("ObjectType") == 0)
					(new DrawableObjectType()).read(ber);
				// object (version >= 210)
				else if (objType.equals("O")) {
					DrawableObject d = drawableObjects.readDrawableObject(ber,
							version);
					if (d == null) {
						System.err.println("Read error at object "
								+ (objNum + 1));
						break;
					}
				}
				// primitive (version >= 210)
				else if (version >= 210) {
					Drawable d = Drawable.readDrawable(ber, objType);
					if (d == null) {
						System.err.println("Read error at primitive "
								+ (objNum + 1));
						break;
					} else {
						drawableObjects.addDrawable(d);
						if (!ber.eol()) {
							long objID = ber.readInt();
							if (objID != 0) {
								DrawableObject obj = drawableObjects
										.getObjectById(objID);
								if (obj == null)
									drawableObjects.newDrawableObject(objID,
											"", "", "").addDrawable(d);
								else
									obj.addDrawable(d);
							}
						}
					}
				}
				// object with primitive (version 200)
				else
					drawableObjects.readDrawableObject(ber, objType);
				objNum++;
			}
		} else
			System.err.println("Wrong file type!");
		// return number of objects
		return objNum;
	}

	/**
	 * Sets the state of the applet.
	 * 
	 * @param state
	 *            the new state
	 */
	protected void setState(int state) {
		this.state = state;
	}

	/**
	 * Stellt die Text-Ausgabe in Abhängigkeit vom Parameter auf Unicode oder
	 * ANSI.
	 * 
	 * @param f
	 *            Unicode?
	 */
	protected void setUnicode(boolean f) {
		DrawableText.setUnicode(f);
	}

	/**
	 * Interprets the parameter "url" and starts the loading thread.
	 */
	protected void startLoadingThread() {
		String urlName = "";
		if (urlName != null) {
			System.out.println("ShowMap: URL-Name: " + urlName);
			URL url = computeURL(urlName);
			System.out.println("ShowMap: URL: " + urlName);
			if (url != null)
				new LoadDrawables(this, url, 0).start();
		} else
			System.err.println("ShowMap: no URL!");
	}

	/**
	 * Transforms pixel position into x-coordinate.
	 * 
	 * @return x-coordinate
	 * @param x
	 *            x-pixel
	 */
	public int xIntoCoord(int x) {
		x = x - viewX + viewMapX - (viewWidth / 2);
		return x * scale;
	}

	/**
	 * Transforms pixel position into y-coordinate.
	 * 
	 * @return y-coordinate
	 * @param y
	 *            y-pixel
	 */
	public int yIntoCoord(int y) {
		y = y - viewY + viewMapY - (viewHeight / 2);
		return y * scale;
	}

}
