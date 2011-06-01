package jbenchmarker.woot.original;

import java.util.ListIterator;
import jbenchmarker.woot.WootDocument;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootOriginalDocument extends WootDocument<WootOriginalNode> {

    public WootOriginalDocument() {
        super(WootOriginalNode.CB, WootOriginalNode.CE);
    }

    
    private void walgo(int ip, int in, WootOriginalNode wn) {
        if (ip == in - 1) {
            elements.add(in, wn);
        } else {
            int d = ip, f = in, i = d+1; 
            ListIterator<WootOriginalNode> it = elements.listIterator(d+1);
            while (i < f) {
                WootOriginalNode e = it.next();
                if ((find(e.getCp().getId()) <= ip) && (find(e.getCn().getId()) >= in)) {
                    // same or greater "legs"
                    if (e.getId().compareTo(wn.getId()) > 0) {
                        f = i;
                    } else {
                        d = i;
                    }
                }
                i++;
            }
            walgo(d, f, wn);
        }
    }


    protected void insertBetween(int ip, int in, WootOperation wop) {
        WootOriginalNode wn = new WootOriginalNode(wop.getId(), elements.get(ip), elements.get(in), wop.getContent(), true);
        walgo(ip, in, wn);
    }

}
