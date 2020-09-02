package edu.berkeley.cs186.database.concurrency;

import edu.berkeley.cs186.database.TimeoutScaling;
import edu.berkeley.cs186.database.categories.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Category({Proj4Tests.class, Proj4Part1Tests.class})
public class TestLockType {
    // 200ms per test
    @Rule
    public TestRule globalTimeout = new DisableOnDebug(Timeout.millis((long) (
                200 * TimeoutScaling.factor)));

    /**
     * Compatability Matrix
     * (Boolean value in cell answers is `left` compatible with `top`?)
     *
     *     | NL  | IS  | IX  |  S  | SIX |  X
     * ----+-----+-----+-----+-----+-----+-----
     * NL  |  T  |  T  |  T  |  T  |  T  |  T
     * ----+-----+-----+-----+-----+-----+-----
     * IS  |  T  |  T  |  T  |  T  |  T  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * IX  |  T  |  T  |  T  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * S   |  T  |  T  |  F  |  T  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * SIX |  T  |  T  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     * X   |  T  |  F  |  F  |  F  |  F  |  F
     * ----+-----+-----+-----+-----+-----+-----
     *
     * The filled in cells are covered by the public tests.
     * You can expect the blank cells to be covered by the hidden tests!
     * Hint: I bet the notes might have something useful for this...
     */

    @Test
    @Category(PublicTests.class)
    public void testCompatibleNL() {
        // NL should be compatible with every lock type
        assertTrue(LockType.compatible(LockType.NL, LockType.NL));
        assertTrue(LockType.compatible(LockType.NL, LockType.S));
        assertTrue(LockType.compatible(LockType.NL, LockType.X));
        assertTrue(LockType.compatible(LockType.NL, LockType.IS));
        assertTrue(LockType.compatible(LockType.NL, LockType.IX));
        assertTrue(LockType.compatible(LockType.NL, LockType.SIX));
        assertTrue(LockType.compatible(LockType.S, LockType.NL));
        assertTrue(LockType.compatible(LockType.X, LockType.NL));
        assertTrue(LockType.compatible(LockType.IS, LockType.NL));
        assertTrue(LockType.compatible(LockType.IX, LockType.NL));
        assertTrue(LockType.compatible(LockType.SIX, LockType.NL));
    }

    @Test
    @Category(PublicTests.class)
    public void testCompatibleS() {
        // S is compatible with S, and IS
        assertTrue(LockType.compatible(LockType.S, LockType.S));
        assertTrue(LockType.compatible(LockType.S, LockType.IS));
        assertTrue(LockType.compatible(LockType.IS, LockType.S));

        // S is incompatible with X, IX, and SIX
        assertFalse(LockType.compatible(LockType.S, LockType.X));
        assertFalse(LockType.compatible(LockType.S, LockType.IX));
        assertFalse(LockType.compatible(LockType.S, LockType.SIX));
        assertFalse(LockType.compatible(LockType.X, LockType.S));
        assertFalse(LockType.compatible(LockType.IX, LockType.S));
        assertFalse(LockType.compatible(LockType.SIX, LockType.S));
    }

    @Test
    @Category(PublicTests.class)
    public void testCompatibleRest() {
        // IS is compatible with all than X:
        for (LockType lock : LockType.values()) {
            if (lock != LockType.X) assertTrue(LockType.compatible(LockType.IS, lock));
            else assertFalse(LockType.compatible(LockType.IS, lock));
        }
        // IX compatible with:
        assertTrue(LockType.compatible(LockType.IX, LockType.IS));
        assertTrue(LockType.compatible(LockType.IX, LockType.IX));

        // IX is not compatible with:
        assertFalse(LockType.compatible(LockType.IX, LockType.S));
        assertFalse(LockType.compatible(LockType.IX, LockType.SIX));
        assertFalse(LockType.compatible(LockType.IX, LockType.X));

        // SIX compatible only with IS
        for (LockType lock : LockType.values()) {
            if (lock == LockType.IS || lock == LockType.NL) assertTrue(LockType.compatible(LockType.SIX, lock));
            else assertFalse(LockType.compatible(LockType.SIX, lock));
        }

        // X not compatible with any
        for (LockType lock : LockType.values()) {
            if (lock != LockType.NL) assertFalse(LockType.compatible(LockType.X, lock));
        }
    }

    @Test
    @Category(SystemTests.class)
    public void testParent() {
        // This is an exhaustive test of what we expect from LockType.parentLock
        // for valid lock types
        assertEquals(LockType.NL, LockType.parentLock(LockType.NL));
        assertEquals(LockType.IS, LockType.parentLock(LockType.S));
        assertEquals(LockType.IX, LockType.parentLock(LockType.X));
        assertEquals(LockType.IS, LockType.parentLock(LockType.IS));
        assertEquals(LockType.IX, LockType.parentLock(LockType.IX));
        assertEquals(LockType.IX, LockType.parentLock(LockType.SIX));
    }

    /**
     * Parent Matrix
     * (Boolean value in cell answers can `left` be the parent of `top`?)
     *
     * To get S or IS lock on a node, must hold IS or IX on parent node.
     * To get X or IX or SIX on a node, must hold IX or SIX on parent node.
     *
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
     *
     * The filled in cells (Only for NLs) are covered by the public test.
     * You can expect the blank cells to be covered by the hidden tests!
     */

