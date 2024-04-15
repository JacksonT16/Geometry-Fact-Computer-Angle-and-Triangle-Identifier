package preprocessor;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.components.exception.NotInDatabaseException;
import preprocessor.delegates.ImplicitPointPreprocessor;

/**
 * @author Jackson Tedesco, Case Riddle
 * @data 4/10/2024
 */
class testImpPointPreprocesser {
	@Test
	void basticTest() throws NotInDatabaseException {
		PointDatabase data = new PointDatabase();
		List<Segment> givenSegment = new ArrayList<>();
		
		givenSegment.add(new Segment(new Point(0, 1), new Point(3, 1)));
		givenSegment.add(new Segment(new Point(2, 0), new Point(2, 3)));
		
		//not connected
		givenSegment.add(new Segment(new Point(0, 5), new Point(0, 6)));
				
		Set<Point> impPoints = ImplicitPointPreprocessor.compute(data, givenSegment);
		
		assertEquals(1 ,impPoints.size());
		assertEquals(1 ,data.size());
		
		assertThrows(NullPointerException.class, () -> {ImplicitPointPreprocessor.compute(null, null);});
	}
	
	@Test
	void endPointTest() throws NotInDatabaseException {
		PointDatabase data = new PointDatabase();
		List<Segment> givenSegment = new ArrayList<>();
		
		givenSegment.add(new Segment(new Point(0, 0), new Point(4, 0)));
		givenSegment.add(new Segment(new Point(0, 0), new Point(0, 4)));		
		givenSegment.add(new Segment(new Point(2, 0), new Point(2, 3)));
		
		Set<Point> impPoints = ImplicitPointPreprocessor.compute(data, givenSegment);
		
		assertEquals(0 ,impPoints.size());
		assertEquals(0 ,data.size());	
	}
	
	@Test
	void diagnalTest() throws NotInDatabaseException {
		PointDatabase data = new PointDatabase();
		List<Segment> givenSegment = new ArrayList<>();
		
		//x
		givenSegment.add(new Segment(new Point(0, 5), new Point(5, -5)));
		givenSegment.add(new Segment(new Point(0, -5), new Point(5, 5)));
		
		//Diagonal and horizontal 
		givenSegment.add(new Segment(new Point(0, 1), new Point(5, 1)));
		
		//Diagonal and vertical  
		givenSegment.add(new Segment(new Point(1, 5), new Point(1, -5)));
		
		Set<Point> impPoints = ImplicitPointPreprocessor.compute(data, givenSegment);
		
		assertEquals(6 ,impPoints.size());
		assertEquals(6 ,data.size());
	}
	
	@Test
	void OverlapingLineTest() throws NotInDatabaseException {
		PointDatabase data = new PointDatabase();
		List<Segment> givenSegment = new ArrayList<>();
		
		givenSegment.add(new Segment(new Point(-1, 0), new Point(1, 0)));
		givenSegment.add(new Segment(new Point(-4, 0), new Point(4, 0)));
		
		Set<Point> impPoints = ImplicitPointPreprocessor.compute(data, givenSegment);
		
		assertEquals(0 ,impPoints.size());
		assertEquals(0 ,data.size());
	}
	
	@Test
	void threeLineOnOnePointTest() throws NotInDatabaseException {
		
		PointDatabase data = new PointDatabase();
		List<Segment> givenSegment = new ArrayList<>();
		
		givenSegment.add(new Segment(new Point(-2, 0), new Point(2, 0)));
		givenSegment.add(new Segment(new Point(-2, 2), new Point(2, -2)));
		givenSegment.add(new Segment(new Point(2, -2), new Point(-2, 2)));
		
		Set<Point> impPoints = ImplicitPointPreprocessor.compute(data, givenSegment);
		
		assertEquals(1 ,impPoints.size());
		assertEquals(1 ,data.size());
	}
}