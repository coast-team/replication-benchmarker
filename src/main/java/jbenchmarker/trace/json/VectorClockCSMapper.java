/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json;

import collect.VectorClock;
import java.util.HashMap;

/**
 *
 * @author damien
 */
public class VectorClockCSMapper {
    private HashMap<String, Integer> tabUId;

    public VectorClockCSMapper() {
       this.tabUId = new HashMap<String, Integer>();
    }
      
    public Integer userId(String key){
        if(!this.tabUId.containsKey(key)){           
           this.tabUId.put(key,tabUId.size()+1);
       }
        return tabUId.get(key);
    }
    
    public VectorClock toVectorClock(VectorClockCS vcs) {
        VectorClock res = new VectorClock();
        
        for (String key : vcs.keys()) {
            res.put(userId(key), vcs.get(key));
        }
        return res;
    }
}
