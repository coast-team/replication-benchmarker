/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package jbenchmarker.trace.json.moulinette.attributs;

import jbenchmarker.trace.json.moulinette.attributs.CommitDiff;
import jbenchmarker.trace.json.moulinette.attributs.AbstractParents;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author romain
 */
public class Modifs extends AbstractParents implements XMLObjetInterface,Serializable {

    public Modifs() {
        commitsDiff = new ArrayList<CommitDiff>();

    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");

        StringBuffer s = new StringBuffer("");
        for (CommitDiff c : commitsDiff) {
            s.append("\n").append(c.toStringXML()).append("\n");
        }

        b.append("<modifs>").append(s).append("</modifs>");
        return b;
    }
}
