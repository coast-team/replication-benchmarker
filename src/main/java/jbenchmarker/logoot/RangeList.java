package jbenchmarker.logoot;

import java.util.ArrayList;

/**
 *
 * @author urso
 */
public class RangeList<T> extends ArrayList<T> {

    void removeRangeOffset(int i, int offset) {
        removeRange(i,i+offset);
    }

}
