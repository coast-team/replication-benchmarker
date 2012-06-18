/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;

/**
 *
 * @author romain
 */
public class RefBranche implements XMLObjetInterface,Serializable {

    private String nomBranche;
    private String nomRepertoire;

    public RefBranche() {
  
    }

    public String getNomBranche() {
        return nomBranche;
    }

    public void setNomBranche(String nomBranche) {
        this.nomBranche = nomBranche;
    }

    //@JsonIgnoreProperties(ignoreUnknown=true)
    public String getNomRepertoire() {
        return nomRepertoire;
    }

    public void setNomRepertoire(String nomRepertoire) {
        this.nomRepertoire = nomRepertoire;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<refBranche>" + "\n\t").append("<nomBranche>").append(nomBranche).append("</nomBranche>" + "\n\t").append("<nomRepertoire>").append(nomRepertoire).append("</nomRepertoire>" + "\n").append("<refBranche>");
        return b;
    }
}
