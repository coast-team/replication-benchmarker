/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;
import crdt.set.lastwriterwins.TypedMessage;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author score
 */
public class OrMessage<T> extends TypedMessage<T> {
    
    private Set<Tag> tags;

    public OrMessage(OpType type, T t, Set<Tag> tag) {  
        super(type, t);
        this.tags = tag;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    @Override
    public String toString() {
        return "OM{" + "content=" + content + ",tags=" + tags + ", type=" + type + '}';
    }

    @Override
    public CommutativeMessage clone() {
        return new OrMessage(type, content, new HashSet(tags));
    }
}
