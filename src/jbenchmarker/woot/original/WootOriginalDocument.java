/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.woot.original;

import java.util.ListIterator;
import jbenchmarker.woot.WootDocument;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootOriginalDocument extends WootDocument<WootOriginalNode> {

    public WootOriginalDocument() {
        super(WootOriginalNode.CB, WootOriginalNode.CE);
    }

    
    private void walgo(int ip, int in, WootOriginalNode wn) {
        if (ip == in - 1) {
            elements.add(in, wn);
        } else {
            int d = ip, f = in, i = d+1; 
            ListIterator<WootOriginalNode> it = elements.listIterator(d+1);
            while (i < f) {
                WootOriginalNode e = it.next();
                if ((find(e.getCp().getId()) <= ip) && (find(e.getCn().getId()) >= in)) {
                    // same or greater "legs"
                    if (e.getId().compareTo(wn.getId()) > 0) {
                        f = i;
                    } else {
                        d = i;
                    }
                }
                i++;
            }
            walgo(d, f, wn);
        }
    }


    protected void insertBetween(int ip, int in, WootOperation wop) {
        WootOriginalNode wn = new WootOriginalNode(wop.getId(), elements.get(ip), elements.get(in), wop.getContent(), true);
        walgo(ip, in, wn);
    }

}
