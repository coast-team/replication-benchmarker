package jbenchmarker.trace.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Damien Flament
 */

public class VectorClockCS {
    private HashMap<String, Integer> tab;
  
    
    public VectorClockCS() {
        this.tab = new HashMap<String, Integer>();
        //this.tabUId = new HashMap<String, Integer>();
    }

    public void put(String k, Integer v) {
        this.tab.put(k, v);
    }
    
    public int get(String key){
        return this.tab.get(key);
    }
    
    public Set<String> keys() {
        return this.tab.keySet();
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("V{tab=");

        for (Map.Entry<String, Integer> e : this.tab.entrySet()) {
            b.append(e.getKey());
            b.append("=>");
            b.append(e.getValue()); 
            b.append(",");
        }
        b.append("}");
        
        return b.toString();
    }
}
