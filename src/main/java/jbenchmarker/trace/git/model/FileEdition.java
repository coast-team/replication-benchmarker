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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;

/**
 *
 * @author urso
 */

public class FileEdition implements Serializable{
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
        this.path = entry.getNewPath() == null ? entry.getOldPath() : entry.getNewPath();
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
