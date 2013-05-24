package spatial;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * An n-dimensional minimum bounding rectangle.
 *
 * @version	1.20	30.07.2003	setBoundsByPoints added
 * @version	1.10	06.12.2002	two constructors added
 * @version	1.00	08.10.2002	first version
 * @author Thomas Brinkhoff
 */
public class MBR {

	/**
	 * The number of dimensions.
	 */
	private int DIM;
	/**
	 * The minimum coordinates.
	 */
	private int[] coord;
	/**
	 * The extensions.
	 */
	private int[] ext;

	/**
	 * The x dimension.
	 */
	public static final int X = 0;
	/**
	 * The y dimension.
	 */
	public static final int Y = 1;
	/**
	 * The z dimension.
	 */
	public static final int Z = 2;

	/**
	 * The width dimension.
	 */
	public static final int WIDTH = 0;
	/**
	 * The height dimension.
	 */
	public static final int HEIGHT = 1;
	/**
	 * The depth dimension.
	 */
	public static final int DEPTH = 2;
	
	/**
	 * Array of 2^i.
	 */
	protected static final int[] P2 = {1,2,4,8,16,32,64,128,256,512};
	
/**
 * Constructor of a MBR from a multi-dimensional point.
 * @param  coords  the coordinates of the point
 */
public MBR (int coords[]) {
	this(coords.length);
	for (int i=0; i<coords.length; i++)
		coord[i] = coords[i];
}
/**
 * Constructor of a MBR from a multi-dimensional point and an extension array.
 * @param  coords  the coordinates of the point
 * @param  exts  the extensions
 */
public MBR (int coords[], int exts[]) {
	this(Math.min(coords.length,exts.length));
	for (int i=0; i<coord.length; i++) {
		coord[i] = coords[i];
		ext[i] = exts[i];
	}
}
/**
 * Constructs a new rectangle with the specified dimension. 
 */
public MBR (int d) {
	if (d <= 0)
		throw new IllegalArgumentException("wrong dimension");
	DIM = d;
	coord = new int[DIM];
	ext = new int[DIM];
}
/**
 * Constructor of a one-dimensional interval.
 * @param  x  the mininum coordinate
 * @param  width  the length of the interval
 */
public MBR (int x, int width) {
	this(1);
	coord[X] = x;
	ext[WIDTH] = width;
}
/**
 * Constructor of a two-dimensional MBR.
 * @param  x, y  the minimum coordinates
 * @param  width  the width of the MBR
 * @param  height  the height of the MBR
 */
public MBR (int x, int y, int width, int height) {
	this(2);
	coord[X] = x;
	coord[Y] = y;
	ext[WIDTH] = width;
	ext[HEIGHT] = height;
}
/**
 * Constructor of a three-dimensional quader.
 * @param  x, y, z  the minimum coordinates
 * @param  width  the width of the MBR
 * @param  height  the height of the MBR
 * @param  depth  the depth of the MBR
 */
public MBR (int x, int y, int z, int width, int height, int depth) {
	this(3);
	coord[X] = x;
	coord[Y] = y;
	coord[Z] = z;
	ext[WIDTH] = width;
	ext[HEIGHT] = height;
	ext[DEPTH] = depth;
}
/**
 * Constructor of a two-dimensional MBR from a point.
 * @param  p  a point
 */
public MBR (Point p) {
	this(2);
	if (p == null)
		throw new IllegalArgumentException("argument null");
	coord[X] = p.x;
	coord[Y] = p.y;
}
/**
 * Constructor of a two-dimensional MBR.
 * @param  r  the rectangle
 */
public MBR (Rectangle r) {
	this(2);
	if (r == null)
		throw new IllegalArgumentException("argument null");
	coord[X] = r.x;
	coord[Y] = r.y;
	ext[WIDTH] = r.width;
	ext[HEIGHT] = r.height;
}
/**
 * Adds a specified point to this MBR.
 * @param  point  the additional point
 */
public void add (int[] point) throws IllegalArgumentException {
	if (point == null)
		return;
	if (point.length < DIM)
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++)
		if (point[i] < coord[i])
			coord[i] = point[i];
		else if (point[i] > coord[i]+ext[i])
			ext[i] = point[i]-coord[i];
}
/**
 * Adds a specified MBR to this MBR.
 * @param  r  the additional MBR
 */
public void add (MBR r) {
	if (r == null)
		return;
	if (r.numOfDimensions() < DIM)
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++) {
		int v1 = Math.min(coord[i], r.coord[i]);
		int v2 = Math.max(coord[i]+ext[i], r.coord[i]+r.ext[i]);
		coord[i] = v1;
		ext[i] = v2 - v1;
	}
}
/**
 * Returns the center coordinate of the specified dimension.
 * @return  the center coordinate
 * @param  d  the dimension (starts with 0)
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public int center (int d) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	return coord[d]+ext[d]/2;	
}
/**
 * Creates a clone of this object.
 * @return  the clone
 */
