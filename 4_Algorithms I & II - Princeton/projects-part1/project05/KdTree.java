package week5;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * 2d-tree implementation
 * Write a mutable data type KdTree.java that uses a 2d-tree to implement 
 * the same API (but replace PointSET with KdTree). A 2d-tree is a 
 * generalization of a BST to two-dimensional keys. The idea is to build 
 * a BST with points in the nodes, using the x- and y-coordinates of the 
 * points as keys in strictly alternating sequence.
 * 
 * Search and insert. 
 * The algorithms for search and insert are similar to those for BSTs, 
 * but at the root we use the x-coordinate (if the point to be inserted 
 * has a smaller x-coordinate than the point at the root, go left; 
 * otherwise go right); then at the next level, we use the y-coordinate 
 * (if the point to be inserted has a smaller y-coordinate than the point 
 * in the node, go left; otherwise go right); then at the next level the 
 * x-coordinate, and so forth.
 * 
 * Draw. 
 * A 2d-tree divides the unit square in a simple way: all the points to
 * the left of the root go in the left subtree; all those to the right go
 * in the right subtree; and so forth, recursively. Your draw() method 
 * should draw all of the points to standard draw in black and the 
 * subdivisions in red (for vertical splits) and blue (for horizontal 
 * splits). This method need not be efficient—it is primarily for 
 * debugging.
 *  
 * The prime advantage of a 2d-tree over a BST is that it supports 
 * efficient implementation of range search and nearest-neighbor search.
 * Each node corresponds to an axis-aligned rectangle in the unit square, 
 * which encloses all of the points in its subtree. The root corresponds 
 * to the unit square; the left and right children of the root corresponds
 * to the two rectangles split by the x-coordinate of the point at the 
 * root; and so forth.
 * 
 * Range search. 
 * To find all points contained in a given query rectangle, 
 * start at the root and recursively search for points in both subtrees 
 * using the following pruning rule: if the query rectangle does not 
 * intersect the rectangle corresponding to a node, there is no need to 
 * explore that node (or its subtrees). A subtree is searched only if it 
 * might contain a point contained in the query rectangle.
 * 
 * Nearest-neighbor search. 
 * To find a closest point to a given query point,
 * start at the root and recursively search in both subtrees using the 
 * following pruning rule: if the closest point discovered so far is 
 * closer than the distance between the query point and the rectangle 
 * corresponding to a node, there is no need to explore that node 
 * (or its subtrees). That is, search a node only only if it might 
 * contain a point that is closer than the best one found so far. 
 * The effectiveness of the pruning rule depends on quickly finding a 
 * nearby point. To do this, organize the recursive method so that when 
 * there are two possible subtrees to go down, you always choose the 
 * subtree that is on the same side of the splitting line as the query 
 * point as the first subtree to explore—the closest point found while 
 * exploring the first subtree may enable pruning of the second subtree.
 * 
 */

public class KdTree {
    private Node root;
    private static final RectHV CANVAS = new RectHV(0, 0, 1, 1); //canvas for points
    private int count; //total nodes

    public KdTree() {
	this.count = 0;
	this.root = null;
    }
    
    /**
     * Node for points in 2d Tree
     * @author anderspedersen
     *
     */
    private static class Node {
	private Point2D p; //the point
	private RectHV rect; // the axis-aligned rectangle corresponding to this node
	private Node lb; // the left/bottom subtree
	private Node rt; // the right/top subtree
	private boolean coordinate; //if true order by x, if false order by y

	public Node(Point2D p, boolean coordinate, RectHV rect) {
	    this.p = p;
	    this.coordinate = coordinate;
	    this.rect = rect;
	}

	public int compareTo(Point2D that) {
	    if (coordinate) {
		if (p.x() < that.x()) return -1;
		else if (p.x() > that.x()) return 1;
	    } else {
		if (p.y() < that.y()) return -1;
		else if (p.y() > that.y()) return 1;
	    }
	    return 0;
	}

    }
    /**
     * is the set empty?
     * @return
     */
    public boolean isEmpty() {
	return root == null;
    }
    
    /**
     * number of points in the set
     * @return
     */
    public int size() {
	return count;
    }

    /**
     * add the point to the set if not already there.
     * O(log n)
     * @param p
     */
    public void insert (Point2D p) {
	if (p == null) throw new IllegalArgumentException();
	root = insert(root, p, true, CANVAS);
    }

    /**
     * create new node for the point
     * @param n
     * @param p
     * @param coordinate
     * @param rect
     * @return
     */
    private Node insert(Node n, Point2D p, boolean coordinate, RectHV rect) {
	if (n == null) {
	    count++;
	    return new Node(p, coordinate, rect);
	}   
	//base case, if point p is the same as the point 
	//in the node, then point p has been inserted 
	if (p.equals(n.p)) return n;
	//compare n with p, as it has defined compareTo
	//so, if n > p, then p < n, and hence p must go left of n.
	if (n.compareTo(p) > 0) {
	    n.lb = insert(n.lb, p, !coordinate, childRect(n, true));
	} else {
	    n.rt = insert(n.rt, p, !coordinate, childRect(n, false));
	}
	//return the first point
	return n;
    }

