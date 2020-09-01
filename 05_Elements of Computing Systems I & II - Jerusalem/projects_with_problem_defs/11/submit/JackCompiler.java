import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Recursive LL(1) top-down compiler that compiles the "Jack" programming language
 * into the intermediary "VM code", to be run on a virtual machine supporting its format. 
 * The input ".jack" file is tokenized by 'JackTokenizer', parsed by 'CompilationEngine' and
 * outputted in a new ".vm"-file by VMWriter.  
 */

public class JackCompiler {

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
		outPath = inPath.getAbsolutePath().substring(0, inPath.getAbsolutePath().lastIndexOf(".")) + ".vm";
	    } 
	    else if (inPath.isDirectory()) {
		jackFiles = getVMFiles(inPath);
		if (jackFiles.size() == 0) 
		    throw new IllegalArgumentException("No jack files in directory.");
	    }

	    // write one .vm file for each .jack file
	    for (File file : jackFiles) {
		if (inPath.isDirectory()) {
		    outPath = inPath.getAbsolutePath() + "/" + file.getName().substring(0,file.getName().indexOf(".")) + ".vm"; //Delete N when done!
		}

		Scanner fileIn = new Scanner(file);
		JackTokenizer jt = new JackTokenizer(fileIn);
		FileWriter vmFile = new FileWriter(outPath, false);
		CompilationEngine cpe = new CompilationEngine(jt, vmFile);
		cpe.compileClass();

		fileIn.close();
		vmFile.close();
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

}
