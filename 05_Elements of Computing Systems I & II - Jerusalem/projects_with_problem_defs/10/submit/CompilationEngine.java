import java.io.FileWriter;
import java.io.IOException;

/**
 * Parsing logic:
 * 1) Follow the right-hand side of the rule, and parse the input accordingly.
 * 2) If the right-hand side specifies a non-terminal rule xxx, call compileXXX
 * 3) Do this recursively
 * 
 * Init:
 * Advance the tokeniser.
 * 
 * Parser’s design:
 * A set of compilexxx methods, one for each non-terminal rule xxx
 * Each method outputs some of the parse tree (XML), and advances the input.
 * The parsing logic of each method follows the right-hand side of the rule that it implements.
 * 
 * 
 * Parsing expressions:
 * Problematic with expressions: When the current token is a varName (some identifier), it can be either a variable name, an array entry, or a subroutine call.
 * To resolve which possibility we are in, the parser should “look ahead”: save the current token and advance to get to the next one. 
 * There is not compileSubroutineCall method; rather, the subroutine call logic is handled in compileTerm.  
 * foo
 * foo[expression]
 * foo.bar(expressionList)
 * Foo.bar(expressionList)
 * bar(expressionList)
 *
 * If terminal element: just output <terimalElement> xxx </terminalElement> where terminal element is: keyword, symbol, integerConstant, stringConstant, identifier
 * if non-terminal element: 
 * <nonTerminal>
 * 	recursive output for the non-terminal body
 * </nonterminal> 
 * non-terminal is: class, classVarDec, subroutineDec, etc. 
 * Shallow non-terminal rule: type, class name, subroutine name, variable name.
 * 
 * Each compilexxx routine is responsible for handling all the tokens that make up xxx, advancing the tokeniser exactly beyond these tokens, and outputting the parsing of xxx.
 * 
 * Plan of implementation
 * Constructor evokes compileClass that compiles a complete class. Which has two loops, one for arguments one for subroutines.
 * Each of the methods follows the logic of the rule.
 * 
 * CompilationEngine: 
 * Build a basic CompilationEngine that handles everything except expressions. Use “ExpressionLessSquare”
 * Add the handling of expressions 
 * (test files to test both states)
 */

public class CompilationEngine {
    private JackTokenizer jt;
    private FileWriter fileOut;

    /**
     * Creates  a new compilation engine with the given input and output. 
     * @param jt
     * @param fileOut
     */
    public CompilationEngine(JackTokenizer jt, FileWriter fileOut) throws IOException {
	if (jt == null | fileOut == null) throw new IllegalArgumentException("CompilationEngine must receive tokenized input and file to write to");
	this.jt = jt;
	this.fileOut = fileOut;
    }

    /**
     * Compiles a complete class
     * class: 'class' className '{' classVarDec* subroutineDec* '}'
     * @throws IOException
     */
    public void compileClass() throws IOException {
	// wrap xml in "<class>"-tag
	fileOut.write("<class>\n");

	// 'class'
	wNextToken();

	// className
	wNextToken();

	// '{'
	wNextToken();

	// classVarDec* subroutineDec* 
	compileClassVarDec();
	compileSubroutineDec();

	// '}'
	wNextToken();

	// wrap xml in "<class>"-tag
	fileOut.write("</class>");
    }

    /**
     * Writes next token to output file
     * @throws IOException
     */
    private void wNextToken() throws IOException {
	jt.advance();
	fileOut.write("<" + jt.getTokenType() + "> " + jt.getCurrentToken() + "</" + jt.getTokenType() + ">\n");
    } 

    /**
     * Compiles a static variable declaration, or a field declaration
     * classVarDec: ('static'|'field') type varName (','varName)* ';'
     * @throws IOException
     */
    private void compileClassVarDec() throws IOException {
	/* if next is '}', no class variables and subroutines, 
	 * or if next is subroutine, i.e. constructor, function or method,
	 * meaning, all class variables compiled and do return.
	 */
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == '}' || jt.keyword() == JGram.CONSTRUCTOR || jt.keyword() == JGram.FUNCTION || jt.keyword() == JGram.METHOD) {
	    jt.pointerBack();
	    return;
	}
	jt.pointerBack();

	// classVarDec 
	fileOut.write("<classVarDec>\n");

