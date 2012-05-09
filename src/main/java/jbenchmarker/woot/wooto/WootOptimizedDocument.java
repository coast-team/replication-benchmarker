/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.woot.wooto;

import java.util.ListIterator;
import jbenchmarker.core.Document;
import jbenchmarker.woot.WootDocument;
import jbenchmarker.woot.WootOperation;

/**
 * WOOTO with degrees
 * @author urso
 */
public class WootOptimizedDocument<T> extends WootDocument<WootOptimizedNode<T>> {
    public WootOptimizedDocument() {
        super(WootOptimizedNode.CB, WootOptimizedNode.CE);
    }

    @Override
    protected void insertBetween(int ip, int in, WootOperation wop) {
        WootOptimizedNode wn = new WootOptimizedNode(wop.getId(), Math.max(elements.get(ip).getDegree(), elements.get(in).getDegree())+1, wop.getContent(), true);
        woalgo(ip, in, wn);        
    }

    private void woalgo(int ip, int in, WootOptimizedNode wn) {
        if (ip == in - 1) {
            elements.add(in, wn);
        } else {
            int d = ip, f = in, dMin = -1;
            ListIterator<WootOptimizedNode<T>> it = elements.listIterator(d + 1);
            for (int i = d+1; i < f; i++) {
                WootOptimizedNode e = it.next();
                if (dMin == -1 || e.getDegree() < dMin) {
                    dMin = e.getDegree();
                }
            }
            it = elements.listIterator(d + 1);
            for (int i = d+1; i < f; i++) {
                WootOptimizedNode e = it.next();
                if (e.getDegree() == dMin) {
                    if (e.getId().compareTo(wn.getId()) < 0) {
                        d = i;
                    } else {
                        f = i;
                    }
                }
            }
            woalgo(d, f, wn);
        }

    }

    @Override
    public Document create() {
        return new WootOptimizedDocument();
    }


}
