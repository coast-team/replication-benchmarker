/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.*;
import crdt.CRDTMessage;
import java.util.LinkedList;

/**
 *
 * @author urso
 */
public abstract class CommutativeSetMessage<T> extends CommutativeMessage<T> {
    protected T content;


    public T getContent() {
        return content;
    }
}
