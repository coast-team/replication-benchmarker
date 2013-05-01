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
import java.util.ArrayList;
import jbenchmarker.trace.json.moulinette.attributs.Type;
import jbenchmarker.trace.json.moulinette.attributs.TypeModif;

/**
 * Permet de rejouer les modifications d'un document pour connaître exactement la position d'une ligne dans ce document 
 * @author Romain
 */

public class DocumentJSON {
    
    /* Contient la liste des lignes d'un document/fichier.
     * L'objet Ligne désigne une ligne du document.
     * Une ligne du document est représentée par un objet java ElementModif qui est l'extraction d'une ligne par le projet MoulinetteGIT associé au nombre de caractères du début du document jusqu'au début de cette ligne 
     * Cette classe permet de "rejouer" les actions des modifications pour trouver le nombre de caratère avant chaque ligne ; nombre nécessaire à la création d'une SequenceOperation
     */
    ArrayList<Ligne> lines;
    
    int numlastLine;
            
    int totcarac;
    
    String nom;
    
    public DocumentJSON(String n){
        nom = n;
        lines = new ArrayList<Ligne>();

        //initialisation du document
        ElementModif e = new ElementModif();
        e.setLigne("");
        e.setNum(0);
        e.setType(TypeModif.addText);
        
        lines.add(new Ligne(e,0));
   
    }
    
    /*
     * Ajoute une ligne au document dans l'intervalle des lignes du document plus une ligne au cas où on l'ajouterai à la fin du document
     */
    public void add(ElementModif e) throws Exception{
 
        if(e.getNum() > 0 && e.getNum() <= (numlastLine+1)){
            
            //Regarde si la ligne existe déjà
            if(lines.get(e.getNum()) == null){
                //Nouvelle ligne à partir du nombre de caractère de l'ancienne ligne + la longueur de l'ancienne ligne
                int pos = lines.get((e.getNum()-1)).getNbCarac() + e.getLigne().length();
                Ligne l = new Ligne(e,pos);
                lines.add(l);
                //mise à jour des lignes supérieures à la ligne qui vient d'être modifiée
                this.upadtelignesSup(e);
            }else{
                throw new Exception("Impossible : numéro de ligne déjà existant");
            }
        }   
        
    }

    /*
     * Supprime une ligne si possible sinon déclenche une Exception car la ligne se doit d'être présente
     */
    public void del(ElementModif e) throws Exception{
        if(e.getNum() > 0 && e.getNum() <= (numlastLine+1)){
            
            //Regarde si la ligne existe déjà
            if(lines.get(e.getNum()) != null){          
                lines.remove(e.getNum().intValue());
                //mise à jour des lignes supérieures à la ligne qui vient d'être modifiée
                this.upadtelignesSup(e);
                
            }else{
                throw new Exception("Impossible : numéro de ligne inexistant");
            }
        }   
        
    }
    
    /*
     * Met à jour le nombre de caractères de la ligne numl en ajoutant ou en supprimant le nombre de caractères nbCaractModifs
     */
    private void updateNbCaracters(int numl, int nbCaractersModif) throws Exception{
        Ligne l = lines.get(numl);
        l.setNbCarac(l.getNbCarac()+nbCaractersModif);
    }

    /*
     * Met à jour le numéro de la ligne en ajoutant ou en supprimant nb_lignes au numéro de la ligne
     */
    private void updateNumLine(int numl, int nbLinesModif) throws Exception{
        ElementModif e = lines.get(numl).getE();
        lines.get(numl).getE().setNum(e.getNum()+nbLinesModif);
    }
    
    /*
     * regarde l'opération de e et lance, sur toutes les lignes suppérieures ou égale du numéro de ligne e dans le document, les fonctions updateNombreCaractère et updateNumLine
     */
    private void upadtelignesSup(ElementModif e) throws Exception{
        int nbLinesModif =0;
        if(e.getType() == TypeModif.addText){
            nbLinesModif = 1;
        }else{
            nbLinesModif = -1;
        }
        
        for(int i = e.getNum();i<lines.size()-1;i++){
            updateNbCaracters(i,e.getLigne().length());
            updateNumLine(i,nbLinesModif);
        }
    }

    //permet de retourner le nombre de caractères de la ligne qui a été modifié par la modif de l'objet java ElementModif e
    public int get(ElementModif e) {
       return lines.get(e.getNum()).getNbCarac();
    }
    
}
