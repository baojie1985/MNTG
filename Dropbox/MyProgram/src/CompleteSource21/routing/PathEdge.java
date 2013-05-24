package routing;

import drawables.*;

/**
 * Class for representing pathes of edges.
 * 
 * @version 1.60	19.08.2003	checkForDestinationNode added, distance and weight becomes double
 * @version	1.51	24.05.2001	simple constructor added
 * @version	1.50	30.04.2000	constructors changed, computeDegree added
 * @version	1.40	09.03.2000	origWeight, setNext, computeNumber added, selection modificated
 * @version	1.30	07.12.1999	highlight and highlightDirected separated
 * @version	1.20	20.06.1999	highlight modificated
 * @version	1.10	07.03.1999	getEdge, getNext, getForwards added
 * @version	1.00	30.11.1998	first version
 * @author Thomas Brinkhoff
 */
 
public class PathEdge {

	/**
	 * The corresponding edge
	 */
	protected Edge edge = null;
	/**
	 * Is the direction forwards?
	 */
	protected boolean forwards = true; 
	/**
	 * Original weight of the edge
	 */
	protected double origWeight = 0; 
	/**
	 * The next edge
	 */
	protected PathEdge next = null;

/**
 * Constructor of a new path element.
 * @param edge the corresponding edge
 * @param forwards is the direction forwards?
 */
public PathEdge (Edge edge, boolean forwards) {
	this.edge = edge;
	this.forwards = forwards;
}
/**
 * Constructor of a new path element.
 * @param edge the corresponding edge
 * @param forwards is the direction forwards?
 * @param origWeight the (original) weigth of the edge
 */
public PathEdge (Edge edge, boolean forwards, double origWeight) {
	this.edge = edge;
	this.forwards = forwards;
	this.origWeight = origWeight;//edge.getWeight(forwards);
}
/**
 * Constructor of a new path element.
 * @param edge the corresponding edge
 * @param forwards is the direction forwards?
 * @param origWeight the (original) weigth of the edge
 * @param next the next path element
 */
public PathEdge (Edge edge, boolean forwards, double origWeight, PathEdge next) {
	this.edge = edge;
	this.forwards = forwards;
	this.origWeight = origWeight;//edge.getWeight(forwards);
	this.next = next;
}
/**
 * Appends a path.
 * @return the additional path
 * @param path the additional path
 */
public PathEdge addPath (PathEdge path) {
	if (next == null) {
		next = path;
		return path;
	}
	else
		return next.addPath (path);
}
/**
 * Ändert die Darstellung der Kanten.
 * @param presName Darstellungsname
 */
public void changePresentation (String presName) {
	edge.setPresentation(DrawablePresentation.get(presName));
	if (next != null)
		next.changePresentation(presName);
}
/**
 * Checks the path for a given destination node.
 * @param  node  the destination node
 */
public boolean checkForDestinationNode (Node node) {
	PathEdge currEdge = this;
	while (currEdge.next != null) {
		currEdge = currEdge.next;
	}
	return currEdge.getDestinationNode() == node;
}

/**
 * Computes the degrees of starting nodes.
 * @return total degree
 */
public int computeDegree () {
	if (next == null)
		return 0;
	else
		return next.computeDegree()+getStartingNode().getNumOfEdges();
}
/**
 * Berechnet die Länge des Pfads.
 * @return Pfadlänge
 */
public double computeDistance () {
	if (next == null)
		return edge.getWeight();
	else
		return edge.getWeight() + next.computeDistance();
}
/**
 * Computes the number of edges.
 * @return number of edges
 */
public int computeNumber () {
	if (next == null)
		return 1;
	else
		return next.computeNumber()+1;
}
/**
 * Gibt den Pfad zu Debugging-Zwecken aus.
 */
public void debugPrint () {
	Node node = getStartingNode();
	System.out.print("1: "); node.debugPrint(1); System.out.println(" - "+node.getDistanceOfWay(2));
	node = edge.getOppositeNode(node);
	System.out.print("2: "); node.debugPrint(1); System.out.println(" - "+node.getDistanceOfWay(2));
	if (next != null)
		next.debugPrint();
	else
		System.out.println("***");
}
/**
 * Sucht übergebene Kante im Pfad. Falls sie gefunden wird, wird das Pfadelement
 * zurückgegeben, ansonsten null.
 * @return gefundenes Pfadelement
 * @param pEdge gesuchte Kante
 */
public PathEdge findEdge (Edge pEdge) {
	if (pEdge == null)
		return null;
	else if (pEdge.equals(edge))
		return this;
	else if (next == null)
		return null;
	else
		return next.findEdge (pEdge);
}
/**
 * Gibt den Knoten zurück, bei dem die Pfadkante endet.
 * @return Endknoten
 */
public Node getDestinationNode () {
	if (forwards)
		return edge.getNode2();
	else
		return edge.getNode1();
}
/**
 * Gibt die Kante des Pfadelements zurück.
 * @return Kante
 */
public Edge getEdge () {
	return edge;
}
/**
 * Gibt die Kantenrichtung zurück.
 * @return Kante vorwärts gerichtet?
 */
public boolean getForwards () {
	return forwards;
}
/**
 * Gint die letzte Kante des Pfads zurück.
 * @return Kante
 */
public Edge getLastEdge () {
	if (next == null)
		return edge;
	else
		return next.getLastEdge();
}
/**
 * Gibt das folgende Pfadelement zurück.
 * @return folgendes Pfadelement
 */
public PathEdge getNext () {
	return next;
}
/**
 * Gibt das Originalgewicht der Kante zurück.
 * @return Originalgewicht der Kante
 */
public double getOrigWeight () {
	return origWeight;
}
/**
 * Gibt den Knoten zurück, bei dem die Pfadkante startet.
 * @return Startknoten
 */
public Node getStartingNode () {
	if (forwards)
		return edge.getNode1();
	else
		return edge.getNode2();
}
/**
 * Selektiert die Kanten ungerichtet.
 */
public void select () {
	edge.setSelection(true);
	if (next != null)
		next.select();
}
/**
 * Selektiert die Kanten gerichtet.
 */
/*public void selectDirected () {
	if (forwards) {
		edge.select(DrawableLine.FORWARDS);
	}
	else {
		edge.select(DrawableLine.BACKWARDS);
	}
	if (next != null)
		next.selectDirected();
}*/
/**
 * Setzt das Feld "next" auf den übergebenen Pfad.
 * @param path neuer Folge-Pfad
 */
public void setNext (PathEdge path) {
	next = path;
}
/**
 * Setzt die Kanten des Pfads auf die Standard-Darstellung.
 */
public void setStandardAppearance () {
	edge.setStandardAppearance();
	if (next != null)
		next.setStandardAppearance();
}
}
