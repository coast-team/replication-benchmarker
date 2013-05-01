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
package crdt.tree.graphtree;
/**
 *
 * @author score
 */
public class Edge<T> {
    
    private T father;
    private T son;
    
    public Edge(T f, T s)
    {
        father = f;
        son = s;
    }
    
    public T getFather()
    {
        return father;
    }
    
    public T getSon()
    {
        return son;
    }

    /**
     * @param father the father to set
     */
    /*public void setFather(T father) {
        this.father = father;
    }*/
    
   
}
