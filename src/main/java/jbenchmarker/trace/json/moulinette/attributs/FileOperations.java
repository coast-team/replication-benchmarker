/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author romain
 */
//Stocke une Opération entre deux états (commits) d'un fichier
public class FileOperations implements XMLObjetInterface, Serializable {

    //SHA-1 du nouveau fichier
    private String idObjet;
    //SHA-1 de l'ancien fichier
    private String idObjetCommitDiff;
    //chemin de l'objet
    private String PathObjet;

    //contenu modifié
    List<ElementModif> contenu;
    
    //typ de la modification : add_file, del_file, update_file, ...
    private Type type;

    public FileOperations() {
        contenu = new LinkedList();
    }

    public String getPathObjet() {
        return PathObjet;
    }

    public void setPathObjet(String PathObjet) {
        this.PathObjet = PathObjet;
    }

    public List<ElementModif> getContenu() {
        return contenu;
    }

    public void setContenu(List<ElementModif> contenu) {
        this.contenu = contenu;
    }

    public String getIdObjet() {
        return idObjet;
    }

    public void setIdObjet(String idObjet) {
        this.idObjet = idObjet;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIdObjetCommitDiff() {
        return idObjetCommitDiff;
    }

    public void setIdObjetCommitDiff(String idObjetCommitDiff) {
        this.idObjetCommitDiff = idObjetCommitDiff;
    }

    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");

        StringBuffer c = new StringBuffer("");
        
       for(ElementModif m : contenu){
                c.append("\n").append(m.toStringXML()).append("</ligne>"); 
        }

        b.append("<fileOperations type=\"").append(this.type).append("\">" + "\n\t").append("<idObjet>").append(this.idObjet).append("</idObjet>" + "\n\t").append("<idObjetCommitDiff>").append(this.idObjetCommitDiff).append("</idObjetCommitDiff>" + "\n\t").append("<pathObjet>").append(this.PathObjet).append("</pathObjet>").append(c).append("\n").append("</fileOperations>");

        return b;

    }
}
