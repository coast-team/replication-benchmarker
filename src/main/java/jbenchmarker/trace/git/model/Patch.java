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
/*
 * Represent a file edition. 
 */
package jbenchmarker.trace.git.model;

import java.io.Serializable;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author urso
 */

@JsonIgnoreProperties({"id", "revision"})
public class Patch implements Serializable{
    public static final String CONTENT = "CONTENT";
    
    protected List<FileEdition> edits;
    protected List<String> paths;
    protected List<byte[]> raws;
    
    public Patch() {
    }
    
    /**
     * Classical diff constructor.
     * @param commit
     * @param parent
     * @param listEdit 
     */
    public Patch(Commit commit, RevCommit parent, List<FileEdition> listEdit) {
        this.edits = listEdit;
        this.id = commit.getId() + ObjectId.toString(parent);
    }
    
    /**
     * Base commit patch constructor.
     * @param commit
     * @param listEdit 
     */
    public Patch(Commit commit, List<FileEdition> listEdit) {
        this.edits = listEdit;
        this.id = commit.getId();
    }
    
    /**
     * Merge commit patch constuctor
     * @param commit
     * @param listEdit 
     */
    public Patch(Commit commit, List<String> paths, List<byte[]> raws) {       
        this.paths = paths;
        this.raws = raws;
        this.id = commit.getId() + CONTENT;
    }
    
    @JsonProperty("_id")
    private String id;

    @JsonProperty("_rev")
    private String revision;
        
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getRevision() {
        return revision;
    }

    public List<FileEdition> getEdits() {
        return edits;
    }

    public void setEdits(List<FileEdition> listEdit) {
        this.edits = listEdit;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<byte[]> getRaws() {
        return raws;
    }

    public void setRaws(List<byte[]> raws) {
        this.raws = raws;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Patch : \n");
        if (edits != null) {
            for (FileEdition e : edits) {
                s.append(e.toString());
            }
        } 
        return s.toString();
    }
}
