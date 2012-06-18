package jbenchmarker.trace.json.moulinette.attributs;

import jbenchmarker.trace.json.*;
import collect.VectorClock;
import java.util.HashMap;

/**
 *
 * @author romain
 */
public class VectorClockMapper {

    //Contient la liste du dernier VectorClock de chaque branche, soit un VectorClock par numéro de réplica
    private HashMap<Integer, VectorClock> tabUId;

    public VectorClockMapper() {
       this.tabUId = new HashMap<Integer, VectorClock>();
    }
      
    public void put(Integer rep, VectorClock v){
        tabUId.put(rep, v);
    }
    
    /* @param key numéro de réplica
     * @return retourne le dernier vectorClock associé au numéro de réplica
    */
    public VectorClock get(Integer key){
        if(!this.tabUId.containsKey(key)){  
            VectorClock v = new VectorClock();
           this.tabUId.put(key,v);
       }
        return tabUId.get(key);
    }
    
}
