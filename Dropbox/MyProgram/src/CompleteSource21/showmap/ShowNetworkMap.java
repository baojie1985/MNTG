package showmap;

import java.awt.Color;
import java.net.URL;

import routing.Network;
import routing.Node;
import routing.PathEdge;
import drawables.DrawableObject;
import drawables.DrawablePresentation;
import drawables.DrawableSymbol;
import drawables.DrawableText;

/**
 * Applet for showing an interactive network map. The applet allows navigating,
 * zooming and retrieval of object information as well as the computations of
 * shortest paths.
 * 
 * @version 1.40 01.06.00 support of presentation modes
 * @version 1.30 09.03.00 Laden des Netwerks im separaten Prozeß, geänderte
 *          Hervorhebung
 * @version 1.20 30.01.00 setEdgeSelectivity, isEdge added
 * @version 1.10 25.11.99 Ausgliederung aus ShowTrafficNetworkMap
 * @author Thomas Brinkhoff
 */

public class ShowNetworkMap extends ShowMap {

	/**
	 * Netzwerk.
	 */
	protected Network net = null;
	/**
	 * aktueller Startknoten.
	 */
	protected Node start = null;
	/**
	 * aktueller Endknoten.
	 */
	protected Node stop = null;
	/**
	 * aktuell berechneter, kürzester Weg.
	 */
	protected PathEdge path = null;

	/**
	 * Knoten selektierbar?.
	 */
	protected boolean nodesSelectable = true;
	/**
	 * Knoten selektierbar?.
	 */
	protected boolean edgesSelectable = false;

	/**
	 * Puffer für URL beim Laden.
	 */
	private URL nodeURL = null;

	/**
	 * Berechnet schnellen Weg zwischen den gesetzten Start- und Endknoten.
	 */
	public void computeFastWay() {
		if (path != null)
			path.setStandardAppearance();
		path = net.computeFastWay2(start, stop);
		if (path != null) {
			path.changePresentation("Way");
			// path.debugPrint();
		}
	}

	/**
	 * Berechnet den kürzesten Weg zwischen den gesetzten Start- und Endknoten.
	 */
	public void computeShortestWay() {
		if (path != null)
			path.setStandardAppearance();
		path = net.computeFastestWay(start, stop);
		if (path != null) {
			path.changePresentation("Way");
			// path.debugPrint();
		}
	}

	/**
	 * Berechnet alle Wege zwischen den gesetzten Start- und Endknoten, die
	 * nicht um den angegebenen Prozentwert schlechter als die beste Verbindung
	 * sind.
	 * 
	 * @param threshold
	 *            Schwellenwert in Prozent
	 */
	public void computeShortestWays(int threshold) {
		if (path != null)
			path.setStandardAppearance();
		path = net.computeFastWays(start, stop, threshold);
		if (path != null) {
			path.changePresentation("Way");
			// path.debugPrint();
		}
	}

	/**
	 * Deselektiert das bisher darstellte Objekt und selektiert das übergebene
	 * Objekt. Falls null übergeben wird, wird die Anzeige zurückgesetzt. Sorgt
	 * für die Anzeige der Attribute des übergebenen Objekts im Applet.
	 * 
	 * @param obj
	 *            Objekt
	 */
	protected void depictObjectAttributes(DrawableObject obj) {
		super.depictObjectAttributes(obj);
		if (obj == null)
			return;
	}

	/**
	 * Gibt den Info-Text zu einem Symbol zurück.
	 * 
	 * @return Info-Text
	 * @param symb
	 *            Drawble-Objekt
	 */
	protected String getInfoText(DrawableObject symb) {
		if (symb == null)
			return null;
		// Fall Knoten
		if (symb.getDrawable(0).getClass().getName().startsWith("routing.Node")) {
			return symb.getName();
		} else
			return null;
	}

	/**
	 * Gibt das Netzwerk zurück.
	 * 
	 * @return Netzwerk
	 */
	public Network getNetwork() {
		return net;
	}

	/**
	 * Initialisieren des Applets.
	 */
	public void init() {
		net = new Network();
		super.init();
	}

