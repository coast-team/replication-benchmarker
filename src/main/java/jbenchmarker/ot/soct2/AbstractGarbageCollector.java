/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author urso
 */
public abstract class AbstractGarbageCollector implements GarbageCollector, Serializable {
    /**
     * Recommanded number operation before garbage collecting (20)
     */
    public static final int RECOMMANDED_GC_FREQUENCY_VALUE = 20;
    final protected int frequencyGC;
    private int countdownBeforeGC;
    protected final Map<Integer, VectorClock> clocksOfAllSites = new TreeMap<Integer, VectorClock>();


    public AbstractGarbageCollector(int frequencyGC) {
        this.frequencyGC = frequencyGC;
        this.countdownBeforeGC = frequencyGC;
    }
    
    public AbstractGarbageCollector() {
        this(RECOMMANDED_GC_FREQUENCY_VALUE);
    }

    /**
     * register the soct2message to get anothers vector clock and count before run
     * @param mess Soct2messages
     */
    @Override
    public void collect(OTAlgorithm soct2Algorithm, OTMessage mess) {
        setRemoteClock(soct2Algorithm, mess.getSiteId(), mess.getClock());
    }
    
    @Override
    public void garbage(OTAlgorithm soct2Algorithm, OTMessage mess) {
        this.countdownBeforeGC--;
        if (this.countdownBeforeGC == 0) {
            gc(soct2Algorithm);
            this.countdownBeforeGC = frequencyGC;
        }
    }
    

    protected void setRemoteClock(OTAlgorithm soct2Algorithm, int siteId, VectorClock clock) {
        clocksOfAllSites.put(siteId, clock);
    }
    
    abstract protected void gc(OTAlgorithm soct2Algorithm);
}
