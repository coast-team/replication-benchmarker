/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import jbenchmarker.trace.json.moulinette.attributs.Dependances;
import jbenchmarker.trace.json.moulinette.attributs.DateXML;
import jbenchmarker.trace.json.moulinette.attributs.AbstractCommit;
import jbenchmarker.trace.json.moulinette.attributs.Auteur;
import java.io.Serializable;

/**
 *
 * @author romain
 */
public class ObjetCommit extends AbstractCommit implements XMLObjetInterface,Serializable {

    private Auteur auteur;
    private DateXML date;
    private Dependances dependances;
    private ListeBranches listeBranches;
    private Modifs modifs;

    public ObjetCommit() {

    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public DateXML getDate() {
        return date;
    }

    public void setDate(DateXML date) {
        this.date = date;
    }

    public Dependances getDependances() {
        return dependances;
    }

    public void setDependances(Dependances dependances) {
        this.dependances = dependances;
    }

    public ListeBranches getListeBranches() {
        return listeBranches;
    }

    public void setListeBranches(ListeBranches listeBranches) {
        this.listeBranches = listeBranches;
    }

    public Modifs getModifs() {
        return modifs;
    }

    public void setModifs(Modifs modifs) {
        this.modifs = modifs;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<objetCommit>" + "\n\t").append("<idCommit>").append(this.getIdCommit()).append("</idCommit>\n").append("</objetCommit>\n");
        //b.append(strNiv()).append("<objetCommit>" + "\n\t").append(strNiv()).append("<idCommit>").append(this.getIdCommit()).append("</idCommit>\n").append(this.auteur.toStringXML()).append("\n").append(this.date.toStringXML()).append("\n" + "</objetCommit>\n");
        //b.append(strNiv()).append("<objetCommit>" + "\n\t").append(strNiv()).append("<idCommit>").append(this.getIdCommit()).append("</idCommit>\n").append(this.auteur.toStringXML()).append("\n").append(this.date.toStringXML()).append("\n").append(this.dependances.toStringXML()).append("\n").append(this.modifs.toStringXML()).append("\n").append(this.listeBranches.toStringXML()).append("\n").append(strNiv()).append("</objetCommit>\n\n\n");
        
        return b;
    }
}
