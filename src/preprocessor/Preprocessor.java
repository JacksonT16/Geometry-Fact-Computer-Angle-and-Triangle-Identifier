package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import java.util.TreeSet;
import java.util.stream.Stream;


import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.components.exception.NotInDatabaseException;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;

/**
 * Preprocesses explicit and implicit points along with segments to construct minimal and non-minimal segments.
 * @author Jackson Tedesco, Case Riddle
 * @date 4/11/2024
 */

public class Preprocessor
{
	// The explicit points provided to us by the user.
	// This database will also be modified to include the implicit
	// points (i.e., all points in the figure).
	protected PointDatabase _pointDatabase;

	// Minimal ('Base') segments provided by the user
	protected Set<Segment> _givenSegments;

	// The set of implicitly defined points caused by segments
	// at implicit points.
	protected Set<Point> _implicitPoints;

	// The set of implicitly defined segments resulting from implicit points.
	protected Set<Segment> _implicitSegments;

	// Given all explicit and implicit points, we have a set of
	// segments that contain no other subsegments; these are minimal ('base') segments
	// That is, minimal segments uniquely define the figure.
	protected Set<Segment> _allMinimalSegments;

	// A collection of non-basic segments
	protected Set<Segment> _nonMinimalSegments;

	// A collection of all possible segments: maximal, minimal, and everything in between
	// For lookup capability, we use a map; each <key, value> has the same segment object
	// That is, key == value. 
	protected Map<Segment, Segment> _segmentDatabase;
	public Map<Segment, Segment> getAllSegments() { return _segmentDatabase; }

	public Preprocessor(PointDatabase points, Set<Segment> segments) throws NotInDatabaseException
	{
		_pointDatabase  = points;
		_givenSegments = segments;

		_segmentDatabase = new HashMap<Segment, Segment>();

		analyze();
	}

	/**
	 * Invoke the precomputation procedure.
	 * @throws NotInDatabaseException 
	 */
	public void analyze() throws NotInDatabaseException
	{
		//
		// Implicit Points
		//
		_implicitPoints = ImplicitPointPreprocessor.compute(_pointDatabase, _givenSegments.stream().toList());

		//
		// Implicit Segments attributed to implicit points
		//
		_implicitSegments = computeImplicitBaseSegments(_implicitPoints);

		//
		// Combine the given minimal segments and implicit segments into a true set of minimal segments
		//     *givenSegments may not be minimal
		//     * implicitSegmen
		//
		_allMinimalSegments = identifyAllMinimalSegments(_implicitPoints, _givenSegments, _implicitSegments);

		//
		// Construct all segments inductively from the base segments
		//
		_nonMinimalSegments = constructAllNonMinimalSegments(_allMinimalSegments);

		//
		// Combine minimal and non-minimal into one package: our database
		//
		_allMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
		_nonMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
	}

	/**
	 * Computes implicitly defined segments from segment intersections, creating new minimal sub-segments.
	 * @param impPoints -- implicit points computed from segment intersections
	 * @return a set of implicitly defined segments
	 */
	protected Set<Segment> computeImplicitBaseSegments(Set<Point> impPoints)
	{
		if(impPoints == null) throw new NullPointerException();

		Set<Segment> implicitSegment = new HashSet<>();

		for(Segment segment: _givenSegments) {

			Set<Point> pointSeg = new HashSet<>();
			//gets all the impPoint between the segment and adds them to pointSeg
			for(Point point: impPoints) {

				if(segment.pointLiesBetweenEndpoints(point)) pointSeg.add(point);
			}

			if(!pointSeg.isEmpty()) {

				pointSeg.add(segment.getPoint1());
				pointSeg.add(segment.getPoint2());

				SortedSet<Point> sortedPoint = segment.collectOrderedPointsOnSegment(pointSeg);

				implicitSegment.addAll(makeSegments(sortedPoint));
			}
		}

		return implicitSegment;
	}

	/**
	 * Generates segments between ordered points to form a set of n-1 segments.
	 * @param points -- an ordred list of points
	 * @return a set of n-1 segments between all points provided
	 */
	protected Set<Segment> makeSegments(SortedSet<Point> points)
	{
		if(points == null) throw new NullPointerException();

		// Handles edge case
		if(points.size() <= 1) return new HashSet<Segment>();

		Set<Segment> newSegments = new HashSet<>();

		Point prev = null;
		for(Point point: points) {

			if(prev == null) prev = point;
			else {
				newSegments.add(new Segment(prev, point));
				prev = point;
			}
		}

		return newSegments;
	}

