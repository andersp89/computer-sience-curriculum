import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizing input .jack-file to valid tokens, one per line, in a .xml-file
 * @author anderspedersen
 *
 */
public class JackTokenizer {
    private Scanner inputFile;
    private String curToken = "";
    private String curTokenType = "";
    private ArrayList<String> allMatchedTokens = new ArrayList<>(); // all valid tokens
    private int pointer = 0; // pointer to go through matched tokens

    // variables for setting up token pattern to identify valid tokens
    private static Pattern tokenPatterns; // regular expression for all valid elements, consisting of the below regexs
    private static String keywordRegex;
    private static String symbolRegex;
    private static String intRegex;
    private static String strRegex;
    private static String idRegex;

    // used to codify valid keywords and operations, used by 
    private static HashMap<String, String> keywordMap = new HashMap<>();
    private static HashSet<Character> opSet = new HashSet<>();

    /* populate hashmap for keywords and hashset for operations
     * KeywordMap is used to type check in CompilationEngine via keyword() method 
     */
    static {
	keywordMap.put("class", JGram.CLASS);keywordMap.put("constructor", JGram.CONSTRUCTOR);
	keywordMap.put("function", JGram.FUNCTION);keywordMap.put("method", JGram.METHOD);
	keywordMap.put("field", JGram.FIELD);keywordMap.put("static", JGram.STATIC);
	keywordMap.put("var", JGram.VAR);keywordMap.put("int", JGram.INT);
	keywordMap.put("char", JGram.CHAR);keywordMap.put("boolean", JGram.BOOLEAN);
	keywordMap.put("void", JGram.VOID);keywordMap.put("true", JGram.TRUE);
	keywordMap.put("false", JGram.FALSE);keywordMap.put("null", JGram.NULL);
	keywordMap.put("this", JGram.THIS);keywordMap.put("let", JGram.LET);
	keywordMap.put("do", JGram.DO);keywordMap.put("if", JGram.IF);
	keywordMap.put("else", JGram.ELSE);keywordMap.put("while", JGram.WHILE);
	keywordMap.put("return", JGram.RETURN);

	opSet.add('+');opSet.add('-');opSet.add('*');opSet.add('/');
	opSet.add('&');opSet.add('|');opSet.add('<');opSet.add('>');
	opSet.add('=');
    }

    /**
     * Create a new jack tokenizer
     * @param fileIn: .jack-file to tokenize
     */
    public JackTokenizer(Scanner fileIn) {
	if(fileIn == null) throw new IllegalArgumentException("JackTokenizer must receive input file");
	this.inputFile = fileIn;

	String processedTextInput = removeEmptySpaceAndComments();
	processedTextInput = removeBlockComments(processedTextInput).trim();

	// Build array list with all valid tokens
	buildRegularExpression();
	Matcher m = tokenPatterns.matcher(processedTextInput);
	while (m.find()) {
	    allMatchedTokens.add(m.group());
	}
    }  

    /**
     * Returns true if input file has another line
     * @return
     */
    public boolean hasMoreTokens() {
	return pointer < allMatchedTokens.size();
    }

    /**
     * Gets the next token from the input, and makes it the current token.
     * This method should be called only if hasMoreTokens is true.
     * Initially there is no current token.
     */
    public void advance() {
	if (hasMoreTokens()) {
	    curToken = allMatchedTokens.get(pointer);
	    pointer++;
	} else {
	    throw new IllegalStateException("No more tokens to process");
	}
	setTokenType();
    }

    /**
     * Get the current token
     * @return
     */
    public String getCurrentToken() {
	if (curToken.length() == 0) throw new IllegalStateException("The current token is empty.");

	if (curTokenType == JGram.STRING_CONST) {
	    return curToken.substring(1, curToken.length()-1);
	} else if (curTokenType == JGram.SYMBOL) {
	    return symbol();
	} else if (curTokenType == JGram.STRING_CONST) {
	    return curToken.substring(1, curToken.length()-1);
	} else {
	    return curToken;
	}
    }
    /**
     * Decrements pointer
     */
    public void pointerBack() {
	if (pointer < 1) throw new IllegalStateException("Pointer cannot be decremented, as it is less than 1");
	pointer--;
    }

    /**
     * Returns the type of the current token, as a constant.
     * @return
     */
    public String getTokenType() {
	if (curToken.length() == 0) throw new IllegalStateException("The current type is not defined.");
	return curTokenType;
    }

    /**
     * Returns the keyword which is the current token, as a constant
     * This method should be called only if tokenType is KEYWORD
     * Keyword: 'CLASS', 'CONSTRUCTOR', etc.
     * Used for checking keyword type in CompilationEngine
     * @return
     */
    public String keyword() {
	if (curTokenType != JGram.KEYWORD) throw new IllegalStateException("Current token is not a keyword."); 
	return keywordMap.get(curToken);
    }
    
