package week5;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class RuntimeTest {
    /**
     * How many nearest-neighbor calculations can 2d tree and brute force do per second?
     * Not counting load time
     * @param args
     */
    
    public static void main(String[] args) {
	// initialize the two data structures with point from file
	String filename = args[0];
	In in = new In(filename);
	PointSET brute = new PointSET();
	KdTree kdtree = new KdTree();
	while (!in.isEmpty()) {
	    double x = in.readDouble();
	    double y = in.readDouble();
	    Point2D p = new Point2D(x, y);
	    kdtree.insert(p);
	    brute.insert(p);
	}
	
	RectHV rect = new RectHV(0, 0, 1, 1);
	//2d tree
	Stopwatch watch = new Stopwatch();
	for (Point2D p : kdtree.range(rect)) {
	    kdtree.nearest(p);
	}
	StdOut.println("KdTree nearest-neighbor in file: " + filename + ".");
	double time = watch.elapsedTime();
	double calcPerSec = kdtree.size() / time;
	StdOut.println("Executed in seconds: " + time + ". Equaling " + calcPerSec + " calculations/sec.");
	
	//brute
	watch = new Stopwatch();
	for (Point2D p : brute.range(rect)) {
	    brute.nearest(p);
	}
	StdOut.println("Brute nearest-neighbor in file: " + filename + ".");
	time = watch.elapsedTime();
	calcPerSec = brute.size() / time;
	StdOut.println("Executed in seconds: " + time + ". Equaling " + calcPerSec + " calculations/sec.");
    }
}
