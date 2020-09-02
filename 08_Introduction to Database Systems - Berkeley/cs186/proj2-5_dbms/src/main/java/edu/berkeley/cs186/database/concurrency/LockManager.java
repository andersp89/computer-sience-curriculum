

package edu.berkeley.cs186.database.concurrency;
// If you see this line, you have successfully pulled the latest changes from the skeleton for proj4!
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import edu.berkeley.cs186.database.Transaction;
import edu.berkeley.cs186.database.TransactionContext;
import edu.berkeley.cs186.database.common.Pair;

import java.util.*;

/**
 * LockManager maintains the bookkeeping for what transactions have
 * what locks on what resources. The lock manager should generally **not**
 * be used directly: instead, code should call methods of LockContext to
 * acquire/release/promote/escalate locks.
 *
 * The LockManager is primarily concerned with the mappings between
 * transactions, resources, and locks, and does not concern itself with
 * multiple levels of granularity (you can and should treat ResourceName
 * as a generic Object, rather than as an object encapsulating levels of
 * granularity, in this class).
 *
 * It follows that LockManager should allow **all**
 * requests that are valid from the perspective of treating every resource
 * as independent objects, even if they would be invalid from a
 * multigranularity locking perspective. For example, if LockManager#acquire
 * is called asking for an X lock on Table A, and the transaction has no
 * locks at the time, the request is considered valid (because the only problem
 * with such a request would be that the transaction does not have the appropriate
 * intent locks, but that is a multigranularity concern).
 *
 * Each resource the lock manager manages has its own queue of LockRequest objects
 * representing a request to acquire (or promote/acquire-and-release) a lock that
 * could not be satisfied at the time. This queue should be processed every time
 * a lock on that resource gets released, starting from the first request, and going
 * in order until a request cannot be satisfied. Requests taken off the queue should
 * be treated as if that transaction had made the request right after the resource was
 * released in absence of a queue (i.e. removing a request by T1 to acquire X(db) should
 * be treated as if T1 had just requested X(db) and there were no queue on db: T1 should
 * be given the X lock on db, and put in an unblocked state via Transaction#unblock).
 *
 * This does mean that in the case of:
 *    queue: S(A) X(A) S(A)
 * only the first request should be removed from the queue when the queue is processed.
 */
public class LockManager {
    // transactionLocks is a mapping from transaction number to a list of lock
    // objects held by that transaction.
    private Map<Long, List<Lock>> transactionLocks = new HashMap<>();
    // resourceEntries is a mapping from resource names to a ResourceEntry
    // object, which contains a list of Locks on the object, as well as a
    // queue for requests on that resource.
    private Map<ResourceName, edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry> resourceEntries = new HashMap<>();

    // A ResourceEntry contains the list of locks on a resource, as well as
    // the queue for requests for locks on the resource.
    private class ResourceEntry {
        // List of currently granted locks on the resource.
        List<Lock> locks = new ArrayList<>();
        // Queue for yet-to-be-satisfied lock requests on this resource.
        Deque<LockRequest> waitingQueue = new ArrayDeque<>();

        // Below are a list of helper methods you should implement!
        // Make sure to use these helper methods to abstract your code and
        // avoid re-implementing every time!

        /**
         * Check if a LOCKTYPE lock is compatible with preexisting locks.
         * Allows conflicts for locks held by transaction id EXCEPT.
         */
        boolean checkCompatible(LockType lockType, long except) {
            // TODO(proj4_part1): implement
            boolean compatible = true;
            for (Lock lock : locks) {
                if (!LockType.compatible(lock.lockType, lockType) && lock.transactionNum != except)
                    compatible = false;
            }
            return compatible;
        }

        /**
         * Gives the transaction the lock LOCK. Assumes that the lock is compatible.
         * Updates lock on resource if the transaction already has a lock. (i.e. on that resource)
         */
        void grantOrUpdateLock(Lock lock) {
            // TODO(proj4_part1): implement
            // Update lock, i.e. if resource holds lock for transaction
            if(!locks.isEmpty()) {
                this.locks.removeIf(curLock -> curLock.transactionNum.equals(lock.transactionNum));
                //&& LockType.substitutable(lock.lockType, curLock.lockType));
            }
            // Add lock
            this.locks.add(lock);
        }

