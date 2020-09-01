package week4;
import java.util.HashSet;
import java.util.Set;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Write a program to play the word game Boggle®.
 * A valid word must be composed by following a sequence of adjacent dice—two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.
 * A valid word can use each die at most once.
 * A valid word must contain at least 3 letters.
 * A valid word must be in the dictionary (which typically does not contain proper nouns).
 * @author anderspedersen
 *
 */

public class BoggleSolver {
    private final TrieSET dict;
    private int row, col;
    private boolean[][] marked;
    private BoggleBoard board;
    private Set<String> legalWords;

    /**
     * Initializes the data structure using the given array of strings as the dictionary.
       (You can assume each word in the dictionary contains only the uppercase letters A through Z.) 
     * @author anderspedersen
     *
     */
    public BoggleSolver(String[] dictionary) {
	dict = new TrieSET();
	for (String word: dictionary) {
	    dict.add(word);
	}
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
	this.board = board;
	legalWords = new HashSet<>();
	row = board.rows();
	col = board.cols();
	marked = new boolean[row][col];

	// search for each word of board
	for (int i = 0; i < row; i++) {
	    for (int j = 0; j < col; j++) {
		dfs(i, j, "");
	    }
	}

	return legalWords;
    }

    /**
     * Recursively call all combinations
     * @param i
     * @param j
     * @param cur
     */
    private void dfs (int i, int j, String cur) {
	if(!dict.hasPrefix(cur)) return;

	// add word if > 2 and in dictionary
	if (cur.length() > 2 && dict.contains(cur)) {
	    legalWords.add(cur);
	}

	if (i >= 0 && i < row && j >= 0 && j < col && !marked[i][j]) {
	    marked[i][j] = true;
	    String str = ""+board.getLetter(i, j);
	    if (str.equals("Q")) str = "QU";

	    String nxtStr = cur + str;
	    dfs(i - 1, j - 1, nxtStr); //upper-left
	    dfs(i - 1, j, nxtStr); //top
	    dfs(i - 1, j + 1, nxtStr); //upper-right
	    dfs(i, j - 1, nxtStr); //left
	    dfs(i, j + 1, nxtStr); //right
	    dfs(i + 1, j - 1, nxtStr); //lower-left
	    dfs(i + 1, j, nxtStr); //bottom
	    dfs(i + 1, j + 1, nxtStr); //lower-right
	    marked[i][j] = false;
	}

    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * (You can assume the word contains only the uppercase letters A through Z.)
     * @param word
     * @return
     */
    public int scoreOf(String word) {
	if (!dict.contains(word)) return 0;

	int len = word.length();
	if (len <= 2) return 0;
	else if (len <= 4) return 1;
	else if (len == 5) return 2;
	else if (len == 6) return 3;
	else if (len == 7) return 5;
	else return 11;
    }

    /**
     * takes the filename of a dictionary and the filename of a Boggle board 
     * as command-line arguments and prints out all valid words for the given 
     * board using the given dictionary.
     */
    public static void main(String[] args) {
	In in = new In(args[0]);
	String[] dictionary = in.readAllStrings();
	BoggleSolver solver = new BoggleSolver(dictionary);
	BoggleBoard board = new BoggleBoard(args[1]);
	    int score = 0;
	    for (String word : solver.getAllValidWords(board)) {
	        StdOut.println(word);
	        score += solver.scoreOf(word);
	    }
	    StdOut.println("Score = " + score);
    }
}