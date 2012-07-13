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
package crdt.tree.edgetree;

/**
 *
 * @author score
 */
public class Edge<T> {

    private T father;
    private T son;
    private boolean visible=true;
    private boolean visibleInCRDT=true;

    public Edge(T f, T s) {
        father = f;
        son = s;
    }

    public T getFather() {
        return father;
    }
    public void setFather(T t){
        this.father=t;
    }

    public T getSon() {
        return son;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the visibleInCRDT
     */
    public boolean isVisibleInCRDT() {
        return visibleInCRDT;
    }

    /**
     * @param visibleInCRDT the visibleInCRDT to set
     */
    public void setVisibleInCRDT(boolean visibleInCRDT) {
        this.visibleInCRDT = visibleInCRDT;
    }
    @Override
    public Edge<T> clone(){
        Edge<T> ret =new Edge(father,son);
        ret.setVisible(visible);
        ret.setVisibleInCRDT(visibleInCRDT);
        return ret;
    }
}
