package week3;

/** 
 * Give a point p, the following method determines whether p participates in a set of 4 or more collinear points
 * _Think of p as the origin
 * _For each other point q, determine the slope it makes with p
 * _Sort the points according to the slopes they make with p.
 * _Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p. If so, these points, together with p, are collinear.
 * 
 * The method segments() should include each maximal line segment containing 4 (or more) points exactly once. 
 * For example, if 5 points appear on a line segment in the order p→q→r→s→t, 
 * then do not include the subsegments p→s or q→t.
 * 
 * This class is using the Comparator 
 * Implement the slopeOrder() method in Point. The complicating issue is that the comparator needed to compare 
 * the slopes that two points q and r make with a third point p, which changes from sort to sort. To do this, 
 * create a private nested (non-static) class SlopeOrder that implements the Comparator<Point> interface. 
 * This class has a single method compare(q1, q2) that compares the slopes that q1 and q2 make with the 
 * invoking object p. the slopeOrder() method should create an instance of this nested class and return it.
 * Implement the sorting solution. Watch out for corner cases. Don't worry about 5 or more points on a line segment yet.
 * 
 * Using Comparator and its compare method with the Arrays.sort(temp, COMPARATOR);
 * Using Comparable and compareTo to check which point is larger, points[i].compareTo(min).
 */

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private ArrayList<LineSegment> lines;
    private Point[] p;
    private int n;

    public FastCollinearPoints(Point[] points)     
    {
	if (points == null) throw new IllegalArgumentException("null points");
	n = points.length;
	p = new Point[n];

	//check for null
        for (int i = 0; i < n; i++)
        {
            if (points[i] == null) 
        	throw new IllegalArgumentException("have any null points");
            p[i] = points[i];
        }

        //sort
        Arrays.sort(p);

        //check for duplicate
        for (int i = 0; i < n-1; i++)
        {
            if (p[i].compareTo(p[i+1]) == 0) 
        	throw new IllegalArgumentException("have any depulicate points");
        }

	lines = new ArrayList<LineSegment>();
	//copy points to temp. Temp is used to hold the points in sorted order compared to i
	Point[] temp = Arrays.copyOf(p, p.length);

	//for each point p
	for (int i = 0; i < p.length; i++)
	{
	    //sort other points by slopeorder compared to p 
	    Arrays.sort(temp, p[i].slopeOrder()); //Using Comparator and its compare

	    //min and max for start and end points of line segment
	    Point min = p[i];
	    Point max = p[i];

	    //count to keep track of collinear points found
	    int count = 2;
	    //for each other point than p
	    for (int j = 0; j < p.length - 1; j++) {
		//Check if any 3 (or more) adjacent points in the sorted order have equal 
		//slopes with respect to p. If so, these points, together with p, are collinear.
		if (p[i].slopeTo(temp[j]) == p[i].slopeTo(temp[j+1])) {
		    //find the biggest and smallest of the 3 (including p) to set min and max
		    if (temp[j+1].compareTo(max) > 0)
		    {
			max = temp[j + 1];
		    } 
		    else if (temp[j+1].compareTo(min) < 0)
		    {
			min = temp[j + 1];
		    }
		    //increment number of collinear points
		    count++;
		    //if all other points have been checked, and at least 4 points have been found,  
		    //and point p is the smallest (to make sure that the collinear points have 
		    //not been returned earlier), add linesegment
		    if (j == p.length - 2 && count >= 4 && p[i].compareTo(min) == 0)
		    {
			lines.add(new LineSegment(min, max));
		    }
		} 
		else {
		    //if all other points have been checked, check if 4 or more collinear points
		    //were found and set max and min accordingly
		    if (count >= 4 && p[i].compareTo(min) == 0) {
			//add line segment
			lines.add(new LineSegment(min, max));
		    }
		    //set max and min
		    //remember, temp[j+¡] is the slope next biggest compared to i
		    if (p[i].compareTo(temp[j+1]) > 0)
		    {
			max = p[i];
			min = temp[j+1];
			count = 2;
		    }
		    else
		    {
			max = temp[j+1];
			min = p[i];
			count = 2;
		    }
		}
	    }

	}

    }

    public int numberOfSegments() {
	// the number of line segments
	return lines.size();
    }

    public LineSegment[] segments() {
	//the line segments
	return lines.toArray(new LineSegment[numberOfSegments()]);
    }
}