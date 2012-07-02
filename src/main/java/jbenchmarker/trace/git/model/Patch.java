/*
 * Represent a file edition. 
 */
package jbenchmarker.trace.git.model;

import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Commit;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;
import org.eclipse.jgit.revwalk.RevCommit;
import org.ektorp.support.TypeDiscriminator;

/**
 *
 * @author urso
 */

@JsonIgnoreProperties({"id", "revision"})
public class Patch {
    public static final String content = "CONTENT";
    
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
        this.id = commit.getId() + content;
    }
    
    /**
     * Merge commit patch constuctor
     * @param commit
     * @param listEdit 
     */
    public Patch(Commit commit, List<String> paths, List<byte[]> raws) {       
        this.paths = paths;
        this.raws = raws;
        this.id = commit.getId() + content;
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