    /**
     * Check if current token is valid operation, see hashset
     * Used by compileTerm() in CompilationEngine()
     * @return
     */
    public boolean validOp() {
	return opSet.contains(getCurrentToken().charAt(0));
    }

    /**
     * Returns the character which is the current token. Should be called
     * only if tokenType is SYMBOL
     * Symbol: {, }, (,...
     * @return
     */
    private String symbol() {
	if (curToken.equals("<")) { 
	    return "&lt;";
	} else if (curToken.equals(">")){
	    return "&gt;";
	} else if (curToken.equals('"')) {
	    return "&quot;";
	} else if (curToken.equals("&")) {
	    return "&amp;";
	} else {
	    return curToken;
	}
    }

    /**
     * Returns the identifier which is the current token. Should be called
     * only if tokenType is IDENTIFIER
     * Identifier: A sequence of unicode characters not including double quote or newline
     * @return
     */
    /*private String identifier() {
	if (curTokenType == JGram.IDENTIFIER) {
	    return curToken;
	} else {
	    throw new IllegalStateException("Current token is not an identifier");
	}
    }*/

    /**
     * Returns the integer value of the current token. Should be called
     * only if tokenType is INT_CONST
     * intVal: A decimal number in the range 0..32767
     * @return
     */
    /*public int intVal() {
	if (curTokenType == JGram.INT_CONST) {
	    return Integer.parseInt(curToken);
	} else {
	    throw new IllegalStateException("Current token is not an int value");
	}
    }*/

    /** 
     * Returns the string value of the current token, without the two
     * enclosing double quotes. SHould be called only if tokenType is 
     * STRING_CONST
     * 
     * @return
     */
    /*public String stringVal() {
	if (curTokenType == JGram.STRING_CONST) {
	    return curToken.substring(1, curToken.length()-1);
	} else {
	    throw new IllegalStateException("Current token is not a valid String");
	}
    }*/

    /* HELPER FUNCTION BELOW */

    /** 
     * Process input file to remove comments "//", empty space, and block comments "\/* *\/"
     * @return processed string
     */
    private String removeEmptySpaceAndComments() {
	String curLine = "";
	String processedTextInput = "";
	while(inputFile.hasNext()) {
	    curLine = removeComments(inputFile.nextLine()).trim();
	    // if length < 0, it is empty space
	    if (curLine.length() > 0) {
		processedTextInput += curLine + "\n";
	    }
	}
	return processedTextInput;
    }

    /**
     * Removes comments from line, "//", and return cleaned version
     * @param strIn
     */
    private static String removeComments(String strIn) {
	int position = strIn.indexOf("//");
	if (position != -1) {
	    strIn = strIn.substring(0, position);
	}
	return strIn;
    }

    /**
     * Removes all block comments, "/*", from text and returns cleaned text
     * @param strIn
     */
    private static String removeBlockComments(String strIn) {
	int startIndex = strIn.indexOf("/*");
	if (startIndex == -1) return strIn;

	String result = strIn;
	int endIndex = strIn.indexOf("*/");

	// remove all block comments
	while(startIndex != -1){
	    // if block comments has no end, the rest of text is block comment
	    if (endIndex == -1){
		return strIn.substring(0,startIndex - 1);
	    }
	    // remove block comment
	    result = result.substring(0,startIndex) + result.substring(endIndex + 2);

	    // find next block comment
	    startIndex = result.indexOf("/*");
	    endIndex = result.indexOf("*/");
	}

	return result;
    }

    /** 
     * Helper to set the token type of the current token
     */
    private void setTokenType() {
	if (curToken.matches(keywordRegex)) {
	    curTokenType = JGram.KEYWORD;
	} else if (curToken.matches(symbolRegex)) {
	    curTokenType = JGram.SYMBOL;
	} else if (curToken.matches(intRegex)) {
	    curTokenType = JGram.INT_CONST;
	} else if (curToken.matches(strRegex)) {
	    curTokenType = JGram.STRING_CONST;
	} else if (curToken.matches(idRegex)) {
	    curTokenType = JGram.IDENTIFIER;
	} else {
	    throw new IllegalArgumentException("Unknown token: " + curToken);
	}
    }

    /**
     * Initializes pattern of regular expression to identify valid tokens 
     */
    private void buildRegularExpression() {
	// set up regex for valid keywords
	keywordRegex = "";
	for (String seg: keywordMap.keySet()) {
	    keywordRegex += seg + "|";
	}

	symbolRegex = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
	intRegex = "[0-9]+";
	strRegex = "\"[^\"\n]*\"";
	idRegex = "[\\w_]+";

	tokenPatterns = Pattern.compile(keywordRegex + symbolRegex + "|" + intRegex + "|" + strRegex + "|" + idRegex);
    }

}
