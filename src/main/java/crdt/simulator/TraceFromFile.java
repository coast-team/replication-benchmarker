/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import java.io.*;
import java.util.Enumeration;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TraceFromFile implements Trace {

    ObjectInputStream input;

    public TraceFromFile(String str)throws IOException{
        this(new ObjectInputStream(new FileInputStream(str)));
    }
    public TraceFromFile(File file) throws IOException {
        this(new ObjectInputStream(new FileInputStream(file)));
    }

    public TraceFromFile(ObjectInputStream input) {
        this.input = input;
    }
    
    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>() {
            TraceOperation nextElement;
                        
            @Override
            public boolean hasMoreElements() {
                  nextElement=readOperation();
                  return nextElement!=null;
            }

            @Override
            public TraceOperation nextElement() {
                        
               return nextElement;

            }
            private TraceOperation readOperation() {
                 try {
                    Object obj = input.readObject();
                    if (obj instanceof TraceOperation) {
                        return (TraceOperation) obj;
                    } else {
                        throw new Exception("File is not a trace");
                    }
                 } catch (EOFException ex1){
                     return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };
    }
}
