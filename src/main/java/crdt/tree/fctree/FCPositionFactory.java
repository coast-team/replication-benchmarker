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
package crdt.tree.fctree;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCPositionFactory implements Serializable {

    public static final boolean alea = false;

    public FCPosition createBetweenNode(FCNode n1, FCNode n2, FCIdentifier id) {
        return createBetweenPosition(n1==null?null:n1.getPosition(),n2==null?null:n2.getPosition(),id);
    }
    
    
    public FCPosition createBetweenPosition(FCPosition n1, FCPosition n2, FCIdentifier id) {
        Iterator<Byte> s1;
        Iterator<Byte> s2;
        /*
         * if(n1==null && n2==null){ return new FCPosition(new ArrayList(new
         * Byte[]{(byte)(Math.random()*(Byte.MAX_VALUE+Byte.MIN_VALUE)+Byte.MIN_VALUE)}));
         }
         */
        if (n1 == null) {
            s1 = new infinitString((byte)(Byte.MIN_VALUE+1));
        } else {
            //s1 = FCPosition.conv(n1.getPosition().getPosition(), n1.getId()).iterator();
            s1 = n1.getPosition().iterator();
        }

        if (n2 == null) {
            s2 = new infinitString((byte) (Byte.MAX_VALUE));
        } else {
            //s2 = FCPosition.conv(n2.getPosition().getPosition(), n2.getId()).iterator();
            s2 = n2.getPosition().iterator();
        }


        LinkedList<Byte> sb = new LinkedList();

        while (s1.hasNext() && s2.hasNext()) {
            byte b1 = s1.next();
            byte b2 = s2.next();
            if (b2 - b1 > 1) {

                sb.addLast(new Byte((byte) ((b1 + b2) / 2)));
                break;
            } else {
                sb.addLast(b1);
            }
        } 
        if (s1.hasNext()) {
            sb.addLast(s1.next());
        }
        sb.addLast(Byte.MIN_VALUE);
        //sb.addAll(Integer.toHexString(id.getReplicaNumber()).getBytes()); 
        byte[] b1 = Integer.toHexString(id.getReplicaNumber()).getBytes();
        for (byte bb : b1) {
            sb.add(bb);
        }
        b1 = Integer.toHexString(id.getOperationNumber()).getBytes();
        for (byte bb : b1) {
            sb.add(bb);
        }
        return new FCPosition(sb);
    }

    class infinitString implements Iterator<Byte> {

        byte ch;

        public infinitString(byte ch) {
            this.ch = ch;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Byte next() {
            return ch;
        }
    }
}
