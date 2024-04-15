package preprocessor.delegates;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.components.exception.NotInDatabaseException;

/**
 * Gets all implicit points from a set of points and segments
 * @author Jackson Tedesco, Case Riddle
 * @date 4/10/2024
 */

public class ImplicitPointPreprocessor
{
	/**
	 * It is possible that some of the defined segments intersect
	 * and points that are not named; we need to capture those
	 * points and name them.
	 * 
	 * @param givenPoints: points of the geometric shapes
	 * @param givenSegments: segments of the geometric shapes
	 * @throws NotInDatabaseException 
	 */
	public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments) throws NotInDatabaseException
	{
		if(givenPoints == null || givenSegments == null) throw new NullPointerException();
		
		Set<Point> implicitPoints = new LinkedHashSet<Point>();

        for(Segment _segment1 : givenSegments) {
        	for(Segment _segment2 : givenSegments) {
        		
        		Point implicitPoint = _segment1.segmentIntersection(_segment2);
        		
        		if(implicitPoint != null && _segment1.pointLiesBetweenEndpoints(implicitPoint) &&
        				_segment2.pointLiesBetweenEndpoints(implicitPoint)) {

        			
        			givenPoints.put(implicitPoint);
        			implicitPoints.add(givenPoints.getPoint(implicitPoint));
        		}
        	}
        }
        
		return implicitPoints;
	}
}