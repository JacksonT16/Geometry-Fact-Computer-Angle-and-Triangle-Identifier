package preprocessor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import components.FigureNode;
import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.InputFacade;
import input.components.exception.NotInDatabaseException;
import preprocessor.delegates.ImplicitPointPreprocessor;

/**
 * @author Jackson Tedesco, Case Riddle
 * @date4/10/2024
 */
class PreprocessorTest
{
	@Test
	void test_implicit_crossings() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("fully_connected_irregular_polygon.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(5, iPoints.size());

		System.out.println(iPoints);

		//
		//
		//		               D(3, 7)
		//
		//
		//   E(-2,4)       D*      E*
		//		         C*          A*       C(6, 3)
		//                      B*
		//		       A(2,0)        B(4, 0)
		//
		//		    An irregular pentagon with 5 C 2 = 10 segments

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertTrue(iPoints.contains(a_star));
		assertTrue(iPoints.contains(b_star));
		assertTrue(iPoints.contains(c_star));
		assertTrue(iPoints.contains(d_star));
		assertTrue(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(15, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertTrue(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("A")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(15, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void testNoImplictPointsAndNoNonminimalSegment() throws NotInDatabaseException {
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);
		points.put("C", 3, 3);
		points.put("D", 3, 0);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		segments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		segments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		segments.add(new Segment(points.getPoint("D"), points.getPoint("A")));

		Preprocessor pp = new Preprocessor(points, segments);

		Set<Segment> baseSegments = pp.computeImplicitBaseSegments(new HashSet<Point>());

		assertEquals(0, baseSegments.size());
		assertFalse(baseSegments.contains(new Segment(points.getPoint("A"), new Point(0, 2))));

		Set<Segment> minSegs = pp.identifyAllMinimalSegments(new HashSet<Point>(), segments, baseSegments);

		assertEquals(4, minSegs.size());
		assertTrue(minSegs.contains(new Segment(points.getPoint("A"), points.getPoint("B"))));
		assertTrue(minSegs.contains(new Segment(points.getPoint("B"), points.getPoint("C"))));
		assertTrue(minSegs.contains(new Segment(points.getPoint("C"), points.getPoint("D"))));
		assertTrue(minSegs.contains(new Segment(points.getPoint("D"), points.getPoint("A"))));
		assertFalse(minSegs.contains(new Segment(points.getPoint("A"), points.getPoint("C"))));

		Set<Segment> nonMinSeg = pp.constructAllNonMinimalSegments(minSegs);

		assertEquals(0, nonMinSeg.size());
		assertFalse(nonMinSeg.contains(new Segment(points.getPoint("A"), points.getPoint("B"))));

	}

	@Test
	void testSeperateCollinearsegments() throws NotInDatabaseException{
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);
		points.put("C", 0, 5);
		points.put("D", 0, 8);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		segments.add(new Segment(points.getPoint("C"), points.getPoint("D")));

		Preprocessor pp = new Preprocessor(points, segments);

		Set<Segment> baseSegments = pp.computeImplicitBaseSegments(new HashSet<Point>());

		assertEquals(0, baseSegments.size());

		Set<Segment> minSegs = pp.identifyAllMinimalSegments(new HashSet<Point>(), segments, baseSegments);

		assertEquals(2, minSegs.size());
		assertTrue(minSegs.contains(new Segment(points.getPoint("A"), points.getPoint("B"))));
		assertTrue(minSegs.contains(new Segment(points.getPoint("C"), points.getPoint("D"))));
		assertFalse(minSegs.contains(new Segment(points.getPoint("A"),points.getPoint("C"))));

		Set<Segment> nonMinSeg = pp.constructAllNonMinimalSegments(minSegs);

		assertEquals(0, nonMinSeg.size());
		assertFalse(minSegs.contains(new Segment(points.getPoint("A"),points.getPoint("D"))));
		assertFalse(minSegs.contains(new Segment(points.getPoint("A"),points.getPoint("C"))));
		assertFalse(minSegs.contains(new Segment(points.getPoint("B"),points.getPoint("D"))));	
	}

	@Test
	void decemalTest() throws NotInDatabaseException {
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);
		points.put("C", 0, 3.001);
		points.put("D", 0, 8);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		segments.add(new Segment(points.getPoint("C"), points.getPoint("D")));

		Preprocessor pp = new Preprocessor(points, segments);

		Set<Segment> baseSegments = pp.computeImplicitBaseSegments(new HashSet<Point>());

		assertEquals(0, baseSegments.size());

		Set<Segment> minSegs = pp.identifyAllMinimalSegments(new HashSet<Point>(), segments, baseSegments);

		assertEquals(2, minSegs.size());

		Set<Segment> nonMinSeg = pp.constructAllNonMinimalSegments(minSegs);

		assertEquals(0, nonMinSeg.size());
	}

	@Test
	void TestComputeImplicitBaseSegmentsWithVertexInput() throws NotInDatabaseException {
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));

		Preprocessor pp = new Preprocessor(points, segments);

		Set<Point> vertexPoints = new HashSet<>();
		vertexPoints.add(new Point(0, 0));
		vertexPoints.add(new Point(0, 3));

		Set<Segment> baseSegments = pp.computeImplicitBaseSegments(vertexPoints);

		assertEquals(0, baseSegments.size());
	}


