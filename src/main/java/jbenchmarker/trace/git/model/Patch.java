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
    
    protected List<FileEdition> listEdit;
    
    @TypeDiscriminator  
    protected String commitId;
    
    protected String parentId;
    
    public Patch() {
    }
    
    public Patch(Commit commit, RevCommit parent, List<FileEdition> listEdit) {
        this.commitId = commit.getId();
        this.parentId = ObjectId.toString(parent);
        this.listEdit = listEdit;
        this.id = commitId + parentId;
    }
    
    public Patch(Commit commit, List<FileEdition> listEdit) {
        this.commitId = commit.getId();
        this.parentId = null;
        this.listEdit = listEdit;
        this.id = commitId + content;
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

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<FileEdition> getListEdit() {
        return listEdit;
    }

    public void setListEdit(List<FileEdition> listEdit) {
        this.listEdit = listEdit;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Patch : \n");
        if (listEdit != null) {
            for (FileEdition e : listEdit) {
                s.append(e.toString());
            }
        } 
        return s.toString();
    }
}
