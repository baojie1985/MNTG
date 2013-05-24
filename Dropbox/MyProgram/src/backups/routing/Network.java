package routing;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import drawables.*;
import util.*;

/**
 * Class representing a network.
 * 
 * @version	1.30	19.08.2003	considered that computeFastWay2 sometimes has a wrong starting node, distance becomes double, adapted to BorderHeap
 * @version	1.22	28.06.2000	adapted to Drawables, Timer removed
 * @version	1.21	30.04.2000	optimized
 * @version	1.20	02.03.2000	Auslagerung MIF und Geocode, Entfernung objects, Redesign Heap
 * @version	1.10	13.12.1999	adapted to DrawableObjects, Nodes and Edges, static removed
 * @version	1.00	02.12.1998	first version
 * @author Thomas Brinkhoff
 */
public class Network {

	/**
	 * 	Container für Knoten
	 */
	protected Nodes nodes = null;
	/**
	 * 	Container für Kanten
	 */
	protected Edges edges = null;
	/**
	 * 	Heap für den Rand
	 */
	private BorderHeap border = new BorderHeap();
	
	/**
	 * 	Distanz-Wert für unendlich
	 */
	private static final double maxDistance = Double.MAX_VALUE;
	/**
	 * 	Knoten-Weg-Index für Weg1
	 */
	private static final int NWAY1 = 1;
	/**
	 * 	Knoten-Weg-Index für Weg2
	 */
	private static final int NWAY2 = 2;
	/**
	 * 	allgemeine Knoten-Markierung für Wege
	 */
	private static final int WAY = 1;
	/**
	 * 	Knoten-Markierung für Weg1
	 */
	private int WAY1 = NWAY1;
	/**
	 * 	Knoten-Markierung für Weg2
	 */
	private int WAY2 = NWAY2;
	/**
	 * 	Knoten-Markierung fertige Wege
	 */
	private int FINAL = 4;
	/**
	 * 	Knoten-Markierung beste Wege
	 */
	private final int BEST = 8;

/**
 * Constructor.
 */
public Network () {
	edges = new Edges();
	edges.setWeightManager(new StandardWeightManager(edges));
	nodes = new Nodes(edges);
}

/**
 * Berechnet nach Dijkstra den schnellsten Weg von einem Start- zu einem Endknoten.
 * @return berechneter Weg
 * @param start Startknoten
 * @param stop Stopknoten
 */
public PathEdge computeFastestWay (Node start, Node stop) {
	if ((start == null) || (stop == null) || (start.equals(stop)))
		return null;
	// Initialisierung
	nodes.clearAllMarks();
	start.clearWays();
	start.mark(FINAL);
	start.mark(BEST);
	start.setDistanceOfWay(WAY,0);
	border.reset();
	// Durchlauf
	enlargeBorder (border, start,stop, WAY, false);
	boolean elementFound = border.fetchFirst();
	Node actNode = null;
	while (elementFound && (!stop.equals(actNode = border.returnNode()))) {
		actNode.mark (WAY);
		enlargeBorder (border, actNode,stop, WAY, false);
		elementFound = border.fetchFirst();
	}
	// Ergebnis erzeugen
	return computeResultPath (actNode,false);
}

/**
 * Berechnet einen schnellen Weg (oft den schnellsten) zwischen 2 Knoten.
 * Die Berechnung wird nur vom Startknoten aus durchgeführt.
 * @return berechneter Weg
 * @param start Startknoten
 * @param stop Stopknoten
 */
public PathEdge computeFastWay (Node start, Node stop) {
	if ((start == null) || (stop == null) || (start.equals(stop)))
		return null;
	// Initialisierung
	nodes.clearAllMarks();
	start.clearWays();
	start.mark(FINAL);
	start.mark(BEST);
	start.setDistanceOfWay(WAY,0);
	border.reset();
	// Durchlauf
	enlargeBorder (border, start,stop, WAY, true);
	boolean elementFound = border.fetchFirst();
	Node actNode = null;
	while (elementFound && (!stop.equals(actNode = border.returnNode()))) {
		actNode.mark(FINAL);
		enlargeBorder (border, actNode,stop, WAY, true);
		elementFound = border.fetchFirst();
	}
	// Ergebnis erzeugen
	return computeResultPath (actNode,false);
}

/**
 * Berechnet einen schnellen Weg (oft den schnellsten) zwischen 2 Knoten.
 * Im Gegensatz zu computeFastWay wird die Berechnung von beiden Knoten
 * gleichberechtigt durchgeführt.
 * @return berechneter Weg
 * @param start Startknoten
 * @param stop Stopknoten
 */
public PathEdge computeFastWay2 (Node start, Node stop) {
	if ((start == null) || (stop == null) || (start.equals(stop)))
		return null;
	// Initialisierung
	nodes.clearAllMarks();
	start.clearWays();
	start.mark(WAY1);
	start.setDistanceOfWay(WAY1,0);
	stop.clearWays();
	stop.mark(WAY2);
	stop.setDistanceOfWay(WAY2,0);
	border.reset();
	// Durchlauf
	enlargeBorder (border, start,stop, WAY1, true);
	enlargeBorder (border, stop,start, WAY2, true);
	boolean elementFound = border.fetchFirst();
	Node actNode = null;
	while (elementFound) {
		//System.out.print("markiere: "); element.debugPrint(); System.out.println();
		actNode = border.returnNode();
		// Fall 1: Knoten gehört zu WAY1
		if (actNode.isMarked(WAY1)) {
			actNode.mark(FINAL);
			// falls er auch zu WAY2 gehört, sind beide Wege nun aufeinander getroffen => fertig
			if (actNode.isMarked(WAY2))
				break;
			enlargeBorder (border, actNode,stop, WAY1, true);
		}	
		// Fall 2: Knoten gehört zu WAY2
		else if (actNode.isMarked(WAY2)) {
			actNode.mark(FINAL);
			enlargeBorder (border, actNode,start,WAY2, true);
		}	
		// Fall 3: gibt es nicht!
		else
			System.err.println("Fehler: Knoten falsch markiert!");
		elementFound = border.fetchFirst();
	}
	// Ergebnis erzeugen
	PathEdge res = computeResultPath (actNode,true);
	if (res == null)
		return null;
	if (res.getStartingNode() != start) {
		res = computeFastWay (start,stop);
		if (res == null)
			return null;
		if (res.getStartingNode() != start) {
			//System.err.println("### start wrong ###");
			return null;
		}
	}
	return res;
}

/**
 * Berechnet die Wege zwischen 2 Knoten, die nicht um den angegebenen Prozentsatz
 * schlechter sind als der kürzeste Weg. <BR>
 * Einschränkungen: <BR>
 * - keine Zyklen <BR>
 * - keine Rückwege (kann evtl. auch ungünstige Querverbindungen treffen <BR>
 * @return berechneter Weg
 * @param start Startknoten
 * @param stop Stopknoten
 * @param threshold Prozentsatz, um den ein Weg schlechter sein darf
 */
public PathEdge computeFastWays (Node start, Node stop, int threshold) {
	// Prüfen, ob Weg berechenbar
	if ((start == null) || (stop == null) || (start.equals(stop)))
		return null;
	// Initialisierung
	border.reset();					// Rand
	PathEdge resPath = null;					// Ergebnis
	double shortestDistance = maxDistance;		// Distanz des kürzesten Wegs
	// Knoten initilisieren und markieren
	nodes.clearAllMarks();
	start.clearWays();
	start.mark(WAY1);
	start.setDistanceOfWay(NWAY1,0);
	stop.clearWays();
	stop.mark(WAY2);
	stop.setDistanceOfWay(NWAY2,0);
	// zweifacher Durchlauf
	for (int numOfLoops=1; true; numOfLoops++) {
		System.out.println ("** Durchlauf "+numOfLoops+" **");
		// Start- und Zielknoten in Rand aufnehmen
		enlargeBorder (border, start,stop, WAY1, threshold,shortestDistance,resPath);
		enlargeBorder (border, stop,start, WAY2, threshold,shortestDistance,resPath);
		// Abarbeiten und Ergänzen des Randes
		boolean elementFound = border.fetchFirst();
		while (elementFound) {
			// Prüfen, ob Weg zu lang => ggf. Durchlauf abbrechen
			if (border.returnDistance()*100/(100+threshold) > shortestDistance)
				break;
			Node actNode = border.returnNode();
			actNode.mark(FINAL);
			// Fall 1: Knoten gehört zu NWAY1
			if (actNode.isMarked(WAY1)) {
				// Fall 1a: BEST-Knoten => diesen im ersten Durchlauf ignorieren
				if ( (numOfLoops == 1) && (actNode.isMarked(BEST)) ) {
					//System.out.print("BEST: "); actNode.debugPrint(NWAY1); System.out.println();
				}
				// Fall 1b: Knoten gehört auch zu NWAY2
				else if (actNode.isMarked(WAY2)) {
					// im ersten Durchgang ggf. Wege berechnen
					if (numOfLoops == 1) {
						// Es handelt sich um den ersten Weg
						if (resPath == null) {
							resPath = computeResultPath (actNode,true);
							if (resPath == null)
								return null;	// Sollte nicht vorkommen
							shortestDistance = actNode.getDistanceOfWay(NWAY1)+actNode.getDistanceOfWay(NWAY2);
						}
						// oder einen weiteren, hinreichend kurzen Weg
						else if ((actNode.getDistanceOfWay(NWAY1)+actNode.getDistanceOfWay(NWAY2))*100/(100+threshold) <= shortestDistance) {
							resPath.addPath (computeResultPath(actNode,true));
							// möglicherweise ist dieser Pfad sogar kürzer!
							if (actNode.getDistanceOfWay(NWAY1)+actNode.getDistanceOfWay(NWAY2) < shortestDistance)
								shortestDistance = actNode.getDistanceOfWay(NWAY1)+actNode.getDistanceOfWay(NWAY2);	
						}
					}	
				}
				// Fall 1c: Knoten gehört nur zu NWAY1 => Rand vergrössern
				else {
					enlargeBorder (border, actNode,stop, WAY1, threshold,shortestDistance,resPath);
				}
			}
			// Fall 2: Knoten gehört nur zu NWAY2 => Rand vergrössern
			else if (actNode.isMarked(WAY2)) {
				enlargeBorder (border, actNode,start,WAY2, threshold,shortestDistance,resPath);
			}
			// Fall 3: Knoten gehört weder zu NWAY1 noch zu NWAY2 => Fehler
			else {
				actNode.debugPrint(WAY1); actNode.debugPrint(WAY2); System.err.println(" ist falsch markiert!");
			}	
			elementFound = border.fetchFirst();
			// wenn Knoten mehrfach kommt, ihn überspringen
			while ( (elementFound) && (actNode.equals(border.returnNode())) ) {
				elementFound = border.fetchFirst();
			}
		} // while
	
		// Prüfen, ob weiterer Durchlauf notwendig
		if (numOfLoops == 2)
			break;
		// Knotenmarkierungen neu setzen
		WAY1 = BEST*2;
		WAY2 = WAY1*2;
		FINAL = WAY2*2;
		start.mark(WAY1);
		stop.mark(WAY2);
		// Neuer Rand
		border.reset();
	} // for
	
	// Markierungswerte zurücksetzen
	WAY1 = NWAY1;
	WAY2 = NWAY2;
	FINAL = WAY2*2;
	// Ergebnis zurückgeben
	return resPath;
}
/**
 * Berechnung der Verbindung zwischen zwei BEST-Knoten, die übergebenene Kante umfaßt.
 * Die Entfernung zum Zielknoten wird in den Rand-Elementen abgelegt.
 * @param border Rand gemäß Dijkstra als Heap
 * @param start Anfangsknoten von actEdge
 * @param actEdge aktuelle Kante
 * @param stop Zielknoten des Routings
 * @param wayMark Index des betrachteten Wegs (WAY1 oder WAY2)
 * @param threshold Prozentsatz, um den Wege länger sein dürfen als der kürzester Weg
 * @param shortestDistance Länge des kürzesten Weges
 * @param resPath Ergebnispfad mit allen bislang berechneter Wege
 */
protected void computeNewConnection (BorderHeap border, Node start, Edge actEdge, Node stop, int wayMark, int threshold, double shortestDistance, PathEdge resPath) {
	// Vorbereitungen
	int way = wayMark / WAY1;
	int theOtherWay = NWAY1;
	if (way == NWAY1)
		theOtherWay = NWAY2;
	Node oppositeNode = actEdge.getOppositeNode (start);
	boolean bothBest = oppositeNode.isMarked(BEST) && start.isMarked(BEST);
	// Endpunkt der Verbindung feststellen und die Weglänge dahin berechnen
	long addDistance = 0;
	while (!oppositeNode.isMarked(BEST)) {
		Edge nextEdge = oppositeNode.getWayEdge(way);
		if (nextEdge == null)
			break;
		addDistance += nextEdge.getWeight();
		oppositeNode = oppositeNode.getWayEdge(way).getOppositeNode (oppositeNode);
	}
	// Ausgangspunkt feststellen
	Node searchNode = start;
	while (!searchNode.isMarked(BEST)) {
		Edge nextEdge = searchNode.getWayEdge(way);
		if (nextEdge == null)
			break;
		searchNode = searchNode.getWayEdge(way).getOppositeNode (searchNode);
	}
	// Falls beide zuvor BEST-Knoten, prüfen ob Verbindung in Resultat vorliegt
	if (bothBest) {
		bothBest = (resPath.findEdge (actEdge) != null);
		if (!bothBest) {
			System.out.print("bothBest: "); searchNode.debugPrint(way); System.out.print(" -> "); oppositeNode.debugPrint(way); System.out.print(" bislang nicht in Lösung."); System.out.println();
		}
	}	

	// Falls man über die Verbindung vorangekommen ist und den Schwellenwert nicht überschreitet: Pfad als Ergebnis aufnehmen
	if ( (searchNode.getDistanceOfWay(way) < oppositeNode.getDistanceOfWay(way)) &&
		(!bothBest)) {
		double actWeight = actEdge.getWeight();
		if ((start.getDistanceOfWay(way)+actWeight+addDistance+oppositeNode.getDistanceOfWay(theOtherWay))*100/(threshold+100) <= shortestDistance) {
			if (addDistance > 0)
				resPath.addPath (computeResultPathBackwards (actEdge.getOppositeNode(start),way));
			resPath.addPath (computeResultPath (start,way,actEdge.getOppositeNode(start).getDistanceOfWay(theOtherWay)+actEdge.getWeight()));
			resPath.addPath (new PathEdge (actEdge,actEdge.isStartingFrom(start),actWeight));
		}
	}	
}
/**
 * Berechnung des Ergebnispfad von einem Knoten ausgehend entlang eines Weges.
 * Dabei wird der Pfad in der Normalrichtung abgelaufen.
 * Die Distanzen des anderen Weges werden an den Pfadknoten gesetzt
 * und diese mit BEST markiert.
 * @return berechneter Pfad
 * @param actNode Aufgangsknoten
 * @param way Index des gewünschten Weges
 * @param distOfOtherWay Distanz des anderen Weges bei actNode
 */
protected PathEdge computeResultPath (Node actNode, int way, double distOfOtherWay) {
	// Vorbereitung
	if (actNode == null)
		return null;
	int theOtherWay = NWAY1;
	if (theOtherWay == way)
		theOtherWay = NWAY2;
	// Berechnung des Weges
	PathEdge path = null;
	Edge actEdge = actNode.getWayEdge(way);
	while ((!actNode.isMarked(BEST)) && (actEdge != null)) {
		actNode.mark (BEST);
		actNode.setDistanceOfWay (theOtherWay,distOfOtherWay);
		double actWeight = actEdge.getWeight(actEdge.isDirectedTo(actNode));
		path = new PathEdge (actEdge,actEdge.isDirectedTo(actNode),actWeight,path);
		distOfOtherWay += actWeight;
		actNode = actEdge.getOppositeNode (actNode);
		actEdge = actNode.getWayEdge(way);
	}	
	return path;
}
/**
 * Berechnung des Ergebnispfad von einem Knoten ausgehend.
 * Ggf. wird neben dem NWAY1-Weg auch der NWAY2-Weg hinzugefügt.
 * Dabei wird der Pfad jeweils in der Normalrichtung abgelaufen.
 * Die Distanzen des jeweils anderen Weges werden an den Pfadknoten gesetzt
 * und diese mit BEST markiert.
 * @return berechneter Pfad
 * @param firstNode Ausgangsknoten
 * @param computeWAY2 Soll auch der mit NWAY2-Weg berechnet werden?
 */
protected PathEdge computeResultPath (Node firstNode, boolean computeNWAY2) {
	// Fall, daß Knoten nicht gesetzt, abfangen
	if (firstNode == null)
		return null;
		
	// Berechnung NWAY1
	firstNode.mark(BEST);
	double actDist = firstNode.getDistanceOfWay(NWAY2);
	PathEdge path = null;
	PathEdge firstPath = null;
	Node actNode = null;
	// Nur falls es eine NWAY1-Kante gibt, Pfad ablaufen
	if (firstNode.getWayEdge(NWAY1) != null) {
		Edge actEdge = firstNode.getWayEdge(NWAY1);
		double actWeight = actEdge.getWeight(actEdge.isDirectedTo(firstNode));
		firstPath = new PathEdge (actEdge,actEdge.isDirectedTo(firstNode),actWeight);
		path = firstPath;
		actDist += actWeight;
		actNode = actEdge.getOppositeNode (firstNode);
		while ((!actNode.isMarked(BEST)) && (actNode.getWayEdge(NWAY1) != null)) {
			actNode.mark (BEST);
			actNode.setDistanceOfWay (NWAY2,actDist);
			actEdge = actNode.getWayEdge(NWAY1);
			actWeight = actEdge.getWeight(actEdge.isDirectedTo(actNode));
			path = new PathEdge (actEdge,actEdge.isDirectedTo(actNode),actWeight,path);
			actDist += actWeight;
			actNode = actEdge.getOppositeNode (actNode);
		}
	}
	// Ggf. Ergebnis zurückgeben	
	if (!computeNWAY2) {
		return path;
	}
		
	// Berechnung NWAY2
	// Fall berücksichtigen, daß es keine NWAY2-Kante gibt, 
	if (firstNode.getWayEdge(NWAY2) == null) {
		return path;
	}
	// In Abhängigkeit vom NWAY1-Weg Resultat verketten
	PathEdge resPath = null;
	Edge actEdge = firstNode.getWayEdge(NWAY2);
	double actWeight = actEdge.getWeight(actEdge.isStartingFrom(firstNode));
	if (path != null) {
		resPath = path;
		firstPath.next = new PathEdge (actEdge,actEdge.isStartingFrom(firstNode),actWeight);
		path = firstPath.next;
	}	
	else {
		path = new PathEdge (actEdge,actEdge.isStartingFrom(firstNode),actWeight);
		resPath = path;
	}
	// Pfad ablaufen
	actDist = firstNode.getDistanceOfWay (NWAY1) + actWeight;
	actNode = actEdge.getOppositeNode (firstNode);
	actEdge = actNode.getWayEdge(NWAY2);
	while ((!actNode.isMarked(BEST)) && (actEdge != null)) {
		actNode.mark(BEST);
		actNode.setDistanceOfWay (NWAY1,actDist);
		actWeight = actEdge.getWeight(actEdge.isStartingFrom(actNode));
		path.next = new PathEdge (actEdge,actEdge.isStartingFrom(actNode),actWeight);
		path = path.next;
		actDist += actWeight;
		actNode = actEdge.getOppositeNode (actNode);
		actEdge = actNode.getWayEdge(NWAY2);
	}
	return resPath;				
}
/**
 * Berechnung des Ergebnispfad von einem Knoten ausgehend entlang eines Weges.
 * Dabei wird der Pfad entgegen der Normalrichtung abgelaufen.
 * Die Distanzen beider Wege werden an den Pfadknoten gesetzt
 * und diese mit BEST markiert.
 * @return berechneter Pfad
 * @param actNode Ausgangsknoten
 * @param way Index des gewünschten Weges
 */
protected PathEdge computeResultPathBackwards (Node firstNode, int way) {
	// Vorbereitungen
	if (firstNode == null)
		return null;
	int theOtherWay = NWAY1;
	if (theOtherWay == way)
		theOtherWay = NWAY2;
	// Berechnung des Weges
	Node actNode = firstNode;
	PathEdge path = null;
	long distOfOtherWay = 0;
	double distOfWay = actNode.getDistanceOfWay(way);
	Edge actEdge = actNode.getWayEdge(way);
	while ((!actNode.isMarked(BEST)) && (actEdge != null)) {
		actNode.mark (BEST);
		actNode.setDistanceOfWay(way,distOfWay);
		double actWeight = actEdge.getWeight(actEdge.isStartingFrom(actNode));
		path = new PathEdge (actEdge,actEdge.isStartingFrom(actNode),actWeight,path);
		distOfOtherWay += actWeight;
		distOfWay += actWeight;
		actNode = actEdge.getOppositeNode (actNode);
		actEdge = actNode.getWayEdge(way);
	}
	// Setzen der Distanzen des anderen Wegs
	distOfOtherWay += actNode.getDistanceOfWay(theOtherWay);
	while (!firstNode.equals(actNode)) {
		firstNode.setDistanceOfWay(theOtherWay,distOfOtherWay);
System.err.println("Ich glaube das nachfolgende ist falsch, da sich path nicht ändert");
		distOfOtherWay -= path.edge.getWeight();
		firstNode = path.edge.getOppositeNode (firstNode);
	}

	return path;
}
/**
 * Erzeugt Netzwerk aus Netzwerk-Dateien.
 * @param nodeIn DataInputStream für Knoten
 * @param edgeIn DataInputStream für Kanten
 * @param objects Container für drawable objects
 */
public void createByNetworkFiles (DataInputStream nodeIn, DataInputStream edgeIn, DrawableObjects objects) {
	System.out.println("read nodes ...");
	//DrawableObjectType nodeType = DrawableObjectType.getObjectType("Node");
	Node actNode = null;
	while ((actNode = nodes.read(nodeIn)) != null) {
		if (objects != null) {
			//DrawableObject obj = objects.newDrawableObject (actNode.getID(),nodeType,null,null);
			//obj.addDrawable(actNode);
			objects.addDrawable(actNode);
		}
	}	
	System.out.println("read edges ...");
	//DrawableObjectType edgeType = DrawableObjectType.getObjectType("Edge");
	Edge actEdge = null;
	int line = 1;
	boolean eof = false;
	while (!eof) {
		try {
			actEdge = edges.read(edgeIn,nodes);
			if (actEdge != null) {
				if (objects != null) {
					//DrawableObject obj = objects.newDrawableObject (actEdge.getID(),edgeType,actEdge.getName(),null);
					//obj.addDrawable(actEdge);
					objects.addDrawable(actEdge);
				}
			}
			else
				System.err.println("Read error for edge on line "+line);
			line++;
		}
		catch (IOException ioe) {
			eof = true;
		}
	}	
}
/**
 * Erzeugt Netzwerk aus Netzwerk-Dateien.
 * @return erfolgreich?
 * @param filename Basiname der Dateien
 * @param objects Container für drawable objects
 */
public boolean createByNetworkFiles (String filename, DrawableObjects objects) {
	try {
		DataInputStream nodeStream = new DataInputStream(new FileInputStream(filename+".node"));
		DataInputStream edgeStream = new DataInputStream(new FileInputStream(filename+".edge"));
		createByNetworkFiles (nodeStream,edgeStream,objects);
		return true;
	}		
	catch (IOException e) {
		System.err.println ("Network.create: I/O-Error!!!");
		return false;
	}		
}
/**
 * Erzeugt Netzwerk aus Netzwerk-Dateien.
 * @param nodeURL URL der Knoten-Datei
 * @param edgeURL URL der Kanten-Datei
 * @param objects Container für drawable objects
 */
public void createByNetworkFiles (URL nodeURL, URL edgeURL, DrawableObjects objects) {
	try {
		DataInputStream nodeStream = null;
		DataInputStream edgeStream = null;
		if (nodeURL.toString().endsWith(".zip")) {
			ZipInputStream zis = new ZipInputStream(nodeURL.openStream());
			zis.getNextEntry();
			nodeStream = new DataInputStream(zis);
		}
		else
			nodeStream = new DataInputStream(nodeURL.openStream());
		if (edgeURL.toString().endsWith(".zip")) {
			ZipInputStream zis = new ZipInputStream(edgeURL.openStream());
			zis.getNextEntry();
			edgeStream = new DataInputStream(zis);
		}
		else
			edgeStream = 	new DataInputStream(edgeURL.openStream());
		createByNetworkFiles (nodeStream,edgeStream,objects);
	}		
	catch (IOException ex) {
		System.err.println ("Network.createByNetworkFiles: I/O-Error!!!\n"+ex);
	}		
}
/**
 * Erweiterung des Randes.
 * Diese Version der Methode unterstützt die Berechnung von Verbindungen,
 * für den Fall, daß die Erweiterung auf einen FINAL- oder BEST-Knoten trifft.
 * Die Entfernung zum Zielknoten wird grundsätzlich in den Rand-Elementen abgelegt.
 * @param border Rand gemäß Dijkstra als Heap
 * @param start aktueller Knoten
 * @param stop Zielknoten
 * @param wayMark Index des betrachteten Wegs (WAY1 oder WAY2)
 * @param threshold Prozentsatz, um den Wege länger sein dürfen als der kürzester Weg
 * @param shortestDistance Länge des kürzesten Weges
 * @param resPath Ergebnispfad mit allen bislang berechneter Wege
 */
protected void enlargeBorder (BorderHeap border, Node start, Node stop, int wayMark, int threshold, double shortestDistance, PathEdge resPath) {
	// Vorbereitungen
	int way = wayMark / WAY1;
	// über alle Kanten des aktuellen Knotens iterieren
	Edge actEdge = start.getFirstEdge();
	while (actEdge != null) {
		// Ausschließen, daß Zurückgelaufen wird
		if (actEdge != start.getWayEdge(way)) {
			Node oppositeNode = actEdge.getOppositeNode(start);
			boolean newInBorder = (!oppositeNode.isMarked(wayMark));
			// neue Knoten mit max. Distanz versehen
			if (newInBorder)
				oppositeNode.setDistanceOfWay (way,maxDistance);
			// den akt. Knoten in Rand aufnehmen, falls dieser nicht mit FINAL markiert
			// und Distanz sich verringert
			if ((!oppositeNode.isMarked(FINAL)) &&
				(start.getDistanceOfWay(way)+actEdge.getWeight() <= oppositeNode.getDistanceOfWay(way)) ) {
				// Distanz am Knoten vermerken
				oppositeNode.setDistanceOfWay (way,start.getDistanceOfWay(way)+actEdge.getWeight());
				// Knoten neu in Rand aufnehmen
				if (newInBorder) {
					oppositeNode.setWay (way,actEdge);
					oppositeNode.mark (wayMark);
					//System.out.print("neu im Heap: "); element.debugPrint(); System.out.println();
					border.insert (oppositeNode,way,edges.getWeightManager().computeWeight (oppositeNode.distanceTo(stop)));
				}
				// oder Gewicht anpassen (bei akt. Heap-Implementierung ist dazu nichts nötig)
				else {
					//System.out.print("geändert im Heap: "); oppositeNode.debugPrint(way); System.out.println();
					// Prüfen, ob über alten Weg neue Verbindung anlegbar ist
					if (oppositeNode.isMarked(BEST)) {
						Edge oldEdge = oppositeNode.getWayEdge(way);
						computeNewConnection (border, oldEdge.getOppositeNode(oppositeNode),oldEdge,stop, wayMark, threshold,shortestDistance, resPath);
					}
					oppositeNode.setWay (way,actEdge);
				}	
			}
			
			// ansonsten, Prüfen, ob eine neue Verbindung anlegbar ist
			else if ((resPath!=null) && (oppositeNode.isMarked(BEST) || oppositeNode.isMarked(FINAL)) && (!start.isMarked(BEST) || (oppositeNode.isMarked(BEST) && start.getDistanceOfWay(way) < oppositeNode.getDistanceOfWay(way))) )
				computeNewConnection (border, start,actEdge,stop, wayMark, threshold,shortestDistance, resPath);
		}	// if 		
		actEdge = start.getNextEdge();
	}	// while
}
/**
 * (Standard-)Erweiterung des Randes.
 * @param border Rand gemäß Dijkstra als Heap
 * @param start Startknoten
 * @param stop Zielknoten
 * @param way betrachteter Weg
 * @param considerDistToDest Soll die Entfernung zum Zielknoten berücksichtigt werden?
 */
protected void enlargeBorder (BorderHeap border, Node start, Node stop, int way, boolean considerDistToDest) {
	Edge actEdge = start.getFirstEdge();
	while (actEdge != null) {
		Node oppositeNode = actEdge.getOppositeNode(start);
		// verbundenen Knoten in Rand aufnehmen, falls Knoten nicht mit FINAL markiert
		if (!oppositeNode.isMarked(FINAL)) {
			boolean newInBorder = (!oppositeNode.isMarked(way));
			// neue Knoten mit max. Distanz versehen
			if (newInBorder)
				oppositeNode.setDistanceOfWay (way,maxDistance);
			// und Distanz sich verringert
			double actWeight = actEdge.getWeight(start);
			if (start.getDistanceOfWay(way)+actWeight < oppositeNode.getDistanceOfWay(way)) {
				// case 1: insert into heap
				if (newInBorder) {
					oppositeNode.setDistanceOfWay (way,start.getDistanceOfWay(way)+actWeight);
					oppositeNode.setWay (way,actEdge);
					oppositeNode.mark (way);
					if (considerDistToDest)
						border.insert(oppositeNode,way,edges.getWeightManager().computeWeight (oppositeNode.distanceTo(stop)));
					else
						border.insert(oppositeNode,way,0);
				}
				// case 2: change 
				else {
					oppositeNode.setDistanceOfWay (way,start.getDistanceOfWay(way)+actWeight);
					oppositeNode.setWay (way,actEdge);
					border.adaptToDecreasedDistance(oppositeNode,way);
					//System.out.print("geändert im Heap: "); oppositeNode.debugPrint(way); System.out.println();
				}	
			}
		}	
		actEdge = start.getNextEdge();
	}
}
/**
 * Gibt Kanten-Container zurück.
 * @return Kanten-Container
 */
public Edges getEdges() {
	return edges;
}
/**
 * Gibt Knoten-Container zurück.
 * @return Knoten-Container
 */
public Nodes getNodes() {
	return nodes;
}
/**
 * Saves the network in network files.
 * @param filename path and basic name of the files
 */
public void save (String filename) {
	// write nodes
	try {
		System.out.println("write nodes ...");
		FileOutputStream out = new FileOutputStream (filename+".node");
		DataOutputStream dOut = new DataOutputStream (out);
		for (Enumeration e = nodes.elements(); e.hasMoreElements();)
			((Node)e.nextElement()).write (dOut);
		dOut.close();
		out.close();
		// write edges
		System.out.println("write edges ...");
		out = new FileOutputStream (filename+".edge");
		dOut = new DataOutputStream (out);
		for (Enumeration e = edges.elements(); e.hasMoreElements();)
			((Edge)e.nextElement()).write (dOut);
		dOut.close();
		out.close();
	}
	catch (IOException ioe) {
		System.err.println("Network.save: I/O-Error" + ioe.getMessage());
	}
}
/**
 * Saves the network in text files.
 * @param filename path and basic name of the files
 */
public void saveAsText (String filename) {
	// write nodes
	try {
		System.out.println("write nodes (text) ...");
		FileOutputStream out = new FileOutputStream (filename+"_node.txt");
		EntryWriter eOut = new EntryWriter (out);
		for (Enumeration e = nodes.elements(); e.hasMoreElements();)
			((Node)e.nextElement()).write (eOut);
		eOut.flush();
		out.close();
		// write edges
		System.out.println("write edges (text) ...");
		out = new FileOutputStream (filename+"_edge.txt");
		eOut = new EntryWriter (out);
		for (Enumeration e = edges.elements(); e.hasMoreElements();)
			((Edge)e.nextElement()).write (eOut);
		eOut.flush();
		out.close();
	}
	catch (IOException ioe) {
		System.err.println("Network.saveAsText: I/O-Error" + ioe.getMessage());
	}
}
}
