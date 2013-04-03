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
package jbenchmarker.trace.json;

import java.util.HashMap;
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
