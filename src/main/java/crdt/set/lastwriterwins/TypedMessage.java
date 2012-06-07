/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;

import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author urso
 */
public class TypedMessage<T> extends CommutativeSetMessage<T> {

    protected final OpType type;

    public TypedMessage(OpType type, T elem) {
        super(elem);
        this.type = type;
    }


    @Override
    public OpType getType() {
        return type;
    }

    @Override
    public String visu() {
        return "" + type + '(' + content + ')';
    }
    
    @Override
    protected CommutativeMessage copy() {
        return new TypedMessage(type, content);
    }
}
