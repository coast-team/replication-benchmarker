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
package jbenchmarker.sim;

import crdt.simulator.random.SequenceOperationProfile;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import collect.VectorClock;
import crdt.simulator.random.RandomGauss;
import jbenchmarker.core.SequenceOperation;

/**
 * An iterator to generate caussally consistent trace of operation. 
 * Each operation produced is a Random operation (SequenceOperation.OpType.rdm) and 
 * should be instanciate by the targeted simulator.
 * @author urso
 */
@Deprecated
public class RandomTraceNC implements Iterator<SequenceOperation> {
    private long time;
    private final long duration, delay;
    private final double probability, sdv;
    private final int replicas;
    private final Map<Long,NavigableSet<VectorClock>>[] delivery;
    private final VectorClock[] states;
    private final ReplicaProfile rp;
    private final SequenceOperationProfile op;
    private int rindex;
    private SequenceOperation next;
    private final RandomGauss r;

     
    static public interface ReplicaProfile {
        boolean willGenerate(int replica, long time, long duration, double probability);
    }
            
    /**
     * FLAT Profile : allways same probaility.
     */
    static public final ReplicaProfile FLAT = new ReplicaProfile() {
        Random r = new Random();
        @Override
        public boolean willGenerate(int replica, long time, long duration, double probability) {
            return r.nextDouble() < probability;
        }
    };
    
    /**
     * Constructor of a random trace.
     * @param duration duration for generation
     * @param profile profile of replicas (FLAT, ...)
     * @param probability base probability for operation generation 
     * @param delay average delay for operation reception (gaussian)
     * @param sdv standard deviation of delay for operation reception (gaussian)
     * @param replicas number of replicas
     */
    public RandomTraceNC(long duration, ReplicaProfile rp, SequenceOperationProfile op, double probability, long delay, double sdv, int replicas) {
        this.duration = duration;
        this.rp = rp;
        this.op = op;
        this.delay = delay;
        this.sdv = sdv;
        this.probability = probability;
        this.replicas = replicas;
        delivery = new Map[replicas];
        for (int i = 0; i < replicas; i++) 
            delivery[i] = new java.util.HashMap<Long,NavigableSet<VectorClock>>();
        states = new VectorClock[replicas];
        for (int i = 0; i < replicas; i++) 
            states[i] = new VectorClock();
        this.time = 0L;
        this.rindex = 0;
        this.r = new RandomGauss();
        next();
    }
   
    
    
    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public SequenceOperation next() {
        SequenceOperation o = next;
        next = null;
        while (next == null && time < duration) {
            VectorClock vc = states[rindex];
            NavigableSet <VectorClock> ds = delivery[rindex].get(time);
            if (ds != null) {
//                next = SequenceOperation.receive(rindex, ds.pollFirst());
            } else {
                if (rp.willGenerate(rindex, time, duration, probability)) {
                    vc.inc(rindex);
                    VectorClock opc = (VectorClock) vc.clone();
//                    next = SequenceOperation.random(rindex, opc, op);
                    for (int i = 0; i < replicas; i++) {
                        long rt = time + r.nextLongGaussian(delay, sdv);
                        NavigableSet<VectorClock> x = delivery[i].get(rt);
                        if (x == null) {
                            x = new java.util.TreeSet<VectorClock>();
                            delivery[i].put(rt, x);
                        }
                        x.add(opc);
                    }
                }
                rindex++;
                if (rindex == replicas) {
                    time++;
                    rindex = 0;
                }
            }
        }
        return o;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
