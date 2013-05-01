/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker.trace.json.moulinette;

import jbenchmarker.trace.json.moulinette.attributs.ElementModif;

/**
 *
 * @author Romain
 */
public class Ligne {
    ElementModif e;
    
    //index de départ pour indiquer le nombre de caractères qui se trouve avant cette ligne depuis le début du document
    int nbCarac;
    
    public Ligne(ElementModif ea,int n){
        ea = e;
        nbCarac = n;
    }

    public ElementModif getE() {
        return e;
    }

    public void setE(ElementModif e) {
        this.e = e;
    }

    public int getNbCarac() {
        return nbCarac;
    }

    public void setNbCarac(int nbCarac) {
        this.nbCarac = nbCarac;
    }


    

}
