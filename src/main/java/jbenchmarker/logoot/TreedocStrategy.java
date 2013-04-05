/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logoot;

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.logoot.LogootBinaryPosition.Direction;

/**
 * Deterministic strategy that produce identifiers along a tree (treedoc).
 * @author urso
 */
public class TreedocStrategy implements LogootStrategy {

    @Override
    public ListIdentifier begin() {
        return new LogootBinaryPosition(Direction.left, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Override
    public ListIdentifier end() {
        return new LogootBinaryPosition(Direction.right, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * To the right of P.
     */
    @Override
    public List<ListIdentifier> generateLineIdentifiers(LogootDocument replica, ListIdentifier P, ListIdentifier Q, int n) {
        LogootBinaryPosition start, root = (LogootBinaryPosition) P;
        if (P.equals(begin()) && Q.equals(end())) {
            start = new LogootBinaryPosition(0);
            List<ListIdentifier> l = balance(Direction.left, start, n / 2, replica);
            l.addAll(balance(Direction.right, start, n - (n / 2), replica));
            return l;
        } else {
            LogootBinaryPosition bp = (LogootBinaryPosition) P, bq = (LogootBinaryPosition) Q;
            if (bq.isRightSonOf(bp)) {
                return balance(Direction.left, bq, n, replica);
            }                
            return balance(Direction.right, bp, n, replica);
        }
    }

    /**
     * Balanced tree of identifiers.
     */
    // TODO : should be optimized using vector and tree positions.
    private List<ListIdentifier> balance(Direction direction, LogootBinaryPosition start, int n, LogootDocument replica) {
        if (n > 0) {
            LogootBinaryPosition pos = start.plus(direction, replica.getReplicaNumber(), replica.nextClock());
            List<ListIdentifier> result = balance(Direction.left, pos, n / 2, replica);
            result.add(pos);
            result.addAll(balance(Direction.right, pos, (n - 1) / 2, replica));
            return result;
        } else {
            return new ArrayList<ListIdentifier>();
        }
    }
    
}
