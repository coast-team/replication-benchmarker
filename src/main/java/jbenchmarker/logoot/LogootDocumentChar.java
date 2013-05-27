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

import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;

/**
 * A Logoot documentStr. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootDocumentChar extends LogootDocument<Character> {
    private int myClock;
    
    
    final protected StringBuilder documentStr;
    
    public LogootDocumentChar(int r, LogootStrategy strategy) {
        super(r, strategy);
        documentStr = new StringBuilder();
        
        this.replicaNumber = r;
        myClock = 0;        
    } 
    
    @Override
    public String view() {
        return documentStr.toString();
    }

  
    
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        ListIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if (lg.getType() == SequenceMessage.MessageType.ins) {
            idTable.add(pos, idToSearch);
            documentStr.insert(pos-1,  lg.getContent());
        } else if (idTable.get(pos).equals(idToSearch)) {
            idTable.remove(pos);
            documentStr.deleteCharAt(pos-1);
        }
    }
    private char[] makeChar(List<Character> o) {
        char[] ret = new char[o.size()];

        for (int i = 0; i < o.size(); i++) {
            ret[i] = o.get(i);
        }
        
        return ret;
    }
    
    
    @Override
    public void insert(int position, List<ListIdentifier> patch, List<Character> lc) {
        idTable.addAll(position + 1, patch);
        
        documentStr.insert(position ,makeChar( lc));
    }
    
    @Override
    public void remove(int position, int offset) {
        idTable.removeRangeOffset(position + 1, offset);
        documentStr.delete(position ,position+offset);
    }
    
    
    @Override
    public int viewLength() {
        return documentStr.length();
    }

    // TODO : duplicate strategy ?
    @Override
    public LogootDocumentChar create() {
        return new LogootDocumentChar(replicaNumber, strategy);
    }

   
   
    
        
   
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootDocumentChar other = (LogootDocumentChar) obj;
        if (this.idTable != other.idTable && (this.idTable == null || !this.idTable.equals(other.idTable))) {
            return false;
        }
        if (this.documentStr != other.documentStr && (this.documentStr == null || !this.documentStr.equals(other.documentStr))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.idTable != null ? this.idTable.hashCode() : 0);
        hash = 97 * hash + (this.documentStr != null ? this.documentStr.hashCode() : 0);
        return hash;
    }
    
   
}
