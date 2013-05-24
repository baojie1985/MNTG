package routing;

import util.CPUTimer;

/**
 * A heap for border elements.
 *
 * @version	2.00	21.08.2003	completely revised
 * @version	1.01	09.03.2000	distance error corrected
 * @version	1.00	01.02.2000	first version
 * @author Thomas Brinkhoff
 */
public class BorderHeap {

	/**
	 * Class representing an element in the border.
	 */
	private class BorderElement {
		protected Node node;	// the node
		protected int way;		// the path the element belongs to 
		protected double dist;	// the (remaining) distance
		protected BorderElement (Node node, int way, double dist) {	//constructor
			this.node = node;
			this.way = way;
			this.dist = dist;
		}
	}
	
	/**
	 * The elements in the heap. The first element is not used.
	 */
	private BorderElement elements[] = new BorderElement[2000];
	/**
	 * The return elemnet.
	 */
	private BorderElement retElement = null;
	/**
	 * The number of entries.
	 */
	private int num = 0;
	/**
	 * Fetch timer.
	 */
	static public CPUTimer fetchTimer = new CPUTimer();
	/**
	 * Insert timer.
	 */
	static public CPUTimer insertTimer = new CPUTimer();
	/**
	 * Change timer.
	 */
	static public CPUTimer changeTimer = new CPUTimer();

/**
 * Creates a new heap.
 */
public BorderHeap () {
}

/**
 * Adapts an element in the heap according to its decreased distances.
 * @param  node  the modified node in the border
 * @param  way  the direction of the distance
 */
public void adaptToDecreasedDistance (Node node, int way) {
	changeTimer.start();
	int k = node.heapPos[way-1];
	if ((k < 1) || (k > num))
		System.err.println("adaptToDecreasedDistance: heapPos "+0+"wrong!");
	else
		upheap(k);
	changeTimer.stop();
}

/**
 * Moves the specified element down in the heap
 * @param  k  the index of the element
 */
private void downheap (int k) {
	if (k > num)
		return;
	BorderElement v = elements[k];
	double vValue = v.dist+v.node.getDistanceOfWay(v.way);
	while (k <= num/2) {
		int j = k+k;
		BorderElement aj = elements[j];
		double ajValue = aj.dist+aj.node.getDistanceOfWay(aj.way);
		if (j < num) {
			BorderElement aj1 = elements[j+1];
			double aj1Value = aj1.dist+aj1.node.getDistanceOfWay(aj1.way);
			if (ajValue > aj1Value) {
				j++;
				aj = aj1;
				ajValue = aj1Value;
			}
		}
		if (vValue <= ajValue)
			break;
		elements[k] = aj;
		setHeapPos(k);
		k = j;
	}
	elements[k] = v;
	setHeapPos(k);
}

/**
 * Gets and removes the first element of the heap.
 * @return object found
 */
public boolean fetchFirst () {
	if (num == 0)
		return false;
	fetchTimer.start();
	retElement = elements[1];
	elements[1] = elements[num];
	num--;
	downheap(1);
	fetchTimer.stop();
	return true;
}

/**
 * Inserts a new border element into the heap.
 * @param obj new border element
 */
public void insert (Node node, int way, double distance) {
	// enlarge Heap if necessary
	insertTimer.start();
	if (num+1 == elements.length) {
		BorderElement newElements[] = new BorderElement[elements.length*2];
		for (int i=0; i<elements.length; i++)
			newElements[i] = elements[i];
		elements = newElements;
	}
	// insert
	num++;
	elements[num] = new BorderElement(node,way,distance);
	upheap (num);
	insertTimer.stop();
}

/**
 * Removes all elements from the heap.
 */
public void reset () {
	for (int i=0; i<=num; i++)
		elements[i] = null;
	num = 0;
}

/**
 * Returns the return distance.
 * @return  the distance
 */
public double returnDistance () {
	return retElement.dist+retElement.node.getDistanceOfWay(retElement.way);
}

/**
 * Returns the return node.
 * @return  the node
 */
public Node returnNode () {
	return retElement.node;
}

/**
 * Returns the return direction.
 * @return  the direction
 */
public int returnWay () {
	return retElement.way;
}

/**
 * Set the position of the node in the heap
 * @param  k  position in the heap
 */
private void setHeapPos (int k) {
	elements[k].node.heapPos[elements[k].way-1] = (short)k;
}

/**
 * Moves the specified element up in the heap
 * @param  k  the index of the element
 */
private void upheap (int k) {
	BorderElement v = elements[k];
	double vValue = v.dist+v.node.getDistanceOfWay(v.way);
	while (k > 1) {
		BorderElement a = elements[k/2];
		double aValue = a.dist+a.node.getDistanceOfWay(v.way);
		if (aValue <= vValue)
			break;
		elements[k] = elements[k/2];
		setHeapPos(k);
		k = k/2;
	}
	elements[k] = v;
	setHeapPos(k);
}

}
