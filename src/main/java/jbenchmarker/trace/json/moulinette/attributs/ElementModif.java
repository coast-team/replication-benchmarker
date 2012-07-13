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
