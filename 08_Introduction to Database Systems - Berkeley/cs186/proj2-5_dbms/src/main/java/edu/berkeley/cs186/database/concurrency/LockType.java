package edu.berkeley.cs186.database.concurrency;

// If you see this line, you have successfully pulled the latest changes from the skeleton for proj4!

public enum LockType {
    S,   // shared
    X,   // exclusive
    IS,  // intention shared
    IX,  // intention exclusive
    SIX, // shared intention exclusive
    NL;  // no lock held

    /**
     * This method checks whether lock types A and B are compatible with
     * each other. If a transaction can hold lock type A on a resource
     * at the same time another transaction holds lock type B on the same
     * resource, the lock types are compatible.
     */
    public static boolean compatible(LockType a, LockType b) {
        if (a == null || b == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        // Implements "Lock Compatibility Matrix"
        if (a == IS) {
            if (b == IS || b == IX || b == S || b == SIX || b == NL) return true;
        }
        else if (a == IX) {
            if (b == IS || b == IX || b == NL) return true;
        }
        else if (a == S) {
            if (b == IS || b == S || b == NL) return true;
        }
        else if (a == SIX) {
            if (b == IS || b == NL) return true;
        }
        else if (a == X) {
            if (b == NL) return true;
        }
        else if (a == NL) {
            return true;
        }
        return false;
    }

    /**
     * This method returns the lock on the parent resource
     * that should be requested for a lock of type A to be granted.
     */
    public static LockType parentLock(LockType a) {
        if (a == null) {
            throw new NullPointerException("null lock type");
        }
        switch (a) {
        case S: return IS;
        case X: return IX;
        case IS: return IS;
        case IX: return IX;
        case SIX: return IX;
        case NL: return NL;
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * This method returns if parentLockType has permissions to grant a childLockType
     * on a child.
     *
     * Parent Matrix
     *     | NL  | IS  | IX  |  S  | SIX |  X
     * ----+-----+-----+-----+-----+-----+-----
     * NL  |  T  |  F  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * IS  |  T  |  T  |  F  |  T  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * IX  |  T  |  T  |  T  |  T  |  T  |  T
     * ----+-----+-----+-----+-----+-----+-----
     * S   |  T  |  F  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * SIX |  T  |  F  |  T  |  F  |  T  |  T
     * ----+-----+-----+-----+-----+-----+-----
     * X   |  T  |  F  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     */
    public static boolean canBeParentLock(LockType parentLockType, LockType childLockType) {
        if (parentLockType == null || childLockType == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        if (parentLockType == NL) {
            if (childLockType == NL) return true;
        }
        else if (parentLockType == IS) {
            if (childLockType == NL || childLockType == IS ||
                    childLockType == S) return true;
        }
        else if (parentLockType == IX) {
            for (LockType lockType : LockType.values()) {
                if (childLockType == lockType) return true;
            }
        }
        else if (parentLockType == S) {
            if (childLockType == NL) return true;
        }
        else if (parentLockType == SIX) {
            if (childLockType == NL || childLockType == IX ||
                    childLockType == SIX || childLockType == X) return true;
        }
        else if (parentLockType == X) {
            if (childLockType == NL) return true;
        }
        return false;
    }

    /**
     * This method returns whether a lock can be used for a situation
     * requiring another lock (e.g. an S lock can be substituted with
     * an X lock, because an X lock allows the transaction to do everything
     * the S lock allowed it to do).
     * Substitutability Matrix
     * (Values along left are `substitute`, values along top are `required`)
     *
     *     | NL  | IS  | IX  |  S  | SIX |  X
     * ----+-----+-----+-----+-----+-----+-----
     * NL  |  T  |  F  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * IS  |  T  |  T  |  F  |  F1 |  F  |  F1
     * ----+-----+-----+-----+-----+-----+-----
     * IX  |  T  |  T  |  T  |  F1 |  F  |  F1
     * ----+-----+-----+-----+-----+-----+-----
     * S   |  T  |  F  |  F  |  T1 |  F  |  F1
     * ----+-----+-----+-----+-----+-----+-----
     * SIX |  T  |  F  |  F  |  T1 |  T  |  F1
     * ----+-----+-----+-----+-----+-----+-----
     * X   |  T  |  F  |  F  |  T1 |  F  |  T1
     * ----+-----+-----+-----+-----+-----+-----
     */
    public static boolean substitutable(LockType substitute, LockType required) {
        if (required == null || substitute == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        if (substitute == NL) {
            if (required == NL) return true;
        }
        else if (substitute == IS) {
            if (required == NL || required == IS) return true;
        }
        else if (substitute == IX) {
            if (required == NL || required == IS || required == IX) return true;
        }
        else if (substitute == S) {
            if (required == NL || required == S) return true;
        }
        else if (substitute == SIX) {
            if (required == NL || required == S || required == SIX) return true;
        }
        else if (substitute == X) {
            if (required == NL || required == S || required == X) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        switch (this) {
        case S: return "S";
        case X: return "X";
        case IS: return "IS";
        case IX: return "IX";
        case SIX: return "SIX";
        case NL: return "NL";
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }
}

