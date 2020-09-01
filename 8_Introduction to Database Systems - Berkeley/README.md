# Introduction to Database Systems

Completed course [Introduction to Database Systems](https://cs186berkeley.net/) by **UC Berkeley**. The course is focused on foundational system principles of modern Database Management Systems (DBMS) with the goal to use existing and build new DBMS technology. 

* [Project 1: SQL](cs186/sp20-proj1/proj1.sql)
* [Project 2: B+ Trees](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/index): Implemented alternative 2 B+ Tree
* Project 3: Joins & Query Optimization
	* [Part 1: Join Algorithms](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/query): Block nested loop join (BNLJ), Sort merge join and grace hash join
	* [Part 2: Optimized Query Plans](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/query/QueryPlan.java): Both single-access (heap or index) and join algorithms
* [Project 4: Concurrency](projects/project04): to implement multigranularity locking
	* [Part 1: Lock Manager](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/concurrency/LockContext.java): Manages all the locks, treating each resource as independent
	* [Part 2: Implement LockContext and LockUtil](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/concurrency): Connect objects acoording to hierarchy and provide utility functions
	* [Part 3: Integrate locking into codebase](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/Database.java)
* [Project 5: Recovery](sp20-moocbase/src/main/java/edu/berkeley/cs186/database/recovery/ARIESRecoveryManager.java): Implemented ARIES Logging handling both forward processing and restart recovery