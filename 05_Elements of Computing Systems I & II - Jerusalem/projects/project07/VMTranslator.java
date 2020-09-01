import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class VMTranslator {

    public static void main(String[] args) {
	if(args.length != 1) {
	    System.out.println("Usage:\njava VMTranslator [Prog.vm]");
	    System.exit(1);
	}
	
	try {
	    // open file, and initialize writer
	    Scanner inputFile = new Scanner(new File(args[0]));
	    String outFileName = args[0].split("\\.")[0] + ".asm";
	    FileWriter writeFile = new FileWriter(outFileName, false);
	    
	    // initialize parser and CodeWriter
	    Parser parser = new Parser(inputFile);
	    CodeWriter writer = new CodeWriter(writeFile);

	    // go through file, line by line, and translate to assembly
	    String curCommand;
	    while(parser.hasMoreCommands()) {	
		parser.advance();
		curCommand = parser.getCurCommand();
		String commandType = parser.commandType();
		if (commandType.equals("C_ARITHMETIC")) {  
		    writer.writeArithmetic(curCommand);
		} else if (commandType.equals("C_PUSH") || commandType.equals("C_POP")) {
		    writer.writePushPop(commandType, parser.arg1(), Integer.parseInt(parser.arg2()));
		}
	    }
	    inputFile.close();
	    writer.close();
	} catch (FileNotFoundException e) {
	    System.out.println("Sorry, the name of the file was not found, try again");
	    e.printStackTrace();
	}
	catch (IOException e) {
	    System.out.println("Something went wrong, as we wrote to the file");
	    e.printStackTrace();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	
	System.out.println("Done!");
    }



}
