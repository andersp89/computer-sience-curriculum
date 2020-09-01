import java.io.FileWriter;
import java.io.IOException;

/**
 * Emits VM code to the output .vm file
 * @author anderspedersen
 *
 */
public class VMWriter {
    private FileWriter vmFile;
    /**
     * Creates a new output .vm file and prepares it for writing
     */
    public VMWriter(FileWriter vmFile) {
	if (vmFile == null) throw new IllegalArgumentException("Argument cannot be null.");
	this.vmFile = vmFile;
    }
    
    /**
     * Writes a VM push command
     * @param segment
     * @param index
     */
    public void writePush(String segment, int index) throws IOException {
	vmFile.write("push " + segment + " " + index + "\n");
    }
    
    /**
     * Writes a VM pop command
     * @param segment
     * @param index
     */
    public void writePop(String segment, int index) throws IOException {
	vmFile.write("pop " + segment + " " + index + "\n");
    }
    
    /**
     * Writes a VM arithmetic-logical command.
     * @param command
     */
    public void writeArithmetic(Character command) throws IOException {
	if (command == null) throw new IllegalArgumentException("Must be a single character math/logical symbol");
	
	// arithmetic commands
	if (command == '+') {
	    vmFile.write("add\n");
	} else if (command == '-') {
	    vmFile.write("sub\n");
	} else if (command == '*') {
	    writeCall("Math.multiply", 2);
	} else if (command == '/') {
	    writeCall("Math.divide", 2);
	} 
	// logical commands
	else if (command == '<') {
	    vmFile.write("lt\n");
	} else if (command == '>') {
	    vmFile.write("gt\n");
	} else if (command == '&') {
	    vmFile.write("and\n");
	} else if (command == '|') {
	    vmFile.write("or\n");
	} else if (command == '=') {
	    vmFile.write("eq\n");
	} else {
	    throw new IllegalArgumentException("Must be valid arithmetic or logical command.");
	}
    }
    
    /**
     * Taking care of negate (-) and not (~) in seperate function,
     * as negate (-) and subtraction (-) has same symbol 
     * @param command
     * @throws IOException
     */
    public void writeNegNot(Character command) throws IOException {
	if (command == null) throw new IllegalArgumentException("Must be ~ or -");
	
	if (command == '-') {
	    vmFile.write("neg\n");
	} else if (command == '~') {
	    vmFile.write("not\n");
	}
    }
    
    /**
     * Writes a VM label command
     * @param label
     */
    public void writeLabel(String label) throws IOException {
	vmFile.write("label " + label + "\n");
    }
    
    /**
     * Writes a VM goto command
     * @param label
     */
    public void writeGoto(String label) throws IOException {
	vmFile.write("goto " + label + "\n");
    }
    
    /**
     * Writes a VM if-goto command
     * @param label
     */
    public void writeIf(String label) throws IOException {
	vmFile.write("if-goto " + label + "\n");
    }
    
    /**
     * Writes a VM call command
     * @param name
     * @param nArgs
     */
    public void writeCall(String name, int nArgs) throws IOException {
	vmFile.write("call " + name + " " + nArgs + "\n");
    }
    
    /**
     * Writes a VM function command
     * @param name
     * @param nLocals
     */
    public void writeFunction(String name, int nLocals) throws IOException {
	vmFile.write("function " + name + " " + nLocals + "\n");
    }
    
    /**
     * Writes a VM return command
     */
    public void writeReturn() throws IOException {
	vmFile.write("return\n");
    }
}
