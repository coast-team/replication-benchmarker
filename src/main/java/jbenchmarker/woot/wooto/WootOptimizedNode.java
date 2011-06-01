package jbenchmarker.woot.wooto;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 *
 * @author urso
 */
public class WootOptimizedNode extends WootNode {
    final private int degree;
    
    public static final WootOptimizedNode CB = new WootOptimizedNode(WootIdentifier.IB, 0, ' ', false);
    public static final WootOptimizedNode CE = new WootOptimizedNode(WootIdentifier.IE, 0, ' ', false);

    public WootOptimizedNode(WootIdentifier id, int degree, char content, boolean visible) {
        super(id, content, visible);
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }
}
