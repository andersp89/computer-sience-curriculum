package week1;
import java.util.Arrays;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Shortest ancestral path. 
 * An ancestral path between two vertices v and w in a digraph 
 * is a directed path from v to a common ancestor x, together
 * with a directed path from w to the same ancestor x. 
 * 
 * A shortest ancestral path is an ancestral path of minimum 
 * total length. We refer to the common ancestor in a shortest 
 * ancestral path as a shortest common ancestor. 
 * Note also that an ancestral path is a path, but not a 
 * directed path.
 * 
 * We generalize the notion of shortest common ancestor to
 * subsets of vertices. A shortest ancestral path of two 
 * subsets of vertices A and B is a shortest ancestral path 
 * over all pairs of vertices v and w, with v in A and w in B. 
 * 
 * @author anderspedersen
 *
 */

public class SAP {

    private final Digraph graph;
    
    /**
     * Private class to calculate the SAP
     * 1) Do bfs for v and w, respectively, and check for hasPath and distTo,
     * to find the shortest path
     * @author anderspedersen
     *
     */
    private class ComputeSAP {
	private final BreadthFirstDirectedPaths vbfs, wbfs;
	private int ancestor = -1; //common ancestor of vertex v and w
	private int length = -1; //shortest path between vertex v and w

	public ComputeSAP(Digraph graph, int v, int w) {
	    //create lists of size v and w to pass into bfdp class
	    this(graph, Arrays.asList(v), Arrays.asList(w));
	}

	public ComputeSAP(Digraph graph, Iterable<Integer> v, Iterable<Integer> w) {
	    checkVertices(v);
	    checkVertices(w);
	    
	    //do bfs for v and w
	    vbfs = new BreadthFirstDirectedPaths(graph, v);
	    wbfs = new BreadthFirstDirectedPaths(graph, w);

	    int min = Integer.MAX_VALUE;
	    
	    for (int i = 0; i < graph.V(); i++)
		//find common ancestor that has path, and that are are closets
		if (hasPathTo(i) && distTo(i) < min) {
		    ancestor = i;
		    min = distTo(i);
		}
	    
	    if (ancestor != -1)
		length = min;
	}
	
	//check that vertex are not null and in range
	private void checkVertices(Iterable<Integer> vertices) {
	    if (vertices == null)
		throw new java.lang.NullPointerException();

	    for (int vertex: vertices) {
		if (vertex < 0 || vertex > graph.V() - 1)
			throw new java.lang.IndexOutOfBoundsException();
	    }
	}

	public int getAncestor() {
	    return ancestor;
	}

	public int getLength() {
	    return length;
	}

	private int distTo(int i) {
	    return vbfs.distTo(i) + wbfs.distTo(i);
	}

	private boolean hasPathTo(int i) {
	    return vbfs.hasPathTo(i) && wbfs.hasPathTo(i);
	}
    }

    /**
     * constructor takes a digraph (not necessarily a DAG)
     * @param G
     */
    public SAP(Digraph G) {
	if (G == null)
	    throw new java.lang.NullPointerException();
	graph = new Digraph(G);
    }

    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(int v, int w) {
	return new ComputeSAP(graph, v, w).getLength();
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
     */
    public int ancestor(int v, int w) {
	return new ComputeSAP(graph, v, w).getAncestor();
    }

    /**
     * length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
	return new ComputeSAP(graph, v, w).getLength();
    }

    /**
     * a common ancestor that participates in shortest ancestral path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	return new ComputeSAP(graph, v, w).getAncestor();
    }

    /**
     * do unit testing of this class
     */
    public static void main(String[] args) {
	In in = new In(args[0]);
	Digraph G = new Digraph(in);
	SAP sap = new SAP(G);

	while (!StdIn.isEmpty()) {
	    int v = StdIn.readInt();
	    int w = StdIn.readInt();
	    int length   = sap.length(v, w);
	    int ancestor = sap.ancestor(v, w);
	    StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	}
    }
}