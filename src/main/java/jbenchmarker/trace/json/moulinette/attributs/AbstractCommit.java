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
package jbenchmarker.trace.json.moulinette.attributs;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 *
 * @author romain
 */
public abstract class AbstractCommit{

    @JsonProperty("_id")
    private String _id;

    @JsonProperty("_rev")    
    private String _rev;
            
    /*
     * Necessaire pour l'introduction dans la base de données couchdb : si null alors est générée automatiquement
     */

    @JsonProperty("_id")   
    public String getIdCommit() {
        return _id;
    }

    @JsonProperty("_id")
    public void setIdCommit(String idCommit) {
        this._id = idCommit;
    }
    
    /*
     * Necessaire pour l'introduction dans la base de données couchdb : si null alors est générée automatiquement
     */
    @JsonProperty("_rev")    
    public String getRevision() {
        return _rev;
    }
        
    @JsonProperty("_rev")
    public void setRevision(String rev) {
        this._rev = rev;
    }
    
    
}
