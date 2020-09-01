import java.io.FileWriter;
import java.io.IOException;

/**  
 * Parser’s design:
 * 1. A set of compilexxx methods, one for each non-terminal rule xxx.
 * 2. Each method outputs some of the parse tree to VMWriter, and advances the input.
 * 3. The parsing logic of each method follows the right-hand side of the rule that it implements.
 */

public class CompilationEngine {
    private JackTokenizer jt;
    private SymbolTable st = new SymbolTable();
    private String className;
    private VMWriter vmFile;
    private int labelWhile = 0;
    private int labelIf = 0;
    private int parameterCountToCallFunc = 0;
    private String subroutineType; // 'constructor' | 'function' | 'method')
    private String subroutineName; 

    /**
     * Creates  a new compilation engine with the given input and output. 
     * @param jt
     * @param fileOut
     */
    public CompilationEngine(JackTokenizer jt, FileWriter vmFile) throws IOException {
	if (jt == null | vmFile == null) throw new IllegalArgumentException("CompilationEngine must receive tokenized input and file to write to");
	this.jt = jt;
	this.vmFile = new VMWriter(vmFile);	
    }

    /**
     * Writes next token to output file
     * @throws IOException
     */
    /*private void wNextToken() throws IOException {
	jt.advance();
	System.out.println(jt.getCurrentToken());
	fileOut.write("<" + jt.getTokenType() + "> " + jt.getCurrentToken() + " </" + jt.getTokenType() + ">\n");
    }*/

    /**
     * Gets the next token and advances the input in JackTokenizer
     * @return
     */
    private String getNextToken() {
	jt.advance();
	return jt.getCurrentToken();
    }


    /**
     * Compiles a complete class
     * class: 'class' className '{' classVarDec* subroutineDec* '}'
     * @throws IOException
     */
    public void compileClass() throws IOException {
	// 'class'
	jt.advance();

	// className
	className = getNextToken();

	// '{'
	jt.advance();

	// classVarDec* subroutineDec* 
	compileClassVarDec();
	compileSubroutineDec(); 

	// '}'
	jt.advance();
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

	// kind: 'static' | 'field'
	String kind = getNextToken();
	// type: int, char, boolean, class name
	String type = getNextToken();
	// populate symbol table
	while (true) {
	    // name: varName
	    String name = getNextToken();
	    st.define(name, type, kind);

	    // ',' or ';'
	    jt.advance();
	    if(jt.getCurrentToken().charAt(0) == ';') break;
	}

	// recursive call to write next class variable
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

	// ('constructor' | 'function' | 'method')
	// reset symbol table per subroutine
	// if 'method', the symbol table must start with this 
	st.startSubroutine();
	subroutineType = getNextToken();
	if (subroutineType.equals("method")) {
	    st.define("this", className, "argument");
	}

	// ('void' | type), where 'type' is int, String, etc.
	jt.advance();

	// subroutineName
	subroutineName = getNextToken();

	// '('
	jt.advance();

	// parameterList
	compileParameterList();

	// ')'
	jt.advance();

	// subroutineBody
	compileSubroutineBody();

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
	// return if no parameters for subroutine
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == ')') {
	    jt.pointerBack();
	    return;
	}
	jt.pointerBack();

