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

import java.util.List;
import jbenchmarker.core.Document;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public interface LogootSDoc extends Document{
    public void addBlock(Identifier id,List l);
    //public void addBlock(LogootSBlock block);
    //void delBlock(LogootSBlock block, int begin, int fin);
    public void delBlock(IdentifierInterval id);
    public LogootSOp insertLocal(int pos,List l);
    public LogootSOp delLocal(int begin,int end);
    public LogootSDoc create();
    //public void setAlgo(LogootSAlgo algo);
    public void setReplicaNumber(int i);
}
