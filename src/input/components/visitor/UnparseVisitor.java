package input.components.visitor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import components.FigureNode;
import input.components.point.*;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

/**
 * Implements a Visitor with the intent of building an unparsed String representation of a geometry figure.
 * @author Case Riddle, Jackson Tedesco, Della Avent
 * @date 3/14/2024
 **/
public class UnparseVisitor implements ComponentNodeVisitor {
	
	/**
	 * Appends the description, points, and segments with the correct indentation.
	 * @return null
	 * @param node: FigureNode
	 * @param o: SimpleEntry that contains a string builder and a level of indentation
	 */
	@Override
	public Object visitFigureNode(FigureNode node, Object o) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		if(level < 0) throw new IllegalArgumentException("Level is negtive number.");
		
		sb.append("Figure\n");
		
		level++;
		pair.setValue(pair.getValue() + 1);
		
		sb.append(StringUtilities.indent(level)).append("Description : \"").append(node.getDescription()).append("\"\n");

		sb.append(StringUtilities.indent(level)).append("Points:\n");
		sb.append(StringUtilities.indent(level)).append("{\n");
		visitPointNodeDatabase(node.getPointsDatabase(), o);
		sb.append(StringUtilities.indent(level)).append("}\n");

		sb.append(StringUtilities.indent(level)).append("Segments:\n");
		sb.append(StringUtilities.indent(level)).append("{\n");
		visitSegmentDatabaseNode(node.getSegments(), o);
		sb.append(StringUtilities.indent(level)).append("}\n");

		return null;
	}
	
	/**
	 * Uses the unparse method from SegmentNodeDatabase.
	 * @return null
	 * @param node: SegmentNodeDatabase
	 * @param o: SimpleEntry that contains a string builder and a level of indentation
	 */
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		if(level < 0) throw new IllegalArgumentException("Level is negtive number.");

		List <SegmentNode> SND = node.asSegmentList();
		ArrayList<PointNode> completedKeys = new ArrayList<>();

		for(SegmentNode key: SND) {
			
			//test if PointNode 1 has been append. If so this SegmentNode will be skipped   
			if(!completedKeys.contains(key.getPoint1())) {
				
				sb.append(StringUtilities.indent(level));
				sb.append(key.getPoint1().getName() +": ");

				for (SegmentNode value: SND) {
					if(value.getPoint1().equals(key.getPoint1())) {
						
						visitSegmentNode(value, o);
						completedKeys.add(value.getPoint1());
					}
				}
				sb.append("\n");
			}

		}
		return null;
	}
	
	/**
	 * Unparses the SegmentNode and inputs it into the string builder.
	 * @return null
	 * @param node: SegmentNode unparsed
	 * @param o: SimpleEntry that contains a string builder and a level of indentation
	 */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();

		sb.append(node.getPoint2().getName() + " ");

		return null;
	}


	/**
	 * Appends the string representation of each point in a PointNodeDatabase to a StringBuilder.
	 * @return null
	 * @param node: PointNodeDatabase
	 * @param o: SimpleEntry that contains a string builder and a level of indentation
	 */
	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		pair.setValue(pair.getValue() + 1);

		for (PointNode point : node.getPoints()) {
			visitPointNode(point, o);
		}
		
		return null;
	}
	
	/**
	 * Appends PointNode with the correct indentation.
	 * @return null
	 * @param node: PointNode
	 * @param o: SimpleEntry that contains a string builder and a level of indentation
	 */
	@Override
	public Object visitPointNode(PointNode node, Object o) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();

		if(level < 0) throw new IllegalArgumentException("Level is negtive number.");

		sb.append(StringUtilities.indent(level));
		sb.append("Point " + node.toString() +"\n");

		return null;
	}

}