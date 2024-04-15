package input.components.parser;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import components.ComponentNode;
import components.FigureNode;
import input.builder.DefaultBuilder;
import input.builder.GeometryBuilder;
import input.components.*;
import input.components.exception.NotInDatabaseException;
import input.components.exception.ParseException;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNodeDatabase;

/**
 * This class will read a JSON data file and create an abstract syntax tree structure 
	for a geometry figure in the form of a figureNode
 * @author Jackson Tedesco, Case Riddle, Della Avent
 * @date 3/12/2024
 */
public class JSONParser
{
	protected ComponentNode  _astRoot;
	private DefaultBuilder _builder;

	public JSONParser(DefaultBuilder builder)
	{
		_astRoot = null;
		_builder = builder;
	}

	private void error(String message)
	{
		throw new ParseException("Parse error: " + message);
	}

	/**
	 * creates an FigureNode out of the inputed JSON file
	 * @param str: JSON file file in the form of a string
	 * @return abstract syntax tree structure 
		for a geometry figure in the form of a figureNode
	 * @throws ParseException
	 * @throws JSONException
	 * @throws NotInDatabaseException
	 */
	public ComponentNode parse(String str) throws ParseException, JSONException, NotInDatabaseException
	{
		if(str.equals("{}")) error("JSON is empty");// 

		// Parsing is accomplished via the JSONTokenizer class.
		JSONTokener tokenizer = new JSONTokener(str);
		JSONObject  JSONroot = ((JSONObject)tokenizer.nextValue()).getJSONObject("Figure");

		String description = getDescription(JSONroot);
		PointNodeDatabase points = getPoints(JSONroot);
		SegmentNodeDatabase segments = getSegmentDatabase(JSONroot.getJSONArray("Segments"), points);

		return _builder.buildFigureNode(description, points, segments);
	}

	/**
	 * @param JSONroot: The JSONObject
	 * @return Description from inputed JSON object
	 */
	private String getDescription(JSONObject JSONroot) {
		return  JSONroot.getString("Description");
	}

	/**
	 * @param JSONroot: The JSONObject
	 * @return PointNodeDatabase from the JSONObject
	 */
	private PointNodeDatabase getPoints(JSONObject JSONroot) {
		List<PointNode> points = new ArrayList<>();
		JSONArray pointlist = JSONroot.getJSONArray("Points");

		for(int i = 0; i < pointlist.length(); i++) {
			points.add(getPoint(pointlist.getJSONObject(i)));
		}
		
		return _builder.buildPointDatabaseNode(points);//
	}

	/**
	 * @param node: JSONObject for a singular node
	 * @return a completed point node from the JSONObject data
	 */
	private PointNode getPoint(JSONObject node) {
		String name = node.getString("name");
		double x = node.getInt("x");
		double y = node.getInt("y");

		return _builder.buildPointNode(name, x, y);
	}

	/**
	 * @param segmentList: JSONOArray of the overall segments list
	 * @param points: PointNodeDatabase that stores the points that will make up the segments
	 * @return a completed SegmentNodeDatabase from the JSONObject data
	 * @throws NotInDatabaseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 */
	private SegmentNodeDatabase getSegmentDatabase(JSONArray segmentList, PointNodeDatabase points) 
			throws NullPointerException, JSONException, NotInDatabaseException {

		SegmentNodeDatabase SNDatabase = _builder.buildSegmentNodeDatabase();//

		for(int i = 0; i < segmentList.length(); i++) {
			JSONObject subList = segmentList.getJSONObject(i);
			getSegment(subList, SNDatabase, points);
		}

		return SNDatabase;
	}

	/**
	 * Creates segments from the PointNode JSONArray from the 
	 * JSONObject segment and adds them to inputed SegmentNodeDatabase.
	 * @param segment: Object that value stores the JSONArray of points
	 * @param output: The SegmentNodeDatabase that the created segments are added too
	 * @param points: PointNodeDatabase that stores the points that will make up the segment
	 * @throws NotInDatabaseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 */
	private void getSegment(JSONObject segmentList, SegmentNodeDatabase SNDatabase, PointNodeDatabase points) 
			throws NullPointerException, JSONException, NotInDatabaseException {
		
		String headNode = segmentList.toString().substring(2, 3);
		JSONArray segmentPoints = segmentList.getJSONArray(headNode);

		for(int i = 0; i < segmentPoints.length(); i++) {
			_builder.addSegmentToDatabase(SNDatabase, points.getPoint(headNode), points.getPoint(segmentPoints.getString(i)));
		}
	}
}