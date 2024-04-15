package input;
/**
* creates a FigureNode object from a given JSON file, then can create a PointDatabase and Segments from there
*
* <p>Bugs: currently untested
*
* @author Della Avent, Jackson Tedesco, Case Riddle
* @date Mar 19 2024
*/

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import components.FigureNode;
import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.builder.GeometryBuilder;
import input.components.parser.JSONParser;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

public class InputFacade
{
	/**
	 * A utility method to acquire a figure from the given JSON file:
	 *     Constructs a parser
	 *     Acquries an input file string.
	 *     Parses the file.
     *
	 * @param filepath -- the path/name defining the input file
	 * @return a FigureNode object corresponding to the input file.
	 */
	public static FigureNode extractFigure(String filepath)
	{
		JSONParser parser = new JSONParser(new GeometryBuilder());

		String figureStr = utilities.io.FileUtilities.readFileFilterComments(filepath);
	
		try {
			return (FigureNode) parser.parse(figureStr);
		} catch (Exception e) { 
			//may catch ParseException, JSONException, or NotInDatabaseException
			return null;
		}
	}
	
	/**
	 * 1) Convert the PointNode and SegmentNode objects to a Point and Segment objects 
	 *    (those classes have more meaningful, geometric functionality).
	 * 2) Return the points and segments as a pair.
     *
	 * @param fig -- a populated FigureNode object corresponding to a geometry figure
	 * @return a point database and a set of segments
	 */
	public static Map.Entry<PointDatabase, Set<Segment>> toGeometryRepresentation(FigureNode fig)
	{
		PointDatabase pd = convertToPointDatabase(fig.getPointsDatabase());
		Set<Segment> segments = convertToSegments(fig.getSegments());
		
		return Map.entry(pd, segments);		
	}

	/**
	 * Converts the given PointNodeDatabase object to a PointDatabase object
     *
	 * @param pnd -- a populated PointNodeDatabase
	 * @return PointDatabase
	 */
    private static PointDatabase convertToPointDatabase (PointNodeDatabase pnd)
    {
    	LinkedList<Point> pd = new LinkedList<Point>();
    	
    	for(PointNode pn : pnd.getPoints())
    	{
    		String name = pn.getName();
    		double x = pn.getX();
    		double y = pn.getY();
    		pd.add(new Point(name, x, y));
    	}
    	
    	return new PointDatabase(pd);
    }
    
    /**
	 * Converts the given SegmentNodeDatabase object to a set of segments
     *
	 * @param snd -- a populated SegmentNodeDatabase
	 * @return set populated with segments
	 */
    private static Set<Segment> convertToSegments (SegmentNodeDatabase snd)
    {
    	Set<Segment> segments = new LinkedHashSet<Segment>();
    	
    	for(SegmentNode s : snd.asUniqueSegmentList()) 
    	{
    		//create new Points from the SegmentNode's two PointNodes
    		Point point1 = new Point(s.getPoint1().getName(), s.getPoint1().getX(), s.getPoint1().getY());
    		Point point2 = new Point(s.getPoint2().getName(), s.getPoint2().getX(), s.getPoint2().getY());
    		
    		segments.add(new Segment(point1,point2));
    	}
    	
    	return segments;
    }
}
