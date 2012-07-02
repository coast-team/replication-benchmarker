/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git.model;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.ektorp.support.TypeDiscriminator;

/**
 *
 * @author urso
 */
@JsonIgnoreProperties({"id", "revision"})
public class Commit {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String revision;
        
    private Person author;
    private Person committer;

    private String encoding;
    
    @TypeDiscriminator
    private String message;
    private List<String> parentsIds;
    private List<String> childrenIds;
    private int replica;
    
    public Commit(String id, RevCommit co, Set<String> children) {
        this.id = id;
        this.author = new Person(co.getAuthorIdent());
        this.committer = new Person(co.getCommitterIdent());
        this.encoding = co.getEncoding().name();
        this.message = co.getFullMessage();
        this.parentsIds = new LinkedList<String>();
        for (RevCommit p : co.getParents()) {
            parentsIds.add(ObjectId.toString(p));
        }
        this.childrenIds = (children == null) ? 
                new LinkedList<String>() : new LinkedList<String>(children);
    }
    
    public Commit(RevCommit co, Set<String> children) {
        this(ObjectId.toString(co), co, children);
    }

    public Commit() {
    }

    public String getRevision() {
        return revision;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Person getCommitter() {
        return committer;
    }

    public void setCommitter(Person committer) {
        this.committer = committer;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getParents() {
        return parentsIds;
    }

    public void setParents(List<String> parentsIds) {
        this.parentsIds = parentsIds;
    }

    public List<String> getChildren() {
        return childrenIds;
    }

    public void setChildren(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    /**
     * Patch id corresponding to ith parent
     * @return id_commit + id_parent[i]
     */
    public String parentPatchId(int i) {
        return id + parentsIds.get(i);
    }
    
    /**
     * Patch id corresponding to ith child
     * @return id_child[i] + id_commit
     */
    public String childPatchId(int i) {
        return childrenIds.get(i) + id;
    }
    
    /**
     * Patch id in case of commit without parent
     * @return id + "CONTENT"
     */
    public String patchId() {
        return id + Patch.content;
    }
    
    public int parentCount() {
        return parentsIds.size();
    }
    
    public int childrenCount() {
        return childrenIds.size();
    }

    public int getReplica() {
        return replica;
    }

    public void setReplica(int replica) {
        this.replica = replica;
    }

    @Override
    public String toString() {
        return "Commit{" + "id=" + id + ", revision=" + revision + ", author=" + author + ", committer=" + committer + ", encoding=" + encoding + ", message=" + message + ", parentsIds=" + parentsIds + ", childrenIds=" + childrenIds + ", replica=" + replica + '}';
    }
}
