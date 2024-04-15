package geometry_objects.points;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import input.components.exception.NotInDatabaseException;
/**
 * Stores Point classes and gives them a name from A > Z every time the Point lacks a name.
 * Once Z is past it will return the beginning, but add another letter(AA, AAA, AAAA).
 * @author Jackson Tedesco, Case Riddle, Della Avent
 * @data 3/24/2024
 */

/*
 * Given a pair of coordinates; generate a unique name for it;
 * return that point object.
 *
 * Names go from A..Z..AA..ZZ..AAA...ZZZ  (a name such as ABA does not occur)
 * 
 */
public class PointNamingFactory
{
	// Prefix associated with each generated name so those names are easily distinguishable
	private static final String _PREFIX = "*_";

	// Constants reflecting our naming characters for generated names.
	private static final char START_LETTER = 'A';
	private static final char END_LETTER = 'Z';

	//
	// the number of characters in the generated names:
	// "A" and 1 -> "A"
	// "B" and 3 -> "BBB"
	//
	private String _currentName = "A";
	private int _numLetters = 1;

	//
	// A hashed container for the database of points; this requires the Point
	// class implement equals based solely on the individual coordinates and
	// not a name. We need a get() method; HashSet doesn't offer one.
	// Each entry is a <Key, Value> pair where Key == Value
	//
	protected Map<Point, Point> _database;

	public PointNamingFactory()
	{
		_database = new LinkedHashMap<>();
	}

	/**
	 * Initialize the database with points; must call put() to ensure all points are named
	 *
	 * @param points -- a list of points, named or not named
	 */
	public PointNamingFactory(List<Point> points)
	{
		this();
		for(Point _point: points) {
			put(_point);
		}
	}

	/**
	 * Overloaded add / lookup mechanism for this database.
	 *
	 * @param pt -- a Point object (may or may not be named)

	 * @return THE point object in the database corresponding to its coordinate pair
	 * the object in the database if it already exists or
	 * a completely new point that has been added to the database
	 */
	public Point put(Point pt)
	{
		if(pt == null) throw new NullPointerException();
		
		if(_database.containsKey(pt)) {
			if(isGeneratedName(_database.get(pt).getName())) {
				_database.remove(pt);
			}
			else {
				return _database.get(pt);
			}
		}
		
		if(pt.isUnnamed() || isGeneratedName(pt.getName())) {
			pt._name = getCurrentName();
		}
		
		//checks for repeat names
		for(Point _point: _database.keySet()) {
			if(_point.getName().equals(pt.getName())) {
				pt._name = getCurrentName();
			}
		}

		_database.put(pt, pt);
		return pt;
	}

	/**
	 * Overloaded add / lookup mechanism for this database for an unnamed coordinate pair.
	 *
	 * @param x -- single coordinate
	 * @param y -- single coordinate

	 * @return THE point object in the database corresponding to its coordinate pair
	 * the object in the database if it already exists or
	 * a completely new point that has been added to the database (with generated name)
	 */
	public Point put(double x, double y)
	{
		return put(new Point(x, y));
	}

	/**
	 * The 'main' overloaded add / lookup mechanism for this database.
	 * 
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * 
	 * @return a point (if it already exists in the database) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *            e.g., a valid name cannot overwrite an existing valid name ;
	 *                  a generated name cannot be overwritten by another generated name
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	public Point put(String name, double x, double y)
	{
		return put(new Point(name, x, y));
	}
	
	/**
	 * Determines if a name is a generated name
	 * @param name: name tested
	 * @return True if generated name; False if not
	 */
	private boolean isGeneratedName(String name) {
		if(name.length() > 1) {
			if(name.substring(0, 2).equals(_PREFIX)) return true;
		}
		
		return false;
	}

	/**
	 * Strict access (read-only of the database)
	 * 
	 * @param x
	 * @param y
	 * @return stored database Object corresponding to (x, y) 
	 * @throws NotInDatabaseException 
	 */
	public Point get(double x, double y) throws NotInDatabaseException
	{
		return get(new Point(x, y));
	}	

	/**
	 * @param pt: point to be retrieved 
	 * @return point equivalent to pt in the PointNamingFactory
	 * @throws NotInDatabaseException 
	 */
	public Point get(Point pt) throws NotInDatabaseException
	{
		if(pt == null) throw new NullPointerException();
		
		if(contains(pt)) return _database.get(pt);

		throw new NotInDatabaseException(); 
	}

	/**
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return simple containment; no updating
	 */
	public boolean contains(double x, double y) { return contains(new Point(x, y)); }
	
	/**
	 * @param p: Point being tested
	 * @return True if PointNamingFactory contains p; False if not
	 */
	public boolean contains(Point p) { return _database.containsKey(p); }

	/**
	 * Constructs the next (complete with prefix) generated name.
	 * Names should be of the form PREFIX + current name
	 *
	 * This method should also invoke updating of the current name
	 * to reflect the 'next' name in the sequence.
	 *	 
	 * @return the next complete name in the sequence including prefix.
	 */
	private String getCurrentName()
	{
		String tmpName = _PREFIX + _currentName.repeat(_numLetters);
		updateName();
		
		return tmpName;
	}

	/**
	 * Advances the current generated name to the next letter in the alphabet:
	 * 'A' -> 'B' -> 'C' -> 'Z' --> 'AA' -> 'BB'
	 */
	private void updateName()
	{
		if(_currentName.equals("" + END_LETTER)) {
			
			_currentName = "" + START_LETTER;
			_numLetters++;
			
		}else {
			char charName = _currentName.charAt(0);

			charName++;
			_currentName = "" + charName;
		}
	}

	/**
	 * @return The entire database of points in a Set.
	 */
	public  Set<Point> getAllPoints()
	{
		return _database.keySet();
	}

	/**
	 * clears the PointNamingFactory
	 */
	public void clear() { _database.clear(); }

	/**
	 * @return size of PointNamingFactory
	 */
	public int size() { return _database.size(); }

	/**
	 * @return String version of PointNamingFactory
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for(Point pt: _database.keySet()) {
			sb.append(pt.getName() + "(" + pt.getX() + ", " + pt.getY() + ")\n");
		}

		return sb.toString();
	}
}