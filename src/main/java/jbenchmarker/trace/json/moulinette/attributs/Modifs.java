/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import jbenchmarker.trace.json.moulinette.attributs.CommitDiff;
import jbenchmarker.trace.json.moulinette.attributs.AbstractParents;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author romain
 */
public class Modifs extends AbstractParents implements XMLObjetInterface,Serializable {

    public Modifs() {
        commitsDiff = new ArrayList<CommitDiff>();

    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");

        StringBuffer s = new StringBuffer("");
        for (CommitDiff c : commitsDiff) {
            s.append("\n").append(c.toStringXML()).append("\n");
        }

        b.append("<modifs>").append(s).append("</modifs>");
        return b;
    }
}
