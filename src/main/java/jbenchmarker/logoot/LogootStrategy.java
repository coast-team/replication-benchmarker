package jbenchmarker.logoot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class LogootStrategy {




    /**
     * Generate N identifier between P and Q;
     */
    abstract ArrayList<LogootIdentifier> generateLineIdentifiers(LogootMerge replica, LogootIdentifier P, LogootIdentifier Q, int N);



    static LogootIdentifier constructIdentifier(List<Long> digits, LogootIdentifier P, LogootIdentifier Q, int peer, int clock) {
        LogootIdentifier R = new LogootIdentifier(digits.size());
        int i = 0, index = digits.size() - 1; 
        while (i < index && i < P.length() && digits.get(i) == P.getDigitAt(i)) {
            R.addComponent(P.getComponentAt(i).clone());
            i++;
        }
        while (i < index && i < Q.length() && digits.get(i) >= Q.getDigitAt(i)) {
            R.addComponent(Q.getComponentAt(i).clone());
            i++;
        }
        while (i <= index) {
            R.addComponent(new Component(digits.get(i), peer, clock));
            i++;
        }
        return R;
    }

    /**
     * An identifier as a biginteger.
     */
    static BigInteger big(LogootIdentifier id, int index, BigInteger base) {
        BigInteger bi = BigInteger.valueOf(id.getDigitAt(0));
        for (int i = 1; i <= index; i++) {
            bi = bi.multiply(base).add(BigInteger.valueOf(id.getDigitAt(i)));
        }
        return bi;
    }
    
    static List<Long> plus(List<Long> lid, long sep, BigInteger base, long max) {
        int index = lid.size() - 1;
        long last = lid.get(index);
        if (max - last < sep) {
            BigInteger dr[] = BigInteger.valueOf(last).add(BigInteger.valueOf(sep)).divideAndRemainder(base);
            lid.set(index, dr[1].longValue());
            while (dr[0].longValue() != 0) {
                --index;
                dr = BigInteger.valueOf(lid.get(index)).add(dr[0]).divideAndRemainder(base);
                lid.set(index, dr[1].longValue());            
            }
        } else {
            lid.set(index, last + sep); 
        }
        return lid;
    }
}
