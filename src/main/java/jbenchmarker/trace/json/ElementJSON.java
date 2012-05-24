package jbenchmarker.trace.json;

/**
 * @author Damien Flament
 */

public class ElementJSON {
    private String key;
    private ElementCS val;

    public ElementJSON(String k, ElementCS v) {
        this.key = k;
        this.val = v;
    }

    public String getKey() {
        return key;
    }

    public ElementCS getVal() {
        return val;
    }
      
    @Override
    public String toString() {
        return "ElementJSON{" + "key=" + key + ", val=" + val + '}';
    }
}
