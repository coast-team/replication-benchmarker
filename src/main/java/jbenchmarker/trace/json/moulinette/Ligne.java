/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
