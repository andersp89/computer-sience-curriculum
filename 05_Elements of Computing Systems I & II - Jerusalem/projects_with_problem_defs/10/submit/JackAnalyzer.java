import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JackAnalyzer {

    public static void main(String[] args) {
	if(args.length != 1) {
	    System.out.println("Usage:\njava JackAnalyzer [file.jack | directory]");
	    System.exit(1);
	}

	try {
	    File inPath = new File(args[0]);
	    String outPath = "";

	    // get .jack file(s) and save in vmFiles[]
	    ArrayList<File> jackFiles = new ArrayList<>();
	    if (inPath.isFile()) {
		String path = inPath.getAbsolutePath();
		if (!getExtension(path).equals(".jack"))
		    throw new IllegalArgumentException("file must be .jack!");
		jackFiles.add(inPath);
		outPath = inPath.getAbsolutePath().substring(0, inPath.getAbsolutePath().lastIndexOf(".")) + ".xml";
	    } 
	    else if (inPath.isDirectory()) {
		jackFiles = getVMFiles(inPath);
		if (jackFiles.size() == 0) 
		    throw new IllegalArgumentException("No jack files in directory.");
	    }

	    // write one .xml file for each .jack file
	    for (File file : jackFiles) {
		if (inPath.isDirectory()) {
		    outPath = inPath.getAbsolutePath() + "/" + file.getName().substring(0,file.getName().indexOf(".")) + ".xml"; //Delete N when done!
		}

		Scanner fileIn = new Scanner(file);
		JackTokenizer jt = new JackTokenizer(fileIn);

		FileWriter fileOut = new FileWriter(outPath, false);
		CompilationEngine cpe = new CompilationEngine(jt, fileOut);
		cpe.compileClass();
		
		fileIn.close();
		fileOut.close();
	    }

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
     * helper to get extension of file, e.g. ".vm"
     * @param fileName
     * @return
     */
    private static String getExtension(String fileName) {
	int index = fileName.lastIndexOf('.'); 
	if (index != -1)
	    return fileName.substring(index);
	else
	    throw new IllegalArgumentException("passed in file must end by .jack");
    }

    /**
     * helper to get all files with ".vm" in dir returned as array
     * @param inPath
     * @return
     */
    private static ArrayList<File> getVMFiles(File inPath) {
	File[] files = inPath.listFiles();
	ArrayList<File> vmFiles = new ArrayList<>();
	for (File file : files) {
	    if (file.getName().endsWith(".jack"))
		vmFiles.add(file);
	}
	return vmFiles;
    }
    
    //private void jackTokenizerTester() {
	// write test program to test xlm output of tokenizer
	/* OBS: ALL METHODS correct, except tokenType 
	 * do not return UPPERCASE CONSTANT NAMES which may not be OK, instead lowercase.
	 * This helps setting curTokenType, that is used when writing it. Might be wrong.
	 * Also, symbol() method do not return char but string, as I need to escape lt, gt etc. 
	 */ 
	/*FileWriter fileOut = new FileWriter(outPath, false);
	String tokenType;
	String currentToken;
	fileOut.write("<tokens>\n");
	while (jt.hasMoreTokens()) {
	    jt.advance();
	    if (jt.getTokenType() == JGram.STRING_CONST) {
		currentToken = jt.getCurrentToken();
	    } else if (jt.getTokenType() == JGram.SYMBOL){
		currentToken = jt.getCurrentToken();
	    } else {
		currentToken = jt.getCurrentToken();
	    }
	    tokenType = jt.getTokenType();
	    fileOut.write("<" + tokenType + "> " + currentToken + " </" + tokenType + ">\n" );
	}
	fileOut.write("</tokens>\n");*/
    //}

}
