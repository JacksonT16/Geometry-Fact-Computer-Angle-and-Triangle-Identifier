package input.builder;
import java.util.List;
import components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

/**
 * A builder that constructs and returns elements of a geometry figure
 * @author Jackson Tedesco, Case Riddle, Della Avent
 * @date 3/11/2024
 */
public class GeometryBuilder extends DefaultBuilder{
	
	public GeometryBuilder() { }
	
	/**
	 * @return a newly created FigureNode
	 * @param description
	 * @param points
	 * @param segments
	 */
	@Override
    public FigureNode buildFigureNode(String description,
    		                          PointNodeDatabase points,
    		                          SegmentNodeDatabase segments)
    {
        return new FigureNode(description, points, segments);
    }
    
    /**
     * @return a newly created SegmentNodeDatabase
     */
    @Override 
    public SegmentNodeDatabase buildSegmentNodeDatabase()
    {
        return new SegmentNodeDatabase();
    }
    
    /**
     * adds a SegmentNode to a SegmentNodeDatabase that is created from two PointNodes 
     * @param segments: SegmentNodeDatabase added to
     * @param from: PointNode one
     * @param to: PointNode two
     */
    @Override
    public void addSegmentToDatabase(SegmentNodeDatabase segments, PointNode from, PointNode to)
    {
    	if (segments != null) segments.addUndirectedEdge(from, to);
    }
    
    /**
     * @return a segmentNode
     * @param pt1: PointNode 1
     * @param pt2: PointNode 2
     */
    @Override
    public SegmentNode buildSegmentNode(PointNode pt1, PointNode pt2)
    {
        return new SegmentNode(pt1, pt2);
    }
    
    /**
     * @return a PointNodeDatabase
     * @param points: a list of points that will make up the PointNodeDatabase
     */
    @Override
    public PointNodeDatabase buildPointDatabaseNode(List<PointNode> points)
    {
    	return new PointNodeDatabase(points);
    }
    
    /**
     * @return a PointNode
     * @param name
     * @param x coordinate 
     * @param y coordinate
     */
    @Override
    public PointNode buildPointNode(String name, double x, double y)
    {
        return new PointNode(name, x, y);
    }
}