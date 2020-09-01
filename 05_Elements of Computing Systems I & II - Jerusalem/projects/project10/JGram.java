/**
 * Class containing the Jack grammar (in short "JGram")
 * @author anderspedersen
 *
 */
public final class JGram {
    //constants for type, used in JackAnalyzer for type checking
    public final static String KEYWORD = "keyword";
    public final static String SYMBOL = "symbol";
    public final static String IDENTIFIER = "identifier";
    public final static String INT_CONST = "integerConstant";
    public final static String STRING_CONST = "stringConstant";

    //constants for "keyword", used in CompilationEngine for error checking
    public final static String CLASS = "class";
    public final static String METHOD = "method";
    public final static String FUNCTION = "function";
    public final static String CONSTRUCTOR = "constructor";
    public final static String INT = "int";
    public final static String BOOLEAN = "boolean";
    public final static String CHAR = "char";
    public final static String VOID = "void";
    public final static String VAR = "var";
    public final static String STATIC = "static";
    public final static String FIELD = "field";
    public final static String LET = "let";
    public final static String DO = "do";
    public final static String IF = "if";
    public final static String ELSE = "else";
    public final static String WHILE = "while";
    public final static String RETURN = "return";
    public final static String TRUE = "true";
    public final static String FALSE = "false";
    public final static String NULL = "null";
    public final static String THIS = "this";
}
