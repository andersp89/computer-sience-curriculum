package week4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * 
 * __A* search__
 * Now, we describe a solution to the 8-puzzle problem that illustrates 
 * a general artificial intelligence methodology known as the A* search algorithm. 
 * 
 * We define a search node of the game to be a board, 
 * the number of moves made to reach the board, 
 * and the previous search node. 
 * 
 * First, insert the initial search node 
 * (the initial board, 0 moves, and a null previous search node) into a priority queue. 
 * Then, delete from the priority queue the search node with the minimum priority, 
 * and insert onto the priority queue all neighboring search nodes 
 * (those that can be reached in one move from the dequeued search node).
 * Repeat this procedure until the search node dequeued corresponds to the goal board.
 * 
 * The efficacy of this approach hinges on the choice of priority function for a search node. 
 * We consider two priority functions:
 * _The Hamming priority function is the Hamming distance of a board plus the number of 
 * moves made so far to get to the search node. Intuitively, a search node with a 
 * small number of tiles in the wrong position is close to the goal, 
 * and we prefer a search node if has been reached using a small number of moves.
 * 
 * _The Manhattan priority function is the Manhattan distance of a board 
 * plus the number of moves made so far to get to the search node.
 * 
 * To solve the puzzle from a given search node on the priority queue, 
 * the total number of moves we need to make (including those already made) 
 * is at least its priority, using either the Hamming or Manhattan priority function. 
 * Why? Consequently, when the goal board is dequeued, we have discovered not 
 * only a sequence of moves from the initial board to the goal board, but one 
 * that makes the fewest moves.
 * 
 * USE MANHATTEN PRIORITY FUNCTION.
 * 
 * __Game tree__
 * One way to view the computation is as a game tree, where each search node
 * is a node in the game tree and the children of a node correspond to its 
 * neighboring search nodes. 
 * The root of the game tree is the initial search node; 
 * the internal nodes have already been processed; (any node that has a child node) 
 * the leaf nodes are maintained in a priority queue; (any node w/o a child node)
 * at each step, the A* algorithm removes the node with the smallest priority 
 * from the priority queue and processes it (by adding its children to both 
 * the game tree and the priority queue).
 * 
 * __Implementation requirement__
 * To implement the A* Algorithm, you must use MinPQ data type for the priority queue.
 * 
 * __Two optimizations__
 * _The critical optimization
 * A* search has one annoying feature: search nodes corresponding to the same 
 * board are enqueued on the priority queue many times (e.g., the bottom-left 
 * search node in the game-tree diagram above). To reduce unnecessary exploration 
 * of useless search nodes, when considering the neighbors of a search node, 
 * don’t enqueue a neighbor if its board is the same as the board of the 
 * previous search node in the game tree.
 * 
 * _Caching the Hamming and Manhatten priorities
 * To avoid recomputing the Manhattan priority of a search node from scratch 
 * each time during various priority queue operations, pre-compute its value 
 * when you construct the search node; save it in an instance variable; 
 * and return the saved value as needed. This caching technique is broadly
 * applicable: consider using it in any situation where you are recomputing 
 * the same quantity many times and for which computing that quantity is a 
 * bottleneck operation.
 * 
 * __Detecting unsolvable boards__
 * Not all initial boards can lead to the goal board by a sequence of moves
 * To detect such situations, use the fact that boards are divided into two equivalence classes with respect to reachability:
 * _Those that can lead to the goal board
 * _Those that can lead to the goal board if we modify the initial 
 * board by swapping any pair of tiles (the blank square is not a tile).
 * 
 * To apply the fact, run the A* algorithm on two puzzle instances—one 
 * with the initial board and one with the initial board modified by 
 * swapping a pair of tiles—in lockstep (alternating back and forth 
 * between exploring search nodes in each of the two game trees). 
 * Exactly one of the two will lead to the goal board.
 * 
 * @author anderspedersen
 *
 */

public class Solver {
    private SearchNode endNode;
    private boolean solvable;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
	if (initial == null) throw new IllegalArgumentException();
	
	//create new searchNode & insert
	SearchNode initialNode = new SearchNode(initial, null);
	MinPQ<SearchNode> minPQ = new MinPQ<SearchNode>();
	minPQ.insert(initialNode);

	//create swap of initial & insert
	SearchNode initialNodeSwap = new SearchNode(initialNode.board.twin(), null);
	MinPQ<SearchNode> minPQSwap = new MinPQ<SearchNode>();
	minPQSwap.insert(initialNodeSwap);

	//check if solvable, create queue and node to search
	MinPQ<SearchNode> queue = minPQ;
	SearchNode searchNode;
	while(true) {
	    if(!queue.isEmpty()) {
		searchNode = queue.delMin();
		for (Board b : searchNode.board.neighbors()) {		
		    //ignore if neighbor is equal to parent
		    if(searchNode.previous == null || !b.equals(searchNode.previous.board)) {
			//sort by comparable at insert
			SearchNode newNode = new SearchNode(b, searchNode);
			queue.insert(newNode);	
		    }
		}
		if (searchNode.board.isGoal()) {
		    endNode = searchNode;
		    //if queue is minPQ, goal is found at this queue run and hence solvable
		    solvable = queue == minPQ;
		    
		    if(!solvable) return;
		    
		    return;
		}
	    }
	    //at every second run switch queue to twin
	    if (queue == minPQ) {
		queue = minPQSwap;
	    } else { 
		queue = minPQ;
	    }
	}
    }


    private final class SearchNode {
	private final Board board;
	private final int moves;
	private final SearchNode previous;

	// constructor for inner class
	public SearchNode (Board board, SearchNode previous) {
	    this.board = board;
	    this.previous = previous;
	    if (previous == null) {
		moves = 0;
	    } else {
		moves = previous.moves + 1;
	    }
	}

	private int priority() {
	    return board.manhattan() + moves;
	}

	public int compareTo(SearchNode that) {
	    // difference in Manhattan distance between a 
	    // board and a neighbor is either −1 or +1.
	    return Integer.signum(priority() - that.priority());
	}
    }

    // is the initial board solvable? 
    public boolean isSolvable() {
	return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
	if (isSolvable()) {
	    return endNode.moves;
	} else {
	    return -1;
	} 
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
	if (isSolvable()) {
	    // stack for all boards to goal
	    Stack<Board> boardSequence = new Stack<Board>();
	    SearchNode current = endNode;
	    boardSequence.push(endNode.board);

	    while (current.previous != null) {
		boardSequence.push(current.previous.board);
		current = current.previous;
	    }
	    return boardSequence;
	} else {
	    return null;
	}
    }

    // test client (see below) 
    public static void main(String[] args) {
	// create initial board from file
	In in = new In(args[0]);
	int n = in.readInt();
	int[][] tiles = new int[n][n];
	for (int i = 0; i < n; i++)
	    for (int j = 0; j < n; j++)
		tiles[i][j] = in.readInt();
	Board initial = new Board(tiles);

	// solve the puzzle
	Solver solver = new Solver(initial);

	// print solution to standard output
	if (!solver.isSolvable())
	    StdOut.println("No solution possible");
	else {
	    StdOut.println("Minimum number of moves = " + solver.moves());
	    for (Board board : solver.solution())
		StdOut.println(board);
	}
    }
}