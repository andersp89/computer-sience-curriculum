import java.util.HashMap;

/**
 * Add the variable and its properties to the symbol table.
 * 
 * Variable properties (in Jack):
 * name (identifier)
 * type (int, char, boolean, class name)
 * kind (field, static, local, argument)
 * scope (class level, subroutine level)
 * @author anderspedersen
 *
 */
public class SymbolTable {
    HashMap<String, String[]> classST;
    HashMap<String, String[]> subrST;
    int classFieldCount;
    int classStaticCount;
    int subrArgumentCount;
    int subrLocalCount; 

    /**
     * Creates new symbol table
     */
    public SymbolTable() {
	classST = new HashMap<>();
	classFieldCount = 0;
	classStaticCount = 0;
	startSubroutine();
    }

    /**
     * Starts a new subroutine scope (i.e. resets the
     * subroutine's symbol table).
     */
    public void startSubroutine() {
	subrST = new HashMap<>();
	subrArgumentCount = 0;
	subrLocalCount = 0;
    }

    /**
     * Defines a new identifier of the given name, type
     * and kind, and assigns it a running index. STATIC
     * and FIELD identifiers have a class scope, while 
     * ARG and VAR identifiers have a subroutine scope.
     */
    public void define(String name, String type, String kind) {
	// static and field, i.e. class
	if (kind.equals("static")) {
	    String[] typeKindIndex = {type, kind, Integer.toString(classStaticCount)};
	    classST.put(name, typeKindIndex);
	    classStaticCount++;
	} 
	else if (kind.equals("field")) {
	    String[] typeKindIndex = {type, kind, Integer.toString(classFieldCount)};
	    classST.put(name, typeKindIndex);
	    classFieldCount++;
	}
	// arg and var, i.e. subroutine
	else if (kind.equals("argument")) {
	    String[] typeKindIndex = {type, kind, Integer.toString(subrArgumentCount)};
	    subrST.put(name, typeKindIndex);
	    subrArgumentCount++;
	} 
	else if (kind.equals("var")) {
	    String[] typeKindIndex = {type, "local", Integer.toString(subrLocalCount)};
	    subrST.put(name, typeKindIndex);
	    subrLocalCount++;
	} 
	else {
	    throw new IllegalArgumentException("Identifier is of unknown type and not STATIC, FIELD, ARG or VAR.");
	}
    }

    /**
     * Returns the number of variables of the given kind (field, static,
     * var, argument) already defined in the current scope.
     * @param kind
     * @return
     */
    public int varCount(String kind) {
	if (kind.equalsIgnoreCase("field")) {
	    return classFieldCount;
	} 
	else if (kind.equalsIgnoreCase("static")) {
	    return classStaticCount;
	}
	else if (kind.equalsIgnoreCase("var")) {
	    return subrLocalCount;
	}
	else if (kind.equalsIgnoreCase("argument")) {
	    return subrArgumentCount;
	} 
	else {
	    throw new IllegalArgumentException("Identifier is of unknown type and not STATIC, FIELD, ARG or VAR.");
	}
    }

    /**
     * Returns the kind of the named identifier in the 
     * current scope. If the identifier is unknown in 
     * the current scope, returns NONE.
     * kind (field, static, local, argument)
     */
    public String kindOf(String name) {
	String kindOfName = searchTypeKindIndex(name, 1);
	if (kindOfName.equals("field")) {
	    return "this";
	} else {
	    return kindOfName;
	}
    }

    /**
     * Returns the type of the named identifier in the 
     * current scope.
     * type (int, char, boolean, class name)
     * @param name
     * @return
     */
    public String typeOf(String name) {
	return searchTypeKindIndex(name, 0);
    }

    /**
     * Returns the index assigned to the named identifier
     * @param name
     * @return
     */
    public int indexOf(String name) {
	return Integer.parseInt(searchTypeKindIndex(name, 2));
    }

    /**
     * Returns the type, kind or index in symbol table of the supplied name
     * @param name
     * @param index
     * @return
     */
    private String searchTypeKindIndex(String name, int index) {
	if (classST.containsKey(name)) {
	    String[] typeKindIndex = classST.get(name);
	    return typeKindIndex[index];
	}
	else if (subrST.containsKey(name)) {
	    String[] typeKindIndex = subrST.get(name);
	    return typeKindIndex[index];
	} else {
	    return "NONE";
	}
    }
}