	/**
	 * Legt die notwendigen Darstellungsobjekte an.
	 */
	protected void initDrawablePresentation() {
		// Initialisierungen
		DrawablePresentation.init();
		// Linien und Flächen
		for (int i = 0; i <= 7; i++)
			DrawablePresentation.newDrawablePresentation("T" + i, false,
					new Color(180 + i * 10, 255, 100), Color.red);
		for (int i = 8; i <= 20; i++)
			DrawablePresentation.newDrawablePresentation("T" + i, false,
					new Color(255, 255 - (i - 8) * 10, 100), Color.red);
		DrawablePresentation.newDrawablePresentation("T21", false, new Color(
				255, 200, 150), Color.red);
		DrawablePresentation.newDrawablePresentation("T22", false, new Color(0,
				200, 0), Color.red);
		DrawablePresentation.newDrawablePresentation("T23", false, new Color(
				120, 255, 255), Color.red);
		DrawablePresentation.newDrawablePresentation("T25", false, new Color(
				120, 255, 255), Color.red);
		DrawablePresentation.newDrawablePresentation("T26", false, new Color(
				120, 255, 255), Color.red);
		DrawablePresentation.newDrawablePresentation("T27", false, new Color(
				120, 255, 255), Color.red);
		DrawablePresentation.newDrawablePresentation("T28", false, Color.red,
				Color.red);
		DrawablePresentation.newDrawablePresentation("T29", false, new Color(
				255, 150, 150), Color.red, 0, 2, 0, 128, 33);
		DrawablePresentation.newDrawablePresentation("T29", false, new Color(
				255, 150, 150), Color.red, 0, 3, 0, 32, 1);
		DrawablePresentation.newDrawablePresentation("T30", false, new Color(
				255, 175, 220), Color.red);
		DrawablePresentation.newDrawablePresentation("T31", false, new Color(
				255, 190, 190), Color.red);
		// Städte
		int size[] = { 10, 9, 9, 8, 8, 8, 8, 7, 6, 6 };
		for (int i = 1; i <= 9; i++) {
			DrawablePresentation.newDrawablePresentation("TN" + i, false,
					Color.black, Color.red, DrawableText.NORMAL, size[i] + 1,
					size[i] / 2 + 3, 65536, 0);
			DrawablePresentation.newDrawablePresentation("TB" + i, false,
					Color.black, Color.red, DrawableText.BOLD, size[i] + 1,
					size[i] / 2 + 3, 65536, 0);
			DrawablePresentation.newDrawablePresentation("TI" + i, false,
					Color.black, Color.red, DrawableText.ITALIC, size[i] + 1,
					size[i] / 2 + 3, 65536, 0);
		}
		DrawablePresentation.newDrawablePresentation("C1", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.SQUAREDOT, 9);
		DrawablePresentation.newDrawablePresentation("C2", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.SQUAREDOT, 8);
		DrawablePresentation.newDrawablePresentation("C3", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.SQUARE, 8);
		DrawablePresentation.newDrawablePresentation("C4", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.CIRCLEDOT, 7);
		DrawablePresentation.newDrawablePresentation("C5", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.CIRCLE, 7);
		DrawablePresentation.newDrawablePresentation("C6", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.RING, 7);
		DrawablePresentation.newDrawablePresentation("C7", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.CIRCLE, 6);
		DrawablePresentation.newDrawablePresentation("C8", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.CIRCLE, 5);
		DrawablePresentation.newDrawablePresentation("C9", false, Color.black,
				Color.lightGray, Color.black, Color.yellow,
				DrawableSymbol.CIRCLE, 5);
		// Texte
		for (int i = 0; i <= 5; i++) {
			DrawablePresentation.newDrawablePresentation("St" + i, false,
					new Color(191, 0, 0), Color.red, DrawableText.BOLD, 10 + i);
			if (i < 2)
				DrawablePresentation.newDrawablePresentation("Ld" + i, false,
						Color.red, Color.red, DrawableText.NORMAL, 6 + 2 * i);
			else
				DrawablePresentation.newDrawablePresentation("Ld" + i, false,
						new Color(223, 0, 0), Color.red, DrawableText.NORMAL,
						6 + 2 * i);
			DrawablePresentation.newDrawablePresentation("Me" + i, false,
					Color.blue, Color.red, DrawableText.NORMAL, 8 + 2 * i);
			DrawablePresentation.newDrawablePresentation("Se" + i, false,
					Color.blue, Color.red, DrawableText.NORMAL, 6 + i);
			DrawablePresentation.newDrawablePresentation("Fl" + i, false,
					Color.blue, Color.red, DrawableText.NORMAL, 5 + i);
			DrawablePresentation
					.newDrawablePresentation("In" + i, false, new Color(0, 159,
							0), Color.red, DrawableText.NORMAL, 5 + i);
		}
		// Berge
		DrawablePresentation.newDrawablePresentation("Mount", false,
				Color.black, Color.black, Color.black, Color.red,
				DrawableSymbol.TRIANGLE, 7);
		DrawablePresentation.newDrawablePresentation("TMount", false,
				Color.black, Color.red, DrawableText.NORMAL, 7, 6, 65536, 0);
	}

