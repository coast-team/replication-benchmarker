/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.simulator.random.OperationProfile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;

/**
 * Special crdt witch take 100ms by integration and the document is the site
 * number repeated until the generation operation times.
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class CRDTMock extends CRDT<String> {

    public static int sent = 0;

    public static void resetCounter() {
        sent = 0;
    }

    static int getCounter() {
        return sent;
    }
    HashMap<Integer, AtomicInteger> counter = new HashMap();
    public static int waitTimeLocal = 20;
    public static int waitTimeRemote = 30;

    public static int getWaitTimeLocal() {
        return waitTimeLocal;
    }

    public static void setWaitTimeLocal(int waitTimeLocal) {
        CRDTMock.waitTimeLocal = waitTimeLocal;
    }

    public static int getWaitTimeRemote() {
        return waitTimeRemote;
    }

    public static void setWaitTimeRemote(int waitTimeRemote) {
        CRDTMock.waitTimeRemote = waitTimeRemote;
    }

    public static void setTime(int waitTimeLocal, int waitTimeRemote) {
        setWaitTimeRemote(waitTimeRemote);
        setWaitTimeLocal(waitTimeLocal);
    }

    public void waitASecond(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(CRDTMock.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addCounter(int i) {
        AtomicInteger c = counter.get(i);
        if (c == null) {
            counter.put(i, new AtomicInteger(1));
        } else {
            c.incrementAndGet();
        }
    }

    @Override
    public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
        waitASecond(waitTimeLocal);
        CRDTMessage mess = new MessageMock(this.getReplicaNumber());
        ((MessageMock)mess).apply(this);
        sent++;
        return mess;
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        waitASecond(waitTimeRemote);
        ((MessageMock)op).apply(this);
    }

    @Override
    public String lookup() {
        StringBuilder ret = new StringBuilder();
        for (Entry<Integer, AtomicInteger> ent : counter.entrySet()) {
            for (int i = 0; i < ent.getValue().intValue(); i++) {
                ret.append(ent.getKey());
            }
        }
        return ret.toString();
    }

    @Override
    public CRDT<String> create() {
        return new CRDTMock();
    }

    public static class MessageMock implements CRDTMessage {

        List<Integer> intr;

        public MessageMock(List<Integer> intr) {
            this.intr = intr;
        }

        public MessageMock(int i) {
            this.intr = new LinkedList();
            intr.add(i);
        }

        @Override
        public CRDTMessage concat(CRDTMessage msg) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void apply(CRDT crdt){
            for (Integer i : intr) {
                ((CRDTMock) crdt).addCounter(i);
            }
        }
        @Override
        public void execute(CRDT crdt) {
            crdt.applyOneRemote(this);
            
        }

        @Override
        public CRDTMessage clone() {
            return new MessageMock(intr);
        }

        @Override
        public int size() {
            return intr.size();
        }
    }

    public static class OperationMock implements OperationProfile {

        @Override
        public LocalOperation nextOperation(CRDT a) {
            return null;
        }
    }
}
