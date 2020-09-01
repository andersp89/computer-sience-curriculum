import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Parser {
    private String curCommand = null;
    private Scanner inputFile;
    private Set<String> arithmeticOps = new HashSet<>();
    private String commandType;
    private String argument1;
    private String argument2;

    public Parser(Scanner inputFile) {
	if(inputFile == null) throw new IllegalArgumentException("Please provide input file");
	this.inputFile = inputFile;
	
	// populate set with arithmetic operation names
	arithmeticOps.add("add");
	arithmeticOps.add("sub");
	arithmeticOps.add("neg");
	arithmeticOps.add("eq");
	arithmeticOps.add("gt");
	arithmeticOps.add("lt");
	arithmeticOps.add("and");
	arithmeticOps.add("or");
	arithmeticOps.add("not");
    }
    
    /**
     * returns true if there are more commands in input file
     * @return
     */
    public boolean hasMoreCommands() {
	return inputFile.hasNextLine();
    }

    /**
     * Reads the next command from the input and makes it the current command, i.e. curCommand
     * ignore comments "//" and empty spaces
     * Called only if hasMoreCommands() is true.
     */
    public void advance() {
	if (hasMoreCommands()) {
	    curCommand = inputFile.nextLine().trim();
	    if (curCommand.isEmpty()) {
		advance();
		return;
	    } else if (curCommand.charAt(0) == '/' && curCommand.charAt(1) == '/') {
		advance();
		return;
	    }
	    
	    // sort out comments of type [SOME_CODE // ANY COMMENT]
	    if (curCommand.contains("//")) {
		curCommand = curCommand.substring(0,curCommand.indexOf("//"));
	    }
	    
	    String[] commandByWord = curCommand.split("\\s+");
	    commandType = commandByWord[0];
	    // if length 1, then arg1 is "add", "sub", etc.
	    if (commandByWord.length == 1) { 
		argument1 = commandByWord[0];
		argument2 = null;
	    }
	    else if (commandByWord.length == 2) {
		argument1 = commandByWord[1];
		argument2 = null;
	    }
	    else  {
		argument1 = commandByWord[1];
		argument2 = commandByWord[2];
	    }
	    
	}
    }
    
    /**
     * Returns a constant representing the type of the current command.
     * C_ARITHMETIC is returned for all the arithmetic/logical commands
     * @return
     */
    public String commandType() {
	if (curCommand == null) throw new IllegalArgumentException("current command must be initialized.");
	if (arithmeticOps.contains(commandType))
	    return "C_ARITHMETIC";
	else if (commandType.equals("push"))
	    return "C_PUSH";
	else if (commandType.equals("pop"))
	    return "C_POP";
	else if (commandType.equals("label"))
	    return "C_LABEL";
	else if (commandType.equals("if-goto"))
	    return "C_IF";
	else if (commandType.equals("goto"))
	    return "C_GOTO";
	else if (commandType.equals("function"))
	    return "C_FUNCTION";
	else if (commandType.equals("return"))
	    return "C_RETURN";
	else if (commandType.equals("call"))
	    return "C_CALL";
	else
	    throw new IllegalArgumentException("The given command is not a valid command type");
    }
    
    
    /**
     * Returns a constant representing the type of the current command.
     * In the case of C_ARITHMETIC, the command itself (add, sub, etc.) is returned
     * Should not be called if current command is C_RETURN.
     */
    public String arg1() {
	if (curCommand == null) throw new IllegalArgumentException("current command must be initialized.");
	return argument1;
    }
    
    /**
     * Returns the second argument of the current command. Should be called only if the
     * current command is C_PUSH, C_POP, C_FUNCTION, or C_CALL.
     * @return
     */
    public String arg2() {
	if (curCommand == null) throw new IllegalArgumentException("current command must be initialized.");
	return argument2;
    }
}