	@Test
	void nullTest() throws NotInDatabaseException {
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);
		points.put("C", 3, 3);
		points.put("D", 3, 0);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		segments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		segments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		segments.add(new Segment(points.getPoint("D"), points.getPoint("A")));

		Preprocessor pp = new Preprocessor(points, segments);

		Set<Segment> baseSegments = pp.computeImplicitBaseSegments(new HashSet<Point>());

		assertThrows(NullPointerException.class, () -> {pp.computeImplicitBaseSegments(null);});

		assertThrows(NullPointerException.class, () -> {pp.identifyAllMinimalSegments(null, segments, baseSegments);});
		assertThrows(NullPointerException.class, () -> {pp.identifyAllMinimalSegments(new HashSet<Point>(), null, baseSegments);});
		assertThrows(NullPointerException.class, () -> {pp.identifyAllMinimalSegments(new HashSet<Point>(), segments, null);});

		assertThrows(NullPointerException.class, () -> {pp.constructAllNonMinimalSegments(null);});
	}

	@Test
	void nonMinimalTest() throws NotInDatabaseException {
		PointDatabase points = new PointDatabase();
		points.put("A", 0, 0);
		points.put("B", 0, 3);
		points.put("C", 0, 6);
		points.put("D", 0, 9);
		points.put("E", .5, 9);

		Set<Segment> segments = new HashSet<>();
		segments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		segments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		segments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		segments.add(new Segment(points.getPoint("D"), points.getPoint("E")));

		Preprocessor pp = new Preprocessor(points, segments);


		Set<Segment> nonMinimal = pp.constructAllNonMinimalSegments(segments);
		assertEquals(3, nonMinimal.size());

		assertTrue(nonMinimal.contains(new Segment(points.getPoint("A"), points.getPoint("C"))));
		assertTrue(nonMinimal.contains(new Segment(points.getPoint("A"), points.getPoint("D"))));
		assertTrue(nonMinimal.contains(new Segment(points.getPoint("B"), points.getPoint("D"))));

		assertFalse(nonMinimal.contains(new Segment(points.getPoint("A"), points.getPoint("B"))));
		assertFalse(nonMinimal.contains(new Segment(points.getPoint("B"), points.getPoint("C"))));
		assertFalse(nonMinimal.contains(new Segment(points.getPoint("C"), points.getPoint("D"))));
		assertFalse(nonMinimal.contains(new Segment(points.getPoint("D"), points.getPoint("E"))));
	}

	@Test
	void test_arrow_pointing_right() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("arrow_pointing_right.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//		            C\
		//		            | \
		//A-----------------B  \
		//|                     \
		//|					     D
		//|					    /
		//G-----------------F  /
		//		            | /
		//		            E/
		//

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));


		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 7 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("F")));
		expectedMinimalSegments.add(new Segment(points.getPoint("F"), points.getPoint("G")));
		expectedMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("A")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();

		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_colinear_line_segments() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("collinear_line_segments.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//
		//
		//	    A----B-----C--D-----E----------F

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("F")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 10 non-minimal segments.
		//
		assertEquals(10, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//A----B-----C--D-----E----------F
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("E")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("F")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("F")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("F")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("F")));



		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_crossing_symmetric_triangle() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("crossing_symmetric_triangle.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(1, iPoints.size());

		System.out.println(iPoints);

		//	    "Comment" : "  A                                 
		//      / \                                
		//     B___C                               
		//    / \ / \                              
		//   /   X   \  X is not a specified point (it is implied) 
		//  D_________E  

		Point a_star = new Point(3, 3);


		assertTrue(iPoints.contains(a_star));

		//
		// There are 4 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(4, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("B"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("D"), a_star));
		expectedISegments.add(new Segment(points.getPoint("E"), a_star));


		for (Segment iSegment : iSegments)
		{
			assertTrue(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.


		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("D")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);

		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		//
		assertEquals(4, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 4 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("E")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("C")));



		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_divided_square() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("divided_square.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 0 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		// A------B
		// |    / |
		// |  /   |
		// D------C

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 0 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 5 in this figure.
		//

		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("A")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("B")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(0, computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_equilateral_triange() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("equilateral_triange.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 0 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);



		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 0 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 3 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("A")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(0, computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_four_point_star() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("four_point_star.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 0 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//      A
		//   
		//   B     C
		//D               G
		//   E     F
		//     
		//      H  

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 0 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 8 in this figure.
		//

		//      A
		//   
		//   B     C
		//D               G
		//   E     F
		//     
		//      H  
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("H"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("H"), points.getPoint("F")));
		expectedMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("F")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(0, computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_grid() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("grid.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 0 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//	    A---B---C
		//	    |   |   |
		//	    D---E---F
		//	    |   |   |
		//	    G---H---I    

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 0 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 12 in this figure.
		//

		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));

		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("F")));

		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("F")));

		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("G")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("H")));
		expectedMinimalSegments.add(new Segment(points.getPoint("F"), points.getPoint("I")));

		expectedMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("H")));
		expectedMinimalSegments.add(new Segment(points.getPoint("H"), points.getPoint("I")));


		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);


		//	    A---B---C
		//	    |   |   |
		//	    D---E---F
		//	    |   |   |
		//	    G---H---I 
		//
		// All Segments will consist of the new 6 non-minimal segments.
		//
		assertEquals(6, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("F")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("I")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("G")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("H")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("I")));

		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_single_triangle() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("single_triangle.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);


		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("A")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(0, computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_square_four_interior_triangle() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("square_four_interior_triangle.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 0 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(1, iPoints.size());

		System.out.println(iPoints);

		// A------B
		// | \  / |
		// |      |
		// |/    \|
		// D------C

		Point a_star = new Point(2.5, -2.5);


		assertTrue(iPoints.contains(a_star));


		//
		// There are 0 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(4, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), a_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("D"), a_star));


		for (Segment iSegment : iSegments)
		{
			assertTrue(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 8 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));


		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(2, computedNonMinimalSegments.size());
		// A------B
		// | \  / |
		// |      |
		// |/    \|
		// D------C



		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));


		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_triangle_with_three_triangles_inside() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("triangle_with_three_triangles_inside.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//		    A
		//		   / \
		//		  B___C
		//		 / \ / \
		//		D___E___F
		//	   / \ / \ / \
		//	  I___G___H___J

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}



		// Ensure we have ALL minimal segments: 19 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);

		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("G")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("H")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("F")));

		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("F")));

		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("I")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("G")));
		expectedMinimalSegments.add(new Segment(points.getPoint("F"), points.getPoint("H")));
		expectedMinimalSegments.add(new Segment(points.getPoint("F"), points.getPoint("J")));

		expectedMinimalSegments.add(new Segment(points.getPoint("I"), points.getPoint("G")));
		expectedMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("H")));
		expectedMinimalSegments.add(new Segment(points.getPoint("H"), points.getPoint("J")));


		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(12, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("I")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("I")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("F")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("J")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("J")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("I"), points.getPoint("H")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("I"), points.getPoint("J")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("G"), points.getPoint("J")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("F")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("H")));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("G")));
		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

	@Test
	void test_two_separate_triangles() throws NotInDatabaseException
	{
		FigureNode fig = InputFacade.extractFigure("two_separate_triangles.json");

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(0, iPoints.size());

		System.out.println(iPoints);

		//      A        D
		//     / \      / \
		//    B---C    E---F   

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);

		assertFalse(iPoints.contains(a_star));
		assertFalse(iPoints.contains(b_star));
		assertFalse(iPoints.contains(c_star));
		assertFalse(iPoints.contains(d_star));
		assertFalse(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(0, iSegments.size());



		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertFalse(expectedISegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		 
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("A")));
		
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("F")));
		expectedMinimalSegments.add(new Segment(points.getPoint("F"), points.getPoint("D")));

		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);


		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}

		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);

		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(0, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));

		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));

		//
		// Check size and content equality
		//
		assertEquals(0, computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertFalse(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}

}