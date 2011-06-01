package jbenchmarker.woot.original;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 *
 * @author urso
 */
public class WootOriginalNode extends WootNode {
    final private WootOriginalNode cp; // previous node
    final private WootOriginalNode cn; // next node
    
    public static final WootOriginalNode CB = new WootOriginalNode(WootIdentifier.IB, null, null, ' ', false);
    public static final WootOriginalNode CE = new WootOriginalNode(WootIdentifier.IE, null, null, ' ', false);

    public WootOriginalNode(WootIdentifier id, WootOriginalNode cp, WootOriginalNode cn, char content, boolean visible) {
        super(id, content, visible);
        this.cp = cp;
        this.cn = cn;
    }

    public WootNode getCn() {
        return cn;
    }

    public WootNode getCp() {
        return cp;
    }
}
