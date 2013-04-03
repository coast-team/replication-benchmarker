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
 
package crdt.simulator.random;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class StandardOrderedTreeOperationProfileWithMoveRename extends StandardOrderedTreeOpProfile {

    private final double perMove;
    private final double perRename;

    /**
     * PerIns+perMove+perRename <= 1
     *
     * @param perIns
     * @param perMove
     * @param perChild
     */
    public StandardOrderedTreeOperationProfileWithMoveRename(double perIns, double perMove, double perRename, double perChild) {
        super(perIns, perChild);
        this.perMove = perMove;
        this.perRename = perRename;
    }

    @Override
    public OrderedTreeOperation<String> nextOperation(CRDT crdt) {
        List<Integer> pathSrc = new LinkedList();
        int n = getPath((OrderedNode) crdt.lookup(), pathSrc).getChildrenNumber();

        double perAddSum = this.getPerChild() + perMove + perRename;
        double perMoveSum = perMove + perRename;
        double random = getRandomGauss().nextDouble();
        if (pathSrc.isEmpty()/* || n == 0*/ || random < perAddSum) {
            /*Generate add operation*/
            return new OrderedTreeOperation<String>(pathSrc, n == 0 ? 0 : getRandomGauss().nextInt(n), nextElement());
        }
        if (random < perMoveSum) {
            List<Integer> pathDst = new LinkedList();
            int n2 = getPath((OrderedNode) crdt.lookup(), pathDst).getChildrenNumber();
            return new OrderedTreeOperation<String>(OrderedTreeOperation.OpType.move, pathSrc, pathDst, n == 0 ? 0 : getRandomGauss().nextInt(n2), null);
        } else if (random < perRename) {
            return new OrderedTreeOperation<String>(OrderedTreeOperation.OpType.chContent, pathSrc, null, 0, nextElement());
        } else {
            /*Generate del operation*/
            return new OrderedTreeOperation<String>(pathSrc);
        }

    }
}
