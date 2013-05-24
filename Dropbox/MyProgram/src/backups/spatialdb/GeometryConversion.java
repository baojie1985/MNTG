package spatialdb;

import java.awt.geom.*;
import java.util.*;
import oracle.sdoapi.geom.*;
import oracle.sdoapi.sref.*;

/**
 * A class offering conversion methods for sdoapi.geom geometries.
 *
 * @author Thomas Brinkhoff
 * @version 1.2  20.01.2002  toAwtShape, toAwtEllipse2D and support of holes added
 * @version 1.1  19.08.2001  toIntegerAwt and toDoubleAwt added
 * @version 1.0  08.06.2001  first version
 */
public class GeometryConversion {

	/**
	 * The geometry factory.
	 */
	private static oracle.sdoapi.geom.GeometryFactory gf;

/**
 * Constructs a java.awt.geom.GeneralPath from a sdoapi.geom.CurveString
 *
 * @param geometry the geometry representing a line
 * @param matrix the affine transformation (or null)
 * @return the transformed line as general path
 */
private static GeneralPath fromCurveStringToAwtGeneralPath (CurveString line, AffineTransform matrix) {
	CurveString r = (LineString)line;
	int n = r.getNumPoints();
	GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO,n);
	CoordPoint cp = r.getPointAt(0);
	Point2D pl = new Point2D.Double(cp.getX(),cp.getY());
	for (int i=1; i<n; i++) {
		cp = r.getPointAt(i);
		Point2D p = new Point2D.Double(cp.getX(),cp.getY());
		Line2D seg = new Line2D.Double(pl,p);
		path.append(seg,true);
		pl = p;
	}
	if (matrix != null)
		path.transform(matrix);
	return path;
}
/**
 * Constructs a java.awt.Polygon from a sdoapi.geom.CurveString
 *
 * @param line the geometry representing a sdoapi.geom.CurveString
 * @return the polygon
 */
private static java.awt.Polygon fromCurveStringToAwtPolygon (CurveString line, AffineTransform matrix) {
	if (line == null)
		return null;
	int n = line.getNumPoints();
	int[] x = new int[n];
	int[] y = new int[n];
	for (int i=0; i<n; i++) {
		CoordPoint p = line.getPointAt(i);
		Point2D p2d = new Point2D.Double(p.getX(),p.getY());
		if (matrix != null)
			matrix.transform(p2d,p2d);
		x[i] = (int)Math.round(p2d.getX());
		y[i] = (int)Math.round(p2d.getY());
	}
	return new java.awt.Polygon(x,y,n);
}
/**
 * Generates a string representating a DOM node out of the given geometry collection.
 * @result the string
 * @param gc the geometry collection
 * @param indentation indentation of the output
 */
private static String fromGeometryCollectionToXML (GeometryCollection gc, String indentation) {
	SpatialReference srs = gc.getSpatialReference();
	if (srs == null)
		return indentation+"<MultiGeometryError>spatial reference is required</MultiGeometryError>\n";
	StringBuffer strBuf = new StringBuffer(indentation + "<MultiGeometry srsName=\""+srs.getName()+"\">\n");
	Enumeration g = gc.getGeometries();
	while(g.hasMoreElements()) {
		strBuf.append(indentation + " <geometryMember>\n");
		strBuf.append(toXML((Geometry)g.nextElement(), indentation + "  "));
		strBuf.append(" </geometryMember>\n");
	}
	strBuf.append(indentation + "</MultiGeometry>\n");
	return strBuf.toString();
}
/**
 * Generates a string representing a DOM node out of the given line ring.
 * @return the string
 * @param ls the line ring
 * @param indentation indentation of the output
 */
private static String fromLinearRingToXML (LineString ls, String indentation) {
	double xArray[] = ls.getOrdArray(0);
	double yArray[] = ls.getOrdArray(1);
	StringBuffer strBuf = new StringBuffer(indentation + "<LinearRing>");
	for (int i=0; i<xArray.length; i++)
		strBuf.append(toXML(xArray[i],yArray[i]));
	strBuf.append("</LinearRing>");
	return strBuf.toString();
 }
/**
 * Generates a string representing a DOM node out of the given line string.
 * @return the string
 * @param ls the line string
 * @param indentation indentation of the output
 */
