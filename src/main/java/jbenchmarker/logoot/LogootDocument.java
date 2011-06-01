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

import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;

import jbenchmarker.trace.TraceOperation;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootDocument implements Document {

    final private RangeList<LogootIdentifier> idTable;
    final private RangeList<Character> document;

    public LogootDocument(long max) {
        super();
        document = new RangeList<Character>();
        idTable = new RangeList<LogootIdentifier>();

        LogootIdentifier Begin = new LogootIdentifier(1), End = new LogootIdentifier(1);
        Begin.addComponent(new Component(0, -1, -1));
        End.addComponent(new Component(max, -1, -1));

        idTable.add(Begin);
        document.add(' ');
        idTable.add(End);
        document.add(' ');
    }
    
    public String view() {
        StringBuilder s = new StringBuilder();
        for (char c : document) {
            s.append(c);
        }
        return s.substring(1, s.length() - 1);
    }

    int dicho(LogootIdentifier idToSearch) {
        int startIndex = 0, endIndex = idTable.size() - 1, middleIndex;
        do {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            int c = idTable.get(middleIndex).compareTo(idToSearch);
            if (c == 0) {
                return middleIndex;
            } else if (c < 0) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex - 1;
            }
        } while (startIndex < endIndex);
        return (idTable.get(startIndex).compareTo(idToSearch) < 0)
                ? startIndex + 1 : startIndex;
    }
    
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        LogootIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if (lg.getType() == TraceOperation.OpType.ins) {
            idTable.add(pos, idToSearch);
            document.add(pos, lg.getContent());
        } else if (idTable.get(pos).equals(idToSearch)) {
            idTable.remove(pos);
            document.remove(pos);
        } 
    }

    public RangeList<LogootIdentifier> getIdTable() {
        return idTable;
    }

    public RangeList<Character> getDocument() {
        return document;
    }
}