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
package jbenchmarker.core;

import java.io.Serializable;

/**
 * Interface for a document. 
 * @author urso
 */
public interface Document extends Serializable {
    
    /* 
     * View of the document (without metadata)
     */
    public String view();
    
    /*
     * Length of the view (to generate random operations)
     */
    public int viewLength();
    
    /**
     * Applies a character operation
     */ 
    public void apply(Operation op);
}