	// kind is always 'argument'
	String kind = "argument";
	while(true) {
	    // type
	    String type = getNextToken();

	    // varName
	    String name = getNextToken();

	    // populate table with argument
	    st.define(name, type, kind);

	    // check if last parameter for subroutine
	    jt.advance();
	    if (jt.getCurrentToken().charAt(0) == ')') {
		jt.pointerBack();
		return;
	    }
	    jt.pointerBack();

	    // ','
	    jt.advance();
	}
    }

    /**
     * Compiles a subroutine's body.
     * subroutineBody: '{' varDec* statements '}'
     * @throws IOException
     */
    private void compileSubroutineBody() throws IOException {
	// '{'
	jt.advance();

	// varDec*
	compileVarDec();

	// type: 'function'
	if (subroutineType.equals("function")) {
	    // nArgs is the number of local arguments
	    vmFile.writeFunction(className + "." + subroutineName, st.varCount("var"));
	}
	// type: 'method'
	else if (subroutineType.equals("method")) {
	    // + 1 to make space for 'this' variable
	    vmFile.writeFunction(className + "." + subroutineName, st.varCount("var") + 1);
	    /*
	     * anchor method at the object it is called on, which we do
	     * by pushing 'argument 0', as it contains 'this' in the symbol table,
	     * after which the 'this' is set by saving the address in pointer 0.
	     */  
	    vmFile.writePush("argument", 0);
	    vmFile.writePop("pointer", 0);
	} 
	// type: 'constructor'
	else if (subroutineType.equals("constructor")) { 
	    /* 
	     * method finds memory block of the required
	     * size, i.e. number of field variables, and
	     * returns its base address 
	     */
	    vmFile.writeFunction(className + "." + subroutineName, st.varCount("var"));
	    vmFile.writePush("constant", st.varCount("field"));
	    vmFile.writeCall("Memory.alloc", 1);
	    // anchors 'this' at the base address
	    vmFile.writePop("pointer", 0);
	}

	// statements
	compileStatements();

	// '}'
	jt.advance();
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

	// 'var'
	String kind = getNextToken();

	// type
	String type = getNextToken();

	// write all varNames till ';'
	while(true) {
	    // varName
	    String name = getNextToken();

	    // populate symbol table with subroutine var
	    st.define(name, type, kind);

	    // check if last variable name
	    jt.advance();
	    if (jt.getCurrentToken().charAt(0) == ';') {
		// ';'
		break;
	    }
	    jt.pointerBack();

	    // ','
	    jt.advance();
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
	//fileOut.write("<statements>\n");

	// helper to compile all statements
	//compileStatement();

	// statements 
	//fileOut.write("</statements>\n");
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
	compileStatements();
    }

    /**
     * Helper to compile statements 
     * statement: letStatement | ifStatement | whileStatement | doStatement | returnStatement
     * @throws IOException
     */
    /* private void compileStatement() throws IOException {
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
    }*/

    /**
     * Compiles a let statement
     * letStatement: 'let' varName('[' expression ']')? '=' expression ';'
     * @throws IOException
     */
    private void compileLet() throws IOException {
	// 'let'
	jt.advance();

	// varName
	String varName = getNextToken();

	boolean expExist = false;
	//('[' expression ']')?
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == '[') {
	    // '['
	    expExist = true;

	    // push array variable, base address into stack
	    vmFile.writePush(st.kindOf(varName), st.indexOf(varName));

	    // expression
	    compileExpression();

	    // ']'
	    jt.advance();

	    // base + expression
	    vmFile.writeArithmetic('+');
	} else {
	    jt.pointerBack();
	}

	// '='
	jt.advance();

	// expression
	compileExpression();

	// ';'
	jt.advance();

	if (expExist) {
	    //*(base+offset) = expression
	    // pop expression value to temp
	    vmFile.writePop("temp", 0);
	    // pop base+index into 'that'
	    vmFile.writePop("pointer", 1);
	    // pop expression value into *(base+index)
	    vmFile.writePush("temp", 0);
	    vmFile.writePop("that", 0);
	}
	// pop expression value to variable
	else {
	    vmFile.writePop(st.kindOf(varName), st.indexOf(varName));
	}
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     * ifStatement: 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?  
     * @throws IOException
     */
    private void compileIf() throws IOException {
	// labels
	String elseLabel = "LabelIf" + labelIf++;
	String endLabel = "LabelIf" + labelIf++;

	// 'if'
	jt.advance();

	// '('
	jt.advance();

	// expression
	compileExpression();

	// ')'
	jt.advance();

	// if '~(condition)' go to else label
	vmFile.writeNegNot('~');
	vmFile.writeIf(elseLabel); 

	// '{'
	jt.advance();

	// statements
	compileStatements();

	// '}'
	jt.advance();

	// goto
	vmFile.writeGoto(endLabel);

	// label
	vmFile.writeLabel(elseLabel);	

	// ('else' '{' statements '}')?
	jt.advance();
	if (jt.getTokenType() == JGram.KEYWORD && jt.keyword() == JGram.ELSE) {
	    // 'else'

	    // '{'
	    jt.advance();

	    // statements
	    compileStatements();

	    // '}'
	    jt.advance();
	} else {
	    jt.pointerBack();    
	}

	// label
	vmFile.writeLabel(endLabel);
    }

    /**
     * Compiles a while statement
     * whileStatement: 'while' '(' expression ')' '{' statements '}'
     */
    private void compileWhile() throws IOException {
	// 'while'
	jt.advance();

	// '('
	jt.advance();

	// labels
	String topLabel = "Lwhile" + labelWhile++;
	String continueLabel = "Lwhile" + labelWhile++;

	// top label
	vmFile.writeLabel(topLabel);

	// expression
	compileExpression();

	// ')'
	jt.advance();

	// if '~(condition)' go to contninueLabel
	vmFile.writeNegNot('~');
	vmFile.writeIf(continueLabel);

	// '{'
	jt.advance();

	// statements
	compileStatements();

	// goto Lwhile0
	vmFile.writeGoto(topLabel);

	// '}'
	jt.advance();

	// label Lwhile1
	vmFile.writeLabel(continueLabel);
    }

    /**
     * Compiles a do statement
     * doStatement: 'do' subroutineCall ';'
     */
    private void compileDo() throws IOException {
	// 'do'
	jt.advance();

	// subroutineCall
	subroutineCall();

	// move return value of function to temp
	vmFile.writePop("temp", 0);	

	// ';'
	jt.advance();
    }

    /**
     * Helper to compileDo to write subroutineCall without <term>-tags
     * subroutineCall: subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'(' expressionList ')' 
     */
    private void subroutineCall() throws IOException {
	// varName | class Name
	String name1 = getNextToken();

	jt.advance();
	// subrountineName '(' expressionList ')', i.e. method call in class
	if (jt.getCurrentToken().charAt(0) == '(') {
	    // '('

	    // push the object that method is called on
	    vmFile.writePush("pointer", 0);
	    // then the other arguments (order is expected by VM)
	    compileExpressionList();

	    // ')'
	    jt.advance();

	    // write call to method, + 1 to include current object, i.e. 'pointer 0'
	    vmFile.writeCall(className + "." + name1, parameterCountToCallFunc + 1);
	} 
	// (className | varName)'.'subroutineName'(' expressionList ')'
	else if (jt.getCurrentToken().charAt(0) == '.') {
	    // 'subroutineName'
	    String subroutineName = getNextToken();

	    /* check if varName, e.g. other.methodName. If, then: 
	     *  _ variable must be pushed from symbol table
	     *  _ name must be its class type
	     *  if not, set name to className.subroutineName,
	     */
	    String type = st.typeOf(name1);

	    if (type.equals("NONE")) {
		name1 = name1 + "." + subroutineName;
	    } else {
		// push the variable from symbol table
		parameterCountToCallFunc = 1;
		vmFile.writePush(st.kindOf(name1), st.indexOf(name1));
		// className.name
		name1 = st.typeOf(name1) + "." + subroutineName;
	    }

	    // '('
	    jt.advance();

	    // expressionList
	    compileExpressionList();

	    // ')'
	    jt.advance();

	    // call subroutine
	    vmFile.writeCall(name1, parameterCountToCallFunc);
	}

	// reset parameter count for next function
	parameterCountToCallFunc = 0;
    }

    /**
     * Compiles a return statement
     * returnStatement: 'return' expression? ';'  
     */
    private void compileReturn() throws IOException {
	// 'return'
	jt.advance();

	// expression?
	jt.advance();
	if (jt.getCurrentToken().charAt(0) != ';') {
	    jt.pointerBack();
	    compileExpression();
	} else {
	    jt.pointerBack();
	    vmFile.writePush("constant", 0); 
	}

	// return statement
	vmFile.writeReturn();
	
	// ';'
	jt.advance();
    }

    /**
     * Compiles an expression
     * expression: term (op term)*
     * @throws IOException
     */
    private void compileExpression() throws IOException {
	// 'term'
	compileTerm();

	// (op term)* 
	while (true) {
	    // 'op'
	    Character opCmd = getNextToken().charAt(0); 
	    if (jt.validOp(opCmd)) {
		// 'term'
		compileTerm();
		vmFile.writeArithmetic(opCmd);
	    }
	    else {
		jt.pointerBack();
		break;
	    }
	}
    }
    
    /**
     * Compiles a term. If the current token is an identifier, the routine
     * must distinguish between a variable, an array, or a subroutine call. A single 
     * look ahead token, which may be either "[", "(", or "." suffices to distinguish
     * between the possibilities.
     * term: integerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '(' expression ')' | unaryOp term
     * subroutineCall: subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'(' expressionList ')'
     * unaryOp: '-' | '~'
     * @throws IOException
     */
    private void compileTerm() throws IOException {
	// term
	jt.advance();
	// varName | className
	if (jt.getTokenType() == JGram.IDENTIFIER) {

	    jt.advance();
	    // '[': array
	    if (jt.getCurrentToken().charAt(0) == '[') {
		jt.pointerBack();
		jt.pointerBack();

		// varName | className
		String varName = getNextToken();
		// push array base address into stack
		vmFile.writePush(st.kindOf(varName), st.indexOf(varName));

		// '['
		jt.advance();

		// expression
		compileExpression();

		// ']'
		jt.advance();

		// base + offset
		vmFile.writeArithmetic('+');
		// pop into 'that' pointer
		vmFile.writePop("pointer", 1);
		// push contents of array index onto stack
		vmFile.writePush("that", 0);
	    } 
	    // '(' or '.': subroutineCall 
	    else if (jt.getCurrentToken().charAt(0) == '(' || jt.getCurrentToken().charAt(0) == '.') {
		jt.pointerBack();
		jt.pointerBack();
		subroutineCall();
	    } 
	    else {
		jt.pointerBack();
		jt.pointerBack();
		
		// varName
		String varName = getNextToken(); 
		vmFile.writePush(st.kindOf(varName), st.indexOf(varName));
	    }
	} 
	// '(' expression ')' 
	else if (jt.getCurrentToken().charAt(0) == '(') {
	    jt.pointerBack();
	    // '('
	    jt.advance();

	    // expression
	    compileExpression();

	    // ')'
	    jt.advance();
	} 
	// unaryOp term
	else if (jt.getCurrentToken().charAt(0) == '~' || jt.getCurrentToken().charAt(0) == '-') {
	    jt.pointerBack();
	    // '-' | '~', i.e. unaryOp
	    Character unaryOp = getNextToken().charAt(0);	    

	    // term
	    compileTerm();

	    vmFile.writeNegNot(unaryOp);
	}
	// integerConstant
	else if (jt.getTokenType() == JGram.INT_CONST) {
	    jt.pointerBack();
	    vmFile.writePush("constant", Integer.parseInt(getNextToken()));
	} 
	// keywordConstant
	else if (jt.getTokenType() == JGram.KEYWORD) {
	    // 'true' is mapped on constant -1
	    if (jt.keyword() == JGram.TRUE) {
		vmFile.writePush("constant", 1);
		vmFile.writeNegNot('-');
	    }
	    // 'false' and 'null' mapped on constant 0
	    else if (jt.keyword() == JGram.FALSE || jt.keyword() == JGram.NULL) {
		// 0 for 'false' and 'null'
		vmFile.writePush("constant", 0);
	    } 
	    // 'this'
	    else if (jt.keyword() == JGram.THIS){
		vmFile.writePush("pointer", 0);
	    }
	}
	// stringConstant 
	else if (jt.getTokenType() == JGram.STRING_CONST) {
	    jt.pointerBack();
	    //wNextToken();
	    String str = getNextToken();

	    vmFile.writePush("constant", str.length());
	    vmFile.writeCall("String.new",1);

	    // push each char's int equivalent to stack
	    // and build string with appendChar method
	    for (int i = 0; i < str.length(); i++){
		vmFile.writePush("constant",(int)str.charAt(i));
		vmFile.writeCall("String.appendChar", 2);
	    }
	}
    }

    /**
     * Compiles a (possibly empty) comma-seperated list of expressions.
     * expressionList: (expression (',' expression)*)?
     * @throws IOException
     */
    private void compileExpressionList() throws IOException {
	jt.advance();
	if (jt.getCurrentToken().charAt(0) == ')') {
	    jt.pointerBack();
	    return;
	} else if (jt.getCurrentToken().charAt(0) == ',') {
	    // ','
	} else {
	    jt.pointerBack();
	    parameterCountToCallFunc++;
	    compileExpression();
	}
	compileExpressionList();
    }
}
