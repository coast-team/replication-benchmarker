/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author score
 */
public class OrMessage<T> extends CommutativeSetMessage<T> {
    
    private Set<Tag> tags;

    /**
     * @return the type
     */
    public OpType getType() {
        return type;
    }

    public enum OpType {add, del}; 
    private OpType type;
    

    public OrMessage(OpType type, T t, Set<Tag> tag) {  
        this.type=type;
        this.tags = tag;
        this.content = t;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    @Override
    public String visu() {
        return "OM{" + "content=" + content + ",tags=" + tags + ", type=" + type + '}';
    }

    @Override
    protected CommutativeMessage copy() {
        return new OrMessage(type, content, new HashSet(tags));
    }
}
