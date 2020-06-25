package week1;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Measuring the semantic relatedness of two nouns. 
 * Semantic relatedness refers to the degree to which 
 * two concepts are related. Measuring semantic relatedness 
 * is a challenging problem. For example, you consider 
 * George W. Bush and John F. Kennedy (two U.S. presidents) 
 * to be more closely related than George W. Bush and chimpanzee 
 * (two primates). It might not be clear whether George W. Bush 
 * and Eric Arthur Blair are more related than two arbitrary people. 
 * However, both George W. Bush and Eric Arthur Blair (a.k.a. 
 * George Orwell) are famous communicators and, therefore, closely related.
 * 
 * We define the semantic relatedness of two WordNet nouns x and y as follows:
 * A = set of synsets in which x appears
 * B = set of synsets in which y appears
 * distance(x, y) = length of shortest ancestral path of subsets A and B
 * sca(x, y) = a shortest common ancestor of subsets A and B
 * This is the notion of distance that you will use to implement the 
 * distance() and sap() methods in the WordNet data type.
 * 
 * Outcast detection. Given a list of WordNet nouns x1, x2, ..., xn, 
 * which noun is the least related to the others? To identify an outcast, 
 * compute the sum of the distances between each noun and every other one:
 * di   =   distance(xi, x1)   +   distance(xi, x2)   +   ...   +   distance(xi, xn)
 * and return a noun xt for which dt is maximum. Note that 
 * distance(xi, xi) = 0, so it will not contribute to the sum.
 * @author anderspedersen
 *
 */
public class Outcast {
    private WordNet wordnet;

    public Outcast(WordNet wordnet) {
	if (wordnet == null) throw new IllegalArgumentException();
	this.wordnet = wordnet;
    }
    
    /**
     * given an array of WordNet nouns, return an outcast
     * for each word, calculate distance to all other words
     * save the word with largest distance
     * 
     * @param nouns
     * @return
     */
    public String outcast(String[] nouns) {
	if (nouns == null) throw new IllegalArgumentException();
	
	int max = Integer.MIN_VALUE;
	String outcast = null;
	
	for (String nounA: nouns) {
	    int distance = 0;
	    for (String nounB: nouns)
		distance += wordnet.distance(nounA, nounB);
	    if (distance > max) {
		max = distance;
		outcast = nounA;
	    }
	}
	return outcast;
    }
    
    public static void main(String[] args) {
	WordNet wordnet = new WordNet(args[0], args[1]);
	Outcast outcast = new Outcast(wordnet);
	for (int t = 2; t < args.length; t++) {
	    In in = new In(args[t]);
	    String[] nouns = in.readAllStrings();
	    StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	}
    }
}