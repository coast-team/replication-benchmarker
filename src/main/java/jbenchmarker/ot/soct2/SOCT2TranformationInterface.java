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
package jbenchmarker.ot.soct2;

/**
 *
 * @param <O> 
 * @author stephane martin
 */
public interface SOCT2TranformationInterface<O> {
    
    /**
     * transopose op1 with op2 is previous occurs
     * @param op1
     * @param op2
     * @return transposed operation
     */
    public  O transpose(O op1, O op2);
    /**
     * restaure op1 modified by op2
     * @param op1 
     * @param op2
     * @return original operation
     */
    public  O transposeBackward(O op1, O op2);
}
