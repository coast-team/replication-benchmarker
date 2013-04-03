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
package jbenchmarker;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.IOException;
import java.util.Enumeration;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.TraceGenerator;

/**
 * @author damien
 */

public class StatsOpConcurrentes {

    public static void main (String[] args) throws IOException{
        Trace trace = TraceGenerator.traceFromJson("../../traces/json/dirtyCSGerald3.db","films002");
        Enumeration<TraceOperation> en1 = trace.enumeration();
       
        int tailleTrace = 0;
        int opConcurente;
        while(en1.hasMoreElements()){
            tailleTrace++;
            en1.nextElement();
        }       
        System.out.println("taille trace"+tailleTrace);
        int tab [] = new int[tailleTrace];
        int tab2[] = new int[tailleTrace];
        int i = 0;
        en1 = trace.enumeration();
        
        while(en1.hasMoreElements()){
            opConcurente = 0;            
            TraceOperation so1 = (TraceOperation)en1.nextElement();
            Enumeration<TraceOperation> en2 = trace.enumeration();
        
             while(en2.hasMoreElements()){
                TraceOperation so2 = (TraceOperation)en2.nextElement();
                if(so1==so2){
                }else if(so1.getVectorClock().concurrent(so2.getVectorClock())){                   
                    opConcurente++;
                }              
                                            
            }
             tab[opConcurente] = tab[opConcurente]+ 1;
             tab2[i] = opConcurente;     
             i++;
        }
        
        for(int j=0; j < tab.length; j++){
            System.out.print(" , "+tab[j]);
        }
        System.out.println("\n");
        int res = 0;
        for(int j=0; j < tab2.length; j++){
            System.out.print(" , "+tab2[j]);
        }
    }
}
