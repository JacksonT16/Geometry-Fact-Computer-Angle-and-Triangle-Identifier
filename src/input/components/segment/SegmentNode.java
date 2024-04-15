package input.components.segment;

import components.ComponentNode;
import input.components.point.PointNode;
import input.components.visitor.ComponentNodeVisitor;

/**
 * A utility class only for representing ONE segment
 * @date 1/22/2024
 * @author Jackson Tedesco
 * @author Tony Song
 * @author Case Riddle
 */
public class SegmentNode implements ComponentNode
{
	protected PointNode _point1;
	protected PointNode _point2;
	
	public PointNode getPoint1() { return _point1; }
	public PointNode getPoint2() { return _point2; }
	
	public SegmentNode(PointNode pt1, PointNode pt2)
	{
		_point1 = pt1;
		_point2 = pt2;
	}
	
	/**
	 * @return True if objects are equal; False otherwise
	 * @param obj: point node being compared 
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SegmentNode)) {
			return false;
		}
		
		if(this == obj) {
			return true;
		}
		
		SegmentNode node = (SegmentNode) obj;
		return (this.getPoint1().equals(node.getPoint1()) && this.getPoint2().equals(node.getPoint2()))
		|| (this.getPoint1().equals(node.getPoint2()) 
	     && this.getPoint2().equals(node.getPoint1()));
	}
	
	/**
	 * @return string version of SegmentNode
	 */
	public String toString() {
		return _point1.toString() + ": " + _point2.toString();
	}
	
	/**
	 * allows visitor methods to be called from SegmentNode class
	 * double dispatching
	 * @param visitor: any visitor implementing ComponentNodeVisitor
	 * @param object: any other required parameters per the visitor
	 * 
	 * from Dr. Alvin's Lab 5 Description
	 */
	@Override
	public Object accept(ComponentNodeVisitor visitor, Object o) {
		return visitor.visitSegmentNode(this, o);
	}
}