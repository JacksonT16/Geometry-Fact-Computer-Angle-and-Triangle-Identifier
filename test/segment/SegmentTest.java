package segment;

import static org.junit.Assert.*;
import org.junit.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class SegmentTest {

	@Test
	public void testHasSubSegment() {
	    //Two segments with the same start and end points
	    Point p1 = new Point(0, 0);
	    Point p2 = new Point(2, 2);

	    Segment segment1 = new Segment(p1, p2);
	    Segment segment2 = new Segment(p1, p2);

	    assertTrue(segment1.HasSubSegment(segment2)); 
	    
	    //Two segments with one contained within the other
	    Point p3 = new Point(1, 1);
	    Point p4 = new Point(1.5, 1.5);
	    Point p5 = new Point(3, 3);

	    Segment segment3 = new Segment(p1, p2);
	    Segment segment4 = new Segment(p3, p4);
	    Segment segment5 = new Segment(p1, p5);

	    assertTrue(segment3.HasSubSegment(segment4));
	    assertFalse(segment3.HasSubSegment(segment5));

	    //Two segments with no common points
	    Point p6 = new Point(4, 4);
	    Point p7 = new Point(5, 5);

	    Segment segment6 = new Segment(p1, p2);
	    Segment segment7 = new Segment(p6, p7);

	    assertFalse(segment6.HasSubSegment(segment7)); 
	    
	    //Two segments with overlapping but not fully contained
	    Point p8 = new Point(1.5, 1.5);
	    Point p9 = new Point(3, 3);

	    Segment segment8 = new Segment(p1, p2);
	    Segment segment9 = new Segment(p8, p9);

	    assertFalse(segment8.HasSubSegment(segment9));
	}
    
    @Test
    public void testCoincideWithoutOverlap() {
        // Non-overlapping segments
        Segment segment1 = new Segment(new Point(1, 5), new Point(4, 5));
        Segment segment2 = new Segment(new Point(4, 5), new Point(8, 5));
        assertTrue(segment1.coincideWithoutOverlap(segment2));
        
        // Overlapping segments
        Segment segment3 = new Segment(new Point(1, 5), new Point(4, 5));
        Segment segment4 = new Segment(new Point(3, 5), new Point(8, 5));
        assertFalse(segment3.coincideWithoutOverlap(segment4));
        
        // Perfectly coincide without overlapping
        Segment segment5 = new Segment(new Point(1, 5), new Point(4, 5));
        Segment segment6 = new Segment(new Point(4, 5), new Point(1, 5));
        assertTrue(segment5.coincideWithoutOverlap(segment6));
        
        // Floating point segments
        Segment segment7 = new Segment(new Point(1.1, 5.2), new Point(4.4, 5.3));
        Segment segment8 = new Segment(new Point(4.4, 5.3), new Point(8.8, 5.4));
        assertFalse(segment7.coincideWithoutOverlap(segment8));
    }

    @Test
    public void testCollectOrderedPointsOnSegment() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 3);

        Segment segment = new Segment(p1, p2);

        Point p3 = new Point(1, 1);
        Point p4 = new Point(2, 2);
        Point p5 = new Point(4, 4);

        Set<Point> points = new HashSet<>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);

        SortedSet<Point> orderedPoints = segment.collectOrderedPointsOnSegment(points);

        assertEquals(4, orderedPoints.size());
        assertTrue(orderedPoints.first().equals(p1));
        assertTrue(orderedPoints.last().equals(p2));
    }
}