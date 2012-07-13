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

package jbenchmarker.trace.json.moulinette;

import jbenchmarker.trace.json.moulinette.attributs.ObjetCommit;
import jbenchmarker.trace.json.moulinette.attributs.TypeModif;
import jbenchmarker.trace.json.moulinette.attributs.VectorClockMapper;
import jbenchmarker.trace.json.moulinette.attributs.FileOperations;
import jbenchmarker.trace.json.moulinette.attributs.ElementModif;
import jbenchmarker.trace.json.moulinette.attributs.CommitDiff;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.TraceGenerator;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * @author romain
 */
//Class INCOMPLETE
public class JSONTraceDB implements Trace{

    private ArrayList<TraceOperation> ops; 
    private CouchDbConnector db;
    
    private HashMap<String,Integer> numReplica;
    
    //permet de stocker la liste des documents qui permettent de stocker les lignes d'un fichier
    private HashMap<String,DocumentJSON> listeDocs;
       
    public JSONTraceDB(String nomBDD) throws Exception, FileNotFoundException, IOException {
       
        
       //Connexion à la base de données
       HttpClient httpClient = new StdHttpClient.Builder().build();
       CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
       // if the second parameter is true, the database will be created if it doesn't exists
       db = dbInstance.createConnector(nomBDD, true);
               
        
        //recherche de la bd
        boolean trouve = false;
        trouve = dbInstance.checkIfDbExists(new DbPath(nomBDD));
        if(!trouve){
            throw new Exception("Base de données "+nomBDD+" inexistante");
        }
            
        //Pour chaque nouvelle branche, on crée un nouveau vector clock
        VectorClockMapper vectorClockMapper = new VectorClockMapper();
        
        //initialistaion de la liste des traces operations
        this.ops = new ArrayList<TraceOperation>();
        //FileReader f = new FileReader("/home/damien/etherpad-lite/var/dirtyCS.db");
        
         //Récupération de tous les ids dans la base de données
        List<String> l = db.getAllDocIds();        
       
        /*
         * Recherche de tous les commits n'ayant aucun fils
        */
        HashMap<String,Boolean> lb = new HashMap<String,Boolean>();
        for(String la : l){
            //test au cas où un parent aurait été ajouter avant que l'on itère dessus
            if(!lb.containsKey(la)){
                //insertion du commit dont on ne sait pas encore si c'est un parent ou non
                lb.put(la,true);
            }
            //mise en place d'un correctif d'erreur d'accès à la base de données
            boolean b = true;
            ArrayList<String> lpa = null;  
            
            while(b){
            try{
               lpa =  db.get(ObjetCommit.class, la).getDependances().getIdParents();
               b = false;
            }catch(DbAccessException e){
                b = true;
                System.out.println("Access DB Error");
            
            }
            }
            //Itération sur chaque commit parent du commit pour dire à chaque parent qu'il possède au moins un enfant
            for(String lp : lpa){
                lb.put(lp,false);
            }
        }
        System.out.println("recherche commits sans fils terminée");
        
        //Creation d'une arraylist pour stocker les commits qui n'ont pas d'enfant
        ArrayList<String> ar = new ArrayList<String>();
        for(String s : lb.keySet()){
            if(lb.get(s)){
                ar.add(s);
            }
        }
        System.out.println("creation arraylist avec commits sans fils terminée, taille arraylist : "+ar.size());
        
        /*
         * Creation des numéros de réplica pour chaque commit
         */ 
        
        //Initialisation des numéros de réplica dans la Map qui contient les associations entre les ids et les numéros de réplica
        numReplica = new HashMap<String,Integer>();
        for(String la : l){
            numReplica.put(la,-2);
        }
        
        boolean verif = false;
        StringBuffer sa = new StringBuffer("");
        //Verification (normalement inutile) si chaque parent des ids présents dans la liste existe bien dans cette même liste
        for(String s : numReplica.keySet()){
      //      System.out.println("-"+s+"-");
            ObjetCommit ca = db.get(ObjetCommit.class, s);
            if(!ca.getDependances().getIdParents().isEmpty()){
            for(String sc : ca.getDependances().getIdParents()){
                if(!numReplica.containsKey(sc)){
                    verif = true;
                   //sa.append(s).append("\n");
                }
            }
            }
        }
        
        System.out.println("verif : "+verif);
        //System.out.println(sa.toString());
        
        
        //Attribution à chaque id issu de la base de données d'un numéro de réplica
        //initialisation du premier numéro d'id
        int num = 0;
        
        //Iteration sur la liste des commits n'ayant aucun enfant
        for(String a : ar){
            ObjetCommit c = db.get(ObjetCommit.class, a);
            System.out.println(c.getIdCommit());
            num = parcourir(c,num);
        }
        
        
        //affichage pour vérification que tout les commits possèdent un numéro de réplica
        System.out.println("\nListe numéro de réplica");
        for(String s : numReplica.keySet()){
            System.out.println(s+" : "+numReplica.get(s));
        }
        System.out.println("Num replica size : "+numReplica.size());
        System.out.println("Liste all commits size : "+l.size());
        
        
        
        
        
        
        /*
         * Recherche des commits sans parents pour démarrer la création de vectorClock sur les plus anciens commits
         */
        ArrayList<String> as = new ArrayList<String>();
        for(String sp : l){
            ObjetCommit cs = db.get(ObjetCommit.class,sp);
            if(cs.getDependances().getIdParents().isEmpty()){
               as.add(cs.getIdCommit());
            }
        }
        
        System.out.println("nombre de commits sans parents : "+as.size());
        
        
        /*
         * Déclaration d'une map pour stocker des objets java DocumentJSON qui permette de "rejouer" chaque modification sur chaque fichier 
         * afin de récupérer le nombre de caractères entre le début du document jusqu'au début de la ligne qui nous intéresse afin de connaître la place de la ligne exprimée avec le nombre de caractères qui la précède et non plus le numéro de ligne
         */
        listeDocs = new HashMap<String,DocumentJSON>();
        /*
         * Itération sur commits sans parents
         */
        
        for(int i=0;i<as.size();i++){
            //Récupération du commit
            ObjetCommit sp = db.get(ObjetCommit.class,as.get(i));
            
            //Récupération du numéro de réplica
            int repli = this.numReplica.get(sp.getIdCommit());
            
            //itération sur les commits parents du commit pour vectorclocker chaque ligne de chaque opération
            for(CommitDiff cd : sp.getModifs().getCommitsDiff()){
                //itération sur chaque opération
                for(FileOperations fo : cd.getFileOperations()){
                    //itération sur chaque ligne du contenu modifié
                    for(ElementModif e : fo.getContenu()){
                       
                       //Récupération du document associé à la modification
                        DocumentJSON d = listeDocs.get(fo.getPathObjet());
                        
                        if(d == null){
                         d = new DocumentJSON(fo.getPathObjet());   
                        }
                        
                        //Modification du document avec l'opération actuelle
                        if(e.getType() == TypeModif.addText){
                            d.add(e);
                        }else{
                            d.del(e);
                        }
                        //ajout du document dans la liste des documents
                        listeDocs.put(fo.getPathObjet(), d);
                      
                                
                        //Création d'une nouvelle SequenceOperation
                        TraceOperation sop = TraceGenerator.oneJSONDB2OP(repli,d,e,vectorClockMapper);
                        
                    }//fin itération sur les lignes d'une opération
                }//fin itération sur fileOperation pour vectorclocker
            }//fin itération parent pour vectorclocker
            
        }//fin itération
        
        /*
         * itération sur le prochain élément qui se doit être un des enfants des commits précédemment (tâche non facilitée, on a que les parents d'un commit : necessite de parcours à chaque commit pour trouver les enfants ...)
         */
        
    }

