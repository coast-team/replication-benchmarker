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
package crdt.set.lastwriterwins;

import crdt.OperationBasedOneMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author score
 */
public class LwwMessage<T> extends TypedMessage<T> {
    
    private int now;
    
    public LwwMessage(OpType type, T t, int now) {
        super(type, t);
        this.now = now;
    }
        
    public int getime() {
        return this.now;
    }
    
    public void settime(int t) {
        this.now = t;
    }

    @Override
    public String toString() {
        return "LM{" + "now=" + now + ", type=" + type + ", content=" + content + '}';
    }

    @Override
    public LwwMessage<T> clone() {
        return new LwwMessage(type, content, now);
    }
}
