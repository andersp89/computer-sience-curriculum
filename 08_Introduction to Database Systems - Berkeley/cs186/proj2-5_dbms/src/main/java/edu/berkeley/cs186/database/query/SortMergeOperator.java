package edu.berkeley.cs186.database.query;

import java.util.*;

import edu.berkeley.cs186.database.TransactionContext;
import edu.berkeley.cs186.database.common.iterator.BacktrackingIterator;
import edu.berkeley.cs186.database.databox.DataBox;
import edu.berkeley.cs186.database.table.Record;

class SortMergeOperator extends JoinOperator {
    SortMergeOperator (QueryOperator leftSource,
                         QueryOperator rightSource,
                         String leftColumnName,
                         String rightColumnName,
                         TransactionContext transaction) {
        super(leftSource, rightSource, leftColumnName, rightColumnName, transaction, JoinType.SORTMERGE);

        this.stats = this.estimateStats();
        this.cost = this.estimateIOCost();
    }

    @Override
    public Iterator<Record> iterator() {
        return new SortMergeIterator();
    }

    @Override
    public int estimateIOCost() {
        //does nothing
        return 0;
    }

    /**
     * An implementation of Iterator that provides an iterator interface for this operator.
     *    See lecture slides.
     *
     * Before proceeding, you should read and understand SNLJOperator.java
     *    You can find it in the same directory as this file.
     *
     * Word of advice: try to decompose the problem into distinguishable sub-problems.
     *    This means you'll probably want to add more methods than those given (Once again,
     *    SNLJOperator.java might be a useful reference).
     *
     */
    private class SortMergeIterator extends JoinIterator {
        /**
         * Some member variables are provided for guidance, but there are many possible solutions.
         * You should implement the solution that's best for you, using any member variables you need.
         * You're free to use these member variables, but you're not obligated to.
         */
        private BacktrackingIterator<Record> leftIterator;
        private BacktrackingIterator<Record> rightIterator;
        private Record leftRecord;
        private Record nextRecord;
        private Record rightRecord;
        private DataBox leftValue;
        private DataBox rightValue;
        private boolean marked = false;

        private SortMergeIterator() {
            super();
            // TODO(proj3_part1): implement
            // sort input, left and right tables
            SortOperator sortLeft = new SortOperator(getTransaction(), getLeftTableName(), new LeftRecordComparator());
            String sLeftTableName = sortLeft.sort();
            SortOperator sortRight = new SortOperator(getTransaction(), getRightTableName(), new RightRecordComparator());
            String sRightTableName = sortRight.sort();

            leftIterator = getRecordIterator(sLeftTableName);
            leftRecord = leftIterator.hasNext() ? leftIterator.next() : null;

            rightIterator = getRecordIterator(sRightTableName);
            rightRecord = rightIterator.hasNext() ? rightIterator.next() : null;

            if (!leftRecord.getValues().isEmpty()) setLeftValue();
            if (!rightRecord.getValues().isEmpty()) setRightValue();

            try {
                this.fetchNextRecord();
            } catch (NoSuchElementException e) {
                this.nextRecord = null;
            }

        }

        /**
         * join pass: merge-scan the sorted partitions and emit tuples that match
         */
        private void fetchNextRecord() {
            nextRecord = null;
            while (leftRecord != null && rightRecord != null) {
                if (!marked) {
                    while (leftValue.compareTo(rightValue) < 0) fetchNextLeftRecord();
                    while (leftValue.compareTo(rightValue) > 0) fetchNextRightRecord();
                    rightIterator.markPrev();
                    marked = true;
                }
                // if join tuple
                if (leftValue.equals(rightValue)) {
                    nextRecord = joinRecords(leftRecord, rightRecord);
                    fetchNextRightRecord();
                    return;
                }
                // else, try next left record
                else {
                    resetAndMarkRightRecord();
                    fetchNextLeftRecord();
                    marked = false;
                }
            }
        }

        private void resetAndMarkRightRecord () {
            rightIterator.reset();
            rightRecord = rightIterator.next();
            setRightValue();
            rightIterator.markPrev();
        }

        private void fetchNextLeftRecord() {
            if (!leftIterator.hasNext()){
                leftRecord = null;
                throw new NoSuchElementException("All Done!");
            } else {
                leftRecord = leftIterator.next();
                setLeftValue();
            }
        }

        private void fetchNextRightRecord() {
            if (!rightIterator.hasNext()) {
                // iterate over remaining left items, if any
                if (leftIterator.hasNext()) {
                    fetchNextLeftRecord();
                    // reset right value if left value needs to check it
                    if(leftValue.compareTo(rightValue) <= 0){
                        resetAndMarkRightRecord();
                    }
                } else {
                  rightRecord = null;
                }
            } else {
                rightRecord = rightIterator.next();
                setRightValue();
            }
        }

        private void setLeftValue() {
            leftValue = leftRecord.getValues().get(getLeftColumnIndex());
        }

        private void setRightValue() {
            rightValue = rightRecord.getValues().get(getRightColumnIndex());
        }

        /**
         * Helper method to create a joined record from a record of the left relation
         * and a record of the right relation.
         * @param leftRecord Record from the left relation
         * @param rightRecord Record from the right relation
         * @return joined record
         */
        private Record joinRecords(Record leftRecord, Record rightRecord) {
            List<DataBox> leftValues = new ArrayList<>(leftRecord.getValues());
            List<DataBox> rightValues = new ArrayList<>(rightRecord.getValues());
            leftValues.addAll(rightValues);
            return new Record(leftValues);
        }

        /**
         * Checks if there are more record(s) to yield
         *
         * @return true if this iterator has another record to yield, otherwise false
         */
        @Override
        public boolean hasNext() {
            // TODO(proj3_part1): implement
            return nextRecord != null;
        }

        /**
         * Yields the next record of this iterator.
         *
         * @return the next Record
         * @throws NoSuchElementException if there are no more Records to yield
         */
        @Override
        public Record next() {
            // TODO(proj3_part1): implement
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            Record nextRecord = this.nextRecord;
            try {
                this.fetchNextRecord();
            } catch (NoSuchElementException e) {
                this.nextRecord = null;
            }
            return nextRecord;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private class LeftRecordComparator implements Comparator<Record> {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getValues().get(getLeftColumnIndex()).compareTo(
                        o2.getValues().get(getLeftColumnIndex()));
            }
        }

        private class RightRecordComparator implements Comparator<Record> {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getValues().get(getRightColumnIndex()).compareTo(
                        o2.getValues().get(getRightColumnIndex()));
            }
        }
    }
}
