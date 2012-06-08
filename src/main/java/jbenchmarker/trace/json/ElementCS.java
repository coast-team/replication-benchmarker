package jbenchmarker.trace.json;

import collect.VectorClock;

/**
 * @author Damien Flament
 */

public class ElementCS {
    private String operation;
    private int number_charDeleted;
    private String chars_inserted;
    private int position;
    private String userId;
    private VectorClockCS vector_clock;
    private int replica;
    private VectorClock vc;
    
    public ElementCS(String op, int offset, String str, int pos, String uid, VectorClockCS vcs) {
        this.operation = op;
        this.number_charDeleted = offset;
        this.chars_inserted = str;
        this.position = pos;
        this.userId = uid;
        this.vector_clock = vcs;
    }

    public ElementCS(String op, int offset, String str, int pos, int repli, VectorClock v) {
        this.operation = op;
        this.number_charDeleted = offset;
        this.chars_inserted = str;
        this.position = pos;
        this.replica = repli;
        this.vc = v;
    }

    public VectorClock getVc() {
        return vc;
    }

    public int getReplica() {
        return replica;
    }
    
    public String getChars_inserted() {
        return chars_inserted;
    }

    public int getNumber_charDeleted() {
        return number_charDeleted;
    }

    public String getOperation() {
        return operation;
    }

    public int getPosition() {
        return position;
    }

    public String getUserId() {
        return userId;
    }

    public VectorClockCS getVector_clock() {
        return vector_clock;
    }
    
    @Override
    public String toString() {
        return "ElementCS{" + "operation=" + operation + ", number_charDeleted=" + number_charDeleted + ", chars_inserted=" + chars_inserted + ", position=" + position + ", userId=" + userId + ", vector_clock=" + vector_clock + '}';
    }
}
