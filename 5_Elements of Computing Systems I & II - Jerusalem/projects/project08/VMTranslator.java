import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
    private Parser parser = null;
    private CodeWriter writer = null;
    
    public static void main(String[] args) {
	if(args.length != 1) {
	    System.out.println("Usage:\njava VMTranslator [file.vm | directory]");
	    System.exit(1);
	}
	
	try {
	    File inPath = new File(args[0]);
	    String outPath = "";
	    
	    // get .vm file(s) and save in vmFiles[]
	    ArrayList<File> vmFiles = new ArrayList<>();
	    if (inPath.isFile()) {
		String path = inPath.getAbsolutePath();
		if (!getExtension(path).equals(".vm"))
		    throw new IllegalArgumentException("file must be .vm!");
		vmFiles.add(inPath);
		outPath = inPath.getAbsolutePath().substring(0, inPath.getAbsolutePath().lastIndexOf(".")) + ".asm";
	    } 
	    else if (inPath.isDirectory()) {
		vmFiles = getVMFiles(inPath);
		if (vmFiles.size() == 0) 
		    throw new IllegalArgumentException("No vm files in directory.");
		outPath = inPath.getAbsolutePath() + "/" + inPath.getName() + ".asm";
 	    }
	    
	    // write assembly for each .vm file
	    FileWriter fileOut = new FileWriter(outPath, false);
	    VMTranslator vmt = new VMTranslator();
	    vmt.writer = new CodeWriter(fileOut);
	    
	    if  (inPath.isDirectory())
		vmt.writer.writeInit();
	    
	    for (File file : vmFiles) {
		vmt.writer.setFileName(file.getName().split("\\.")[0]);
		Scanner inFile = new Scanner(file);
		vmt.parser = new Parser(inFile);
		vmt.writeToFile();
		inFile.close();
	    }

	    /*if (inFileOrDir.isDirectory()) {
		FileWriter outFile = new FileWriter(inFileOrDir.getName() + ".asm", false); 
		vmt.writer = new CodeWriter(outFile);
		File[] vmFiles = inFileOrDir.listFiles((dir, name) -> name.endsWith(".vm"));
		vmt.writer.writeInit(); // initialize stack pointer and call Sys.init

		// parse and assemble each file in dir
		for (File file : vmFiles) {
		    vmt.writer.setFileName(file.getName().split("\\.")[0]);
		    vmt.parser = new Parser(new Scanner(file)); // Scanner inputFile = new Scanner(file);
		    vmt.writeToFile();
		}
	    } else {
		// if not directory, initialize Parser and CodeWriter with file
		Scanner inFile = new Scanner(new File(args[0]));
		vmt.parser = new Parser(inFile);
		FileWriter outFile = new FileWriter(args[0].split("\\.")[0] + ".asm", false);
		vmt.writer = new CodeWriter(outFile);
		vmt.writer.setFileName(args[0].split("\\.")[0]);
		vmt.writeToFile();
		inFile.close();
	    }*/
	    vmt.writer.close();
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

	System.out.println("Program finished!");
    }

    /**
     * go through file, line by line, and translate to assembly
     * @param fileName
     */
    private void writeToFile() throws IOException {
	if (parser == null || writer == null) throw new IllegalArgumentException();

	while(parser.hasMoreCommands()) {	
	    parser.advance();
	    String commandType = parser.commandType();
	    if (commandType.equals("C_ARITHMETIC")) 
		writer.writeArithmetic(parser.arg1());
	    else if (commandType.equals("C_PUSH") || commandType.equals("C_POP"))
		writer.writePushPop(commandType, parser.arg1(), Integer.parseInt(parser.arg2()));
	    else if (commandType.equals("C_LABEL"))
		writer.writeLabel(parser.arg1());
	    else if (commandType.equals("C_GOTO"))
		writer.writeGoto(parser.arg1());
	    else if (commandType.equals("C_IF"))
		writer.writeIf(parser.arg1());
	    else if (commandType.equals("C_FUNCTION")) 
		writer.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
	    else if (commandType.equals("C_RETURN")) 
		writer.writeReturn();
	    else if (commandType.equals("C_CALL"))
		writer.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
	}
    }
    
    /**
     * helper to get extension of file, e.g. ".vm"
     * @param fileName
     * @return
     */
    private static String getExtension(String fileName) {
	int index = fileName.lastIndexOf('.'); 
	if (index != -1)
	    return fileName.substring(index);
	else
	    throw new IllegalArgumentException("passed in file must end by .vm");
    }
    
    /**
     * helper to get all files with ".vm" in dir
     * @param inPath
     * @return
     */
    private static ArrayList<File> getVMFiles(File inPath) {
	File[] files = inPath.listFiles();
	ArrayList<File> vmFiles = new ArrayList<>();
	for (File file : files) {
	    if (file.getName().endsWith(".vm"))
		vmFiles.add(file);
	}
	return vmFiles;
    }



}
