package routing;

import java.awt.*;
import java.io.*;
import java.util.*;
import drawables.*;
import util.Num;

/**
 * Container class for edges.
 * 
 * @version 1.20	24.05.01	Marking added
 * @version 1.12	28.06.00	Timer removed
 * @version	1.11	24.04.00	superclass corrected, support of edge with null-strings
 * @version	1.10	04.01.00	WeightManager, set/getNumOfClasses, searchEdge added
 * @version	1.00	26.11.99	separated from the class "Edge"
 * @author Thomas Brinkhoff
 */
public class Edges {
	
	/**
	 * Container
	 */
	protected Hashtable hashTable =  null;
	/**
	 * Highest id of a node
	 */
	private long maxId = 0;

	/**
	 * Object for weighting edges
	 */
	private WeightManager wm = null;
	/**
	 * Number of edge classes (a change requires also to adapt speed, minscale and the class Node!)
	 */
	private int numOfClasses = 7;
	/**
	 * Edge used for searching
	 */
	private Edge searchEdge = new Edge (0);

	/**
	 * Value of the mark which corresponds to unmarked
	 */
	protected int nullMark = 0;

	/**
	 * Speed of the edge classes
	 */
	protected int speed[] = {140,70,50,40,20,20,5};
	/**
	 * Scale limit for visibility
	 */
	protected int[] minScale = {128,32,8,4,2,1,1};

/**
 * Konstruktor.
 */
public Edges() {
	hashTable = new Hashtable (10000);
}
/**
 * Clears all marks.
 */
public void clearAllMarks () {
	nullMark++;
}
/**
 * Gibt Enumeration über alle Kanten zurück.
 * @return Enumeration der Kanten
 */
public Enumeration elements () {
	return hashTable.elements();
}
/**
 * Gibt die Kante zurück, dieer die angegebene ID besitzt.
 * Gibt es keine solche Kante, wird null zurückgegeben.
 * @return ggf. gefundene Kante
 * @param id ID der Kante
 */
public Edge get (long id) {
	searchEdge.setID (id);
	return (Edge) hashTable.get (searchEdge);
}
/**
 * Returns the next free identifier.
 * @return free identifier
 */
public long getNextFreeId () {
	return maxId+1;
}
/**
 * Returns the number of edge classes.
 * @return number of edge classes
 */
public int getNumOfClasses () {
	return numOfClasses;
}
/**
 * Gibt die Geschwindigkeit der angegebenen Kantenklasse zurück.
 * @return Geschwindigkeit
 * @param pClass Kantenklasse
 */
public int getSpeed (int pClass) {
	return speed[Num.putIntoInterval (pClass,0,numOfClasses)];
}
/**
 * Gibt das Objekt zur Kantenbewertung zurück.
 * @return Objekt zur Kantenbewertung
 */
public WeightManager getWeightManager () {
	return wm;
}
/**
 * Initialisiert die Darstellung der Kanten.
 * @param color Farben für die Kantenklassen
 * @param highlightColor Hervorhebungsfarbe
 */
public void initPresentation (Color color[], Color highlightColor) {
	for (int i=0; i<numOfClasses; i++)
		DrawablePresentation.newDrawablePresentation("Edge"+i,false,color[i],highlightColor);
	DrawablePresentation.newDrawablePresentation("EdgeH",false,highlightColor,highlightColor);
}
/**
 * Erzeugt neue Kante und fügt diese in Container ein.
 * Existiert bereits eine Kante mit der angegebenen ID, wird diese statt dessen zurückgegeben.
 * @return neu erzeugte / gefundene Kante
 * @param id ID
 * @param edgsClass Kantenklasse
 * @param node1 Startknoten
 * @param node2 Zielknoten
 * @param name Kantenname
 */
public Edge newEdge (long id, int edgeClass, Node node1, Node node2, String name) {
	if ((node1 == null) || (node2 == null))
		return null;
	Edge edge = get(id);
	if (edge != null)
		return edge;
	edge = new Edge (id, edgeClass, node1, node2, name, this);
	hashTable.put (edge,edge);
	node1.addEdge (edge);
	node2.addEdge (edge);
	if (id > maxId)
		maxId = id;
	return edge;
}
/**
 * Gibt die Anzahl der gespeicherten Kanten zurück.
 * @return Anzahl
 */
public int numOfEdges () {
	return hashTable.size();
}
/**
 * Liest eine Kante vom DataInput.
 * Schlägt das Einlesen fehl, wird null zurückgegeben.
 * @return eingelesene Kante
 * @param in DataInput
 * @param nodes Container für Knoten
 */
public Edge read (DataInput in, Nodes nodes) throws IOException {
	Edge res = null;
	long id1 = in.readLong();
	long id2 = in.readLong();
	byte len = in.readByte();
	Node pNode1 = nodes.get(id1);
	Node pNode2 = nodes.get(id2);
	if (len > 0) {
		byte[] data = new byte[len];
		in.readFully (data);
		long eID = in.readLong();
		int eClass = in.readInt();
		res = newEdge (eID,eClass,pNode1,pNode2,new String(data));
	}	
	else {
		long eID = in.readLong();
		int eClass = in.readInt();
		res = newEdge (eID,eClass,pNode1,pNode2,null);
	}	
	return res;
}
/**
 * Removes an edge.
 * @return sucessful?
 * @param edge the edge
 */
public boolean removeEdge (Edge edge) {
	if (edge == null)
		return false;
	edge.getNode1().removeEdge (edge);
	edge.getNode2().removeEdge (edge);
	hashTable.remove (edge);
	return true;
}
/**
 * Setzt die Maßstabsgrenzen der Kantenklassen neu.
 * @param newMinScale Maßstabsgrenzen
 */
public void setMinScaleArray (int newMinScale[]) {
	minScale = newMinScale;
}
/**
 * Sets the number of edge classes.
 * @param num number of edge classes
 */
public void setNumOfClasses (int num) {
	numOfClasses = num;
}
/**
 * Setzt die Standard-Geschwindigkeiten der Kantenklassen neu.
 * @param newSpeed Standard-Geschwindigkeiten
 */
public void setSpeedArray (int newSpeed[]) {
	speed = newSpeed;
}
/**
 * Setzt das Objekt zur Kantenbewertung.
 * @param wm neuer WeightManager
 */
public void setWeightManager (WeightManager wm) {
	this.wm = wm;
}
}