private static String fromLineStringToXML(LineString ls, String indentation) {
	double xArray[] = ls.getOrdArray(0);
	double yArray[] = ls.getOrdArray(1);
	StringBuffer strBuf = new StringBuffer(indentation + "<LineString>");
	for (int i=0; i<xArray.length; i++)
		strBuf.append(toXML(xArray[i],yArray[i]));
	strBuf.append("</LineString>");
	return strBuf.toString();
}
/**
 * Generates a string representating a DOM node out of the given multilinestring.
 * @return the string
 * @param mls the multilinestring
 * @param indentation indentation of the output
 */
private static String fromMultiLineStringToXML(MultiLineString mls, String indentation) {
	StringBuffer strBuf = new StringBuffer(indentation + "<MultiLineString>\n");
	Enumeration ls = mls.getGeometries();
	while(ls.hasMoreElements())	{
		strBuf.append(indentation + "  <lineStringMember> ");
		strBuf.append(fromLineStringToXML((LineString)ls.nextElement(), indentation + "  "));
		strBuf.append(" </lineStringMember>\n");
	}
	strBuf.append(indentation + "</MultiLineString>\n");
	return strBuf.toString();
}
/**
 * Generates a string representating a DOM node out of the given multipoint.
 * @return the string
 * @param mp the multipoint
 * @param indentation indentation of the output
 */
private static String fromMultiPointToXML (MultiPoint mp, String indentation) {
	StringBuffer strBuf = new StringBuffer(indentation + "<MultiPoint>\n");
	Enumeration p = mp.getGeometries();
	while(p.hasMoreElements()) {
		strBuf.append(indentation + "  <pointMember> ");
		strBuf.append(fromPointToXML((Point)p.nextElement(), ""));
		strBuf.append(" </pointMember>\n");
	}
	strBuf.append(indentation + "</MultiPoint>\n");
	return strBuf.toString();
}
/**
 * Generates a string representating a DOM node out of the given multipolygon.
 * @return the string
 * @param mpg the multipolygon
 * @param indentation indentation of the output
 */
private static String fromMultiPolygonToXML (MultiPolygon mpg, String indentation) {
	StringBuffer strBuf = new StringBuffer(indentation + "<MultiPolygon>\n");
	Enumeration pg = mpg.getGeometries();
	while(pg.hasMoreElements())	{
		strBuf.append(indentation + " <polygonMember>\n");
		strBuf.append(fromPolygonToXML((Polygon)pg.nextElement(), indentation + "  "));
		strBuf.append(" </polygonMember>\n");
	}
	strBuf.append(indentation + "</MultiPolygon>\n");
	return strBuf.toString();
}
/**
 * Generates a string representing a DOM node for the given Point.
 * @return the string
 * @param p the point
 * @param indentation indentation of the output
 */
private static String fromPointToXML (Point p, String indentation) {
	return new String(indentation + "<Point>"+toXML(p.getX(),p.getY())+"</Point>");
}
/**
 * Generates a string representating a DOM node out of the given polygon.
 * @return the string
 * @param pg the polygon
 * @param indentation indentation of the output
 */
private static String fromPolygonToXML (Polygon pg, String indentation) {
	StringBuffer strBuf = new StringBuffer(indentation + "<Polygon>\n"+indentation+" <outerBoundaryIs>\n");
	LineString ring = (LineString)pg.getExteriorRing();
	strBuf.append(fromLinearRingToXML(ring, indentation + "  "));
	strBuf.append("\n"+indentation+" </outerBoundaryIs>\n");
	if (pg.getNumRings() > 1) {
		strBuf.append(indentation+" <innerBoundaryIs>\n");
		Enumeration e = pg.getInteriorRings();
		while(e.hasMoreElements()) {
			strBuf.append(fromLinearRingToXML((LineString)e.nextElement(), indentation + "  "));
			strBuf.append("\n");
		}
		strBuf.append(indentation+" </innerBoundaryIs>\n");
	}
	strBuf.append(indentation + "</Polygon>\n");
	return strBuf.toString();
}
/**
 * Constructs a java.awt.geom.Area from a sdoapi.geom.Polygon
 *
 * @param polygon the geometry representing a polygon
 * @return the polygon as area
 */
