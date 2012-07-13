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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author romain
 */
public class CommitDiff extends AbstractCommit implements XMLObjetInterface,Serializable {

    private List<FileOperations> fileOperations;

    public CommitDiff() {

        fileOperations = new ArrayList<FileOperations>();
    }

    public List<FileOperations> getFileOperations() {
        return fileOperations;
    }

    public void setFileOperations(List<FileOperations> fileOperations) {
        this.fileOperations = fileOperations;
    }

    public void addFileOperations(FileOperations f){
        this.fileOperations.add(f);
    }
    
    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        //b.append(strNiv()).append("<commitDiff>" + "\n\t").append(strNiv()).append("<idCommitDiff>").append(this.getIdCommit()).append("</idCommitDiff>" + "\n").append(strNiv()).append("</commitDiff>");
        StringBuffer s = new StringBuffer("");
        for(FileOperations f : fileOperations){
        s.append(f.toStringXML());
        }
        
        b.append("<commitDiff>" + "\n\t").append("<idCommitDiff>").append(this.getIdCommit()).append("</idCommitDiff>" + "\n").append(s).append("\n").append("</commitDiff>");
        return b;
    }
}