    //helper to insert()
    private RectHV childRect(Node n, boolean leftBottom) {
	RectHV rect;
	//point to the rectangle of the prior node
	RectHV temp = n.rect;
	//if this node is left of the last node
	if (leftBottom) {
	    //if rectangle is already defined for node, return that
	    if (n.lb != null) {
		return n.lb.rect;
	    }
	    //draw new rectangle at left side, from bottom to this piont's x or y coordinate 
	    if (n.coordinate) {
		rect = new RectHV(temp.xmin(), temp.ymin(), n.p.x(), temp.ymax());
	    } else {
		rect = new RectHV(temp.xmin(), temp.ymin(), temp.xmax(), n.p.y());
	    }
	} else {
	    if (n.rt != null) {
		return n.rt.rect;
	    }
	    //draw new rectangle at right side, from this point's x or y coordinate to the top
	    if (n.coordinate) {	
		rect = new RectHV(n.p.x(), temp.ymin(), temp.xmax(), temp.ymax());
	    } else {
		rect = new RectHV(temp.xmin(), n.p.y(), temp.xmax(), temp.ymax());
	    }
	}
	return rect;
    }

    /**
     * is point already there?
     * O(log n)
     */
    public boolean contains(Point2D p) {
	// does the set contain point p?
	if (p == null) throw new IllegalArgumentException();
	return get(p, root) != null;
    }


    //helper to contains()
    private Node get(Point2D p, Node n) {
	//if empty
	if (n == null) {
	    return null;
	}
	//base case, if point p is the same at the point
	//in the node, then point p has been found
	if (n.p.equals(p)) {
	    return n;
	}
	//if n > p, p < n
	if (n.compareTo(p) > 0) {
	    return get(p, n.lb);
	} else {
	    return get(p, n.rt);
	}

    }

    /**
     * Draw the points with lines in the canvas
     */
    public void draw() {
	draw(root);
    }
    
    //helper to draw()
    private void draw(Node n) {
	//base case, when all nodes have been drawn
	if (n == null) return;
	draw(n.lb);
	StdDraw.setPenColor(StdDraw.BLACK);
	StdDraw.setPenRadius(0.01);
	n.p.draw();
	StdDraw.setPenRadius();	
	// if sort by x coordinate, then draw line horizontally, else vertically 
	if (n.coordinate) {
	    StdDraw.setPenColor(StdDraw.RED);
	    StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
	} else {
	    StdDraw.setPenColor(StdDraw.BLUE);
	    StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
	}
	draw(n.rt);
    }

    /**
     * all points inside the rectangle or at its boundary
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) {
	if (rect == null) throw new IllegalArgumentException();
	SET<Point2D> set = new SET<>();
	range(set, rect, root);
	return set;
    }
    
    //helper for range(RectHV rect)
    private void range(SET<Point2D> set, RectHV rect, Node n) {
	//base case, if no more nodes or the rect does not intersect
	if (n == null || !n.rect.intersects(rect)) return;
	//look if this rectangle is within (smaller than) either former points x or y
	//true only if the former point was also ordered according to the point within range, either x or y
	boolean left = (n.coordinate && rect.xmin() < n.p.x()) || (!n.coordinate && rect.ymin() < n.p.y());
	//look if this rectangle is within (larger than) points x or y 
	//true only if the former point was also ordered according to the point within range, either x or y
	boolean right = (n.coordinate && rect.xmax() >= n.p.x() || (!n.coordinate && rect.ymax() >= n.p.y()));
	//search left
	if (left) {
	    range(set, rect, n.lb);
	}
	//if contains, add
	if(rect.contains(n.p)) {
	    set.add(n.p);
	}
	//search right
	if(right) {
	    range(set, rect, n.rt);
	}
    }
    
    /**
     * return nearest neighbor to point p in the set
     * null if set is empty
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {
	if (p == null) throw new IllegalArgumentException();
	if (isEmpty()) return null;
	
	//nearest point
	Point2D nearP = null;
	//initialize to some value, max_value
	double min = Double.MAX_VALUE;
	Queue<Node> queue = new Queue<>();
	queue.enqueue(root);
	while(!queue.isEmpty()) {
	    Node x = queue.dequeue();
	    //compare squared distances to avoid expensive operation of squaring them 
	    double dis = p.distanceSquaredTo(x.p);
	    //update nearest point if dist is lower than min
	    if (dis < min) {
		nearP = x.p;
		min = dis;
	    }
	    //look left, if not empty and less than current min (root)
	    if (x.lb != null && x.lb.rect.distanceSquaredTo(p) < min) {
		queue.enqueue(x.lb);
	    }
	    //look right, if not empty and less than current min (root)
	    if (x.rt != null && x.rt.rect.distanceSquaredTo(p) < min) {
		queue.enqueue(x.rt);
	    }
	}
	return nearP;
    }
    
    public static void main(String[] args) {
	//insert	
	Point2D p1 = new Point2D(0.5, 0.5);
	Point2D p2 = new Point2D(0.7, 0.5);
	Point2D p3 = new Point2D(0.8, 0.4);
	KdTree kdt = new KdTree();
	kdt.insert(p1);
	kdt.insert(p2);
	kdt.insert(p3);
	kdt.insert(new Point2D(0.1, 0.2));
	kdt.insert(new Point2D(0.3, 0.9));

	//contains
	boolean contains = kdt.contains(p3);
	StdOut.println("Does set contain point: " + p1 + "? " + contains);
	
	//draw
	kdt.draw();
        StdDraw.show();
        
        //what points in rectangle, should return two points, p2, p3
        RectHV rect = new RectHV(0.6, 0.4, 0.9, 0.6);
        StdOut.print("Points in rectangle " + rect + " are the following: ");
        for (Point2D p : kdt.range(rect)) {
            StdOut.print(" " + p + " ");
        }
        StdOut.println();
        
        //nearest
        StdOut.println("What is the nearest point to " + p2 + "?:" + kdt.nearest(p2));
        
    }
}
