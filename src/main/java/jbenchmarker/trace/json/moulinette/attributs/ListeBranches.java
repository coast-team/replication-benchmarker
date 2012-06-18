/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author romain
 */
public class ListeBranches implements XMLObjetInterface,Serializable {

    private List<RefBranche> refBranche;

    public ListeBranches() {
        refBranche = new ArrayList<RefBranche>();
 
    }

    public List<RefBranche> getRefBranche() {
        return refBranche;
    }

    public void setRefBranche(List<RefBranche> refBranche) {
        this.refBranche = refBranche;
    }

    public void addBranche(RefBranche ref) {
        this.refBranche.add(ref);
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        StringBuffer s = new StringBuffer("");

        for (RefBranche r : refBranche) {
            s.append("\n").append(r.toStringXML()).append("\n");
        }
        b.append("<listeBranche>").append(s).append("</listeBranche>");
        return b;
    }
}
