package generator2;

import routing.*;

/**
 * Class for weighting the edges.
 * 
 * @version	2.00	16.08.2003	long becomes double
 * @version	1.20	30.04.2000	support of external objects
 * @version	1.10	02.03.2000	distances as long
 * @version	1.00	10.01.2000	first version
 * @author FH Oldenburg
 */
public class WeightManagerForDataGenerator implements WeightManager {

	/**
	 * description of the edge classes
	 */
	private EdgeClasses edgeClasses = null;
	/**
	 * description of the object classes
	 */
	private ObjectClasses objClasses = null;
	/**
	 * description of the object classes
	 */
	private ExternalObjects extObjects = null;
	/**
	 * class of the actual moving object
	 */
	private int actObjClass = 0;

/**
 * WeightManagerForDataGenerator constructor.
 * @param edgeClasses description of the edge classes
 * @param objClasses description of the object classes
 * @param extObjects container of the external objects (may be null)
 */
public WeightManagerForDataGenerator (EdgeClasses edgeClasses, ObjectClasses objClasses, ExternalObjects extObjects) {
	this.edgeClasses = edgeClasses;
	this.objClasses = objClasses;
	this.extObjects = extObjects;
}
/**
 * Computes the weight for a distance assuming edge class 0.
 * The class of the actual moving object must be set before by setActualObjectClass!
 * @return weight
 * @param distance distance
 */
public double computeWeight (double distance) {
	return computeWeight (distance,objClasses.getMaxSpeed(0));
}
/**
 * Computes the weight for a given distance and speed.
 * The class of the actual moving object must be set before by setActualObjectClass!
 * @return weight
 * @param distance distance
 * @param speed speed
 */
public double computeWeight (double distance, double speed) {
	if (speed != 0)
		return distance / speed;
	else
		return Double.MAX_VALUE;
}
/**
 * Returns the description of the edge classes.
 * @return description of the edge classes
 */
public EdgeClasses getEdgeClasses () {
	return edgeClasses;
}
/**
 * Returns the description of the object classes.
 * @return description of the object classes
 */
public ObjectClasses getObjectClasses () {
	return objClasses;
}
/**
 * Returns the weight of the edge.
 * The class of the actual moving object must be set before by setActualObjectClass!
 * @return weigth
 * @param edge the edge
 */
public double getWeight(Edge edge) {
	return getWeight(edge,edge.getLength());
}
/**
 * Computes the weight of the edge considering the usage, external
 * objects and the maximum object speed.
 * The class of the actual moving object must be set before by setActualObjectClass!
 * @return directed weight
 * @param  edge  the edge
 * @param  edgeLength  the length of the edge
 */
private double getWeight (Edge edge, double edgeLength) {
	double objWeight = computeWeight(edgeLength,objClasses.getMaxSpeed(actObjClass));
	double edgeWeight = computeWeight(edgeLength,edgeClasses.deceleratedSpeed(edge.getEdgeClass(),edge.getUsage()));
	if (extObjects != null) {
		int decFactor = extObjects.computeDecrease(edge);
		if (decFactor < 100) {
			double newEdgeWeight = computeWeight(edgeLength,edgeClasses.getMaxSpeed(edge.getEdgeClass())*decFactor/100);
			if (newEdgeWeight > edgeWeight)
				edgeWeight = newEdgeWeight;
		}
	}
	if (edgeWeight > objWeight)
		return edgeWeight;
	else
		return objWeight;
}
/**
 * Returns the directed weight of the edge.
 * The class of the actual moving object must be set before by setActualObjectClass!
 * @return directed weight
 * @param edge the edge
 * @param forwards forwards direction?
 */
public double getWeight(Edge edge, boolean forwards) {
	return getWeight(edge,edge.getLength());
}
/**
 * Sets the class of the actual moving object.
 * @param actObjClass actual class
 */
public void setActualObjectClass (int actObjClass) {
	this.actObjClass = actObjClass;
}
}