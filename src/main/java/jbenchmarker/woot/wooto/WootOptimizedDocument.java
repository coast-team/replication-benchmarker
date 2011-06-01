package jbenchmarker.woot.wooto;

import java.util.ListIterator;
import jbenchmarker.woot.WootDocument;
import jbenchmarker.woot.WootOperation;

/**
 * WOOTO with degrees
 * @author urso
 */
public class WootOptimizedDocument extends WootDocument<WootOptimizedNode> {
    public WootOptimizedDocument() {
        super(WootOptimizedNode.CB, WootOptimizedNode.CE);
    }

    protected void insertBetween(int ip, int in, WootOperation wop) {
        WootOptimizedNode wn = new WootOptimizedNode(wop.getId(), Math.max(elements.get(ip).getDegree(), elements.get(in).getDegree())+1, wop.getContent(), true);
        woalgo(ip, in, wn);        
    }

    private void woalgo(int ip, int in, WootOptimizedNode wn) {
        if (ip == in - 1) {
            elements.add(in, wn);
        } else {
            int d = ip, f = in, dMin = -1;
            ListIterator<WootOptimizedNode> it = elements.listIterator(d + 1);
            for (int i = d+1; i < f; i++) {
                WootOptimizedNode e = it.next();
                if (dMin == -1 || e.getDegree() < dMin) {
                    dMin = e.getDegree();
                }
            }
            it = elements.listIterator(d + 1);
            for (int i = d+1; i < f; i++) {
                WootOptimizedNode e = it.next();
                if (e.getDegree() == dMin) {
                    if (e.getId().compareTo(wn.getId()) < 0) {
                        d = i;
                    } else {
                        f = i;
                    }
                }
            }
            woalgo(d, f, wn);
        }

    }


}
