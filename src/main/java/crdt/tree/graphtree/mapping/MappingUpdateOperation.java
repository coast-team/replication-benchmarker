/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

/**
 *
 * @author moi
 */
public class MappingUpdateOperation<T> {
    public  enum Type{add,del,move};
    private T object;
    private T old;
    private Type type;
    /**
     * @return the object
     */
    public T getObject() {
        return object;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public T getOld() {
        return old;
    }
    
    

    public MappingUpdateOperation( Type type,T object) {
        if (type==MappingUpdateOperation.Type.move){
            throw new UnsupportedOperationException("Error move operation");
        }
        this.object = object;
        this.type = type;
    }
    public MappingUpdateOperation( Type type,T old,T object) {
        this.object = object;
        this.type = type;
        this.old=old;
    }
    
}
