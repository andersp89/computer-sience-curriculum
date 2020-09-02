package edu.berkeley.cs186.database.query;

import edu.berkeley.cs186.database.TransactionContext;
import edu.berkeley.cs186.database.DatabaseException;
import edu.berkeley.cs186.database.common.iterator.BacktrackingIterator;
import edu.berkeley.cs186.database.databox.DataBox;
import edu.berkeley.cs186.database.table.Record;
import edu.berkeley.cs186.database.table.Schema;
import edu.berkeley.cs186.database.common.Pair;
import edu.berkeley.cs186.database.memory.Page;

import java.util.*;

public class SortOperator {
    private TransactionContext transaction;
    private String tableName;
    private Comparator<Record> comparator;
    private Schema operatorSchema;
    private int numBuffers;
    private String sortedTableName = null;

    public SortOperator(TransactionContext transaction, String tableName,
                        Comparator<Record> comparator) {
        this.transaction = transaction;
        this.tableName = tableName;
        this.comparator = comparator;
        this.operatorSchema = this.computeSchema();
        this.numBuffers = this.transaction.getWorkMemSize();
    }

    private Schema computeSchema() {
        try {
            return this.transaction.getFullyQualifiedSchema(this.tableName);
        } catch (DatabaseException de) {
            throw new QueryPlanException(de);
        }
    }

    /**
     * Interface for a run. Also see createRun/createRunFromIterator.
     */
    public interface Run extends Iterable<Record> {
        /**
         * Add a record to the run.
         * @param values set of values of the record to add to run
         */
        void addRecord(List<DataBox> values);

        /**
         * Add a list of records to the run.
         * @param records records to add to the run
         */
        void addRecords(List<Record> records);

        @Override
        Iterator<Record> iterator();

        /**
         * Table name of table backing the run.
         * @return table name
         */
        String tableName();
    }

    /**
     * Returns a NEW run that is the sorted version of the input run.
     * Can do an in memory sort over all the records in this run
     * using one of Java's built-in sorting methods.
     * Note: Don't worry about modifying the original run.
     * Returning a new run would bring one extra page in memory beyond the
     * size of the buffer, but it is done this way for ease.
     */
    public Run sortRun(Run run) {
        // TODO(proj3_part1): implement
        Run sortedRun = createRun();
        List<Record> unsortedRun = new ArrayList<>();
        Iterator<Record> unsortedIter = run.iterator();
        // load run into memory
        while (unsortedIter.hasNext()) {
            unsortedRun.add(unsortedIter.next());
        }
        unsortedRun.sort(comparator);
        sortedRun.addRecords(unsortedRun);
        return sortedRun;
    }

    /**
     * Given a list of sorted runs, returns a NEW run that is the result
     * of merging the input runs. You should use a Priority Queue (java.util.PriorityQueue)
     * to determine which record should be should be added to the output run next.
     * It is recommended that your Priority Queue hold Pair<Record, Integer> objects
     * where a Pair (r, i) is the Record r with the smallest value you are
     * sorting on currently unmerged from run i.
     */
    public Run mergeSortedRuns(List<Run> runs) {
        // TODO(proj3_part1): implement
        // load runs into pq
        PriorityQueue<Pair<Record, Integer>> pqRuns = new PriorityQueue(new RecordPairComparator());
        int run = 0;
        while (run < runs.size()) {
            Run curRun = runs.get(run);
            // load current run
            Iterator<Record> curRunIter = curRun.iterator();
            while (curRunIter.hasNext()) {
                Record nextRecord = curRunIter.next();
                Pair<Record, Integer> nextPair = new Pair<>(nextRecord, run);
                pqRuns.add(nextPair);
            }
            ++run;
        }

        // create sorted run from pq
        Run sortedRunFromRuns = createRun();
        while (!pqRuns.isEmpty()) {
            sortedRunFromRuns.addRecord(pqRuns.poll().getFirst().getValues());
        }
        return sortedRunFromRuns;
    }

    /**
     * Given a list of N sorted runs, returns a list of
     * sorted runs that is the result of merging (numBuffers - 1)
     * of the input runs at a time. It is okay for the last sorted run
     * to use less than (numBuffers - 1) input runs if N is not a
     * perfect multiple.
     */
    public List<Run> mergePass(List<Run> runs) {
        // TODO(proj3_part1): implement
        List<Run> runsToMerge = new ArrayList<>();
        List<Run> sortedRuns = new ArrayList<>();
        int usableBuffers = numBuffers - 1;
        int NToRun = calculateRuns(runs.size(), usableBuffers);
        int nRun = 0;
        for (int N = 0; N < NToRun; ++N) {
            for (int i = 0; i < usableBuffers; ++i) {
                runsToMerge.add(runs.get(nRun));
                ++nRun;
            }
            // merge runs and reset
            sortedRuns.add(mergeSortedRuns(runsToMerge));
            runsToMerge = new ArrayList<>();
        }
        return sortedRuns;
    }