    @Test
    @Category(PublicTests.class)
    public void testCanBeParentNL() {
        // Any lock type can be parent of NL
        for (LockType lockType : LockType.values()) {
            assertTrue(LockType.canBeParentLock(lockType, LockType.NL));
        }

        // The only lock type that can be a child of NL is NL
        for (LockType lockType : LockType.values()) {
            if (lockType != LockType.NL) {
                assertFalse(LockType.canBeParentLock(LockType.NL, lockType));
            }
        }
        // IS - parent of:
        assertTrue(LockType.canBeParentLock(LockType.IS, LockType.NL));
        assertTrue(LockType.canBeParentLock(LockType.IS, LockType.IS));
        assertTrue(LockType.canBeParentLock(LockType.IS, LockType.S));
        // IS - NOT parent of:
        assertFalse(LockType.canBeParentLock(LockType.IS, LockType.IX));
        assertFalse(LockType.canBeParentLock(LockType.IS, LockType.SIX));
        // IX - parent of all
        for (LockType lockType : LockType.values()) {
            assertTrue(LockType.canBeParentLock(LockType.IX, lockType));
        }
        // S - parent of no other than NL
        for (LockType lockType : LockType.values()) {
            if (lockType != LockType.NL) {
                assertFalse(LockType.canBeParentLock(LockType.S, lockType));
            } else if (lockType == LockType.NL) {
                assertTrue(LockType.canBeParentLock(LockType.S, lockType));
            }
        }
        // SIX - parent of:
        assertTrue(LockType.canBeParentLock(LockType.SIX, LockType.IX));
        assertTrue(LockType.canBeParentLock(LockType.SIX, LockType.SIX));
        assertTrue(LockType.canBeParentLock(LockType.SIX, LockType.X));
        // SIX - NOT parent of:
        assertFalse(LockType.canBeParentLock(LockType.SIX, LockType.IS));
        assertFalse(LockType.canBeParentLock(LockType.SIX, LockType.S));
        // X - parent of no other than NL
        for (LockType lockType : LockType.values()) {
            if (lockType != LockType.NL) {
                assertFalse(LockType.canBeParentLock(LockType.X, lockType));
            } else if (lockType == LockType.NL) {
                assertTrue(LockType.canBeParentLock(LockType.X, lockType));
            }
        }
    }

    /**
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
     *
     * The filled in cells ("1") are covered by the public test.
     * You can expect the blank cells to be covered by the hidden tests!
     *
     * The boolean value in the cell answers the question:
     * "Can `left` substitute `top`?"
     *
     * or alternatively:
     * "Are the privileges of `left` a superset of those of `top`?"
     */

    @Test
    @Category(PublicTests.class)
    public void testSubstitutableReal() {
        // NL cannot substitute any other than itself
        for (LockType lockType : LockType.values()) {
            if (lockType != LockType.NL) assertFalse(LockType.substitutable(LockType.NL, lockType));
            else assertTrue(LockType.substitutable(LockType.NL, lockType));
        }
        // IS can substitute:
        assertTrue(LockType.substitutable(LockType.IS, LockType.NL));
        assertTrue(LockType.substitutable(LockType.IS, LockType.IS));

        // IS cannot substitute:
        assertFalse(LockType.substitutable(LockType.IS, LockType.S));
        assertFalse(LockType.substitutable(LockType.IS, LockType.X));
        assertFalse(LockType.substitutable(LockType.IS, LockType.IX));
        assertFalse(LockType.substitutable(LockType.IS, LockType.SIX));

        // IX can substitute:
        assertTrue(LockType.substitutable(LockType.IX, LockType.NL));
        assertTrue(LockType.substitutable(LockType.IX, LockType.IS));
        assertTrue(LockType.substitutable(LockType.IX, LockType.IX));

        // IX cannot substitute:
        assertFalse(LockType.substitutable(LockType.IX, LockType.S));
        assertFalse(LockType.substitutable(LockType.IX, LockType.X));
        assertFalse(LockType.substitutable(LockType.IX, LockType.SIX));

        // S can substitute:
        assertTrue(LockType.substitutable(LockType.S, LockType.NL));
        assertTrue(LockType.substitutable(LockType.S, LockType.S));

        // S cannot substitute
        assertFalse(LockType.substitutable(LockType.S, LockType.IS));
        assertFalse(LockType.substitutable(LockType.S, LockType.IX));
        assertFalse(LockType.substitutable(LockType.S, LockType.X));
        assertFalse(LockType.substitutable(LockType.S, LockType.SIX));

        // SIX can substitute:
        assertTrue(LockType.substitutable(LockType.SIX, LockType.NL));
        assertTrue(LockType.substitutable(LockType.SIX, LockType.S));
        assertTrue(LockType.substitutable(LockType.SIX, LockType.SIX));

        // SIX cannot substitute
        assertFalse(LockType.substitutable(LockType.SIX, LockType.IS));
        assertFalse(LockType.substitutable(LockType.SIX, LockType.IX));
        assertFalse(LockType.substitutable(LockType.SIX, LockType.X));

        // X can substitute:
        assertTrue(LockType.substitutable(LockType.X, LockType.NL));
        assertTrue(LockType.substitutable(LockType.X, LockType.S));
        assertTrue(LockType.substitutable(LockType.X, LockType.X));

        // X cannot substitute
        assertFalse(LockType.substitutable(LockType.X, LockType.IS));
        assertFalse(LockType.substitutable(LockType.X, LockType.SIX));
        assertFalse(LockType.substitutable(LockType.X, LockType.IX));
    }

}