public static Area toAwtArea (Geometry polygon) {
	return toAwtArea(polygon,null);
}
/**
 * Constructs a java.awt.geom.Area from a sdoapi.geom.Polygon
 *
 * @param polygon the geometry representing a polygon
 * @param matrix the affine transformation (or null)
 * @return the transformed polygon as area
 */
public static Area toAwtArea (Geometry polygon, AffineTransform matrix) {
	Polygon pol = (Polygon)polygon;
	CurveString r = pol.getExteriorRing();
	Area result = new Area(fromCurveStringToAwtGeneralPath (r,matrix));
	for (int i=1; i<pol.getNumRings(); i++) {
		r = pol.getRingAt(i);
		result.subtract(new Area(fromCurveStringToAwtGeneralPath (r,matrix)));
	}
	return result;
}
/**
 * Constructs a java.awt.geom.Ellipse2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @param pixels the radius of the circle in pixels
 * @return the transformed point as a circle
 */
public static Ellipse2D.Double toAwtEllipse2D (Geometry geometry, int pixels) {
	return toAwtEllipse2D (geometry,null,pixels);
}
/**
 * Constructs a java.awt.geom.Ellipse2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @param matrix the affine transformation (or null)
 * @param pixels the radius of the circle in pixels
 * @return the transformed point as a circle
 */
public static Ellipse2D.Double toAwtEllipse2D (Geometry geometry, AffineTransform matrix, int pixels) {
	try {
		Point2D.Double p = new Point2D.Double(((Point)geometry).getX(),((Point)geometry).getY());
		p = (matrix==null)? p : (Point2D.Double)matrix.transform(p,p);
		Ellipse2D.Double circle = new Ellipse2D.Double(p.getX()-pixels,p.getY()-pixels,2*pixels,2*pixels);
		return circle;
	}
	catch (Exception ex) {
		return null;
	}
}
/**
 * Constructs a java.awt.geom.GeneralPath from a sdoapi.geom.LineStrimg
 *
 * @param line the geometry representing a line
 * @return the line as general path
 */
public static GeneralPath toAwtGeneralPath (Geometry line) {
	return toAwtGeneralPath (line,null);
}
/**
 * Constructs a java.awt.geom.GeneralPath from a sdoapi.geom.LineStrimg
 *
 * @param line the geometry representing a line
 * @param matrix the affine transformation (or null)
 * @return the transformed line as general path
 */
public static GeneralPath toAwtGeneralPath (Geometry line, AffineTransform matrix) {
	return fromCurveStringToAwtGeneralPath ((LineString)line,matrix);
}
/**
 * Constructs a java.awt.Point from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @return the rounded point
 */
public static java.awt.Point toAwtPoint (Geometry geometry) {
	return toAwtPoint(geometry,null);
}
/**
 * Constructs a java.awt.Point from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @param matrix the affine transformation (or null)
 * @return the transformed and rounded point
 */
public static java.awt.Point toAwtPoint (Geometry geometry, AffineTransform matrix) {
	Point2D p = toAwtPoint2D (geometry,matrix);
	if (p == null)
		return null;
	return new java.awt.Point((int)Math.round(p.getX()),(int)Math.round(p.getY()));
}
/**
 * Constructs a java.awt.geom.Point2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @return the point
 */
public static Point2D.Double toAwtPoint2D (Geometry geometry) {
	return toAwtPoint2D(geometry,null);
}
/**
 * Constructs a java.awt.geom.Point2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a point
 * @param matrix the affine transformation (or null)
 * @return the transformed point
 */
public static Point2D.Double toAwtPoint2D (Geometry geometry, AffineTransform matrix) {
	if (geometry == null)
		return null;
	Point2D.Double p = new Point2D.Double(((Point)geometry).getX(),((Point)geometry).getY());
	return matrix==null? p : (Point2D.Double)matrix.transform(p,p);
}
/**
 * Constructs a java.awt.Polygon from a sdoapi.geom.Geometry
 *
 * @param  geometry  the geometry representing a sdoapi.geom.Polygon
 * @return  the outer polygon
 */
