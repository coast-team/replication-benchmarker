package jbenchmarker.trace;

/**
 *
 * @author urso
 */
public class IncorrectTrace extends Exception {
    public IncorrectTrace() {
    }
    
    public IncorrectTrace(Throwable thrwbl) {
        super(thrwbl);
    }

    public IncorrectTrace(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public IncorrectTrace(String string) {
        super(string);
    }

}
