/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

/**
 * Bean corresponding to JGit DiffEntry
 * @author urso
 */
public class Entry {
    private ChangeType type;
    private String newPath;
    private String oldPath;
    private int score;
    
    public Entry() {
    }
    
    public Entry(DiffEntry e) {
        type = e.getChangeType();
        newPath = e.getNewPath();
        oldPath = e.getOldPath();
        score = e.getScore();
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }
}
