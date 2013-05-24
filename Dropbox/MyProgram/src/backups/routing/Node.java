package routing;

import java.awt.*;
import java.io.*;
import drawables.*;
import util.*;

/**
 * Class for representing nodes.
 * 
 * @version	4.00	18.08.2003	super class Symbol replaced by Drawable, distance becomes double, dynamic number of edge per node, heapPos added
 * @version	3.30	03.07.2001	getContainer renamed to getNodeContainer
 * @version	3.21	01.06.2000	adapted to new versions of drawable classes
 * @version	3.20	24.04.2000	additional Constrctor, support of null-text
 * @version	3.10	09.03.2000	getContainer, replace, remove, setName, setID added
 * @version	3.00	13.12.1999	split into the classes Node and Nodes; removeEdge added
 * @version	2.00	22.03.1999	adapted the class Drawables, version 2.0
 * @version	1.00	02.12.1998	first version
 * @author Thomas Brinkhoff
 */
 
public class Node extends Drawable {

	/**
	 * x-coordinate.
	 */
	protected int x = 0;
	/**
	 * y-coordinate.
	 */
	protected int y = 0;
	/**
	 * The identifier.
	 */
	private long id = 0; 
	/**
	 * Class of the node.
	 */
	private short nodeClass = 0;
	/**
	 * Number of edges.
	 */
	private byte numOfEdges = 0;
	/**
	 * Index of the current edge.
	 */
	private byte actEdge = 0;
	/**
	 * Name of the node (may be null).
	 */
	private String name = null;
	/**
	 * The edges of the node.
	 */
	private Edge[] edge = new Edge[3];
	/**
	 * The container, the node belongs to.
	 */
	private Nodes nodes = null;

	/**
	 * The current mark.
	 */
	private int mark = 0;
	
