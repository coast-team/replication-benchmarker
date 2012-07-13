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