        /**
         * Releases the lock LOCK and processes the queue. Assumes it had been granted before.
         */
        void releaseLock(Lock lock) {
            // TODO(proj4_part1): implement
            // Release lock
            locks.remove(lock);
        }

        /**
         * Adds a request for LOCK by the transaction to the queue and puts the transaction
         * in a blocked state.
         */
        void addToQueue(LockRequest request, boolean addFront) {
            // TODO(proj4_part1): implement
            if (addFront) {
                waitingQueue.addFirst(request);
            } else {
                waitingQueue.add(request);
            }
        }

        /**
         * Grant locks to requests from front to back of the queue, stopping
         * when the next lock cannot be granted.
         */
        private void processQueue() {
            // TODO(proj4_part1): implement
            boolean compatible;
            for (LockRequest requestInQueue : this.waitingQueue) {
                // Check whether requestInQueue is compatible with active locks
                if (!checkCompatible(requestInQueue.lock.lockType, requestInQueue.transaction.getTransNum())) return;
                // Processes lock, below
                waitingQueue.remove(requestInQueue);
                // Release locks on this resource
                if (requestInQueue.releasedLocks != null && !requestInQueue.releasedLocks.isEmpty()) {
                    locks.removeAll(requestInQueue.releasedLocks);
                }
                grantOrUpdateLock(requestInQueue.lock);
                grantLockToTransaction(requestInQueue.transaction, requestInQueue.lock);
                requestInQueue.transaction.unblock();
            }
        }

        /**
         * Gets the type of lock TRANSACTION has on this resource.
         */
        LockType getTransactionLockType(long transaction) {
            // TODO(proj4_part1): implement
            for (Lock lock : locks) {
                if (lock.transactionNum == transaction) return lock.lockType;
            }
            return LockType.NL;
        }

        /**
         * Get lock on resource owned by transaction
         * or "null" if no lock by transaction on resource
         * @param transaction
         * @return
         */
        public Lock getLockForXact (long transaction) {
            for (Lock lock : locks) {
                if (lock.transactionNum == transaction) return lock;
            }
            return null;
        }

        @Override
        public String toString() {
            return "Active Locks: " + Arrays.toString(this.locks.toArray()) +
                    ", Queue: " + Arrays.toString(this.waitingQueue.toArray());
        }
    }

    // You should not modify or use this directly.
    private Map<Long, LockContext> contexts = new HashMap<>();

    /**
     * Helper method to fetch the resourceEntry corresponding to NAME.
     * Inserts a new (empty) resourceEntry into the map if no entry exists yet.
     */
    private edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry getResourceEntry(ResourceName name) {
        resourceEntries.putIfAbsent(name, new edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry());
        return resourceEntries.get(name);
    }

    public Deque<LockRequest> getWaitingQueueForResource(ResourceName name) {
        edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry re = getResourceEntry(name);
        return re.waitingQueue;
    }

    // TODO(proj4_part1): You may add helper methods here if you wish
    /**
     * Releases lock on resource and processes it.
     * And releases lock on transaction thereafter.
     * @param transaction
     * @param name
     */
    private void releaseLockForResourceAndTransaction(TransactionContext transaction, ResourceName name) {
        edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceToRelease = getResourceEntry(name);

        // Release lock on resource
        Lock lockToRelease = resourceToRelease.getLockForXact(transaction.getTransNum());
        resourceToRelease.releaseLock(lockToRelease);
        resourceToRelease.processQueue();
        // Remove resource if no more locks
        if(resourceToRelease.locks.isEmpty() && resourceToRelease.waitingQueue.isEmpty()) {
            this.resourceEntries.remove(name);
        }

        // Remove lock from Transaction
        this.transactionLocks.get(transaction.getTransNum()).remove(lockToRelease);
        // Remove transaction if no more locks
        if(this.transactionLocks.get(transaction.getTransNum()).isEmpty()) {
            this.transactionLocks.remove(transaction.getTransNum());
        }
    }