	/**
	 * distances of the path 1 and 2
	 */
	private double distanceOfWay[] = {0.0,0.0};
	/**
	 * marks of edges using path 1 and 2
	 */
	private byte wayEdge[] = {-1,-1};
	/**
	 * Positions in a heap depending of the path (1 and 2)
	 */
	protected short heapPos[] = {0,0};

/**
 * Constructor.
 * @param  id  id of the node
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 */
public Node (long id, int x, int y) {
	this.id = id;
	this.x = x;
	this.y = y;
	layer = POINTLAYER;
	pres = DrawablePresentation.get("Node0");
}

/**
 * Constructor.
 * @param  id  id of the node
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  name  name
 * @param  nodes  container
 */
public Node (long id, int x, int y, String name, Nodes nodes) {
	this.id = id;
	this.x = x;
	this.y = y;
	layer = POINTLAYER;
	pres = DrawablePresentation.get("Node"+(nodes.getNumOfClasses()-1));
	minScale = nodes.minScale[nodes.getNumOfClasses()-1];
	maxScale = 0;
	this.name = name;
	this.nodes = nodes;
	nodeClass = (short)(nodes.getNumOfClasses()-1);
}

/**
 * Constructor.
 * @param  id  id of the node
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  nodes  container
 */
public Node (long id, int x, int y, Nodes nodes) {
	this (id,x,y,null,nodes);
}

/**
 * Passt die Knotenklasse an die neue Kantenklasse an.
 * @param newEdgeClass neue Kantenklasse
 */
protected void adaptClass (int newEdgeClass) {
	// Fall 1: Kante von wichtigerer Klasse => Knotenklasse anpassen
	if (newEdgeClass+1 < nodeClass) {
		nodeClass = (short)(newEdgeClass+1);
		setMinScale (nodes.minScale[nodeClass]);
		setPresentation (DrawablePresentation.get("Node"+nodeClass));
		/*if (getNext() != null) {
			getNext().setMinScale (nodes.minTextScale[nodeClass]);
			getNext().setPresentation (DrawablePresentation.get("NodeText"+nodeClass));
		}*/
	}
	// Fall 2: Kante von gleicher Klasse und viele anliegende Kanten => ggf. Knotenklasse anpassen
	else if ((newEdgeClass+1 == nodeClass) && (numOfEdges > 2)) {
		int n = 0;
		for (int i=0; i<numOfEdges; i++)
			if (edge[i].getEdgeClass()+1 == nodeClass)
				n++;
		if (n > 2) {
			nodeClass--;
			setMinScale (nodes.minScale[nodeClass]);
			setPresentation (DrawablePresentation.get("Node"+nodeClass));
			/*if (getNext() != null) {
				getNext().setMinScale (nodes.minTextScale[nodeClass]);
				getNext().setPresentation (DrawablePresentation.get("NodeText"+nodeClass));
			}*/
		}	
	}	
}
/**
 * Fügt eine neue Kante dem Knoten zu.
 * @param newEdge die neue Kante
 */
public void addEdge (Edge newEdge) {
	if (numOfEdges >= edge.length) {
		Edge[] newEdges = new Edge[2*edge.length];
		for (int i=0; i<edge.length; i++)
			newEdges[i] = edge[i];
		edge = newEdges;
	}
	try {
	edge[numOfEdges++] = newEdge;
	adaptClass (newEdge.getEdgeClass());
	} catch (Exception ex) {
		System.out.println("ex "+ex);
	}
}
/**
 * Löscht die angegebene Markierung vom Knoten.
 * @param value Wert der Markierung
 */
public void clearMark (int value) {
	if (isMarked (value))
		mark = mark-value;
}
/**
 * Löscht alle Wege, die vom Knoten ausgehen.
 */
public void clearWays () {
	wayEdge[0] = -1;
	wayEdge[1] = -1;
}
/**
 * Computes the distance of the drawing primitive to a point.
 * @return distance
 * @param x x-coordinate of the point
 * @param y x-coordinate of the point
 */
public double computeDistanceTo (int x, int y) {
	return computeDistance (this.x,this.y, x,y);
}
/**
 * Ausgabe des Knotens zu Debugging-Zwecken.
 */
public void debugPrint (int way) {
	System.out.print(name+ " - "+ getDistanceOfWay(way));
}
/**
 * Berechnet den Abstand des Knotens zu dem angegebenen Knoten.
 * @return Abstand
 * @param node Vergleichs-Knoten
 */
public double distanceTo (Node node) {
	return computeDistanceTo (node.getX(),node.getY());
}

/**
 * Draws the node if it is visible according to the given scale.
 * @param  g  graphic context
 * @param  scale  current scale
 * @param  mode  draw mode
 * @param  value  value
 */
protected void drawProtected (Graphics g, int scale, int mode, int pvalue) {
	DrawablePresentation ap = pres.get(scale,mode,pvalue);
	int size = ap.getSize();
	try {
		size = size*obj.getDataValue(0);
	}
	catch (Exception ex) {
	}
	// Symbol-Füllung zeichnen
	if (selected)
		g.setColor (ap.getSelectionFillColor());
	else
		g.setColor (ap.getFillColor());
	g.fillOval (x/scale-size/2,y/scale-size/2,size,size);
	// Symbol-Umriß zeichnen
	if (selected)
		g.setColor (ap.getSelectionColor());
	else
		g.setColor (ap.getColor());
	g.drawOval (x/scale-size/2,y/scale-size/2,size,size);
}

/**
 * Vergleich von zwei Knoten auf Gleichheit über die ID.
 * @return Knoten gleich?
 * @param node zu vergleichender Knoten
 */
public boolean equals (Object node) {
	if (node == null)
		return false;
	return (id == ((Node)node).id); 
}
/**
 * Gibt die Distanz bei dem Knoten bezüglich des angegebenen Weges zurück.
 * @return Distanz
 * @param Index des Weges
 */
public double getDistanceOfWay (int way) {
	return distanceOfWay[way-1];
}
/**
 * Gibt die erste Kante des Knotens zurück.
 * Existiert keine Kante, wird null zurückgegeben.
 * @return erste Kante
 */
public Edge getFirstEdge () {
	if (numOfEdges == 0)
		return null;
	actEdge = 0;
	return edge[actEdge++];
}
/**
 * Gibt die ID des Knotens zurück.
 * @return ID des Knotens
 */
public long getID () {
	return id;
}

/**
 * Returns the minimum bounding rectangle of the primitive.
 * @return  the MBR
 */
public Rectangle getMBR () {
	return new Rectangle (x,y,0,0);
}

/**
 * Returns the name of the node.
 * @return Name
 */
public String getName () {
	if (name != null)
		return name;
	else
		return "";
}
/**
 * Gibt die nächste Kante des Knotens zurück, nachdem zuvor getFirstEdge
 * und ggf. darauffolgend getNextEdge aufgerufen worden waren.
 * Existiert nächste Kante, wird null zurückgegeben.
 * @return nächste Kante
 */
public Edge getNextEdge () {
	if (actEdge >= numOfEdges)
		return null;
	return edge[actEdge++];
}
/**
 * Gibt Knotenklasse zurück.
 * @return Knotenklasse
 */
public int getNodeClass () {
	return nodeClass;
}
/**
 * Returns the container 'Nodes'.
 * @return the container
 */
public Nodes getNodeContainer () {
	return nodes;
}
/**
 * Gibt die Anzahl der anliegenden Kanten zurück.
 * @return int
 */
public int getNumOfEdges () {
	return numOfEdges;
}
/**
 * Gibt die Kante zurück, über die der angefragte Weg verläuft.
 * @return  Kante, über die der Weg verläuft
 * @param  way  Index des Weges
 */
public Edge getWayEdge (int way) {
	if (wayEdge[way-1] < 0)
		return null;
	else
		return edge[wayEdge[way-1]];
}
/**
 * Gibt Hashcode für den Knoten zurück.
 * @return Hashcode
 */
public int hashCode () {
	return (int) id;
}

/**
 * Returns the x-coordinate.
 * @return  x
 */
public int getX () {
	return x;
}

/**
 * Returns the y-coordinate.
 * @return  y
 */
public int getY () {
	return y;
}

/**
 * Färbt den Knoten in der Hervorhebungsfarbe ein.
 * @param presName Darstellungsname
 */
public void highlight (String presName) {
	setPresentation(DrawablePresentation.get(presName));
}

/**
 * Testet, ob das Symbol durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param px x-Koordinate in Basis-Koordinaten
 * @param py y-Koordinate in Basis-Koordinaten
 * @param scale akt. Maßstab
 */
public boolean interacts (int px, int py, int scale) {
	int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
	int pvalue = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
	DrawablePresentation ap = pres.get(scale,actMode,pvalue);
	int size = Math.abs(ap.getSize()*pvalue) / 10;
	if (size < 4)
		size = 4;
	return (px >= x-size/2*scale) && (px <= x+size/2*scale) &&
		   (py >= y-size/2*scale-1) && (py <= y+size/2*scale);
}

/**
 * Gibt zurück, ob der Knoten mit dem angegebenen Wert markiert ist.
 * @return markiert?
 * @param value Markierungswert
 */
public boolean isMarked (int value) {
	if (mark > nodes.nullMark)
		return ((mark-nodes.nullMark) & value) > 0;
	else
		return false;
}
/**
 * Markiert den Knoten mit dem angegebenen Wert.
 * @param value Markierunsgwert
 */
public void mark (int value) {
	if (mark > nodes.nullMark)
		mark = nodes.nullMark + ((mark-nodes.nullMark) | value);
	else
		mark = nodes.nullMark + value;
	if (nodes.maxMark < mark)
		nodes.maxMark = mark;
}
/**
 * Moves the node to a new position.
 * @param x new x-coordinate
 * @param y new y-coordinate
 */
public void moveTo (int x, int y) {
	this.x = x;
	this.y = y;
	for (int i=0; i<numOfEdges; i++)
		edge[i].announceMove(this);
}

/**
 * Not implemented.
 */
public EntryReadable read (EntryInput r) {
	throw new UnsupportedOperationException("Node.read is not implemented!");
}

/**
 * Entfernt Kante vom Knoten.
 * Die Knotenklassen werden z.Zt. nicht angepasst.
 * @param oldEdge zu entfernende Kante
 */
public void removeEdge (Edge oldEdge) {
	// Kante suchen
	int edgeIndex = 0;
	while ((edgeIndex < numOfEdges) && (edge[edgeIndex] != oldEdge))
		edgeIndex++;
	if (edgeIndex == numOfEdges)
		return;
	// und entfernen
	edge[edgeIndex] = edge[numOfEdges-1];
	numOfEdges--;
	// Die Knotenklassen werden z.Zt. nicht angepasst !!!
}
/**
 * Replaces the actual node by the parameter node.
 * @param node replacing node
 */
public void replaceBy (Node node) {
	for (int i=numOfEdges-1; i>=0; i--)
		edge[i].replaceNode (this,node);
	if (numOfEdges != 0)
		System.err.println("Node.replaceBy: numOfEdges != 0");
}
/**
 * Setzt die Distanz bezüglich des angegebenen Weges.
 * @param way Index des Weges
 * @param distance Distanz
 */
public void setDistanceOfWay (int way, double distance) {
	distanceOfWay[way-1] = distance;
}
/**
 * Sets the ID of the node.
 * @param newID the new ID
 */
protected void setID (long newID) {
	id = newID;
}
/**
 * Sets the name of the node.
 * @param name new name
 */
public void setName (String name) {
	this.name = name;
	/*if (next != null)
		((DrawableText)next).setText(name);
	else
		next = new DrawableText (x,y, name, "NodeText"+nodeClass, DrawableText.LEFT,DrawableText.CENTER, nodes.minTextScale[nodeClass], 0);*/
}
/**
 * Färbt den Knoten in der Standardfarbe ein.
 */
public void setStandardAppearance () {
	setPresentation(DrawablePresentation.get("Node"+nodeClass));
}

/**
 * Merkt sich die angegebene Kante als weiteren Verlauf des angegebenen Weges.
 * @param way Index des Wegs
 * @param e Kante, über der der Weg verläuft
 */
public void setWay (int way, Edge e) {
	// ggf. alte Wegkanten zurücksetzen
	if (!isMarked(1))
		wayEdge[0] = -1;
	if (!isMarked(2))
		wayEdge[1] = -1;
	// Kante merken
	for (int i=0; i < numOfEdges; i++)
		if (edge[i] == e)
			wayEdge[way-1] = (byte)i;
}
/**
 * Schreibt den Knoten in den DataOutput.
 * @return  erfolgreich?
 * @param  out  DataOutput
 */
public boolean write (DataOutput out) {
	try {
		byte l = (byte)getName().length();
		out.writeByte(l);
		if (l > 0)
			out.write(getName().getBytes());
		out.writeLong(id);
		out.writeInt(getX());
		out.writeInt(getY());
		return true;
	}
	catch (IOException e) {
		return false;
	}		
}
/**
 * Schreibt den Knoten in den EntryWriter.
 * @param out EntryWriter
 */
public void write (EntryWriter out) {
	out.print(id); out.print('\t'); out.print(getX()); out.print('\t');
	out.print(getY()); out.println('\t'+getName());
}

/**
 * Not implemented.
 */
protected void writeProtected (EntryWriter out, int type) {
	throw new UnsupportedOperationException("Node.writeProtected is not implemented!");
}

}
