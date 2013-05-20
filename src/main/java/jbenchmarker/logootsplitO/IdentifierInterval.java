/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootsplitO;

import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class IdentifierInterval {
    List <Integer> base;
    int begin;
    int end;

    public IdentifierInterval(List<Integer> base, int begin, int end) {
        this.base = base;
        this.begin = begin;
        this.end = end;
    }

    public List<Integer> getBase() {
        return base;
    }

    public Identifier getBeginId(){
        return new Identifier(base,begin);
    }
    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }
    public Identifier getBaseId(){
        return new Identifier(base);
    }

    public Identifier getBaseId(Integer u){
        return new Identifier(base,u);
    }
    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }
     public void addBegin(int begin) {
        this.begin += begin;
    }

    public void addEnd(int end) {
        this.end += end;
    }

    @Override
    public String toString() {
        return "IdentifiantInterval{" +  base + ",[" + begin + ".." + end + "]}";
    }
    
}
