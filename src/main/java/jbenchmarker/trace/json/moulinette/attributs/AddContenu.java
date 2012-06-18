/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author romain
 */
public class AddContenu implements XMLObjetInterface,Serializable {

    private HashMap<Integer,String> addText;
    
    public AddContenu() {

    }
    
    public HashMap<Integer,String> getAddText() {
        return addText;
    }

    public void setAddText(HashMap<Integer,String> addText) {
        this.addText = addText;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<addContenu>").append(this.addText).append("\n").append("</addContenu>");
        return b;
    }
}
