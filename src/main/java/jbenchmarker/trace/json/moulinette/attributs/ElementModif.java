/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

/**
 *
 * @author Romain
 */
//Permet de stocker le contenu de l'ajout ou de la suppression de ligne dans un fichier modifié
public class ElementModif implements XMLObjetInterface {
    
    //numéro de la ligne
    private Integer num;
    
    // Type de la modification : ajout ou suppression de texte
    private TypeModif type;
    
    // Texte modifié
    private String ligne;    

    
    public ElementModif(){
    }

    public String getLigne() {
        return ligne;
    }

    public void setLigne(String ligne) {
        this.ligne = ligne;
    }

    public TypeModif getType() {
        return type;
    }

    public void setType(TypeModif type) {
        this.type = type;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
    
    
    

    @Override
    public StringBuffer toStringXML() {
        StringBuffer s = new StringBuffer();
        s.append("<ligne num=\"").append(num).append("\" ").append("type=\"").append(type).append("\">").append(s).append("</ligne>");
        return s;
    }
        
    
    
}
