/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.logootOneId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class LogootOneIdStrategy implements Serializable{

    /**
     * Generate N identifier between P and Q;
     */
    abstract ArrayList<LogootOneIdentifier> generateLineIdentifiers(LogootOneIdDocument replica, LogootOneIdentifier P, LogootOneIdentifier Q, int N);

    static LogootOneIdentifier constructIdentifier(BigDecimal digits, int peer, int clock) {
        if(peer == -1) peer = -peer; //Test
        int posRep = 1, posClock = 1;
         if(peer!=0) 
            posRep = (int) Math.log10(peer)+1;
        if(clock!=0) 
        posClock = (int) Math.log10(clock)+1;
        
        BigDecimal newId = new BigDecimal(digits+""+peer+""+clock+"0"+posRep+"0"+posClock);
        LogootOneIdentifier R = new LogootOneIdentifier(newId);
        return R;
    }
}
