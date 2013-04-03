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
