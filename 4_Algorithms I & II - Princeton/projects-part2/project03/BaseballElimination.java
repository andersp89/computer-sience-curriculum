package week3;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Given the standings in a sports division at some point during 
 * the season, determine which teams have been mathematically 
 * eliminated from winning their division.
 * 
 * A team is mathematically eliminated if it cannot possibly 
 * finish the season in (or tied for) first place. The goal is 
 * to determine exactly which teams are mathematically eliminated. 
 * For simplicity, we assume that no games end in a tie (as is 
 * the case in Major League Baseball) and that there are no 
 * rainouts (i.e., every scheduled game is played).
 * @author anderspedersen
 */
public class BaseballElimination {
    private int SOURCE = 0;
    private int N, SINK;
    private HashMap<String, Integer> teamIndex = new HashMap<>();
    private HashMap<Integer, String> indexTeam = new HashMap<>();
    private int[] wins, losses, remainingGames;
    private int[][] numberOfGamesLeft;

    /**
     * create a baseball division from given filename in format specified below
     * @param filename
     */
    public BaseballElimination(String filename) {
	In in = new In(filename);
	N = in.readInt();
	SINK = (2 + N * (N - 1) / 2 + N)-1;

	wins = new int[N];
	losses = new int[N];
	remainingGames = new int[N];
	numberOfGamesLeft = new int[N][N];

	String teamName;
	int i = 0;
	while(!in.isEmpty()) {
	    teamName = in.readString(); 
	    teamIndex.put(teamName, i);
	    indexTeam.put(i, teamName);

	    wins[i] = in.readInt();
	    losses[i] = in.readInt();
	    remainingGames[i] = in.readInt();

	    for (int j = 0; j < N; ++j)
		numberOfGamesLeft[i][j] = in.readInt();

	    ++i;
	}
    }

    /**
     * number of teams
     * @return
     */
    public int numberOfTeams() {
	return N;
    }

    /**
     * all teams
     */
    public Iterable<String> teams() {
	return Collections.unmodifiableSet(teamIndex.keySet());
    }
    /**
     * number of wins for given team
     * @param team
     * @return
     */
    public int wins(String team) {
	checkTeam(team);
	return wins[teamIndex.get(team)];
    }

    /**
     * number of losses for given team
     * @param team
     * @return
     */
    public int losses(String team) {
	checkTeam(team);
	return losses[teamIndex.get(team)];
    }

    /**
     * number of remaining games for given team
     * @param team
     * @return
     */
    public int remaining(String team) {
	checkTeam(team);
	return remainingGames[teamIndex.get(team)]; 
    }
    /**
     * number of remaining games between team1 and team2
     * @param team1
     * @param team2
     * @return
     */
    public int against(String team1, String team2) {
	checkTeam(team1);
	checkTeam(team2);
	return numberOfGamesLeft[teamIndex.get(team1)][teamIndex.get(team2)];
    }
    /**
     * is given team eliminated?
     * @param team
     * @return
     */
    public boolean isEliminated(String team) {
	checkTeam(team);
	return trivialElimination(team) || isEliminatedByMaxFlow(team);
    }

    /**
     * trivial elimination, i.e.:
     * w[x] + r[x] < w[i], where team is x
     * @return
     */
    private boolean trivialElimination(String team) {
	for (String s : teamIndex.keySet()) {
	    if (wins(team) + remaining(team) < wins(s)) {
		return true;
	    }
	}
	return false;
    }

    private boolean isEliminatedByMaxFlow(String team) {
	FlowNetwork fn = createFlowNetwork(team);
	FordFulkerson ff = new FordFulkerson(fn, SOURCE, SINK);

	// if capacity is not filled up, then eliminated
	for (FlowEdge edge: fn.adj(SOURCE)) {
	    if (edge.flow() < edge.capacity()) {
		return true;
	    }
	}

	return false;
    }

    /**
     * create FlowNetwork
     */
    private FlowNetwork createFlowNetwork(String team) {	
	FlowNetwork fn = new FlowNetwork(2 + N * (N - 1) / 2 + N);
	int gameCount = 1; //games i-j

	for (int i = 0; i < N; ++i)
	    if (i != teamIndex.get(team))
		for (int j = i+1; j < N; ++j) 
		    if (j != teamIndex.get(team)) {
			//StdOut.println(i + ":" + getTeamVertex(i) + "-" + j + ":" + getTeamVertex(j));
			//connect source to each game vertex
			fn.addEdge(new FlowEdge(SOURCE, gameCount, numberOfGamesLeft[i][j]));

			//connect each game vertex i-j to teams i and j.
			fn.addEdge(new FlowEdge(gameCount, getTeamVertex(i), Double.POSITIVE_INFINITY));
			fn.addEdge(new FlowEdge(gameCount++, getTeamVertex(j), Double.POSITIVE_INFINITY));		
		    }

	for (int i = 0; i < N; ++i) {
	    if (i == teamIndex.get(team))
		continue;

	    int capacity = wins(team) + remaining(team) - wins(indexTeam.get(i));

	    if (capacity < 0)
		capacity = 0;

	    fn.addEdge(new FlowEdge(getTeamVertex(i), SINK, capacity));
	}

	return fn;
    }

    private void checkTeam(String team) {
	if (team == null || !teamIndex.containsKey(team))
	    throw new IllegalArgumentException();
    }

    private int getTeamVertex(int i) {
	return 1 + N * (N - 1) / 2 + i;
    }

    /**
     * subset R of teams that eliminates given team; null if not eliminated
     * @param team
     * @return
     */
    public Iterable<String> certificateOfElimination(String team) {
	checkTeam(team);

	Set<String> set = new HashSet<String>();

	// trivial elimination
	for (int i = 0; i < N; i++) 
	    if (wins(team) + remaining(team) < wins(indexTeam.get(i)))
		set.add(indexTeam.get(i));

	FlowNetwork fn = createFlowNetwork(team);
	FordFulkerson ff = new FordFulkerson(fn, SOURCE, SINK);

	// check for each team, if they are part of source
	// if so, then they are mathematically eliminating team x
	for (FlowEdge edge : fn.adj(SOURCE))
	    for (String t : teams()) {
		int teamVertex = getTeamVertex(teamIndex.get(t));
		if (ff.inCut(teamVertex))
		    set.add(t);
	    }
	
	if (set.isEmpty())
	    return null;

	return set;
    }

    public static void main(String[] args) {
	BaseballElimination division = new BaseballElimination(args[0]);

	for (String team : division.teams()) {
	    if (division.isEliminated(team)) {
		StdOut.print(team + " is eliminated by the subset R = { ");
		for (String t : division.certificateOfElimination(team)) {
		    StdOut.print(t + " ");
		}
		StdOut.println("}");
	    }
	    else {
		StdOut.println(team + " is not eliminated");
	    }
	}
    }
}
