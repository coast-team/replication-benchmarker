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
package jbenchmarker.trace.json.moulinette.attributs;

import java.util.List;

/**
 *
 * @author romain
 */
public abstract class AbstractParents {
    
    //Liste des commits parents d'un commit, identifiés dans la HashMap par leurs SHA-1 et représenté par l'objet CommiDiff
    /*
     * L'objet CommitDiff dans la HashMap n'a aucune valeur dans le programme, c'est de l'ajout inutile de donné pour ce qui est actuellement demander dans le sujet : A SUPPRIMER SI NECESSAIRE
     * Il contient également SHA-1, il y a redondance de données ...
    */
    List<CommitDiff> commitsDiff;

    public List<CommitDiff> getCommitsDiff() {
        return commitsDiff;
    }

    public void setCommitsDiff(List<CommitDiff> commitsDiff) {
        this.commitsDiff = commitsDiff;
    }


    
}