public Object clone() {
	MBR c = new MBR(DIM);
	for (int i=0; i<DIM; i++) {
		c.coord[i] = coord[i];
		c.ext[i] = ext[i];
	}
	return c;
}
/**
 * Creates a clone of a spefied MBR.
 * @return  the clone (or null if r == null)
 */
public static MBR clone (MBR r) {
	if (r == null)
		return null;
	return (MBR)r.clone();
}
/**
 * Computes the border of the MBR.
 * @return  the border
 */
public double computeBorder() {
	double border = 0;
	for (int i=0; i<DIM; i++)
		border += P2[DIM-1]*extension(i);
	return border;
}
/**
 * Computes the volume of the MBR.
 * @return  the volume
 */
public double computeVolume() {
	double volume = extension(0);
	for (int i=1; i<DIM; i++)
		volume *= extension(i);
	return volume;
}
/**
 * Checks whether this MBR contains the specified point.
 * @return  true if the point is inside this MBR; false otherwise.
 * @param  point  the point
 */
public boolean contains (int[] point) {
	return contains(point,0);
}
/**
 * Checks whether this MBR contains the specified point with a given tolerance.
 * @return  true if the point is inside this MBR; false otherwise.
 * @param  point  the point
 * @param  tolerance  the tolerance
 */
public boolean contains (int[] point, int tolerance) {
	if ((point == null) || (point.length < DIM))
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++)
		if (point[i]+tolerance < coord[i])
			return false;
		else if (point[i]-tolerance > coord[i]+ext[i])
			return false;
	return true;
}
/**
 * Determines whether this MBR contains the specified MBR.
 * @return  true if this MBR contains the other MBR; false otherwise
 * @param  r  the other MBR
 */
public boolean contains (MBR r) {
	if ((r == null) || (r.numOfDimensions() != DIM))
		return false;
	return contains(r.getMinCoords()) && contains(r.getMaxCoords());
}
/**
 * Determines whether this MBR contains the specified MBR with a given tolerance.
 * @return  true if this MBR contains the other MBR; false otherwise
 * @param  r  the other MBR
 * @param  tolerance  the tolerance
 */
public boolean contains (MBR r, int tolerance) {
	if ((r == null) || (r.numOfDimensions() != DIM))
		return false;
	return contains(r.getMinCoords(),tolerance) && contains(r.getMaxCoords(),tolerance);
}
/**
 * Checks whether two MBRs are equal.
 * @return  true if the objects are equal; false otherwise.
 * @param  obj  the other object
 */
public boolean equals (Object obj) {
	if (obj instanceof MBR) {
	    MBR r = (MBR)obj;
	    if (DIM != r.DIM)
	    	return false;
	    for (int i=0; i<DIM; i++)
	    	if ((coord[i] != r.coord[i]) || (ext[i] != r.ext[i]))
				return false;
	}
	return true;
}
/**
 * Returns the extension of the specified dimension.
 * @return  the extension
 * @param  d  the dimension (starts with 0)
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public int extension (int d) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	return ext[d];	
}
/**
 * Extracts a rectangle representing the specified dimensions.
 * @return  the rectangle
 * @param  dimX  the x dimension (starts with 0)
 * @param  dimY  the y dimension (starts with 0)
 * @exception  IllegalArgumentException  If the dimensions are too small or too large.
 */
