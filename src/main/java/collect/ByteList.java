/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

/**
 * Linked list of unsigned bytes.
 * @author urso
 */
public class ByteList {
    /** First node of list **/
    private ByteNode first;
    
    /** Last node of list **/
    private ByteNode last;

    /** 
     * Node that contains an unsigned byte. 
     **/
    private static class ByteNode {
        byte value;
        public ByteNode() {
        }
    }
    
    
}
