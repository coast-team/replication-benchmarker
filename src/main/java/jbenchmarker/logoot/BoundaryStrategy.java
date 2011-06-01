package jbenchmarker.logoot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author urso
 */
class BoundaryStrategy extends LogootStrategy {

    private Random ran = new Random();
    private final long bound;
    private final BigInteger boundBI;

    public BoundaryStrategy(int bound) {
        this.bound = bound;
        this.boundBI = BigInteger.valueOf(bound);
    }    
        
    public long nextLong(long l) {
        long x = ran.nextLong() % l;
        if (x<0) x += l;
        return x;
    }
    
    /**
     * Generate N identifier between P and Q;
     */
    public ArrayList<LogootIdentifier> generateLineIdentifiers(LogootMerge replica, LogootIdentifier P, LogootIdentifier Q, int n) {
        int index = 0, tMin = Math.min(P.length(), Q.length());
        
        while ((index < tMin && P.getComponentAt(index).equals(Q.getComponentAt(index))   
                || (P.length() <= index && Q.length() > index && Q.getDigitAt(index) == 0))) {
            index++;
        }         
        
        long interval, d = Q.getDigitAt(index) - P.getDigitAt(index) - 1;
        if (d >= n) {
            interval = Math.min(d/n, bound); 
        } else {
            BigInteger diff = d == -1 ? BigInteger.ZERO : BigInteger.valueOf(d),
                    N = BigInteger.valueOf(n);
            while (diff.compareTo(N) < 0) {
                index++;
                diff = diff.multiply(replica.getBase()).
                        add(BigInteger.valueOf(replica.getMax() - P.getDigitAt(index)).
                        add(BigInteger.valueOf(Q.getDigitAt(index))));
            }           
            interval = diff.divide(N).min(boundBI).longValue();
        }
        
        ArrayList<LogootIdentifier> patch = new ArrayList<LogootIdentifier>();        
        List<Long> digits = P.digits(index);
        for (int i = 0; i < n; i++) {
            plus(digits, nextLong(interval) + 1, replica.getBase(), replica.getMax());
            P = constructIdentifier(digits, P, Q, replica.getReplicaNb(), replica.getClock());
            replica.incClock();
            patch.add(P);
        }  
        return patch;
    }
}