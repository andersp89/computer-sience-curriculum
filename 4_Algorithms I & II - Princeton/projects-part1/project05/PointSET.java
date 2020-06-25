package week5;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * Write a data type to represent a set of points in the unit 
 * square (all points have x- and y-coordinates between 0 and 
 * 1) using a 2d-tree to support efficient range search (find 
 * all of the points contained in a query rectangle) and 
 * nearest-neighbor search (find a closest point to a query 
 * point). 2d-trees have numerous applications, ranging from 
 * classifying astronomical objects to computer animation to 
 * speeding up neural networks to mining data to image retrieval.
 * 
 * Geometric primitives
 * Use the following:
 * Point2D: represents points in the place.
 * RectHV: represents axis-aligned rectangles.
 * 
 * Brute-force implemenation
 * Write a mutable data type PointSET.java that represents a 
 * set of points in the unit square. Implement the following 
 * API by using a red–black BST.
 * 
 * Implementation requirements
 * Use either SET or java.util.TreeSet. Do not implement own red-black BST
 * 
 * Corner cases:
 * Throw an IllegalArgumentException if any argument is null. 
 * 
 * Performance requirements
 * support insert() and contains() in time proportional to the logarithm 
 * of the number of points in the set in the worst case; (Log N)
 * it should support nearest() and range() in time proportional to the 
 * number of points in the set. (N)
 */

/*
 * Represent a set of points in the unit square using a red-black BST (SET from princeton) 
 */
public class PointSET {
    //The SET class represents an ordered set of comparable keys.
    private SET<Point2D> set;
    /**
     * construct an *empty* set of points
     */
    public PointSET() {
	set = new SET<Point2D>();	
    }

    /**
     * is the set empty?
     * @return
     */
    public boolean isEmpty() {
	return set.isEmpty() == true;
    }

    /**
     * number of points in the set
     * @return
     */
    public int size() {
	return set.size(); 
    }
    
    /**
     * add the point to the set (if it is not already in the set)
     * @param p
     */
    public void insert(Point2D p) {
	if (p == null) throw new IllegalArgumentException();
	set.add(p);
    }
    
    /**
     * does the set contain point p?
     * @param p
     * @return
     */
    public boolean contains(Point2D p) {
	if (p == null) throw new IllegalArgumentException();
	return set.contains(p) == true;
    }
    
    /**
     * draw all points to standard draw
     */
    public void draw() {
	for (Point2D p : set) {
	    p.draw();
	}
    }
    
    /**
     * all points that are inside the rectangle (or on the boundary)
     * O(n)
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) { 
	if (rect == null) throw new IllegalArgumentException();
	Queue<Point2D> pFound = new Queue<Point2D>();
	for (Point2D p : set) {
	    if (rect.contains(p)) {
		pFound.enqueue(p);
	    }
	}
	return pFound;
    }
    
    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     * O(n)
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {
	if (p == null) throw new IllegalArgumentException();
	if (size() < 2) return null;
	
	double minDis = Double.MAX_VALUE;
	Point2D nearP = null;
	for (Point2D q : set) {
	    double dis = q.distanceSquaredTo(p);
	    if (dis < minDis) {
		minDis = dis;
		nearP = q;
	    }
	}
	return nearP;
    }

    public static void main(String[] args) {
	// unit testing of the methods (optional)
	PointSET ps = new PointSET();

	//insert
	ps.insert(new Point2D(0.5, 0.7));
	ps.insert(new Point2D(0.9, 0.9));
	ps.insert(new Point2D(0.6, 0.9));
	
	//isEmpty
	StdOut.println("Empty?: " + ps.isEmpty());

	//size
	StdOut.println("Size: " + ps.size());

	//draw
	//ps.draw();
	
	//nearest
	StdOut.println("Nearest point to 0.6, 0.7: " + ps.nearest(new Point2D(0.6, 0.7)));
    }
}