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
    public static CRDTMessage emptyMessage = new CRDTMessage() {

        @Override
        public CRDTMessage concat(CRDTMessage msg) {
            return msg;
        }

        @Override
        public void execute(CRDT crdt) {
        }

        @Override
        public CRDTMessage clone() {
            return this; 
        }

        @Override
        public int size() {
            return 1;
        }
    };
    
    public CRDTMessage concat(CRDTMessage msg);

    public void execute(CRDT crdt);
    public CRDTMessage clone();
    
    public int size();
}
