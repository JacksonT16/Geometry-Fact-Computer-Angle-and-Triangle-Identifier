package geometry_objects.points;

import java.util.List;
import java.util.Set;

import input.components.exception.NotInDatabaseException;

/**
 * Represents a bi-directional database of points. Allows you to return points and coordinates.
 * @author Case Riddle, Jackson Tedesco, Della Avent
 * @date 3/19/2024
 */
public class PointDatabase {

	protected PointNamingFactory _factory;

	public Set<Point> getPoints() { return _factory.getAllPoints(); }

	public PointDatabase() {
		_factory = new PointNamingFactory();
	}

	public PointDatabase(List<Point> points) {
		_factory = new PointNamingFactory(points);
	}

	public int size() { return _factory.size(); }

	/**
	 * Adds a point to the database.
	 * @param name
	 * @param x,y
	 * @return void
	 **/
	public void put(String name, double x, double y) { _factory.put(name, x, y); }
	
	/**
	 * Adds a point to the database.
	 * @param point
	 * @return void
	 **/
	public void put(Point p) { _factory.put(p); }

	/**
	 * Determines if a point is named based off of a specified pair of coordinates.
	 * @param x,y -- doubles defining a point (x,y)
	 * @return a string corresponding to that point, if it is named.
	 * @throws NotInDatabaseException 
	 **/
	public String getName(double x, double y) throws NotInDatabaseException {
		Point point = _factory.get(x, y);
		if (point != null && !point.isUnnamed()) { 
			return point.getName(); 
		}
		return null;
	}

	/**
	 * Searches the database for the specified point and returns the name.
	 * @param pt
	 * @return a string corresponding to the point.
	 **/
	public String getName(Point pt) {
		Set<Point> points = _factory.getAllPoints();
		for (Point point : points) {
			if (point.equals(pt)) { return point.getName(); }
		}
		return null;
	}

	/**
	 * Determines the coordinates of a point given a specified name.
	 * @param name -- a String name
	 * @return a Point object containing (x,y) corresponding to name, if it has been defined.
	 **/
	public Point getPoint(String name) {
		for (Point point : _factory.getAllPoints()) {
			if (point.getName().equals(name)) { return point; }
		}
		return null;
	}
	
	/**
	 * Returns the database object when given a point.
	 * @param pt -- a basic point
	 * @return the database entry for the point.
	 * @throws NotInDatabaseException 
	 **/
	public Point getPoint(Point pt) throws NotInDatabaseException { 
		return _factory.get(pt); 
		}

	/**
	 * Finds the database object when given a coordinate.
	 * @param x,y -- doubles defining a point (x,y)
	 * @return the database entry for the point
	 * @throws NotInDatabaseException 
	 **/
	public Point getPoint(double x, double y) throws NotInDatabaseException { 
		return _factory.get(x, y); 
		}
}
