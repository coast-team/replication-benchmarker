/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

/**
 * A vector with holes
 * @author urso
 */
public interface VectorWithHoles {
    /**
     * Does the clock belongs to the vector ?
     */
    boolean contains(int key, int clock);
    
    /**
     * Adds the clock to the vector.
     */
    void add(int key, int clock);
}
