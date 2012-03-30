/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;

import collect.Node;
import crdt.CRDTMessage;
import java.util.*;

/**
 *
 * @author score
 */
public class GTreeMessage implements CRDTMessage {
    
    private CRDTMessage node;
    private CRDTMessage edge;
    
    public GTreeMessage(CRDTMessage nd , CRDTMessage edg)
    {
        this.node = nd;
        this.edge = edg;
    }

    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        return new GTreeMessage(this.node.concat(((GTreeMessage)msg).getNode()),
                this.edge.concat(((GTreeMessage)msg).getEdge()));
    }
    
    public CRDTMessage getNode()
    {
        return node;
    }
    
    public CRDTMessage getEdge()
    {
        return edge;
    }

    @Override
    public CRDTMessage clone() {
        return new GTreeMessage(node.clone(), edge.clone());
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
