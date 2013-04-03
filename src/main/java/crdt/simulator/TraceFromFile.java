/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TraceFromFile implements Trace {

    ObjectInputStream input;
    InputStream inputf;

    public TraceFromFile(String str) throws IOException {
        this(new ObjectInputStream(new FileInputStream(str)));
    }

    public TraceFromFile(File file) throws IOException {
        this(new ObjectInputStream(new FileInputStream(file)));
    }

    public TraceFromFile(File file, boolean progress) throws IOException {
        if (progress) {
            fromInputStream(new FileInputStreamProgress(file));
        } else {
            fromInputStream(new FileInputStream(file));
        }
    }

    public TraceFromFile(String file, boolean progress) throws IOException {
        if (progress) {
            fromInputStream(new FileInputStreamProgress(file));
        } else {
            fromInputStream(new FileInputStream(file));
        }
    }

    
    public TraceFromFile(InputStream inputf) throws IOException{
        fromInputStream(inputf);
    }
            
    private void fromInputStream(InputStream inputf)throws IOException{
        this.inputf=inputf;
        this.input = new ObjectInputStream(inputf);
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
                nextElement = readOperation();
                return nextElement != null;
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
                } catch (EOFException ex1) {
                    try {
                        input.close();
                        if (inputf != null) {
                            inputf.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(TraceFromFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };
    }
}
