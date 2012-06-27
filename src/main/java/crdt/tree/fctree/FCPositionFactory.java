/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCPositionFactory {

    FCPosition createBetween(FCNode n1, FCNode n2) {
        Iterator<Byte> s1;
        Iterator<Byte> s2;
        /*
         * if(n1==null && n2==null){ return new FCPosition(new ArrayList(new
         * Byte[]{(byte)(Math.random()*(Byte.MAX_VALUE+Byte.MIN_VALUE)+Byte.MIN_VALUE)}));
        }
         */
        if (n1 == null) {
            s1 = new infinitString(Byte.MIN_VALUE);
        } else {
            s1 = FCPosition.conv(n1.getPosition().getPosition(), n1.getId()).iterator();
        }

        if (n2 == null) {
            s2 = new infinitString(Byte.MAX_VALUE);
        } else {
            s2 = FCPosition.conv(n2.getPosition().getPosition(), n2.getId()).iterator();
        }


        LinkedList<Byte> sb = new LinkedList();

        while (s1.hasNext() && s2.hasNext()) {
            byte b1 = s1.next();
            byte b2 = s2.next();
            if (b1 == b2) {
                sb.addLast(b1);
            } else if (b2 - b1 > 2) {
                sb.addLast(new Byte((byte) ((b1 + b2) / 2)));
                break;
            }
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
