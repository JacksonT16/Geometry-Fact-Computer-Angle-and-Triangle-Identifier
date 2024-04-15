package geometry_objects;

import java.util.List;

import exceptions.FactException;
import geometry_objects.points.Point;

public class Triangle
{
	/**
	 * A triangle consists of three points with 3 corresponding segments
	 */
	protected Point _point1;
	protected Point _point2;
	protected Point _point3;

	protected Segment _segmentA;
	protected Segment _segmentB;
	protected Segment _segmentC;

	public Triangle(List<Segment> segs) throws FactException
	{
		if (!isValidTriangle(segs)) throw new FactException();

		setTriangle(segs);
	}
	
	/**
	 * Determine if these 3 segments constitute a legitimate triangle.
	 * A legitimate triangle is built from 3 segments that properly share endpoints.
	 * 
	 * @param s1
	 * @param s2
	 * @param s3
	 * @return true if the 3 segments constitute a triangle
	 */
	protected static boolean isValidTriangle(List<Segment> segs)
	{
		// 3 sides
		if (segs.size() != 3) return false;

		// Gather distinct endpoints
		Point endpoint1 = segs.get(0).sharedVertex(segs.get(1));
		Point endpoint2 = segs.get(1).sharedVertex(segs.get(2));
		Point endpoint3 = segs.get(0).sharedVertex(segs.get(2));
		
		// 3 endpoints must be shared.
		if (endpoint1 == null || endpoint2 == null || endpoint3 == null) return false;
		
		//
		// 3 endpoints cannot be collinear:
		//       avoids the case where          A --- B -- C
		// the 3 segments are AB, AC, and BC.
		//
		if (segs.get(0).isCollinearWith(segs.get(1)) ||
			segs.get(0).isCollinearWith(segs.get(2)) ||
			segs.get(1).isCollinearWith(segs.get(2))) return false;
		
		// 3 distinct endpoints must be shared.
		if (endpoint1.equals(endpoint2) ||
            endpoint1.equals(endpoint3) ||
			endpoint2.equals(endpoint3)) return false;

		return true;
	}

	/*
	 * @param a -- the segment opposite point a
	 * @param b -- the segment opposite point b
	 * @param c -- the segment opposite point c
	 * Create a new triangle bounded by the 3 given segments. The set of points that define these segments should have only 3 distinct elements.
	 */
	private void setTriangle(List<Segment> segments)
	{
		_segmentA = segments.get(0);
		_segmentB = segments.get(1);
		_segmentC = segments.get(2);

		_point1 = _segmentA.getPoint1();
		_point2 = _segmentA.getPoint2();
		_point3 = _point1.equals(_segmentB.getPoint1()) ||
				  _point2.equals(_segmentB.getPoint1()) ? _segmentB.getPoint2() : _segmentB.getPoint1();
	}

	/*
	 * @param pt -- a point 
	 * @return true if @pt is one of the vertices
	 */
	public boolean has(Point p) { return _point1.equals(p) || _point2.equals(p) || _point3.equals(p); }

	@Override
	public int hashCode()
	{
		return _point1.hashCode() + _point2.hashCode() + _point3.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(this instanceof Triangle)) return false;
		Triangle that = (Triangle)obj ;

		return that.has(this._point1) &&
			   that.has(this._point2) &&
			   that.has(this._point3);
	}

	@Override
	public String toString()
	{
		return "Triangle(" + _point1.getName() + ", " +
				_point2.getName() + ", " +
				_point3.getName() + ")";
	}
}