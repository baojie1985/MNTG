package routing;

import java.awt.*;
import java.io.*;
import java.util.*;
import drawables.*;

/**
 * Container class for the class Node.
 * 
 * @version	1.21	16.08.2003	distance becomes double
 * @version	1.20	03.07.2001	parameter DrawableObjects removed from findNearest
 * @version	1.12	28.06.2000	adapted to DrawableObjects v4.0, Timer removed
 * @version	1.11	24.04.2000	support of node with null-text
 * @version	1.10	01.02.2000	numOfNodes, getNextFreeId, searchNode added
 * @version	1.00	19.12.1999	extracted from class Node
 * @author Thomas Brinkhoff
 */
 
public class Nodes {

	/**
	 * Container of all nodes
	 */
	private Hashtable hashTable = null;
	/**
	 * Container of all drawable objects
	 */
	private DrawableObjects objects = null;
	/**
	 * Highest id of a node
	 */
	private long maxId = 0;

	/**
	 * Number of node classes
	 */
	private int numOfClasses = 0;

	/**
	 * Index of the actual edge
	 */
	private int actEdge = 0;
	/**
	 * Value of the mark which corresponds to unmarked
	 */
	protected int nullMark = 0;
	/**
	 * Highest existing mark
	 */
	protected int maxMark = 0;
	/**
	 * Index of the actual edge
	 */
	private Node searchNode = new Node(0,0,0);

	/**
	 * Scale boundaries for the visiblity of the nodes
	 */
	protected int[] minScale = {16,8,4,2,1,1,1,1};
	/**
	 * Scale boundaries for the visiblity of the node texts
	 */
	protected int[] minTextScale = {8,4,2,1,1,1,1,1};

/**
 * Konstruktor.
 * @param numOfClasses Anzahl der Knotenklassen
 */
public Nodes (int numOfClasses) {
	this.numOfClasses = numOfClasses;
	hashTable = new Hashtable (10000);
}
/**
 * Konstruktor.
 * @param edges Container über Kanten
 */
public Nodes (Edges edges) {
	this.numOfClasses = edges.getNumOfClasses()+1;
	hashTable = new Hashtable (10000);
}
/**
 * Die Markierungen aller Knoten werden gelöscht.
 */
public void clearAllMarks () {
	maxMark++;
	nullMark = maxMark;
}
/**
 * Gibt Enumeration über alle Knoten zurück.
 * @return Enumeration der Knoten
 */
public Enumeration elements () {
	return hashTable.elements();
}
/**
 * Finds the nearest node to the position (x,y).
 * @return the node
 * @param x x-coordinate
 * @param y y-coordinate
 */
public Node findNearest (int x, int y) {
	if (numOfNodes() == 0)
		return null;
	Enumeration e = null;
	Node node = null;
	if (objects == null) {
		e = hashTable.elements();
		node = (Node)e.nextElement();
		objects = node.getContainer();
	}
	Node testNode = new Node (0,x,y);
	if (objects != null)
		try {
			return (Node)((DrawableObjectsWithSearchTree)objects).findNearestDrawable (x,y,null,new DrawableSpatialSearchTreeObject(testNode));
		}
		catch (Exception ex) {
			System.err.println("Exception in Nodes.findNearest: "+ex);
		}
	Node nearestNode = node;
	double distance = testNode.distanceTo(nearestNode);
	for (; e.hasMoreElements();) {
		Node next = (Node)e.nextElement();
		double actDist = testNode.distanceTo(next);
		if (actDist < distance) {
			nearestNode = next;
			distance = actDist;
		}
	}
	return nearestNode;
}
/**
 * Gibt den Knoten zurück, der die angegebene ID besitzt.
 * Gibt es keinen solchen Knoten, wird null zurückgegeben.
 * @return ggf. gefundener Knoten
 * @param id ID des Knotens
 */
public Node get (long id) {
	searchNode.setID(id);
	return (Node) hashTable.get (searchNode);
}
/**
 * Returns the next free identifier.
 * @return free identifier
 */
public long getNextFreeId () {
	return maxId+1;
}
/**
 * Returns the number of node classes.
 * @return number of node classes
 */
public int getNumOfClasses () {
	return numOfClasses;
}
/**
 * Initialisiert die Darstellung der Knoten.
 * @param color Farben für die Kantenklassen
 * @param highlightColor Hervorhebungsfarbe
 */
public void initPresentation (Color color[], Color highlightColor) {
	for (int i=0; i<numOfClasses; i++) {
		DrawablePresentation.newDrawablePresentation("Node"+i,true,Color.black,color[i],Color.black,highlightColor,DrawableSymbol.CIRCLE,6);
		DrawablePresentation.newDrawablePresentation("NodeText"+i,true,Color.black,highlightColor,DrawableText.NORMAL,7,6/2+3,65535,0);
	}	
	DrawablePresentation.newDrawablePresentation("NodeH",true,Color.black,highlightColor,Color.black,highlightColor,DrawableSymbol.CIRCLE,6);
}
/**
 * Creates a new node in the container.
 * @return the new node
 * @param id id of the node
 * @param x x-coordinate
 * @param y y-coordinate
 * @param name name (may be null)
 */
public Node newNode (long id, int x, int y, String name) {
	Node node;
	if (name != null)
		node = new Node (id, x,y, name, this);
	else
		node = new Node (id, x,y, this);
	hashTable.put (node,node);
	if (id > maxId)
		maxId = id;
	return node;
}
/**
 * Gibt die Anzahl der gespeicherten Knoten zurück.
 * @return Anzahl
 */
public int numOfNodes () {
	return hashTable.size();
}
/**
 * Liest einen Knoten vom DataInput.
 * Schlägt das Einlesen fehl, wird null zurückgegeben.
 * @return eingelesener Knoten
 * @param in Data-Input
 */
public Node read (DataInput in) {
	Node res = null;
	try {
		byte len = in.readByte();
		if (len > 0) {
			byte[] data = new byte[len];
			in.readFully (data);
			long pID = in.readLong();
			int x = in.readInt();
			int y = in.readInt();
			res = newNode (pID,x,y,new String(data));
		}	
		else {
			long pID = in.readLong();
			int x = in.readInt();
			int y = in.readInt();
			res = newNode (pID,x,y,null);
		}
		return res;
	}
	catch (IOException e) {
		return res;
	}	
}
/**
 * Removes the node if its number of edges is zero.
 * @return successful?
 * @param node node to be removed
 */
public boolean removeNode (Node node) {
	if ((node != null) && (node.getNumOfEdges() == 0)) {
		hashTable.remove (node);
		return true;
	}
	return false;
}
/**
 * Setzt die Maßstabsgrenzen der Kantenklassen neu.
 * @param newMinScale Maßstabsgrenzen
 */
public void setMinScaleArray (int newMinScale[]) {
	minScale = newMinScale;
}
/**
 * Setzt die Text-Maßstabsgrenzen der Kantenklassen neu.
 * @param newMinTextScale Text-Maßstabsgrenzen
 */
public void setMinTextScaleArray (int newMinTextScale[]) {
	minTextScale = newMinTextScale;
}
/**
 * Sets the number of node classes.
 * @param num number of node classes
 */
public void setNumOfClasses (int num) {
	numOfClasses = num;
}
}
