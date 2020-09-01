package week3;
import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Insertion;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Brute force algorithm 
 * Examines 4 points at a time, and checks whether they are on the same line. 
 * To check whether the 4 points p, q, r, s are collinear, 
 * check whether slopes between p and q, between p and r, 
 * and between p and s are equal
 * 
 * This class is using the compareTo method from Comparable 
 * to throw exception
 */


public class BruteCollinearPoints {
    private ArrayList<LineSegment> lineSegments;

    public BruteCollinearPoints(Point[] points) {
	if (points == null) { throw new IllegalArgumentException(); }
	
	//check if any point in array is null or repeated
	for (int i = 0; i < points.length-1; i++) {
	    if (points[i] == null || points[i].compareTo(points[i+1]) == 0) { throw new IllegalArgumentException(); }
	    if (i == points.length-2 && points[i+1] == null) { throw new IllegalArgumentException(); }	    
	}
	
	// finds all line segments containing 4 points
	lineSegments = new ArrayList<LineSegment>();
	Arrays.sort(points);
	
	//I need 4 for loops, instead of i, i+1, i+2... because I have not sort by slope, as I will in FastCollinear
	for (int i = 0; i < points.length; i++)
	    for (int j = i + 1; j < points.length; j++)
		for (int k = j + 1; k < points.length; k++)
		    for (int l = k + 1; l < points.length; l++)
		    {
			if (points[i].slopeTo(points[j]) == points[j].slopeTo(points[k]) && points[j].slopeTo(points[k]) == points[k].slopeTo(points[l])) {
			    LineSegment line = new LineSegment(points[i], points[i+3]);
			    lineSegments.add(line);
			}
		    }
	    }
    public int numberOfSegments() {
	// the number of line segments
	return lineSegments.size();

    }
    public LineSegment[] segments() {
	// the line segments
	return lineSegments.toArray(new LineSegment[lineSegments.size()]); 
    }

    public static void main (String[] args) {
	//some points as input?
	// read the n points from a file
	In in = new In(args[0]);
	int n = in.readInt();
	Point[] points = new Point[n];
	for (int i = 0; i < n; i++) {
	    int x = in.readInt();
	    int y = in.readInt();
	    points[i] = new Point(x, y);
	}

	// draw the points
	StdDraw.enableDoubleBuffering();
	StdDraw.setXscale(0, 32768);
	StdDraw.setYscale(0, 32768);
	for (Point p : points) {
	    p.draw();
	}
	StdDraw.show();

	//measure running time
	Stopwatch stopw = new Stopwatch();

	// print and draw the line segments
	BruteCollinearPoints collinear = new BruteCollinearPoints(points);
	for (LineSegment segment : collinear.segments()) {
	    StdOut.println(segment);
	    segment.draw();
	}

	StdOut.println("Running time Brute: " + stopw.elapsedTime());        
	StdDraw.show();
	StdOut.println("Total lines: " + collinear.numberOfSegments());
    }
}