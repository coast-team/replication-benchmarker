/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootsplitO;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSOpDel extends LogootSOp {

    List<IdentifierInterval> lid;

    public LogootSOpDel(List<IdentifierInterval> lid) {
        this.lid = lid;
    }

    @Override
    public LogootSOp clone() {
        return new LogootSOpDel(new LinkedList(lid));
    }

    @Override
    public void apply(LogootSDoc doc) {
        for (IdentifierInterval id : lid) {
            doc.delBlock(id);
        }
    }

    @Override
    public String toString() {
        return "LogootSOpDel{" + "lid=" + lid + '}';
    }
    
}
