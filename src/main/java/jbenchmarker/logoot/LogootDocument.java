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
package jbenchmarker.logoot;

import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceMessage;

import jbenchmarker.core.SequenceOperation;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootDocument<T> implements Document{

    final protected RangeList<LogootIdentifier> idTable;
    final private RangeList<T> document;

    public LogootDocument(long max) {
        super();
        document = new RangeList<T>();
        idTable = new RangeList<LogootIdentifier>();

        LogootIdentifier Begin = new LogootIdentifier(1), End = new LogootIdentifier(1);
        Begin.addComponent(new Component(0, -1, -1));
        End.addComponent(new Component(max, -1, -1));

        idTable.add(Begin);
        document.add(null);
        idTable.add(End);
        document.add(null);
    }
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (int i=1; i< document.size()-1; ++i) {
            s.append(document.get(i));
        }
        return s.toString();
    }

    int dicho(List<LogootIdentifier> idTable, LogootIdentifier idToSearch) {
        int startIndex = 1, endIndex = idTable.size() - 1, middleIndex;
        while (startIndex < endIndex) {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            int c = idTable.get(middleIndex).compareTo(idToSearch);
            if (c == 0) {
                return middleIndex;
            } else if (c < 0) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex;
            }
        } 
        return startIndex;
    }
    
    int dicho(LogootIdentifier idToSearch) {
        return dicho(idTable, idToSearch);
    }
    
    @Override
    public void apply(SequenceMessage op) {
        LogootOperation lg = (LogootOperation) op;
        LogootIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idTable, idToSearch);
        //Insertion et Delete
        if (lg.getType() == SequenceOperation.OpType.ins) {
            idTable.add(pos, idToSearch);
            document.add(pos, (T) lg.getContent());
        } else if (idTable.get(pos).equals(idToSearch)) {
            idTable.remove(pos);
            document.remove(pos);
        }
    }
    
    public void insert(int position, List<LogootIdentifier> patch, List<T> lc) {
        idTable.addAll(position + 1, patch);
        document.addAll(position + 1, lc);
    }
    
    public void remove(int position, int offset) {
        idTable.removeRangeOffset(position + 1, offset);
        document.removeRangeOffset(position + 1, offset);
    }
    
    public LogootIdentifier getId(int pos) {
        return idTable.get(pos);
    }

    @Override
    public int viewLength() {
        return document.size()-2;
    }
}
