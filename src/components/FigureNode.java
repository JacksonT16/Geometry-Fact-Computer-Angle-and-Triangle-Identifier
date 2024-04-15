package components;
import input.components.visitor.ComponentNodeVisitor;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

/**
 * A basic figure consists of points, segments, and an optional description
 * Each figure has distinct points and segments (thus unique database objects).
 * @author Jackson Tedesco, Case Riddle
 * @date 2/20/2024
 */
public class FigureNode implements ComponentNode {
	
	protected String              _description;
	protected PointNodeDatabase   _points;
	protected SegmentNodeDatabase _segments;

	public String              getDescription()    { return _description; }
	public PointNodeDatabase   getPointsDatabase() { return _points; }
	public SegmentNodeDatabase getSegments()       { return _segments; }

	public FigureNode(String description, PointNodeDatabase points, SegmentNodeDatabase segments)
	{
		_description = description;
		_points = points;
		_segments = segments;
	}

	/**
	 * allows visitor methods to be called from FigureNode class
	 * double dispatching
	 * @param visitor: any visitor implementing ComponentNodeVisitor
	 * @param object: any other required parameters per the visitor
	 * 
	 * Copied from Dr.Alvin's Lab 5 Description
	 */
	@Override
	public Object accept(ComponentNodeVisitor visitor, Object o) {
		return visitor.visitFigureNode(this, o);
	}
}