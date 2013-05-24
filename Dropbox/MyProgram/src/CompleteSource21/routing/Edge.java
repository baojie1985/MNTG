package routing;

import java.awt.*;
import java.io.*;
import drawables.*;
import util.*;

/**
 * Class representing edges.
 * 
 * @version	4.00	17.08.2003	superclass DrawableLine replaced by Drawable, length become double, weight removed, additional constructor
 * @version	3.30	03.07.2001	getContainer renamed to getEdgeContainer
 * @version	3.20	24.05.2901	marking added
 * @version	3.11	01.06.2000	adapted the class Drawables, version 2.5
 * @version	3.10	09.03.2000	usage, length, WeightManager, replaceNode, announceMove, setName, setId, Sortable added
 * @version	3.00	13.12.1999	seperation of the class Edges
 * @version	2.00	16.03.1999	adapted the class Drawables, version 2.0
 * @version	1.00	02.12.1998	first version
 * @author Thomas Brinkhoff
 */
public class Edge extends Drawable implements Comparable {
	
	/**
	 * The identifier.
	 */
	private long id = 0;
	/**
	 * The length of the edge.
	 */
	private double length = 0;	
	/**
	 * Starting node.
	 */
	protected Node node1 = null;
	/**
	 * End node.
	 */
	protected Node node2 = null;
	/**
	 * Optional name of the edge.
	 */
	private String name = null;
	/**
	 * The class of the edge.
	 */
	private short edgeClass = 0;	
	/**
	 * Usage of the edge.
	 */
	private short usage = 0;
	/**
	 * Current mark.
	 */
	private int mark = 0;
	/**
	 * Link to the container.
	 */
	private Edges edges = null;	

/**
 * Constructor.
 * @param  id  ID
 */
protected Edge (long id) {
	this.id = id;
	pres = DrawablePresentation.get("default");
}

/**
 * Constructor.
 * @param  id  ID
 * @param  edgeClass  class of the edge
 * @param  node1  first node
 * @param  node2  second node
 * @param  name  name of the node (may be null)
 */
public Edge (long id, int edgeClass, Node node1, Node node2, String name) {
	this.id = id;
	pres = DrawablePresentation.get("default");
	this.edgeClass = (short)edgeClass;
	this.node1 = node1;
	this.node2 = node2;
	this.name = name;
	this.length = node1.distanceTo(node2);
}

/**
 * Constructor.
 * @param  id  ID
 * @param  edgeClass  class of the edge
 * @param  node1  first node
 * @param  node2  second node
 * @param  name  name of the node (may be null)
 * @param  edges  the container of the edges
 */
public Edge (long id, int edgeClass, Node node1, Node node2, String name, Edges edges) {
	this.id = id;
	pres = DrawablePresentation.get("Edge"+Num.putIntoInterval(edgeClass,0,edges.getNumOfClasses()));
	this.edgeClass = (short)Num.putIntoInterval (edgeClass,0,edges.getNumOfClasses());
	this.node1 = node1;
	this.node2 = node2;
	this.name = name;
	this.edges = edges;
	this.minScale = edges.minScale[Num.putIntoInterval(edgeClass,0,edges.getNumOfClasses())];
	this.maxScale = 0;
	this.length = node1.distanceTo(node2);
}

/**
 * Announces to the edge that the node has moved to a new position.
 * @param node one node of the edge
 */
public void announceMove (Node node) {
	this.length = node1.distanceTo(node2);
}

/**
 * Clears the mark.
 */
public void clearMark () {
	mark = edges.nullMark;
}

/**
 * Comparing function concerning the edge names.
 * @return  the result of compareTo on the names
 * @param  obj  the other edge to be compared
 */
public int compareTo (Object edge) {
	if (edge == null)
		return 1;
	return getName().compareTo(((Edge)edge).getName()); 
}

/**
 * Ausgabe der Kante zu Debugging-Zwecken.
 */
public void debugPrint () {
	System.out.print(node1.getId()+" - "+node2.getId());
}
/**
 * Decrements the usage of the edge.
 */
public void decUsage() {
	if (usage < 1) System.out.println("Edge "+id+": "+usage+"--");
	usage--;
}

/**
 * Draws the edge if it is visible according to the given scale.
 * @param  g  graphic context
 * @param  scale  current scale
 * @param  mode  draw mode
 * @param  value  value
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	// Darstellung vorbereiten
	DrawablePresentation ap = pres.get(scale,mode,value);
	Color color = ap.getColor();
	if (selected)
		color = ap.getSelectionColor();
	// Koordinaten berechnen
	int cx1 = node1.getX()/scale;
	int cy1 = node1.getY()/scale;
	int cx2 = node2.getX()/scale;
	int cy2 = node2.getY()/scale;
	// Zeichnen
	DrawableLine.drawProtected (g,ap,color,color,cx1,cy1,cx2,cy2);
}

/**
 * Vergleich von zwei Kanten auf Gleichheit über die ID.
 * @return Kanten gleich?
 * @param edge zu vergleichende Kante
 */
public boolean equals (Object edge) {
	if (edge == null)
		return false;
	return (id == ((Edge)edge).id); 
}
/**
 * Gibt Kantenklasse zurück.
 * @return Kantenklasse
 */
public int getEdgeClass () {
	return edgeClass;
}
/**
 * Returns the container 'Edges'.
 * @return the container
 */
public Edges getEdgeContainer () {
	return edges;
}
/**
 * Gibt die ID der Kante zurück.
 * @return ID
 */
public long getID () {
	return id;
}
/**
 * Gibt die Länge der Kante zurück.
 * @return Länge
 */
public double getLength() {
	return length;
}

/**
 * Returns the minimum bounding rectangle of the primitive.
 * @return  the MBR
 */
public Rectangle getMBR () {
	int minX = Math.min(node1.getX(),node2.getX());
	int minY = Math.min(node1.getY(),node2.getY());
	int maxX = Math.max(node1.getX(),node2.getX());
	if (maxX == minX)
		maxX++;
	int maxY = Math.max(node1.getY(),node2.getY());
	if (maxY == minY)
		maxY++;
	return new Rectangle (minX,minY,maxX-minX,maxY-minY);
}

/**
 * Returns the name of the edge.
 * @return the name
 */
public String getName () {
	if (name != null)
		return name;
	else
		return "";
}
/**
 * Gibt den Startknoten der Kante zurück.
 * @return Startknoten
 */
public Node getNode1 () {
	return node1;
}
/**
 * Gibt den Endknoten der Kante zurück.
 * @return Endknoten
 */
public Node getNode2 () {
	return node2;
}
/**
 * Gibt einen Knoten der Kante zurück.
 * @return der Knoten
 */
public Node getOneNode () {
	return node1;
}
/**
 * Gibt den Knoten der Kante zurück, der nicht dem übergebenden Knoten entspricht.
 * @return gegenüberliegender Knoten
 * @param oneNode Vergleichsknoten
 */
public Node getOppositeNode (Node oneNode) {
	if (node1.equals(oneNode))
		return node2;
	else
		return node1;
}
/**
 * Returns the usage of the edge.
 * @return usage
 */
public int getUsage() {
	return usage;
}
/**
 * Gibt das (gemittelte) Gewicht der Kante (in Hin- und Rückrichtung) mittels 
 * des WeightManagers zurück.
 * @return Gewicht der Kante
 */
public double getWeight() {
	return edges.getWeightManager().getWeight (this);
}
/**
 * Gibt das Kantengewicht aus Richtung des übergebenen Knotens mittels des WeightManagers zurück.
 * @return Kantengewicht
 * @param from Ausgangsknoten
 */
public double getWeight (Node from) {
	return edges.getWeightManager().getWeight(this,node1.equals(from));
}
/**
 * Gibt das gerichte Kantengewicht mittels des WeightManagers zurück.
 * @return Kantengewicht
 * @param forwards Kante vorwärts gerichtet?
 */
public double getWeight (boolean forwards) {
	return edges.getWeightManager().getWeight(this,forwards);
}
/**
 * Testet, ob die Kante den übergebenen Knoten als Start- oder Endknoten besitzt.
 * @return besitzt den Knoten?
 * @param node Vergleichsknoten
 */
public boolean hasAsNode (Node node) {
	return node1.equals(node) || node2.equals(node);
}
/**
 * Gibt Hashcode für die Kante zurück.
 * @return Hashcode
 */
public int hashCode () {
	return (int) id;
}
/**
 * Increments the usage of the edge.
 */
public void incUsage() {
	/*if (usage > 2) System.out.println("Edge "+id+": "+usage+"++");*/
	usage++;
}

/*
 * Testet, ob die Linie durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param x x-Koordinate des zu testenden Punktes
 * @param y y-Koordinate des zu testenden Punktes
 * @param scale aktuelle Maßstab
 */
public boolean interacts (int x, int y, int scale) {
	int minX = Math.min(node1.getX(),node2.getX());
	int minY = Math.min(node1.getY(),node2.getY());
	int maxX = Math.max(node1.getX(),node2.getX());
	int maxY = Math.max(node1.getY(),node2.getY());
	return DrawableLine.interacts (x,y,node1.getX(),node1.getY(),node2.getX(),node2.getY(),minX,minY,maxX,maxY,scale);
}

/**
 * Testet, ob die Kante den übergebenen Vergleichsknoten als Zielknoten besitzt.
 * @return ist Zielknoten?
 * @param pNode2 Vergleichsknoten
 */
public boolean isDirectedTo (Node pNode2) {
	return node2.equals(pNode2);
}
/**
 * Is the edge marked?
 * @return marked?
 */
public boolean isMarked () {
	return (mark > edges.nullMark);
}
/**
 * Testet, ob die Kante den übergebenen Vergleichsknoten als Anfangsknoten besitzt.
 * @return ist Anfangsknoten?
 * @param pNode1 Vergleichsknoten
 */
public boolean isStartingFrom (Node pNode1) {
	return node1.equals(pNode1);
}

/**
 * Marks the edges.
 */
public void mark () {
	mark = edges.nullMark+1;
}

/**
 * Not implemented.
 */
public EntryReadable read (EntryInput r) {
	throw new UnsupportedOperationException("Node.read is not implemented!");
}

/**
 * Replaces one node of the edge by another node.
 * @param oldNode old node
 * @param newNode new node
 */
public void replaceNode (Node oldNode, Node newNode) {
	if (node1 == oldNode) {
		node1.removeEdge (this);
		node1 = newNode;
		node1.addEdge (this);
	}
	else if (node2 == oldNode) {
		node2.removeEdge (this);
		node2 = newNode;
		node2.addEdge (this);
	}
	length = node1.distanceTo(node2);
}
/**
 * Setzt die Klasse der Kante neu.
 * @param edgeClass Kantenklasse
 */
public void setEdgeClass (short edgeClass) {
	if (edgeClass < this.edgeClass) {
		getNode1().adaptClass(edgeClass);
		getNode2().adaptClass(edgeClass);
	}
	this.edgeClass = edgeClass;
	setPresentation(DrawablePresentation.get("Edge"+edgeClass));
}
/**
 * Set a new ID.
 * @param newID the new ID
 */
protected void setID (long newID) {
	id = newID;
}
/**
 * Sets the name of the edge.
 * @param name new name
 */
public void setName (String name) {
	this.name = name;
}
/**
 * Färbt die Kante gemäß der Standardfarbe ihrer Klasse ein.
 */
public void setStandardAppearance () {
	setPresentation(DrawablePresentation.get("Edge"+edgeClass));
}
/**
 * Setzt die Benutzung der Kante neu.
 * @param usage Benutzung
 */
public void setUsage (short usage) {
	this.usage = usage;
}
/**
 * Schreibt die Kante in den DataOutput.
 * @return erfolgreich?
 * @param out DataOutput
 */
public boolean write (DataOutput out) {
	try {
		out.writeLong(node1.getID());
		out.writeLong(node2.getID());
		byte l = (byte)getName().length();
		out.writeByte(l);
		if (l > 0)
			out.write(getName().getBytes());
		out.writeLong(id);
		out.writeInt(edgeClass);
		return true;
	}
	catch (IOException e) {
		return false;
	}		
}
/**
 * Schreibt die Kante in den EntryWriter.
 * @param out EntryWriter
 */
public void write (EntryWriter out) {
	out.print(id); out.print('\t'); out.print(node1.getID()); out.print('\t');
	out.print(node2.getID()); out.print('\t'); out.print(edgeClass);
	out.println('\t'+name);
}

/**
 * Not implemented.
 */
protected void writeProtected (EntryWriter out, int type) {
	throw new UnsupportedOperationException("Edge.writeProtected is not implemented!");
}

}