public static java.awt.Polygon toAwtPolygon (Geometry geometry) {
	return toAwtPolygon (geometry,null);
}
/**
 * Constructs a java.awt.Polygon from a sdoapi.geom.Geometry
 *
 * @param  geometry  the geometry representing a sdoapi.geom.Polygon
 * @param  matrix  the affine transformation (or null)
 * @return  the transformed outer polygon
 */
public static java.awt.Polygon toAwtPolygon (Geometry geometry, AffineTransform matrix) {
	if (geometry == null)
		return null;
	return fromCurveStringToAwtPolygon(((Polygon)geometry).getExteriorRing(),matrix);
}
/**
 * Constructs a java.awt.Polygon (because of a missing Polyline class) from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a sdoapi.geom.LineString
 * @return the polyline as polygon
 */
public static java.awt.Polygon toAwtPolyline (Geometry geometry) {
	return toAwtPolyline (geometry,null);
}
/**
 * Constructs a java.awt.Polygon (because of a missing Polyline class) from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a sdoapi.geom.LineString
 * @param matrix the affine transformation (or null)
 * @return the transformed polyline as polygon
 */
public static java.awt.Polygon toAwtPolyline (Geometry geometry, AffineTransform matrix) {
	return fromCurveStringToAwtPolygon((LineString)geometry,matrix);
}
/**
 * Constructs a java.awt.Rectangle from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a rectangle
 * @return the rounded rectangle
 */
public static java.awt.Rectangle toAwtRectangle (Geometry geometry) {
	return toAwtRectangle (geometry,null);
}
/**
 * Constructs a java.awt.Rectangle from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a rectangle
 * @param matrix the affine transformation (or null)
 * @return the transformed and rounded rectangle
 */
public static java.awt.Rectangle toAwtRectangle (Geometry geometry, AffineTransform matrix) {
	Rectangle2D r = toAwtRectangle2D (geometry,matrix);
	if (r == null)
		return null;
	return new java.awt.Rectangle((int)Math.round(r.getX()),(int)Math.round(r.getY()),
		(int)Math.round(r.getWidth()),(int)Math.round(r.getHeight()));
}
/**
 * Constructs a java.awt.geom.Rectangle2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a rectangle
 * @return the rectangle
 */
public static Rectangle2D.Double toAwtRectangle2D (Geometry geometry) {
	return toAwtRectangle2D(geometry,null);
}
/**
 * Constructs a java.awt.geom.Rectangle2D from a sdoapi.geom.Geometry
 *
 * @param geometry the geometry representing a rectangle
 * @param matrix the affine transformation (or null)
 * @return the transformed rectangle
 */
public static Rectangle2D.Double toAwtRectangle2D (Geometry geometry, AffineTransform matrix) {
	if (geometry == null)
		return null;
	CurveString ring = ((Polygon)geometry).getExteriorRing();
	CoordPoint p1 = ring.getPointAt(0);
	CoordPoint p2 = ring.getPointAt(2);
	if (matrix == null)
		return new Rectangle2D.Double(Math.min(p1.getX(),p2.getX()),Math.min(p1.getY(),p2.getY()),
			Math.abs(p1.getX()-p2.getX()),Math.abs(p1.getY()-p2.getY()));
	Point2D p2d1 = new Point2D.Double(p1.getX(),p1.getY());
	Point2D p2d2 = new Point2D.Double(p2.getX(),p2.getY());
	matrix.transform(p2d1,p2d1);
	matrix.transform(p2d2,p2d2);
	return new Rectangle2D.Double(Math.min(p2d1.getX(),p2d2.getX()),Math.min(p2d1.getY(),p2d2.getY()),
		Math.abs(p2d1.getX()-p2d2.getX()),Math.abs(p2d1.getY()-p2d2.getY()));
}
/**
 * Constructs one or several objects of java.awt.Shape from a sdoapi.geom.Geometry
 *
 * @param  geom  the geometry
 * @return  vector of the shape(s)
 */
public static Vector toAwtShape (Geometry geom) {
	return toAwtShape (geom,null);
}
/**
 * Constructs one or several objects of java.awt.Shape from a sdoapi.geom.Geometry
 *
 * @param  geom  the geometry
 * @param  matrix  the affine transformation (or null)
 * @return  vector of the shape(s)
 */
