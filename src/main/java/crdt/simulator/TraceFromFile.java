/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

            @Override
            public boolean hasMoreElements() {
                try {
                    boolean ret=input.available() > 0;
                    if (!ret){
                        input.close();
                    }
                    return ret;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            public TraceOperation nextElement() {
                try {
                    Object obj = input.readObject();
                    if (obj instanceof TraceOperation) {

                        return (TraceOperation) obj;
                    } else {
                        throw new Exception("File is not a trace");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }

            }
        };
    }
}
