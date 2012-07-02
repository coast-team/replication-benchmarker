/*
 * Represent a file edition. 
 */
package jbenchmarker.trace.git.model;

import java.util.List;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;

/**
 *
 * @author urso
 */

public class FileEdition {
    protected Entry entry;
    protected String path;
    protected List<Edition> listDiff;
    protected FileHeader.PatchType type;
    
    public FileEdition() {
    }
    
    /**
     * Constructor for a diff
     */
    public FileEdition(DiffEntry entry, PatchType type, List<Edition> listEdit) {
        this.entry = new Entry(entry);
        this.path = null;
        this.listDiff = listEdit;
        this.type = type;
    }
    
    /**
     * Constructor for an initial commit 
     */
    public FileEdition(String path, List<Edition> listEdit) {
        this.entry = null;
        this.path = path;
        this.listDiff = listEdit;
        this.type = (listEdit == null) ? 
                FileHeader.PatchType.BINARY : FileHeader.PatchType.UNIFIED;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Edition> getListDiff() {
        return listDiff;
    }

    public void setListDiff(List<Edition> listEdit) {
        this.listDiff = listEdit;
    }
    
    public PatchType getType() {
        return type;
    }

    public void setType(PatchType type) {
        this.type = type;
    }
    
    public void addEdition(Edition e) {
        listDiff.add(e);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("FileEdition : type=").append(type).append(", entry=").append(entry).append("}\n");
        if (listDiff != null) {
            for (Edition e : listDiff) {
                s.append(e.toString());
            }
        } 
        return s.toString();
    }
}