public static Vector toAwtShape (Geometry geom, AffineTransform matrix) {
	Vector result = new Vector(5);
	if (geom instanceof Point)
		result.add(toAwtEllipse2D(geom,matrix,3));
	else if (geom instanceof LineString)
		result.add(toAwtGeneralPath(geom,matrix));
	else if (geom instanceof Polygon)
		result.add(toAwtArea (geom,matrix));
	else if (geom instanceof GeometryCollection) {
		GeometryCollection coll = (GeometryCollection) geom;
		for (int i=0; i<coll.getNumGeometries(); i++)
			result.addAll(toAwtShape(coll.getGeometryAt(i),matrix));
	}
	return result;
}
/**
 * Generates a double-based AWT geometry for the given Geometry.
 * @return the object
 * @param geom the geometry
 */
public static Object toDoubleAwt (Geometry geom) {
	return toIntegerAwt (geom,null);
}
/**
 * Generates a double-based AWT geometry for the given Geometry.
 * @return the object
 * @param geom the geometry
 * @param matrix the affine transformation (or null)
 */
public static Object toDoubleAwt (Geometry geom, AffineTransform matrix) {
	if (geom instanceof Point)
	  return toAwtPoint2D(geom,matrix);
	else if (geom instanceof LineString)
	  return toAwtGeneralPath(geom,matrix);
	else if (geom instanceof Polygon)
	  return toAwtArea(geom,matrix);
	else
	  return null;
}
/**
 * Generates an integer-based AWT geometry for the given Geometry.
 * @return the object
 * @param geom the geometry
 */
public static Object toIntegerAwt (Geometry geom) {
	return toIntegerAwt (geom,null);
}
/**
 * Generates an integer-based AWT geometry for the given Geometry.
 * @return the object
 * @param geom the geometry
 * @param matrix the affine transformation (or null)
 */
public static Object toIntegerAwt (Geometry geom, AffineTransform matrix) {
	if (geom instanceof Point)
	  return toAwtPoint(geom,matrix);
	else if (geom instanceof LineString)
	  return toAwtPolyline(geom,matrix);
	else if (geom instanceof Polygon)
	  return toAwtPolyline(geom,matrix);
	else
	  return null;
}
/**
 * Generates a string representing a DOM node out of the given coordinates.
 * @return the string
 * @param x the x-coordinate
 * @param y the y-coordinate
 */
public static String toXML (double x, double y) {
	return "<coord><X>"+x+"</X><Y>"+y+"</Y></coord>";
 }
/**
 * Generates a string representing a DOM node out of the given Box.
 * @return the string
 * @param xmin the minimum x-coordinate
 * @param ymin the minimum y-coordinate
 * @param xmax the maximum x-coordinate
 * @param ymax the maximum y-coordinate
 */
public static String toXML (double xmin, double ymin, double xmax, double ymax) {
	return "<Box>"+toXML(xmin,ymin)+toXML(xmax,ymax)+"</Box>";
 }
/**
 * Generates a string representing a DOM node for the given Geometry.
 * @return the string
 * @param geom the geometry
 */
public static String toXML (Geometry geom) {
	return toXML (geom, "");
}
/**
 * Generates a string representing a DOM node for the given Geometry.
 * @return the string
 * @param geom the geometry
 * @param indentation indentation of the output
 */
public static String toXML (Geometry geom, String indentation) {
	if (geom instanceof Point)
	  return fromPointToXML((Point)geom, indentation);
	else if (geom instanceof LineString)
	  return fromLineStringToXML((LineString)geom, indentation);
	else if (geom instanceof Polygon)
	  return fromPolygonToXML((Polygon)geom, indentation + "");
	else if (geom instanceof MultiPolygon)
	  return fromMultiPolygonToXML((MultiPolygon)geom, indentation);
	else if (geom instanceof MultiLineString)
	  return fromMultiLineStringToXML((MultiLineString)geom, indentation);
	else if (geom instanceof MultiPoint)
	  return fromMultiPointToXML((MultiPoint)geom, indentation);
	else if (geom instanceof GeometryCollection)
	  return fromGeometryCollectionToXML((GeometryCollection)geom, indentation);
	else
	  return indentation+"<GeometryError>"+geom.getClass().getName()+" is not supported yet</GeometryError>";
}
}
