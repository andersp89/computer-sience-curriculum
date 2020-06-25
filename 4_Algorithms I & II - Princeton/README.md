<h1>Algorithms I & II</h1>

Completed course [Algorithms I](https://www.coursera.org/learn/algorithms-part1) and [Algorithms II](https://www.coursera.org/learn/algorithms-part2) by **Princeton University**. The course is an introduction to fundamental data types, algorithms, and data structures. The emphasis is on applications and scientific performance analysis of Java implementations.

* **Part I** focuses on elementary data structures, sorting, and searching. Topics include union−find, binary search, stacks, queues, bags, insertion sort, selection sort, shellsort, quicksort, 3-way quicksort, mergesort, heapsort, binary heaps, binary search trees, red−black trees, separate-chaining and linear-probing hash tables, Graham scan, and kd-trees.

* **Part II** focuses on graph and string-processing algorithms. Topics include depth-first search, breadth-first search, topological sort, Kosaraju−Sharir, Kruskal, Prim, Dijkistra, Bellman−Ford, Ford−Fulkerson, LSD radix sort, MSD radix sort, 3-way radix quicksort, multiway tries, ternary search tries, Knuth−Morris−Pratt, Boyer−Moore, Rabin–Karp, regular expression matching, run-length coding, Huffman coding, LZW compression and Burrows−Wheeler transform. Part II also introduces reductions and intractability, including the P = NP problem.

<h2>Projects of part I</h2>
* [Week 1: Union-Find, Analysis of Algorithms](/projects-part1/project01)
 - **Percolation.java** - A model for the percolation problem, determines if a 2d system of open / closed sites percolates from top to bottom.
 - **PercolationStats.java** - Generates statistics using the percolation model.
* [Week 2: Stacks and Queues, Elementary Sorts](/projects-part1/project02)
 - **Deque.java** - A generic double ended queue implementation, double-linked list based.
 - **RandomizedQueue.java** - A generic random queue implementation, array based.
 - **Subset.java** - Prints n number of random strings provided through standard input.
* [Week 3: Mergesort, Quicksort](/projects-part1/project03)
 - **Point.java** - A simple point class.
 - **Brute.java** - A n^4 alg for calculating 4 collinear points.
 - **Fast.java** - A fast implementation for finding collinear points.
* [Week 4: Priority Queues, Elementary Symbol Tables](/projects-part1/project04)
 - **Board.java** - Represents a sliding puzzle board.
 - **Solver.java** - Uses A* and to find solution to the puzzle board.
* [Week 5: Balanced Search Trees, Geometric Applications of BSTs](/projects-part1/project05)
 - **PointSet.java** - A set of points on a 2D Euclidian plane, some simple function like nearest and contains.
 - **kDTree.java** - Uses a 2d tree to more efficiently perform functions such as nearest and contains.
* Week 6: Hash Tables, Symbol Table Applications

<h2>Projects of part II</h2>
* [Week 1: Undirected Graphs, Directed Graphs](/projects-part2/project01)
 - **WordNet.java** - Grouping words into sets of synonyms called synsets in a digraph.
 - **SAP.java** - Finding the Shortest Ancestral Path (i.e. path of minimum total length) between any two synonyms
 - **Outcast.java** - Returning the word in synset, i.e. collection of synonyms, with the largest distance to the others
* [Week 2: Minimum Spanning Trees, Shortest Paths](/projects-part2/project02)
 - **SeamCarver.java** - Automatically resize picture by removing "least significant" pixels horizontally and vertically
* [Week 3: Maximum Flow and Minimum Cut, Radix Sorts](/projects-part2/project03)
 - **BaseballElimination.java** - Determines which teams have been mathematically eliminated from winning their division by use of Maximum Flow.
* [Week 4: Tries, Substring Search](/projects-part2/project04)
 - **BoogleSolver.java** - Program to play the word game Boggle®.
 - **TrieSET.java** - Data structure used to solve game.
* [Week 5: Regular Expressions, Data Compression](/projects-part2/project05)
 The Burrows–Wheeler data compression algorithm consists of three algorithmic components, which are applied in succession:
 - **BurrowsWheeler.java** - Transforms text file into file in which sequences of the same character occur near each other many times.
 - **MoveToFront.java** - Given BurrowWheeler, convert it into a text file in which certain characters appear much more frequently than others.
 - **CircularSuffixArray.java** - Given MoveToFront, compress it by encoding frequently occurring characters with short codewords and infrequently occurring characters with long codewords.
 * Week 6: Reductions, Linear Programming, Intractability