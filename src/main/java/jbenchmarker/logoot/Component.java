package jbenchmarker.logoot;

public class Component implements Comparable<Component> {

    final private long digit;
    final private int peerID;
    final private int clock;

    public Component(long d, int pid, int c) {
        this.digit = d;
        this.peerID = pid;
        this.clock = c;
    }

    public long getDigit() {
        return digit;
    }

    public int getPeerID() {
        return peerID;
    }

    public int getClock() {
        return clock;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Component other = (Component) obj;
        if (this.digit != other.digit) {
            return false;
        }
        if (this.peerID != other.peerID) {
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
        hash = 97 * hash + (int) (this.digit ^ (this.digit >>> 32));
        hash = 97 * hash + this.peerID;
        hash = 97 * hash + this.clock;
        return hash;
    }

    @Override
    public String toString() {
        return "<" + digit + ',' + peerID + ',' + clock + '>';
    }

    public int compareTo(Component t) {
        if (this.digit == t.digit) {
            return (this.peerID == t.peerID) ? this.clock - t.clock : this.peerID - t.peerID;
        } else {
            return (this.digit - t.digit > 0) ? 1 : -1;
        }
    }

    public Component clone() {
        return new Component(digit, peerID, clock);
    }
}