public Rectangle extractRectangle (int dimX, int dimY) throws IllegalArgumentException {
	if ((dimX < 0) || (dimX >= DIM) || (dimY < 0) || (dimY >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	return new Rectangle (coord[dimX],coord[dimY], ext[dimX],ext[dimY]);
}
/**
 * Returns the center coordinates.
 * @return  the center coordinates
 */
public int[] getCenterCoords() {
	int[] res = new int[DIM];
	for (int i=0; i<DIM; i++)
		res[i] = coord[i]+ext[i]/2;
	return res;
}
/**
 * Returns the extensions.
 * @return  the extensions
 */
public int[] getExtensions() {
	return ext;
}
/**
 * Returns the maximum coordinates.
 * @return  the maximum coordinates
 */
public int[] getMaxCoords() {
	int[] res = new int[DIM];
	for (int i=0; i<DIM; i++)
		res[i] = coord[i]+ext[i];
	return res;
}
/**
 * Returns the minimum coordinates.
 * @return  the minimum coordinates
 */
public int[] getMinCoords() {
	return coord;
}
/**
 * Grows the MBR according to the specified values.
 * The grow method does not check whether the resulting extensions are non-negative.
 * @param  values  the delta extensions
 * @exception  IllegalArgumentException  If array size is wrong.
 */
public void grow (int[] values) throws IllegalArgumentException {
	if (values == null)
		return;
	if (values.length < DIM)
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++)
		ext[i] += values[i];
}
/**
 * Computes the intersection of this MBR with the specified MBR.
 * Returns a new MBR that represents the intersection of the two MBRs. 
 * @return  the resulting rectangle (or null)
 * @param  r  the other MBR
 */
public MBR intersection (MBR r) {
	if ((r == null) || (r.numOfDimensions() != DIM))
		return null;
	MBR res = new MBR(DIM);
	for (int i=0; i<DIM; i++) {
		int v1 = Math.max(coord[i], r.coord[i]);
		int v2 = Math.min(coord[i]+ext[i], r.coord[i]+r.ext[i]);
		if (v2 < v1)
			return null;
		res.coord[i] = v1;
		res.ext[i] = v2-v1;
	}
	return res;
}
/**
 * Computes the volume of the intersection of this MBR with the specified MBR.
 * @return  the resulting volume (0 if the MBRs do not intersect)
 * @param  r  the other MBR
 */
public double intersectionVolume (MBR r) {
	MBR iRect = intersection(r);
	return (iRect == null) ? 0 : iRect.computeVolume();
}
/**
 * Determines whether this MBR and the specified MBR intersect.
 * Two MBRs intersect if their intersection is nonempty.  
 * @return  true if the specified MBR and this MBR intersect; false otherwise
 * @param  r  the other MBR
 */
public boolean intersects(MBR r) {
	return intersects(r,0);
}
/**
 * Determines whether this MBR and the specified MBR intersect with a given tolerance.
 * Two MBRs intersect if their intersection is nonempty.  
 * @return  true if the specified MBR and this MBR intersect; false otherwise
 * @param  r  the other MBR
 * @param  tolerance  the tolerance
 */
public boolean intersects (MBR r, int tolerance) {
	if ((r == null) || (r.numOfDimensions() != DIM))
		return false;
	for (int i=0; i<DIM; i++) {
		if (coord[i] + ext[i] + tolerance <= r.coord[i])
			return false;
		if (coord[i] - tolerance >= r.coord[i] + r.ext[i])
			return false;
	}
	return true;
}
/**
 * Returns the maximum coordinate of the specified dimension.
 * @return  the maximum coordinate
 * @param  d  the dimension (starts with 0)
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public int max (int d) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	return coord[d]+ext[d];	
}
/**
 * Returns the minimum coordinate of the specified dimension.
 * @return  the minimum coordinate
 * @param  d  the dimension (starts with 0)
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public int min (int d) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	return coord[d];	
}
/**
 * Returns the number of dimensions.
 * @return 2
 */
public int numOfDimensions() {
	return DIM;
}
/**
 * Returns the quadratic distance from the center of the MBR to the specified point.
 * @return  the distance
 * @param  point  the point
 * @exception  IllegalArgumentException  If array size is wrong.
 */
public long quadDistanceFromCenter(int[] point) {
	if ((point == null) || (point.length < DIM))
		throw new IllegalArgumentException("wrong argument");
	long res = 0;
	for (int i=0; i<DIM; i++) {
		long dv = center(i)-point[i];
		res += dv*dv;
	}
	return res;
}
/**
 * Sets this MBR to the specified coordinates and extensions.
 * @param  coords  the minimum coordinates
 * @param  extensions  the extensions
 * @exception  IllegalArgumentException  If array sizes are wrong.
 */
public void setBounds (int[] coords, int[] extensions) throws IllegalArgumentException {
	if (coords != null) {
		if (coords.length < DIM)
			throw new IllegalArgumentException("wrong argument");
		for (int i=0; i<DIM; i++)
			coord[i] = coords[i];
	}
	if (extensions != null) {
		if (extensions.length < DIM)
			throw new IllegalArgumentException("wrong argument");
		for (int i=0; i<DIM; i++)
			ext[i] = extensions[i];
	}
}
/**
 * Sets this MBR to the specified points.
 * @return  this
 * @param  p1  one extreme point
 * @param  p2  the other extreme point
 * @exception  IllegalArgumentException  If array sizes are wrong.
 */
public MBR setBoundsByPoints (int[] p1, int[] p2) throws IllegalArgumentException {
	if ((p1 == null) || (p1.length < DIM) || (p2 == null) || (p2.length < DIM))
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++) {
		coord[i] = Math.min(p1[i],p2[i]);
		ext[i] = Math.max(p1[i],p2[i]) - coord[i];
	}
	return this;
}