    /*
     * Iterateur pour parcourir la liste des commits et créer une 
     */
     public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>() {
            private Iterator<TraceOperation> it = ops.iterator();
            
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public TraceOperation nextElement() {
                return it.next();
            }            
        };
    }
    
    
    /*
    * Fonctions de parcourt des commits pour attribuer à chacun un numéro de réplica
    * Les numéros de réplica avec le SHA-1 du commit sont contenus dans la HashMap "numReplica"
    * 
    */
    
    private int parcourir(ObjetCommit n, int num){
        
        if(numReplica.get(n.getIdCommit()) == -2){
            numReplica.put(n.getIdCommit(),num);
        }
        
        //recherche du premier parent qui n'ait pas de numéro de réplica et descente sur celui ci
        for(String p : n.getDependances().getIdParents()){
            
            ObjetCommit fat = db.get(ObjetCommit.class, p);
            
            if(numReplica.get(p) == -2){
                descente(fat,num);
                num++;
            }
            
        }
        
        //Itération du parcours sur chaque parent du noeud
        for(String pp : n.getDependances().getIdParents()){
            num = parcourir(db.get(ObjetCommit.class, pp),num);
        }
        
        return num;
    }
    
    private void descente(ObjetCommit s,int num){
        //attribution au noeud s du numéro de réplica num
        numReplica.put(s.getIdCommit(),num);
        
        
        /*
         * recherche du premier parent de s tel que c.rep == -1
         */
        //on regarde si la liste des parents du noeud s n'est pas vide
         if (!s.getDependances().getIdParents().isEmpty()) {
    
             boolean bool = false;
             ObjetCommit c = null;
             
             for(String id : s.getDependances().getIdParents()){
                 if(!bool && (numReplica.get(id) == -2)){
                     c = db.get(ObjetCommit.class, id);
                     bool = true;
                 }
             }        
      
         /*
          * Recherche des parents de c qui n'ont pas de numéros de réplicas
          */
         
            //regarde si c n'est pas null et a des parents
            if ((c != null) && (!c.getDependances().getIdParents().isEmpty())) {
                boolean b = false;

                //Vérification qu'il existe un parent qui n'a pas de numéro de réplica dans les parents de c
                for(String pr : c.getDependances().getIdParents()){
                    if(numReplica.get(pr) == -2){
                        b = true;
                    }
                }

                //verification qu'un parent ait été trouvé
                if (b) {
                    descente(c, num);
                }
                
            }//fin verif c a des parents  
            
        }//fin verif s a des parents
 
    }
    
  
    
    public static void main(String[] args) throws Exception{
        JSONTraceDB db = new JSONTraceDB("gitmatthieu112");
    }
    
}
