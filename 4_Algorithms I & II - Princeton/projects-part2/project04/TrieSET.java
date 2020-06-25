package week4;
/**
 * Nodes to trie
 * @param args
 */
public class TrieSET {
	private final Node root = new Node();

	/**
	 * Each node in trie with 26 children, one for each letter
	 * @author anderspedersen
	 *
	 */
	private class Node{
	    private boolean isWord = false;
	    private Node[] next = new Node[26];
	}

	/**
	 * Add to trie 
	 * @param word
	 */
	public void add(String word) {
	    put(root, word, 0);
	}

	/**
	 * Put in trie 
	 * @param cur
	 * @param word
	 * @param d
	 * @return
	 */
	private Node put(Node cur, String word, int d) {
	    if(cur == null) cur = new Node();

	    // base case, return when all letters have been put
	    if(d == word.length()) {
		cur.isWord = true;
		return cur;
	    }

	    // call resursively to take next letter and place in trie
	    char c = word.charAt(d);
	    cur.next[c - 'A'] = put(cur.next[c - 'A'], word, d + 1);
	    return cur;
	}

	/**
	 * Check if word is contained in trie
	 * @param word
	 * @return
	 */
	public boolean contains(String word) {
	    Node t = get(root, word, 0);
	    if(t != null && t.isWord == true) return true;
	    return false;
	}

	/**
	 * Get Node from trie
	 * @param cur
	 * @param word
	 * @param d
	 * @return
	 */
	private Node get(Node cur, String word, int d) {
	    if(cur == null) return null;
	    // base case, return if word has been searched
	    if(d == word.length()) return cur;

	    // call resursively to get to word
	    char c = word.charAt(d);
	    return get(cur.next[c - 'A'], word, d + 1);
	}

	/**
	 * Check if prefix exists in trie
	 * @param prefix
	 * @return
	 */
	public boolean hasPrefix(String prefix) {
	    Node t = get(root, prefix, 0);
	    if(t == null) return false;
	    else return true;
	}
}
