/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator.random;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import collect.VectorClock;
import crdt.simulator.random.NTrace.RandomParameters;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;

/**
 * An enumeration to generate caussally consistent trace of operation. Each
 * operation produced is a Random operation (TraceOperation.OpType.rdm) and
 * should be instanciate by the targeted simulator.
 *
 * @author urso
 */
public class RandomTrace implements Trace {

    private final long duration, delay;
    private final double probability, sdv;
    private final int replicas;
    private final Map<Long, VectorClock>[] delivery;
    private final VectorClock[] states;
    private final ReplicaProfile rp;
    private final RandomGauss r;
    private final OperationProfile op;

    public OperationProfile getOperationProfile() {
        return op;
    }

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
     *
     * @param duration duration for generation
     * @param profile profile of replicas (FLAT, ...)
     * @param probability base probability for operation generation
     * @param delay average delay for operation reception (gaussian)
     * @param sdv standard deviation of delay for operation reception (gaussian)
     * @param replicas number of replicas
     */
    public RandomTrace(final long duration, final ReplicaProfile rp, final OperationProfile op,
            final double probability, final long delay, final double sdv, final int replicas) {
        this.duration = duration;
        this.rp = rp;
        this.delay = delay;
        this.sdv = sdv;
        this.probability = probability;
        this.replicas = replicas;
        delivery = new Map[replicas];
        for (int i = 0; i < replicas; i++) {
            delivery[i] = new java.util.HashMap<Long, VectorClock>();
        }
        states = new VectorClock[replicas];
        for (int i = 0; i < replicas; i++) {
            states[i] = new VectorClock();
        }
        this.r = new RandomGauss();
        this.op = op;

    }

    public RandomTrace(RandomTrace old, RandomParameters n) {
        this.duration = n.duration;
        this.rp = n.rp;
        this.delay = n.delay;
        this.sdv = n.sdv;
        this.op = n.op;
        this.probability = n.probability;
        this.replicas = n.replicas;
        if (old != null) {
            this.delivery = new Map[Math.max(old.delivery.length, n.replicas)];
            System.arraycopy(old.delivery, 0, delivery, 0, old.replicas);
            for (int i = old.replicas; i < delivery.length; i++) {
                delivery[i] = new java.util.HashMap<Long, VectorClock>();
            }

            states = new VectorClock[delivery.length];
            System.arraycopy(old.states, 0, states, 0, old.states.length);
            for (int i = old.states.length; i < states.length; i++) {
                states[i] = new VectorClock();
            }
            this.r = old.r;
        }else{
            this.delivery = new Map[n.replicas];
            for (int i = 0; i < delivery.length; i++) {
                delivery[i] = new java.util.HashMap<Long, VectorClock>();
            }
            states = new VectorClock[delivery.length];
             for (int i = 0; i < states.length; i++) {
                states[i] = new VectorClock();
            }
             this.r = new RandomGauss();
        }
    }

    @Override
    public Enumeration<TraceOperation> enumeration() {
        Enumeration<TraceOperation> it = new Enumeration<TraceOperation>() {
            private TraceOperation next;
            private int rindex;
            private long nbop;

            @Override
            public boolean hasMoreElements() {
                return next != null;
            }

            @Override
            public TraceOperation nextElement() {
//                System.out.println(time);
                TraceOperation o = next;
                next = null;
                while (next == null && nbop < duration) {
                    VectorClock vc = states[rindex], d = delivery[rindex].get(nbop);
                    if (d != null) {
                        vc.upTo(d);
                    }
                    if (rp.willGenerate(rindex, nbop, duration, probability)) {
                        vc.inc(rindex);
                        VectorClock opc = (VectorClock) vc.clone();
                        next = new RandomOperation(op, rindex, opc);
                        for (int i = 0; i < replicas; i++) {
                            long rt = nbop + r.nextLongGaussian(delay, sdv);
                            VectorClock x = delivery[i].get(rt);
                            if (x == null) {
                                delivery[i].put(rt, opc);
                            } else {
                                x = (VectorClock) x.clone();
                                x.upTo(opc);
                                delivery[i].put(rt, x);
                            }
                        }
                        nbop++;
                    }
                    rindex = (rindex + 1) % replicas;
                }
                return o;
            }
        };
        it.nextElement();
        return it;
    }
}