	/**
	 * From the 'given' segments we remove any non-minimal segment.
	 * @param impPoints -- the implicit points for the figure
	 * @param givenSegments -- segments provided by the user
	 * @param minimalImpSegments -- minimal implicit segments computed from the implicit points
	 * @return -- a 
	 */
	protected Set<Segment> identifyAllMinimalSegments(Set<Point> impPoints,
			Set<Segment> givenSegments,
			Set<Segment> minimalImpSegments)
	{

		if(impPoints == null || givenSegments == null || minimalImpSegments == null) throw new NullPointerException();


		Set<Segment> minimal = new HashSet<Segment>(minimalImpSegments);

		for(Segment seg: givenSegments) {
			boolean isMinimalSegment = true;

			Iterator<Point> points = impPoints.iterator();

			//identifies segments with new points in them, meaning their not minimal
			while(isMinimalSegment && points.hasNext()) {
				Point impPoint = (Point) points.next();

				if(seg.pointLiesBetweenEndpoints(impPoint)) isMinimalSegment = false;
			}

			if(isMinimalSegment) minimal.add(seg);
		}

		return minimal;
	}

	/**
	 * Constructs non-minimal segments from minimal segments.
	 * @param minimalSegs
	 * @return a set of non-minimal segments
	 */
	public Set<Segment> constructAllNonMinimalSegments(Set<Segment> minimalSegs)
	{
		if(minimalSegs == null) throw new NullPointerException();

		Set<Segment> nonMinimalSegs = new HashSet<Segment>();

		constructAllNonMinimalSegments( minimalSegs.stream().toList(), nonMinimalSegs);

		return nonMinimalSegs;
	}

	private void constructAllNonMinimalSegments(List<Segment> minimalSegs, Set<Segment> nonMinimalSegs)
	{
		if(minimalSegs.isEmpty()) return;

		Queue<Segment> q = new LinkedList<>(minimalSegs);
		Segment seg1 = q.remove();

		Set<Segment> segments = new HashSet<>(/*minimalSegs*/q);

		for(Segment seg2: segments) {
			Point vertex = seg1.sharedVertex(seg2);
			Segment seg = combineToNewSegment(seg1, seg2);

			if(vertex != null && seg != null) {
				nonMinimalSegs.add(seg);
				q.add(seg);
			}
			/*if(seg1.isCollinearWith(seg2) && vertex != null && !seg1.HasSubSegment(seg2) && !seg2.HasSubSegment(seg1)) {
				// the order segments are added together in 
				int order = seg1.getPoint1().compareTo(seg2.getPoint1());

				if(order < 0) {
					nonMinimalSegs.add(combineToNewSegment(seg1, seg2));
					q.add(combineToNewSegment(seg1, seg2));
				}

				if(order > 0) {
					nonMinimalSegs.add(combineToNewSegment(seg2, seg1));
					q.add(combineToNewSegment(seg2, seg1));
				}
			}*/
		}

		constructAllNonMinimalSegments(q.stream().toList(), nonMinimalSegs);
	}

	/**
	 * Stitches together segments that are on the same line.
	 * @param left segment
	 * @param right segment
	 * @return combined segments
	 */
	private Segment combineToNewSegment(Segment left, Segment right)
	{
		if(left.equals(right)) return left;

		//if(left.isCollinearWith(right)) return new Segment(left.getPoint1(), right.getPoint2());

		if(left.isCollinearWith(right) && !left.HasSubSegment(right) && !right.HasSubSegment(left)) {
			// the order segments are added together in 
			/*int order = left.getPoint1().compareTo(right.getPoint1());

			if(order < 0) {
				return new Segment(left.getPoint1(), right.getPoint2());
			}

			if(order > 0) {
				return new Segment(right.getPoint1(), left.getPoint2());
			}*/

			SortedSet<Point> order = new TreeSet<>();
			order.add(left.getPoint1());
			order.add(left.getPoint2());
			order.add(right.getPoint1());
			order.add(right.getPoint2());
			
			return new Segment(order.first(), order.last());
		
		}

		return null;
	}
}