package week1;
import java.util.Collections;
import java.util.HashMap;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * WordNet groups words into sets of synonyms called synsets. 
 * For example, { AND circuit, AND gate } is a synset.WordNet 
 * also describes semantic relationships between synsets. 
 * 
 * One such relationship is the is-a relationship, which connects 
 * a hyponym (more specific synset) to a hypernym (more general synset). 
 * For example, the synset { gate, logic gate } is a hypernym of 
 * { AND circuit, AND gate } because an AND gate is a kind of logic gate.
 * 
 * The WordNet digraph. Your first task is to build the WordNet digraph:
 * each vertex v is an integer that represents a synset, 
 * and each directed edge v→w represents that w is a hypernym of v. 
 * The WordNet digraph is a rooted DAG: it is acyclic and has 
 * one vertex—the root—that is an ancestor of every other vertex. 
 * However, it is not necessarily a tree because a synset can 
 * have more than one hypernym. 
 * 
 * The WordNet input file formats. We now describe the two 
 * data files that you will use to create the WordNet digraph. 
 * The files are in comma-separated values (CSV) format: 
 * each line contains a sequence of fields, separated by commas.
 * 
 * List of synsets. The file synsets.txt contains all noun 
 * synsets in WordNet, one per line. Line i of the file 
 * (counting from 0) contains the information for synset i. 
 * The first field is the synset id, which is always the integer i; 
 * the second field is the synonym set (or synset); 
 * and the third field is its dictionary definition (or gloss), 
 * which is not relevant to this assignment.
 * 
 * List of hypernyms. The file hypernyms.txt contains 
 * the hypernym relationships. Line i of the file (counting from 0) 
 * contains the hypernyms of synset i. 
 * The first field is the synset id, which is always the integer i;
 * subsequent fields are the id numbers of the synset’s hypernyms.
 * 
 * WordNet data type. Implement an immutable data type WordNet with the following API:
 * 
 * @author anderspedersen 
 */

public class WordNet {
    private int V = 0;			//number of vertices
    private int synsetIDs = 0;		//number of synset ids, to find if one root only
    private Digraph dig;		//immutable digraph 
    private HashMap<String, Stack<Integer>> nouns = new HashMap<>(); //noun -> synset-id
    private HashMap<Integer, String> synsetIndex = new HashMap<>(); //synset -> synset-id, parse input to SAP to find length
    private SAP sap; 			//to search for length between two vertecis

    /**
     * constructor takes the name of the two input files
     * reads synsets, create digraph, connects digraph with hypernyms
     * and ensures that digraph is a DAG.
     * Running time: O(N + log N)  (log N to sort!)
     * @param synsets
     * @param hypernyms
     */
    public WordNet(String synsets, String hypernyms) {
	if (synsets == null || hypernyms == null) throw new IllegalArgumentException("Arguments for WordNet are null.");
	synsetReader(synsets);
	dig = new Digraph(V);
	hypernymReader(hypernyms);
	checkIfDag(); 
	sap = new SAP(dig);
    }

    /**
     * Checks if digraph is a DAG, i.e. no directed cycle and only one root
     * Running time O(E + V)
     */
    private void checkIfDag () {
	DirectedCycle dc = new DirectedCycle(dig);
	if (dc.hasCycle() || V - synsetIDs > 1)
	    throw new IllegalArgumentException("Graph is not Dag");
    }

    /**
     * Save synsets in queue of Vertex objects
     * Synsets are formattet: [ID, word1 word2_word2,description], e.g. 
     * "36,AND_circuit AND_gate,a circuit in a computer that fires only when all of its inputs fire"
     * Running time O(N)   
     * @param synset
     */

    private void synsetReader(String synsets) {
	In in = new In(synsets);
	while (!in.isEmpty()) {
	    String[] input = in.readLine().split(",");
	    int id = Integer.parseInt(input[0]);
	    String[] words = input[1].split(" ");
	    synsetIndex.put(id, input[1]);
	    //split all synsets by spaces, to get individual nouns
	    //replace "_" with whitespaces in individual nouns
	    for (String noun : words) {
		Stack<Integer> ids = nouns.get(noun);
		if (ids == null) {
		    ids = new Stack<>();
		    nouns.put(noun, ids);
		}
		//update ids of noun
		ids.push(id);
	    }
	    V++;
	}
    }

    /**
     * Save hypernyms in digraph
     * Hypernyms: [Synset-id,hypernym-id1,hypernymid2...], e.g. "78,1512,38439"
     * Running time: O(N)
     * @return
     */

    private void hypernymReader(String hypernyms) {
	In in = new In(hypernyms);
	while (!in.isEmpty()) {
	    synsetIDs++;
	    String[] input = in.readLine().split(",");
	    Integer id = Integer.parseInt(input[0]);
	    // hypernyms ids for id
	    for(int i = 1; i < input.length; i++) {
		dig.addEdge(id, Integer.parseInt(input[i]));
		//StdOut.printf("vertex id = %d, hyperIDs = %d\n", id, hyperIDs[i]);
	    }
	}
    }
    /**
     * returns all WordNet nouns
     * O(N)
     * @return
     */
    public Iterable<String> nouns() {
	return Collections.unmodifiableSet(nouns.keySet());
    }

    /**
     * is the word a WordNet noun?
     * Running time: O(log N)
     * @param word
     * @return
     */
    public boolean isNoun(String word) {
	if (!nouns.containsKey(word)) return false;
	else return true;
    }

    /**
     * distance between nounA and nounB (defined below)
     * takes time proportional to E+V
     * O(N)
     * @param nounA
     * @param nounB
     * @return
     */
    public int distance(String nounA, String nounB) {
	if (nounA == null || nounB == null) throw new IllegalArgumentException("Null values to distance.");
	if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Words are not nouns.");
	return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    /**
     * a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below)
     * O(N)
     */
    public String sap(String nounA, String nounB) {
	if (nounA == null || nounB == null) throw new IllegalArgumentException("Arguments to sap are null.");
	if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Arguments to sap are not nouns in WordNet.");
	return synsetIndex.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    /**
     * do unit testing of this class
     * @param args
     */ 
    public static void main(String[] args) {
	WordNet wn = new WordNet("synsets.txt","hypernyms.txt"); // DAG
	//WordNet wn = new WordNet("synsets3.txt","hypernyms3InvalidCycle.txt"); // Not DAG, cycle
	//WordNet wn = new WordNet("synsets3.txt","hypernyms3InvalidTwoRoots.txt"); // Not DAG, two roots

	//print digraph
	//StdOut.println(wn.dig);
	StdOut.println("Vertices: " + wn.dig.V());
	StdOut.println("Edges: " + wn.dig.E());
	StdOut.println("Nouns: " + wn.nouns.size());

	//distance
	StdOut.println("Distance: " + wn.distance("Abadan", "city")); //1
	StdOut.println("Distance: " + wn.distance("citrine", "quartile")); //16

	//isNoun
	StdOut.println("ASCII character set, false: " + wn.isNoun("ASCII character set"));
	StdOut.println("ASCII_character_set, true: " + wn.isNoun("ASCII_character_set")); 



    }
}