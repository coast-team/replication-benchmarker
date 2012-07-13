/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