/**
 * Sets this MBR to the specified interval.
 * @param  x  the mininum coordinate
 * @param  width  the length of the interval
 * @exception  IllegalArgumentException  If dimension is wrong.
 */
public void setBounds (int x, int width) throws IllegalArgumentException {
	if (DIM != 1)
		throw new IllegalArgumentException("wrong argument");
	coord[X] = x;
	ext[WIDTH] = width;
}
/**
 * Sets this MBR to the specified 2D-MBR.
 * @param  x, y  the minimum coordinates
 * @param  width  the width of the MBR
 * @param  height  the height of the MBR
 * @exception  IllegalArgumentException  If dimension is wrong.
 */
public void setBounds (int x, int y, int width, int height) throws IllegalArgumentException {
	if (DIM != 2)
		throw new IllegalArgumentException("wrong argument");
	coord[X] = x;
	coord[Y] = y;
	ext[WIDTH] = width;
	ext[HEIGHT] = height;
}
/**
 * Sets this MBR to the specified 3D-MBR.
 * @param  x, y, z  the minimum coordinates
 * @param  width  the width of the MBR
 * @param  height  the height of the MBR
 * @param  depth  the depth of the MBR
 * @exception  IllegalArgumentException  If dimension is wrong.
 */
public void setBounds (int x, int y, int z, int width, int height, int depth) throws IllegalArgumentException {
	if (DIM != 3)
		throw new IllegalArgumentException("wrong argument");
	coord[X] = x;
	coord[Y] = y;
	coord[Z] = z;
	ext[WIDTH] = width;
	ext[HEIGHT] = height;
	ext[DEPTH] = depth;
}
/**
 * Sets this MBR to the specified MBR.
 * @param  r  the bounds
 * @exception  IllegalArgumentException  If argument is wrong.
 */
public void setBounds (MBR r) throws IllegalArgumentException {
	if ((r == null) || (r.numOfDimensions() != DIM))
		throw new IllegalArgumentException("wrong argument");
	setBounds (r.getMinCoords(),r.getExtensions());
}
/**
 * Sets the extension of the specified dimension.
 * @param  d  the dimension (starts with 0)
 * @param  value  the new value of the extension
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public void setExtension (int d, int value) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	ext[d] = value;	
}
/**
 * Moves the MBR to the specified location.
 * @param  coords  the minimum coordinates
 * @exception  IllegalArgumentException  If array size is wrong.
 */
public void setLocation (int[] coords) throws IllegalArgumentException {
	setBounds(coords,null);
}
/**
 * Sets the minimum coordinate of the specified dimension.
 * @param  d  the dimension (starts with 0)
 * @param  value  the new value of the minimum coordinate
 * @exception  IllegalArgumentException  If the dimension is too small or too large.
 */
public void setMin (int d, int value) throws IllegalArgumentException {
	if ((d < 0) || (d >= DIM))
		throw new IllegalArgumentException("wrong dimension");
	coord[d] = value;	
}
/**
 * Sets the size of this MBR to match the specified extensions.
 * @param  extensions  the extensions
 * @exception  IllegalArgumentException  If array size is wrong.
 */
public void setSize (int[] extensions) throws IllegalArgumentException {
	setBounds(null,extensions);
}
/**
 * Returns the string represenation of the mbr.
 * @return  the string
 */
public String toString() {
	String res = "spatial.MBR[DIM="+DIM+" coord[]=";
	for (int i=0; i<DIM; i++)
		res += ((i==0)?"":",")+coord[i];
	res += " ext[]=";
	for (int i=0; i<DIM; i++)
		res += ((i==0)?"":",")+ext[i];
	return res;
}
/**
 * Translates the MBR according to the specified values.
 * @param  values  the delta coordinates
 * @exception  IllegalArgumentException  If array size is wrong.
 */
public void translate (int[] values) throws IllegalArgumentException {
	if (values == null)
		return;
	if (values.length < DIM)
		throw new IllegalArgumentException("wrong argument");
	for (int i=0; i<DIM; i++)
		coord[i] += values[i];
}
/**
 * Computes the union of this MBR with the specified MBR.
 * Returns a new MBR that represents the union of the two MBRs. 
 * @return  the resulting rectangle
 * @param  r  the other MBR
 */
public MBR union (MBR r) {
	if ((r == null) || (r.numOfDimensions() != DIM))
		return (MBR)clone();
	MBR res = new MBR(DIM);
	for (int i=0; i<DIM; i++) {
		int v1 = Math.min(coord[i], r.coord[i]);
		int v2 = Math.max(coord[i]+ext[i], r.coord[i]+r.ext[i]);
		res.coord[i] = v1;
		res.ext[i] = v2-v1;
	}
	return res;
}
}
