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
public class DateXML implements XMLObjetInterface,Serializable {

    private String date = "";
    private Format format;

    public DateXML() {

    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<date format=\"").append(format).append("\">").append(date).append("</date>");
        return b;
    }
}
