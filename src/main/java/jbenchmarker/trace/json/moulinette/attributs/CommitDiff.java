/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
