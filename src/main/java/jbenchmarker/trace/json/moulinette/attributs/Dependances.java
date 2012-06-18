/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author romain
 */
public class Dependances implements XMLObjetInterface,Serializable {
    ArrayList<String> idParents;
    
    public Dependances() {

        idParents = new ArrayList<String>();
    }

    public ArrayList<String> getIdParents() {
        return idParents;
    }

    public void setIdParents(ArrayList<String> idParents) {
        this.idParents = idParents;
    }
    
    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        StringBuffer s = new StringBuffer("");

        for (String c : idParents) {
            s.append("\n\t").append("<idParent>").append(c).append("</idParent>\n");
        }

        b.append("<dependances>").append(s).append("</dependances>");
        s = null;
        return b;
    }
    
    
    
    
}
