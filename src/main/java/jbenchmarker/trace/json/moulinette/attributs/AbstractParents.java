/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
