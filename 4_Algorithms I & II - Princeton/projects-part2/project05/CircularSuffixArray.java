package week5;
import java.util.Arrays;

public class CircularSuffixArray {
    private int len;
    private CircularSuffix[] suffix; //array of suffixes, to be sorted
    
    // circular suffix array of s
    public CircularSuffixArray(String s) {	
	if (s == null) throw new IllegalArgumentException("Argument is null.");
	
	len = s.length();
	
	//create all CiruclarSuffix objects
	suffix = new CircularSuffix[len];
	for (int i = 0; i < len; i++) {
	    suffix[i] = new CircularSuffix(s, i);
	}
	
	//sort suffixes
	Arrays.sort(suffix);	 
    }
    /**
     * Represents a circular suffix implicitly (via a reference 
     * to the input string and a pointer to the first character 
     * in the circular suffix).
     * @author anderspedersen
     *
     */
    private class CircularSuffix implements Comparable<CircularSuffix>{
	private int pointer;
	private String s;
	
	public CircularSuffix(String s, int pointer) {
	    this.s = s;
	    this.pointer = pointer; 
	}
	
	public int compareTo(CircularSuffix other) {
	    int pointer1 = pointer;
	    char char1 = s.charAt(pointer1);
	    int length1 = s.length();
	    
	    int pointer2 = other.pointer;
	    char char2 = other.s.charAt(pointer2);
	    int length2 = other.s.length();
	    
	    int counter = 0;
	    
	    //if chars even, compare till uneven
	    while (char1 == char2) {
		++pointer1;
		++pointer2;
		++counter;
		
		//even, if all chars scanned
		if (counter == length1 || counter == length2) return 0;
		
		//start from beginning, if end of word reached
		if (pointer1 == length1) pointer1 = 0;
		if (pointer2 == length2) pointer2 = 0;
		
		char1 = s.charAt(pointer1);
		char2 = other.s.charAt(pointer2);
	    }
	    
	    if (char1 > char2) return 1;
	    else return -1;
	}
    }

    // length of s
    public int length() {
	return len;
    }

    /**
     * returns index of ith sorted suffix
     * index[11] = 2, means that the 2nd original 
     * suffix appears 11th in the sorted order
     */
    public int index(int i) {
	if (i < 0 || i > length()-1) throw new IllegalArgumentException("Index is outside range of word.");
	return suffix[i].pointer;
    }

    // unit testing (required)
    public static void main(String[] args) {
	CircularSuffixArray csa = new CircularSuffixArray("abracadabra!");
	System.out.println(csa.length()); //12
	System.out.println(csa.index(0)); //11
	System.out.println(csa.index(3)); //0
	System.out.println(csa.suffix[3].s); //abracadabra!
    }

}