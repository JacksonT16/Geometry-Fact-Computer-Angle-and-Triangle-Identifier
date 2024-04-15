package input.components.visitor;

/**
 * class to create a JSONObject from a FigureNode
 * @author Jackson Tedesco, Case Riddle, Della Avent
 * @date 3/13/2024
 */

//import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONArray;

import components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

public class ToJSONvisitor implements ComponentNodeVisitor{

	/**
	 * creates a JSONObject from a FigureNode
	 * @param the figure node
	 * @param null
	 */
	@Override
	public Object visitFigureNode(FigureNode node, Object o) {
		JSONObject nodeAsJSONObj = new JSONObject();
		
		JSONObject fnJSONObj = new JSONObject();
		
		fnJSONObj.put("Description",node.getDescription());
		
		JSONArray pndArray = (JSONArray) visitPointNodeDatabase(node.getPointsDatabase(),null);
		fnJSONObj.put("Points", pndArray);
		
		JSONArray sndArray = (JSONArray) visitSegmentDatabaseNode(node.getSegments(),null);
		fnJSONObj.put("Segments", sndArray);
		
		nodeAsJSONObj.put("Figure", fnJSONObj);
		
		
		return nodeAsJSONObj;
	}

	/**
	 * creates a JSONArray with all the segment nodes from a given database
	 * @param the segment node database
	 * @param null
	 */
	@Override
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o) {
		JSONArray sndArray = new JSONArray();
		for(SegmentNode sn : node.asSegmentList()) {
			sndArray.put(visitSegmentNode(sn, null));
		}
		return sndArray;
	}

	/**
	 * creates a JSONObject from a SegmentNode
	 * @param the segment node
	 * @param null
	 */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o) {
		JSONObject snObj = new JSONObject();
		snObj.put(node.getPoint1().getName(), node.getPoint2().getName());
		return snObj;
	}

	/**
	 * creates a JSONObject from a PointNode
	 * @param the point node
	 * @param null
	 */
	@Override
	public Object visitPointNode(PointNode node, Object o) {
		JSONObject pnObj = new JSONObject();
		pnObj.put("name", node.getName());
		pnObj.put("x", node.getX());
		pnObj.put("y", node.getY());
		return pnObj;
	}

	/**
	 * creates a JSONArray with all the point nodes from a given database
	 * @param the point node database
	 * @param null
	 */
	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o) {
		JSONArray pndArray = new JSONArray();
		for (PointNode point : node.getPoints()) {
			pndArray.put(visitPointNode(point, null));
	    }
		return pndArray;
	}

}