    /**
     * Helper to return number of iterations with the buffers available
     */
    private int calculateRuns(int runs, int buffers) {
        return runs % buffers == 0 ? runs / buffers : runs / buffers + 1;
    }

    /**
     * Does an external merge sort on the table with name tableName
     * using numBuffers.
     * Returns the name of the table that backs the final run.
     */
    public String sort() {
        // TODO(proj3_part1): implement
        // pass 0: sort runs up to numBuffers
        BacktrackingIterator<Page> pagesInTable = transaction.getPageIterator(tableName);
        List<Run> sortedRuns = new ArrayList<>();
        while (pagesInTable.hasNext()) {
            BacktrackingIterator<Record> recordsInBuffers = transaction.getBlockIterator(tableName, pagesInTable, numBuffers);
            Run r = createRunFromIterator(recordsInBuffers);
            sortedRuns.add(sortRun(r));
        }

        // pass 1...n: merge and write to db
        int nPasses = getNumberOfPasses();
        for (int i = 0; i < nPasses; ++i) {
            mergePass(sortedRuns);
        }
        String tableNameOfFinalRun = sortedRuns.get(sortedRuns.size() - 1).tableName();
        return tableNameOfFinalRun;
    }

    /**
     * Helper to return number of passes used to sort all records
     * while staying within numBuffers
     */
    private int getNumberOfPasses() {
        int nPagesOnDisk = transaction.getNumDataPages(tableName);
        int pagesToBufferRatio = (int) Math.ceil(nPagesOnDisk / numBuffers);
        return (int) Math.ceil(logWithBase(numBuffers - 1, pagesToBufferRatio));
    }

    /**
     * Helper to return log with custom base of any value
     */
    private double logWithBase(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    public Iterator<Record> iterator() {
        if (sortedTableName == null) {
            sortedTableName = sort();
        }
        return this.transaction.getRecordIterator(sortedTableName);
    }

    /**
     * Creates a new run for intermediate steps of sorting. The created
     * run supports adding records.
     * @return a new, empty run
     */
    Run createRun() {
        return new IntermediateRun();
    }

    /**
     * Creates a run given a backtracking iterator of records. Record adding
     * is not supported, but creating this run will not incur any I/Os aside
     * from any I/Os incurred while reading from the given iterator.
     * @param records iterator of records
     * @return run backed by the iterator of records
     */
    Run createRunFromIterator(BacktrackingIterator<Record> records) {
        return new InputDataRun(records);
    }

    private class IntermediateRun implements Run {
        String tempTableName;

        IntermediateRun() {
            this.tempTableName = SortOperator.this.transaction.createTempTable(
                                     SortOperator.this.operatorSchema);
        }

        @Override
        public void addRecord(List<DataBox> values) {
            SortOperator.this.transaction.addRecord(this.tempTableName, values);
        }

        @Override
        public void addRecords(List<Record> records) {
            for (Record r : records) {
                this.addRecord(r.getValues());
            }
        }

        @Override
        public Iterator<Record> iterator() {
            return SortOperator.this.transaction.getRecordIterator(this.tempTableName);
        }

        @Override
        public String tableName() {
            return this.tempTableName;
        }
    }

    private static class InputDataRun implements Run {
        BacktrackingIterator<Record> iterator;

        InputDataRun(BacktrackingIterator<Record> iterator) {
            this.iterator = iterator;
            this.iterator.markPrev();
        }

        @Override
        public void addRecord(List<DataBox> values) {
            throw new UnsupportedOperationException("cannot add record to input data run");
        }

        @Override
        public void addRecords(List<Record> records) {
            throw new UnsupportedOperationException("cannot add records to input data run");
        }

        @Override
        public Iterator<Record> iterator() {
            iterator.reset();
            return iterator;
        }

        @Override
        public String tableName() {
            throw new UnsupportedOperationException("cannot get table name of input data run");
        }
    }

    private class RecordPairComparator implements Comparator<Pair<Record, Integer>> {
        @Override
        public int compare(Pair<Record, Integer> o1, Pair<Record, Integer> o2) {
            return SortOperator.this.comparator.compare(o1.getFirst(), o2.getFirst());
        }
    }
}

