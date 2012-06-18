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
public class Auteur implements XMLObjetInterface,Serializable {

    private String nom = "";
    private String email = "";

    public Auteur() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<auteur>").append(nom).append("<").append(email).append(">" + "</auteur>");
        return b;
    }
}
