import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private FileWriter outputFile;
    private int jumpInstance = 0;
    
    public CodeWriter(FileWriter outputFile) {
	if(outputFile == null) throw new IllegalArgumentException("Please provide outputfile.");
	this.outputFile = outputFile;
    }
    
    /**
     * Writes to the output file the assembly code that implements the given
     * arithmetic command.
     */
    public void writeArithmetic(String command) throws IOException {
	if (command.equals("add")) {
	    outputFile.write("// add\n"
	    	+ arithTemplate1()
	    	+ "M=M+D\n");
	}
	else if (command.equals("sub")) {
	    outputFile.write("// sub \n"
	    	+ arithTemplate1()
	    	+ "M=M-D\n");
	}
	else if (command.equals("neg")) {
	    outputFile.write("// neg\n"
	    	+ "@SP\n"
	    	+ "A=M-1\n"
	    	+ "M=-M\n");
	}
	else if (command.equals("eq")) {
	    outputFile.write("// eq\n"
	    	+ arithTemplate2("JNE")); // !=
	    jumpInstance++;
	}
	else if (command.equals("gt")) {
	    outputFile.write("// gt\n"
	    	+ arithTemplate2("JLE")); // <= (false condition)
	    jumpInstance++;
	}
	else if (command.equals("lt")) {
	    outputFile.write("// lt\n"
	    	+ arithTemplate2("JGE")); // >= (false condition)
	    jumpInstance++;
	}
	else if (command.equals("and")) {
	    outputFile.write("// and\n"
	    	+ arithTemplate1()
	    	+ "M=M&D\n");
	}
	else if (command.equals("or")) {
	    outputFile.write("// or\n"
	    	+ arithTemplate1()
	    	+ "M=M|D\n");
	}
	else if (command.equals("not")) {
	    outputFile.write("// not\n"
	    	+ "@SP\n"
	    	+ "A=M-1\n"
	    	+ "M=!M");
	} else {
	    throw new IllegalArgumentException("Call was a none-arithmetic operation");
	}
    }
    
    /**
     * Writes to the output file the assembly code that implements the given command,
     * where command is either C_PUSH or C_POP
     * Push: pushes from memory segment to stack and increments stack pointer
     * Pop: pops the last added element from Stack to memory segment and decrements stack pointer
     * @param command
     * @param segment
     * @param index
     */
    public void writePushPop(String command, String segment, int index) throws IOException {
	if (command.equals("C_PUSH")) {
	    if (segment.equals("constant")) {
		outputFile.write("// push constant " + index + "\n"
		    	+ "@" + index + "\n"
		    	+ "D=A\n"
		    	+ "@SP\n"
		    	+ "A=M\n"
		    	+ "M=D\n"
		    	+ "@SP\n"
		    	+ "M=M+1\n");	
	    }
	    else if (segment.equals("local")) 
		outputFile.write(pushTemplate("LCL", index, false));
	    else if (segment.equals("argument"))
		outputFile.write(pushTemplate("ARG", index, false));
	    else if (segment.equals("this"))
		outputFile.write(pushTemplate("THIS", index, false));
	    else if (segment.equals("that"))
		outputFile.write(pushTemplate("THAT", index, false));
	    else if (segment.equals("pointer") && index == 0)
		outputFile.write(pushTemplate("THIS", index, true));
	    else if (segment.equals("pointer") && index == 1)
		outputFile.write(pushTemplate("THAT", index, true));
	    else if (segment.equals("static"))
		outputFile.write(pushTemplate(String.valueOf(16 + index), index, true));
	    else if (segment.equals("temp"))
		outputFile.write(pushTemplate("R5", index + 5, false));
	    else
		throw new IllegalArgumentException("Call was a none-push operation");
	} 
	else if (command.equals("C_POP")) {
	    if (segment.equals("local"))
		outputFile.write(popTemplate("LCL", index, false));
	    else if (segment.equals("argument")) 
		outputFile.write(popTemplate("ARG", index, false));
	    else if (segment.equals("this"))
		outputFile.write(popTemplate("THIS", index, false));
	    else if (segment.equals("that"))
		outputFile.write(popTemplate("THAT", index, false));
	    else if (segment.equals("pointer") && index == 0)
		outputFile.write(popTemplate("THIS", index, true));
	    else if (segment.equals("pointer") && index == 1)
		outputFile.write(popTemplate("THAT", index, true));
	    else if (segment.equals("static"))
		outputFile.write(popTemplate(String.valueOf(16 + index), index, true));
	    else if (segment.equals("temp"))
		outputFile.write(popTemplate("R5", index + 5, false));
	    else
		throw new IllegalArgumentException("Call was a none-pop operation");
	}
    }
    
    /**
     * Closes the output file.
     * @throws IOException
     */
    public void close() throws IOException {
	outputFile.close();
    }
    
    
    /**
     * Template for arithmetic operations add sub and or
     * gets last item of stack and the address of the second last
     * @return
     */
    private String arithTemplate1() {
	return "@SP\n"
		+ "AM=M-1\n"
		+ "D=M\n"
		+ "A=A-1\n";
    }
    
    /**
     * Template for artihmetic operations gt lt eq
     * gets two last items of stack, subtract them, and evaluate them with jump expression
     * evaluates to "-1" if true, "0" if false.
     */
    private String arithTemplate2(String jumpType) {
	return "@SP\n"
		+ "AM=M-1\n"
		+ "D=M\n"
		+ "A=A-1\n"
		+ "D=M-D\n"
		+ "@FALSE" + jumpInstance + "\n"
		+ "D;" + jumpType + "\n"
		+ "@SP\n"
		+ "A=M-1\n"
		+ "M=-1\n"
		+ "@CONTINUE" + jumpInstance + "\n"
		+ "0;JMP\n"
		+ "(FALSE" + jumpInstance + ")\n"
		+ "@SP\n"
		+ "A=M-1\n"
		+ "M=0\n"
		+ "(CONTINUE" + jumpInstance + ")\n";
    }
    
    /**
     * Template for the following push memory segments: local, argument, this, that, static
     * temp, pointer
     * Go to address, i.e. base address (stored in "segment") + "index", saves value in D, 
     * updates stack with value, and increments stack pointer. 
     * @param segment
     * @param index
     * @param isDirect
     * @return
     */
    private String pushTemplate(String segment, int index, boolean isStaticOrPointer) {
	// When it is pointer, read the data stored in THIS or THAT
	// When it is static, read the data stored in that address
	String noStaticOrPointerCode = isStaticOrPointer? "" : "@" + index + "\n" + "A=D+A\n" + "D=M\n";
	return "@" + segment + "\n"
		+ "D=M\n"
		+ noStaticOrPointerCode
		+ "@SP\n"
		+ "A=M\n"
		+ "M=D\n"
		+ "@SP\n"
		+ "M=M+1\n";
    }
    
    /**
     * Template for the following pop memory segments: local, argument, this, that, static
     * temp, pointer
     * Go to address, i.e. base address + "index", saves address-to-pop-to in R13, decrements pointer,
     * saves stack-element-to-pop in D, go to address-to-pop-to saved in R13, 
     * and save stack-element-to-pop. 
     * @param segment
     * @param index
     * @param isStaticOrPointer
     * @return
     */
    private String popTemplate(String segment, int index, boolean isStaticOrPointer) {
	// When it is a pointer R13 will store the address of THIS or THAT, as per spec
	// When it is a static R13 will store the index address, as per spec
	
	String noStaticOrPointerCode = isStaticOrPointer? "D=A\n" : "D=M\n" + "@" + index + "\n" + "D=D+A\n";
	return "@" + segment + "\n"
		+ noStaticOrPointerCode
		+ "@R13\n"
		+ "M=D\n"
		+ "@SP\n"
		+ "AM=M-1\n"
		+ "D=M\n"
		+ "@R13\n"
		+ "A=M\n"
		+ "M=D\n";
    }
}
