package edu.berkeley.cs186.database.concurrency;
// If you see this line, you have successfully pulled the latest changes from the skeleton for proj4!
import edu.berkeley.cs186.database.TransactionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * LockUtil is a declarative layer which simplifies multigranularity lock acquisition
 * for the user (you, in the second half of Part 2). Generally speaking, you should use LockUtil
 * for lock acquisition instead of calling LockContext methods directly.
 */
public class LockUtil {
    /**
     * Ensure that the current transaction can perform actions requiring LOCKTYPE on LOCKCONTEXT.
     *
     * This method should promote/escalate as needed, but should only grant the least
     * permissive set of locks needed.
     *
     * lockType is guaranteed to be one of: S, X, NL.
     *
     * If the current transaction is null (i.e. there is no current transaction), this method should do nothing.
     */
    public static void ensureSufficientLockHeld(LockContext lockContext, LockType lockType) {
        // TODO(proj4_part2): implement
        TransactionContext transaction = TransactionContext.getTransaction();

        // Policy to automatically escalate page-level locks into a table-level lock.
        // You should modify the codebase to escalate locks from page-level to table-level
        // when both of two conditions are met:
        // _The transaction holds at least 20% of the table's pages.
        // _The table has at least 10 pages (to avoid locking the entire table
        // unnecessarily when the table is very small).
        if (!ifDisabled(lockContext) && lockContext.parent != null && lockContext.parent.parent != null &&
                lockContext.parent.capacity >= 10 && lockContext.parent.saturation(transaction) >= 0.2) {
            lockContext.parent.escalate(transaction);
            ensureSufficientLockHeld(lockContext, lockType);
            return;
        }

        if (transaction == null) return;

        // If substitutable, return
        if (LockType.substitutable(lockContext.getEffectiveLockType(transaction), lockType)) {
            return;
        }

        // Call recursively with parent context
        if (lockContext.parentContext() != null) {
            ensureSufficientLockHeld(lockContext.parentContext(), LockType.parentLock(lockType));
        }

        // If current context is NL, acquire the wanted
        if (lockContext.getEffectiveLockType(transaction) == LockType.NL) {
            lockContext.acquire(transaction, lockType);
        }
        // Try to promote
        else {
            try {
                lockContext.promote(transaction, lockType);
            } catch (InvalidLockException e) {
                // If S lock wanted, release all non-X/IX/NL descendant locks
                if (lockType.equals(LockType.S)) {
                    List<ResourceName> releaseLocks = new ArrayList<>();
                    releaseLocks.add(lockContext.getResourceName());
                    for (Lock lock : lockContext.lockman.getLocks(transaction)) {
                        if (lock.name.isDescendantOf(lockContext.getResourceName()) &&
                                (lock.lockType != LockType.NL && lock.lockType != LockType.X
                                        && lock.lockType != LockType.IX)) {
                            releaseLocks.add(lock.name);
                        }
                    }
                    // Acquire SIX, if current resource has IX lock
                    if (lockContext.getEffectiveLockType(transaction).equals(LockType.IX)) {
                        lockContext.lockman.acquireAndRelease(transaction, lockContext.getResourceName(), LockType.SIX, releaseLocks);
                        return;
                    }
                }
                lockContext.escalate(transaction);
                ensureSufficientLockHeld(lockContext, lockType);
            }
        }
    }
    // TODO(proj4_part2): add helper methods as you see fit
    private static boolean ifDisabled(LockContext lc) {
        if (lc.parent != null && lc.parent.parent != null) {
            if (lc.parent.getLockDisable()) {
                return true;
            }
        } else if (lc.parent != null && lc.parent.parent == null) {
            if (lc.getLockDisable()) {
                return true;
            }
        }
        return false;
    }
}