/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.set.observedremove.*;
import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;
import crdt.set.CommutativeSetMessage.OpType;
import crdt.set.lastwriterwins.TypedMessage;


/**
 *
 * @author score
 */
public class TwoPhasesMessage<T> extends TypedMessage<T> {

    public TwoPhasesMessage(OpType type, T t) {  
        super(type, t);
    }

    
    public void addTag(int r, int o) {
        Tag t = new Tag(r, o);
    }
    

    public void setContent(T c) {
        content = c;
    }

    @Override
    public String toString() {
        return "2M{" + "content=" + content + ", type=" + type + '}';
    }

    @Override
    public CommutativeMessage clone() {
        return new TwoPhasesMessage(type, content);
    }  
}
