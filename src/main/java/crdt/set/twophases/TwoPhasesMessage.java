/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.set.observedremove.*;
import crdt.CommutativeMessage;
import java.util.Set;


/**
 *
 * @author score
 */
public class TwoPhasesMessage<T> extends CommutativeMessage{
    
    private T content;

    /**
     * @return the type
     */
    public OpType getType() {
        return type;
    }

  
    public enum OpType {add, del}; 
    private OpType type;
    

    public TwoPhasesMessage(OpType type, T t) {  
        this.type=type;
        this.content = t;
    }
    
    
    public T getContent() {
        return content;
    }

    
    public void addTag(int r, int o) {
        Tag t = new Tag(r, o);
    }
    

    public void setContent(T c) {
        content = c;
    }

    @Override
    public String visu() {
        return "2M{" + "content=" + content + ", type=" + type + '}';
    }

    @Override
    protected CommutativeMessage copy() {
        return new TwoPhasesMessage(type, content);
    }  
}
