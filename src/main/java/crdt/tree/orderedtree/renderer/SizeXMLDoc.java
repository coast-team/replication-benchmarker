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
 
package crdt.tree.orderedtree.renderer;

import collect.OrderedNode;
import collect.SimpleNode;
import crdt.CRDT;
import crdt.simulator.sizecalculator.SizeCalculator;
import java.io.IOException;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class SizeXMLDoc implements SizeCalculator {

    public String view(SimpleNode<String> in) {
        StringBuilder str = new StringBuilder();

        for (Object no : in) {
            SimpleNode<String> node = (SimpleNode<String>) no;


            if (node.getChildrenNumber() == 0) {
                str.append("<");
                str.append(node.getValue());
                str.append("/>");
            } else {
                str.append("<");
                str.append(node.getValue());
                str.append(">");

                str.append(view(node));

                str.append("</");
                str.append(node.getValue());
                str.append(">");
            }
        }
        return str.toString();

    }

    /**
     *
     * @param m the value of m
     * @return the long
     * @throws IOException
     */
    @Override
    public long serializ(CRDT m) throws IOException {
        String xml = view((OrderedNode<String>) m.lookup());
        return xml.length();
    }
}
