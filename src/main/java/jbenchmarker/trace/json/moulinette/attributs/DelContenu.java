/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author romain
 */
public class DelContenu implements XMLObjetInterface,Serializable {

    private HashMap<Integer,String> delText;

    public DelContenu() {
    }

    public HashMap<Integer, String> getDelText() {
        return delText;
    }

    public void setDelText(HashMap<Integer, String> delText) {
        this.delText = delText;
    }
    
    
    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<delContenu>").append(delText).append("\n").append("</delContenu>");
        return b;
    }
}
