package jbenchmarker.core;

/**
 * Interface for a document. 
 * @author urso
 */
public interface Document {
    
    /* 
     * View of the document (without metadata)
     */
    public String view();
    
    /**
     * Applies a character operation
     */ 
    public void apply(Operation op);
}