	/**
	 * Testet, ob es sich bei dem Objekt um eine Kante handelt.
	 * 
	 * @return Kante?
	 * @param id
	 *            Objekt-ID
	 */
	public boolean isEdge(String id) {
		return net.getEdges().get(new Long(id).longValue()) != null;
	}

	/**
	 * Testet, ob es sich bei dem Objekt um einen Knoten handelt.
	 * 
	 * @return Knoten?
	 * @param id
	 *            Objekt-ID
	 */
	public boolean isNode(String id) {
		return net.getNodes().get(new Long(id).longValue()) != null;
	}

	/**
	 * Wertet URL aus: entweder Aufruf der Methode der Oberklasse oder Laden des
	 * Netzwerkes.
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
		// Case: basic map
		if ((index == 0) || (index > 2))
			return super.readDrawables(objNum, url, index);
		else if (index == 1) {
			nodeURL = url;
			return objNum;
		} else /* if (index == 2) */{
			net.createByNetworkFiles(nodeURL, url, drawableObjects);
			return objNum;
		}
	}

	/**
	 * Setzt den/die zuvor berechneten Weg(e) zurück.
	 */
	public void resetWay() {
		if (path != null)
			path.setStandardAppearance();
	}

	/**
	 * Setzt den Selektionsmodus für Kanten.
	 * 
	 * @param on
	 *            an?
	 */
	public void setEdgeSelectability(boolean on) {
		edgesSelectable = on;
		for (int i = 0; i < net.getEdges().getNumOfClasses(); i++)
			setSelectionMode("Edge" + i, on);
	}

	/**
	 * Setzt den Selektionsmodus für Knoten.
	 * 
	 * @param on
	 *            an?
	 */
	public void setNodeSelectability(boolean on) {
		nodesSelectable = on;
		for (int i = 0; i < net.getNodes().getNumOfClasses(); i++)
			setSelectionMode("Node" + i, on);
	}

	/**
	 * Setzt den Selektionsmodus für alle Darstellungsobjekte mit einem Namen.
	 * 
	 * @param presName
	 *            Darstellungsname
	 * @param on
	 *            selektierbar?
	 */
	protected void setSelectionMode(String presName, boolean on) {
		DrawablePresentation p = DrawablePresentation.get(presName);
		while (p != null) {
			p.setSelectability(on);
			p = p.getNext();
		}
	}

	/**
	 * Setzt den Knoten mit der übergebenen ID als Start-Knoten.
	 * 
	 * @param id
	 *            Knoten-ID
	 */
	public void setStartNode(long id) {
		start = net.getNodes().get(id);
	}

	/**
	 * Setzt den Knoten mit der übergebenen ID als Start-Knoten.
	 * 
	 * @param id
	 *            Knoten-ID als String
	 */
	public void setStartNodeAsString(String id) {
		try {
			long pId = new Long(id).longValue();
			setStartNode(pId);
		} catch (NumberFormatException numFormatExcep) {
		}
		;
	}

	/**
	 * Setzt den Knoten mit der übergebenen ID als End-Knoten.
	 * 
	 * @param id
	 *            Knoten-ID
	 */
	public void setStopNode(long id) {
		stop = net.getNodes().get(id);
	}

	/**
	 * Setzt den Knoten mit der übergebenen ID als End-Knoten.
	 * 
	 * @param id
	 *            Knoten-ID als String
	 */
	public void setStopNodeAsString(String id) {
		try {
			long pId = new Long(id).longValue();
			setStopNode(pId);
		} catch (NumberFormatException numFormatExcep) {
		}
		;
	}

	/**
	 * Interprets the parameters "url*" and starts the loading thread.
	 */
	protected void startLoadingThread() {
		URL url[] = { null, null, null };

		new LoadDrawables(this, url, 0).start();
	}
}