    /**
     * Block xact only if conflict with locks other than those
     * owned by it or waitingQueue is not empty (i.e. others are waiting on resource)
     * @param transaction
     * @param resource
     * @param lockType
     * @return
     */
    private boolean checkBlockTransaction(TransactionContext transaction, edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resource,
                                          LockType lockType) {
        // Allow conflicts on resource for lock of this transaction, as it must be replaced
        if (!resource.checkCompatible(lockType, transaction.getTransNum()) || !resource.waitingQueue.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Update lock manager with the lock for both transaction and resource.
     * @param transaction
     * @param name
     * @param lockToAcquire
     */
    private void grantLock(TransactionContext transaction, ResourceName name, Lock lockToAcquire) {
        // Grant lock to LockManager's transaction
        grantLockToTransaction(transaction, lockToAcquire);
        // Grant lock to LockManager's resource
        edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceEntry = getResourceEntry(name);
        this.resourceEntries.put(name, resourceEntry);
        resourceEntry.grantOrUpdateLock(lockToAcquire);
    }

    /**
     * Grants a lock to a transaction.
     * @param transaction
     * @param lockToAcquire
     */
    private void grantLockToTransaction(TransactionContext transaction, Lock lockToAcquire) {
        Long transId = lockToAcquire.transactionNum;
        if (!transactionLocks.containsKey(transId)) {
            transactionLocks.put(transId, new ArrayList<Lock>());
        }
        List<Lock> locksForTrans = transactionLocks.get(transId);
        locksForTrans.add(lockToAcquire);
        transactionLocks.put(transId, locksForTrans);
    }

    /**
     * returns true if added to waiting queue or false if lock was acquired
     * @return
     */
    private boolean acquireLockOrAddToQueue(TransactionContext transaction,
                                            ResourceName name, LockType lockType,
                                            boolean addFront) {
        edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceEntry = getResourceEntry(name);
        boolean shouldBlock = checkBlockTransaction(transaction, resourceEntry, lockType);
        Lock lockToAcquire = new Lock(name, lockType, transaction.getTransNum());
        // If not compatible
        if(shouldBlock) {
            LockRequest lockRequest = new LockRequest(transaction, lockToAcquire);
            resourceEntry.addToQueue(lockRequest, addFront);
            transaction.prepareBlock();
        }
        // Acquire directly
        else {
            grantLock(transaction, name, lockToAcquire);
        }
        return shouldBlock;
    }

    // ### Main methods ###
    /**
     * Acquire a LOCKTYPE lock on NAME, for transaction TRANSACTION, and releases all locks
     * in RELEASELOCKS after acquiring the lock, in one atomic action.
     *
     * Error checking must be done before any locks are acquired or released. If the new lock
     * is not compatible with another transaction's lock on the resource, the transaction is
     * blocked and the request is placed at the **front** of ITEM's queue.
     *
     * Locks in RELEASELOCKS should be released only after the requested lock has been acquired.
     * The corresponding queues should be processed.
     *
     * An acquire-and-release that releases an old lock on NAME **should not** change the
     * acquisition time of the lock on NAME, i.e.
     * if a transaction acquired locks in the order: S(A), X(B), acquire X(A) and release S(A), the
     * lock on A is considered to have been acquired before the lock on B.
     *
     * @throws DuplicateLockRequestException if a lock on NAME is held by TRANSACTION and
     * isn't being released
     * @throws NoLockHeldException if no lock on a name in RELEASELOCKS is held by TRANSACTION
     */
    public void acquireAndRelease(TransactionContext transaction, ResourceName name,
                                  LockType lockType, List<ResourceName> releaseLocks)
            throws DuplicateLockRequestException, NoLockHeldException {
        // TODO(proj4_part1): implement
        // You may modify any part of this method. You are not required to keep all your
        // code within the given synchronized block -- in fact,
        // you will have to write some code outside the synchronized block to avoid locking up
        // the entire lock manager when a transaction is blocked. You are also allowed to
        // move the synchronized block elsewhere if you wish.
        boolean shouldBlock = false;
        synchronized (this) {
            // Check if transaction already has lock on resource
            LockType curLockType = getLockType(transaction, name);
            if (curLockType == lockType || (curLockType != LockType.NL && !releaseLocks.contains(name))) {
                throw new DuplicateLockRequestException("No duplicate lock request allowed");
            }

            // If transaction is to release locks that it does not hold
            List<Lock> locks = transactionLocks.get(transaction.getTransNum());
            if ((locks == null || locks.isEmpty()) && !releaseLocks.isEmpty()) {
                throw new NoLockHeldException("transaction does not hold any lock, " +
                        "but is going to release locks");
            }
            // Check if matching locks
            else if (locks != null && !locks.isEmpty()) {
                for (ResourceName rn : releaseLocks) {
                    edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry re = getResourceEntry(rn);
                    if (re.getLockForXact(transaction.getTransNum()) == null) {
                        throw new NoLockHeldException("transaction does not hold lock");
                    }
                }
            }

            // Acquire lock directly if compatible and no waiting queue
            edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceEntry = getResourceEntry(name);
            Lock lockToAcquire = new Lock(name, lockType, transaction.getTransNum());
            shouldBlock = checkBlockTransaction(transaction, resourceEntry, lockType);

            // Generate a Lock Request for every lock to release
            if (shouldBlock) {
                for (ResourceName releaseName : releaseLocks) {
                    edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry releaseResource = resourceEntries.get(releaseName);
                    Lock releaseLock = releaseResource.getLockForXact(transaction.getTransNum());
                    LockRequest releaseLockRequest;
                    // If releaseResource is the resource to acquire, include the lock
                    if (name.equals(releaseName)) {
                        releaseLockRequest = new LockRequest(transaction, lockToAcquire,
                                Collections.singletonList(releaseLock));
                    } else {
                        releaseLockRequest = new LockRequest(transaction, null,
                                Collections.singletonList(releaseLock));
                    }
                    releaseResource.addToQueue(releaseLockRequest, true);
                }
                transaction.prepareBlock();
            }
            // Release locks and process corresponding queue
            else {
                for(ResourceName resourceName : releaseLocks) {
                    releaseLockForResourceAndTransaction(transaction, resourceName);
                }
                grantLock(transaction, name, lockToAcquire);
            }
        }
        if (shouldBlock) transaction.block();
    }

    /**
     * Acquire a LOCKTYPE lock on NAME, for transaction TRANSACTION.
     *
     * Error checking must be done before the lock is acquired. If the new lock
     * is not compatible with another transaction's lock on the resource, or if there are
     * other transaction in queue for the resource, the transaction is
     * blocked and the request is placed at the **back** of NAME's queue.
     *
     * @throws DuplicateLockRequestException if a lock on NAME is held by
     * TRANSACTION
     */
    public void acquire(TransactionContext transaction, ResourceName name,
                        LockType lockType) throws DuplicateLockRequestException {
        // TODO(proj4_part1): implement
        // You may modify any part of this method. You are not required to keep all your
        // code within the given synchronized block -- in fact,
        // you will have to write some code outside the synchronized block to avoid locking up
        // the entire lock manager when a transaction is blocked. You are also allowed to
        // move the synchronized block elsewhere if you wish.

        boolean shouldBlock;
        synchronized (this) {
            // Check if there are duplicate lock
            LockType existingLock = getLockType(transaction, name);
            if(existingLock != LockType.NL) {
                throw new DuplicateLockRequestException(String.format("transaction %d already holds a lock on resource %s", transaction.getTransNum(), name));
            }

            // Acquire directly or add to waiting queue
            shouldBlock = acquireLockOrAddToQueue(transaction, name, lockType, false);
        }
        if(shouldBlock) transaction.block();
    }

    /**
     * Release TRANSACTION's lock on NAME.
     *
     * Error checking must be done before the lock is released.
     *
     * NAME's queue should be processed after this call. If any requests in
     * the queue have locks to be released, those should be released, and the
     * corresponding queues also processed.
     *
     * @throws NoLockHeldException if no lock on NAME is held by TRANSACTION
     */
    public void release(TransactionContext transaction, ResourceName name)
            throws NoLockHeldException {
        // TODO(proj4_part1): implement
        // You may modify any part of this method.
        synchronized (this) {
            // Check that transaction holds lock on resource
            edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceEntry = getResourceEntry(name);
            Lock lockByXactOnResource = resourceEntry.getLockForXact(transaction.getTransNum());
            if (lockByXactOnResource == null) {
                throw new NoLockHeldException(String.format("No lock " +
                        "held by %s on resource %s", transaction, name));
            }

            // Release transaction's lock on resource
            // And process corresponding queues
            releaseLockForResourceAndTransaction(transaction, name);
        }
    }

    /**
     * Promote TRANSACTION's lock on NAME to NEWLOCKTYPE (i.e. change TRANSACTION's lock
     * on NAME from the current lock type to NEWLOCKTYPE, which must be strictly more
     * permissive).
     *
     * Error checking must be done before any locks are changed. If the new lock
     * is not compatible with another transaction's lock on the resource, the transaction is
     * blocked and the request is placed at the **front** of ITEM's queue.
     *
     * A lock promotion **should not** change the acquisition time of the lock, i.e.
     * if a transaction acquired locks in the order: S(A), X(B), promote X(A), the
     * lock on A is considered to have been acquired before the lock on B.
     *
     * @throws DuplicateLockRequestException if TRANSACTION already has a
     * NEWLOCKTYPE lock on NAME
     * @throws NoLockHeldException if TRANSACTION has no lock on NAME
     * @throws InvalidLockException if the requested lock type is not a promotion. A promotion
     * from lock type A to lock type B is valid if and only if B is substitutable
     * for A, and B is not equal to A.
     */
    public void promote(TransactionContext transaction, ResourceName name,
                        LockType newLockType)
            throws DuplicateLockRequestException, NoLockHeldException, InvalidLockException {
        // TODO(proj4_part1): implement
        // You may modify any part of this method.
        boolean shouldBlock = false;
        synchronized (this) {
            // Error checking
            edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resourceEntry = getResourceEntry(name);
            Lock lockByXactOnResource = resourceEntry.getLockForXact(transaction.getTransNum());
            if (lockByXactOnResource == null) {
                throw new NoLockHeldException(String.format("Transaction %s has " +
                        "no lock on resource %s", transaction, name));
            }
            else if (lockByXactOnResource.lockType == newLockType) {
                throw new DuplicateLockRequestException(String.format("Transaction %s " +
                        "has already lock on resource %s of type %s", transaction, name, newLockType));
            }
            else if (!LockType.substitutable(newLockType, lockByXactOnResource.lockType)) {
                throw new InvalidLockException(String.format("newLockType %s is not " +
                        "substitutable for existing lock type %s.", newLockType, lockByXactOnResource.lockType));
            }
            // Promote transaction's lock on NAME to newlocktype
            shouldBlock = acquireLockOrAddToQueue(transaction, name, newLockType, true);
        }
        if (shouldBlock) transaction.block();
    }

    /**
     * Return the type of lock TRANSACTION has on NAME (return NL if no lock is held).
     */
    public synchronized LockType getLockType(TransactionContext transaction, ResourceName name) {
        // TODO(proj4_part1): implement
        edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry resource = getResourceEntry(name);
        return resource.getTransactionLockType(transaction.getTransNum());
    }

    /**
     * Returns the list of locks held on NAME, in order of acquisition.
     * A promotion or acquire-and-release should count as acquired
     * at the original time.
     */
    public synchronized List<Lock> getLocks(ResourceName name) {
        return new ArrayList<>(resourceEntries.getOrDefault(name, new edu.berkeley.cs186.database.concurrency.LockManager.ResourceEntry()).locks);
    }

    /**
     * Returns the list of locks held by
     * TRANSACTION, in order of acquisition. A promotion or
     * acquire-and-release should count as acquired at the original time.
     */
    public synchronized List<Lock> getLocks(TransactionContext transaction) {
        return new ArrayList<>(transactionLocks.getOrDefault(transaction.getTransNum(),
                Collections.emptyList()));
    }

    /**
     * Creates a lock context. See comments at
     * he top of this file and the top of LockContext.java for more information.
     */
    public synchronized LockContext context(String readable, long name) {
        if (!contexts.containsKey(name)) {
            contexts.put(name, new LockContext(this, null, new Pair<>(readable, name)));
        }
        return contexts.get(name);
    }

    /**
     * Create a lock context for the database. See comments at
     * the top of this file and the top of LockContext.java for more information.
     */
    public synchronized LockContext databaseContext() {
        return context("database", 0L);
    }
}