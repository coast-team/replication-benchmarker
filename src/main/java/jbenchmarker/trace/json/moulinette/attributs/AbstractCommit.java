/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
