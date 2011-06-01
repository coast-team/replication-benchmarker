package jbenchmarker.woot;

/**
 *
 * @author urso
 */
public class WootIdentifier implements Comparable<WootIdentifier> {
    public static final WootIdentifier IB = new WootIdentifier(-1,0);
    public static final WootIdentifier IE = new WootIdentifier(-1,1);;

    public WootIdentifier(int replica, int clock) {
        this.replica = replica;
        this.clock = clock;
    }
    
    private int replica;
    private int clock;

    public int getClock() {
        return clock;
    }

    public int getReplica() {
        return replica;
    }

    public int compareTo(WootIdentifier t) {
        if (this.replica == t.replica) 
            return this.clock - t.clock;
        else 
            return this.replica - t.replica;
    }
    
    public WootIdentifier clone() {
        return new WootIdentifier(replica,clock);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootIdentifier other = (WootIdentifier) obj;
        if (this.replica != other.replica) {
            return false;
        }
        if (this.clock != other.clock) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.replica;
        hash = 13 * hash + this.clock;
        return hash;
    }
}
