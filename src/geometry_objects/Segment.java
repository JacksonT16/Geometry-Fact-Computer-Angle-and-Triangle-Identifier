package geometry_objects;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import geometry_objects.delegates.LineDelegate;
import geometry_objects.delegates.SegmentDelegate;
import geometry_objects.delegates.intersections.IntersectionDelegate;
import geometry_objects.points.Point;
import utilities.math.MathUtilities;
import utilities.math.analytic_geometry.GeometryUtilities;

/**
 * Implements additional functionality to be utilized by Preprocessor.
 * @author Case Riddle and Jackson Tedesco
 * @date 4/11/2024
 */
public class Segment extends GeometricObject
{
	protected Point _point1;
	protected Point _point2;

	protected double _length;
	protected double _slope;

	public Point getPoint1() { return _point1; }
	public Point getPoint2() { return _point2; }
	public double length() { return _length; }
	public double slope()
	{
		try { return GeometryUtilities.slope(_point1, _point2); }
		catch(ArithmeticException ae) { return Double.POSITIVE_INFINITY; }
	}

	public Segment(Segment in) { this(in._point1, in._point2); }
	public Segment(Point p1, Point p2)
	{
		_point1 = p1;
		_point2 = p2;
	}

	/**
	 * @param that -- a segment (as a segment: finite)
	 * @return the midpoint of this segment (finite)
	 */
	public Point segmentIntersection(Segment that) {  return IntersectionDelegate.segmentIntersection(this, that); }

	/**
	 * @param pt -- a point
	 * @return true / false if this segment (finite) contains the point
	 */
	public boolean pointLiesOn(Point pt) { return this.pointLiesOnSegment(pt); }

	/**
	 * @param pt -- a point
	 * @return true / false if this segment (finite) contains the point
	 */
	public boolean pointLiesOnSegment(Point pt) { return SegmentDelegate.pointLiesOnSegment(this, pt); }

	/**
	 * @param pt -- a point
	 * @return true if the point is on the segment (Excluding endpoints); finite examination only
	 */
	public boolean pointLiesBetweenEndpoints(Point pt) { return SegmentDelegate.pointLiesBetweenEndpoints(this, pt); }

	/**
	 * Determines whether or not a specified segment contains a subsegment.
	 * @param candidate: Segment
	 * @return true if this segment contains candidate as subsegment.
	 */
	public boolean HasSubSegment(Segment candidate) {
	    if (candidate != null) {
	        boolean p1 = this.pointLiesOnSegment(candidate.getPoint1());
	        boolean p2 = this.pointLiesOnSegment(candidate.getPoint2());

	        return p1 && p2;
	    }
	    return false;
	}

	/**
	 * Determines if this segment and that segment share an endpoint.
	 * @param s -- a segment
	 * @return the shared endpoint.
	 */
	public Point sharedVertex(Segment that)
	{
		if (this.equals(that)) return null;

		if (_point1.equals(that._point1)) return _point1;
		if (_point1.equals(that._point2)) return _point1;
		if (_point2.equals(that._point1)) return _point2;
		if (_point2.equals(that._point2)) return _point2;
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;

		if (!(obj instanceof Segment)) return false;
		Segment that = (Segment)obj;

		return this.has(that.getPoint1()) && this.has(that.getPoint2());
	}

	/**
	 * @param that -- another segment
	 * @return true / false if the two lines (infinite) are collinear
	 */
	public boolean isCollinearWith(Segment that) { return LineDelegate.areCollinear(this, that); }

	/**
	 * @param pt -- a point
	 * @return true if @pt is one of the endpoints of this segment
	 */
	public boolean has(Point pt) { return _point1.equals(pt) || _point2.equals(pt); }

	/**
	 * @return true if this segment is horizontal (by analysis of both endpoints having same y-coordinate)
	 */
	public boolean isHorizontal() { return MathUtilities.doubleEquals(_point1.getY(), _point2.getY()); }

	/**
	 * @return true if this segment is vertical (by analysis of both endpoints having same x-coordinate)
	 */
	public boolean isVertical() { return MathUtilities.doubleEquals(_point1.getX(), _point2.getX()); }

	/**
	 * @param pt -- one of the endpoints of this segment
	 * @return the 'other' endpoint of the segment (null if neither endpoint is given)
	 */
	public Point other(Point p)
	{
		if (p.equals(_point1)) return _point2;
		if (p.equals(_point2)) return _point1;

		return null;
	}

	@Override
	public int hashCode()
	{
		return _point1.hashCode() +_point2.hashCode();
	}

	/**
	 * Can still be true if the segments share a vertex.
	 * @param that: Segment
	 * @return true if the segments coincide, but do not overlap.
	 */
	public boolean coincideWithoutOverlap(Segment that) {
		if (!this.isCollinearWith(that)) { return false; }
		
		return (!this.pointLiesBetweenEndpoints(that._point1) && 
				!this.pointLiesBetweenEndpoints(that._point2));
	}
	
	/**
	 * Collects the points on the segment and orders them lexicographically.
	 * @param points: Set<Point>
	 * @return the sorted subset of Points that lie on this segment
	 */
	public SortedSet<Point> collectOrderedPointsOnSegment(Set<Point> points) {
		SortedSet<Point> orderedPoints = new TreeSet<>();
		for (Point point : points) {
			if (this.pointLiesOnSegment(point)) {
				orderedPoints.add(point);
			}
		}
		return orderedPoints;
	}
}