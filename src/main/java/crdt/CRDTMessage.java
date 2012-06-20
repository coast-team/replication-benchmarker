/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

/**
 *
 * @author urso
 */
public interface CRDTMessage extends Cloneable {
    public CRDTMessage concat(CRDTMessage msg);

    public void execute(CRDT crdt);
    public CRDTMessage clone();
    
    public int size();
}