	// 'static' | 'field'
	wNextToken();

	// type
	wNextToken();

	// varName (','varName)* ';'
	while (true) {
	    // varName
	    wNextToken();

	    // ',' or ';'
	    wNextToken();
	    if (jt.getCurrentToken().charAt(0) == ';') break;
	}
	fileOut.write("</classVarDec>\n");

	// Recursive call to write next class variable
	compileClassVarDec();
    }

    /**
     * Compiles a complete method, function, or constructor.
     * subroutineDec: ('constructor' | 'function' | 'method') ('void' | type) subroutineName '(' parameterList ')' subroutineBody 
     * @throws IOException
     */
    private void compileSubroutineDec() throws IOException {
	// check if end of class, i.e. '}'
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == '}') {
	    jt.pointerBack();
	    return;
	}
	jt.pointerBack();

	// subroutineDec
	fileOut.write("<subroutineDec>\n");

	// ('constructor' | 'function' | 'method')
	wNextToken();

	// ('void' | type)
	wNextToken();

	// subroutineName
	wNextToken();

	// '('
	wNextToken();

	// parameterList
	compileParameterList();

	// ')'
	wNextToken();

	// subroutineBody
	compileSubroutineBody();
	
	// subroutineDec
	fileOut.write("</subroutineDec>\n");
	
	// compile next subroutine
	compileSubroutineDec();
    }

    /**
     * Compiles a (possibly empty) parameter list. Does not handle
     * the enclosing "()".
     * parameterList: ((type varName) (',' type varName)*)?
     * @throws IOException
     */
    private void compileParameterList() throws IOException {
	// parameterList
	fileOut.write("<parameterList>\n");

	// return if no parameters for subroutine
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == ')') {
	    // parameterList
	    fileOut.write("</parameterList>\n");
	    jt.pointerBack();
	    return;
	}
	jt.pointerBack();

	while(true) {
	    // type
	    wNextToken();

	    // varName
	    wNextToken();

	    // check if last parameter for subroutine
	    jt.advance();
	    if (jt.getCurrentToken().charAt(0) == ')') {
		jt.pointerBack();
		// parameterList
		fileOut.write("</parameterList>\n");
		return;
	    }
	    jt.pointerBack();

	    // ','
	    wNextToken();
	}
    }

    /**
     * Compiles a subroutine's body.
     * subroutineBody: '{' varDec* statements '}'
     * @throws IOException
     */
    private void compileSubroutineBody() throws IOException {
	// subroutineBody
	fileOut.write("<subroutineBody>\n");

	// '{'
	wNextToken();

	// varDec*
	compileVarDec();

	// statements
	compileStatements();

	// '}'
	wNextToken();

	// subroutineBody
	fileOut.write("</subroutineBody>\n");
    }

    /**
     * Compiles a var declaration
     * varDec: 'var' type varName (',' varName)* ";"
     * @throws IOException
     */
    private void compileVarDec() throws IOException {
	// return if no variable
	jt.advance();
	if (!jt.getCurrentToken().equals("var")) {
	    jt.pointerBack();
	    return;
	}
	jt.pointerBack();

	// varDec
	fileOut.write("<varDec>\n");

	// 'var'
	wNextToken();

	// type
	wNextToken();

	// write all varNames till ';'
	while(true) {
	    // varName
	    wNextToken();

	    // check if last parameter for subroutine
	    jt.advance();
	    if (jt.getCurrentToken().charAt(0) == ';') {
		jt.pointerBack();
		// ';'
		wNextToken();
		// varDec
		fileOut.write("</varDec>\n");
		break;
	    }
	    jt.pointerBack();

	    // ','
	    wNextToken();
	}

	// any more variables 
	compileVarDec();
    }

    /**
     * Compiles a sequence of statements. Does not handle the enclosing "{}".
     * statements: statement*
     * @throws IOException
     */
    private void compileStatements() throws IOException {
	// statements
	fileOut.write("<statements>\n");

	// helper to compile all statements
	compileStatement();

	// statements 
	fileOut.write("</statements>\n");
    }

    /**
     * Helper to compile statements 
     * statement: letStatement | ifStatement | whileStatement | doStatement | returnStatement
     * @throws IOException
     */
    private void compileStatement() throws IOException {
	jt.advance();
	if (jt.getTokenType() != JGram.KEYWORD) {
	    jt.pointerBack();
	    return; 
	} else if (jt.keyword() == JGram.LET) {
	    jt.pointerBack();
	    compileLet();
	} else if (jt.keyword() == JGram.IF) {
	    jt.pointerBack();
	    compileIf();
	} else if (jt.keyword() == JGram.WHILE) {
	    jt.pointerBack();
	    compileWhile();
	} else if (jt.keyword() == JGram.DO) {
	    jt.pointerBack();
	    compileDo();
	} else if (jt.keyword() == JGram.RETURN) {
	    jt.pointerBack();
	    compileReturn();
	}
	compileStatement();
    }

    /**
     * Compiles a let statement
     * letStatement: 'let' varName('[' expression ']')? '=' expression ';'
     * @throws IOException
     */
    private void compileLet() throws IOException {
	// letStatement
	fileOut.write("<letStatement>\n");

	// 'let'
	wNextToken();


	// varName
	wNextToken();
	
	//('[' expression ']')?
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == '[') {
	    jt.pointerBack();
	    // '['
	    wNextToken();
	    
	    // expression
	    compileExpression();
	    
	    // ']'
	    wNextToken();
	} else {
	    jt.pointerBack();
	}

	// '='
	wNextToken();

	// expression
	compileExpression();

	// ';'
	wNextToken();

	// letStatement
	fileOut.write("</letStatement>\n");
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     * ifStatement: 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?  
     * @throws IOException
     */
    private void compileIf() throws IOException {
	// ifStatement
	fileOut.write("<ifStatement>\n");

	// 'if'
	wNextToken();

	// '('
	wNextToken();

	// expression
	compileExpression();

	// ')'
	wNextToken();

	// '{'
	wNextToken();

	// statements
	compileStatements();

	// '}'
	wNextToken();

	// ('else' '{' statements '}')?
	jt.advance();
	if (jt.getTokenType() == JGram.KEYWORD && jt.keyword() == JGram.ELSE) {
	    jt.pointerBack();
	    // 'else'
	    wNextToken();

	    // '{'
	    wNextToken();

	    // statements
	    compileStatements();

	    // '}'
	    wNextToken();
	} else {
	    jt.pointerBack();    
	}

	// ifStatement
	fileOut.write("</ifStatement>\n");
    }

    /**
     * Compiles a while statement
     * whileStatement: 'while' '(' expression ')' '{' statements '}'
     */
    private void compileWhile() throws IOException {
	// whileStatement
	fileOut.write("<whileStatement>\n");

	// 'while'
	wNextToken();

	// '('
	wNextToken();

	// expression
	compileExpression();

	// ')'
	wNextToken();

	// '{'
	wNextToken();

	// statements
	compileStatements();

	// '}'
	wNextToken();

	// whileStatement
	fileOut.write("</whileStatement>\n");
    }

    /**
     * Compiles a do statement
     * doStatement: 'do' subroutineCall ';'
     */
    private void compileDo() throws IOException {
	// doStatement
	fileOut.write("<doStatement>\n");

	// 'do'
	wNextToken();

	// subroutineCall
	subroutineCall();

	// ';'
	wNextToken();

	// doStatement
	fileOut.write("</doStatement>\n");
    }
    
    /**
     * Helper to compileDo to write subroutineCall without <term>-tags
     * subroutineCall: subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'(' expressionList ')' 
     */
    private void subroutineCall() throws IOException {
	// varName | class Name
	wNextToken();
	
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == '.') {
	    jt.pointerBack();
	    // '.'
	    wNextToken();
	    subroutineCall();
	    return;
	} else {
	    jt.pointerBack();
	}
	
	// '('
	wNextToken();
	
	// expressionList
	compileExpressionList();
	
	// ')'
	wNextToken();
    }

    /**
     * Compiles a return statement
     * returnStatement: 'return' expression? ';'  
     */
    private void compileReturn() throws IOException {
	// returnStatement
	fileOut.write("<returnStatement>\n");

	// 'return'
	wNextToken();

	// expression?
	jt.advance();
	if (jt.getCurrentToken().charAt(0) != ';') {
	    jt.pointerBack();
	    compileExpression();
	} else {
	    jt.pointerBack();
	}

	// ';'
	wNextToken();

	// returnStatement
	fileOut.write("</returnStatement>\n");
    }

    /**
     * Compiles an expression
     * expression: term (op term)*
     * @throws IOException
     */
    private void compileExpression() throws IOException {
	// for now, just output name running "ExpressionLessSquare"

	// expression
	fileOut.write("<expression>\n");

	// term
	compileTerm();

	// (op term)* 
	jt.advance();
	if (jt.validOp()) {
	    jt.pointerBack();
	    compileOpTerm();
	} else {
	    jt.pointerBack();
	}

	// expression
	fileOut.write("</expression>\n");
    }

    /**
     * Compiles (op term)* recursively, helper to compileExpression()
     */
    private void compileOpTerm() throws IOException {
	if (!jt.validOp()) return;
	// op
	wNextToken();

	// term
	compileTerm();
    }

    /* 2ND PART OF COMPILER: */
    /**
     * Compiles a term. If the current token is an identifier, the routine
     * must distinguish between a variable, an array, or a subroutine call. A single 
     * look ahead token, which may be either "[", "(", or "." suffices to distinguish
     * between the possbilities.
     * term: integerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '(' expression ')' | unaryOp term
     * subroutineCall: subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'(' expressionList ')'
     * unaryOp: '-' | '~'
     * @throws IOException
     */
    private void compileTerm() throws IOException {
	// term
	fileOut.write("<term>\n");

	jt.advance();
	// varName | className
	if (jt.getTokenType() == JGram.IDENTIFIER) {

	    jt.advance();
	    // '[': array
	    if (jt.getCurrentToken().charAt(0) == '[') {
		jt.pointerBack();
		jt.pointerBack();
		// varName | className
		wNextToken();
		
		// '['
		wNextToken();

		// expression
		compileExpression();

		// ']'
		wNextToken();
	    } 
	    // '(' or '.': subroutineCall 
	    else if (jt.getCurrentToken().charAt(0) == '(' || jt.getCurrentToken().charAt(0) == '.') {
		jt.pointerBack();
		jt.pointerBack();
		subroutineCall();
	    } 
	    // '.': subroutineCall
	    /*else if (jt.getCurrentToken().charAt(0) == '.'){
		jt.pointerBack();
		// '.'
		wNextToken();

		// subroutineName'(' expressionList ')'
		compileTerm();
	    }*/
	    else {
		jt.pointerBack();
		jt.pointerBack();
		// varName
		wNextToken();
	    }
	} 
	// '(' expression ')' 
	else if (jt.getCurrentToken().charAt(0) == '(') {
	    jt.pointerBack();
	    // '('
	    wNextToken();

	    // expression
	    compileExpression();

	    // ')'
	    wNextToken();
	} 
	// unaryOp term
	else if (jt.getCurrentToken().charAt(0) == '~' || jt.getCurrentToken().charAt(0) == '-') {
	    jt.pointerBack();

	    // '-' | '~', i.e. unaryOp
	    wNextToken();

	    // term
	    compileTerm();
	}
	// integerConstant | stringConstant | keywordConstant
	else if (jt.getTokenType() == JGram.INT_CONST || jt.getTokenType() == JGram.STRING_CONST || jt.getTokenType() == JGram.KEYWORD) {
	    jt.pointerBack();
	    wNextToken();
	}

	// term
	fileOut.write("</term>\n");
    }

    /**
     * Compiles a (possibly empty) comma-seperated list of expressions.
     * expressionList: (expression (',' expression)*)?
     * @throws IOException
     */
    private void compileExpressionList() throws IOException {
	// expressionList
	fileOut.write("<expressionList>\n");

	// (expression (',' expression)*)
	compileExpressionListHelper();

	// expressionList
	fileOut.write("</expressionList>\n");
    }

    /**
     * Helper to recursively write all expressions in expressionList
     * @throws IOException
     */
    private void compileExpressionListHelper() throws IOException {
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == ')') {
	    jt.pointerBack();
	    return;
	} else if (jt.getCurrentToken().charAt(0) == ',') {
	    jt.pointerBack();
	    wNextToken();
	} else {
	    jt.pointerBack();
	    compileExpression();
	}
	compileExpressionListHelper();
    }

}
