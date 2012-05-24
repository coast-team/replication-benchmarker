package jbenchmarker.trace.json;

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

    public ElementCS(String op, int offset, String str, int pos, String uid, VectorClockCS vc) {
        this.operation = op;
        this.number_charDeleted = offset;
        this.chars_inserted = str;
        this.position = pos;
        this.userId = uid;
        this.vector_clock = vc;
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
