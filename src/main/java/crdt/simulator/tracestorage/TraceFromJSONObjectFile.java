/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator.tracestorage;

import com.thoughtworks.xstream.XStream;
import Tools.FileInputStreamProgress;
import Tools.FileReaderProgress;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TraceFromJSONObjectFile implements Trace {

    ObjectInputStream input;
    InputStream inputf;

    public TraceFromJSONObjectFile(String str) throws IOException {
         fromFileReader(new FileReader(str));
    }

    public TraceFromJSONObjectFile(File file) throws IOException {
         fromFileReader(new FileReader(file));
    }

    public TraceFromJSONObjectFile(File file, boolean progress) throws IOException {
        if (progress) {
            fromFileReader(new FileReaderProgress(file));
        } else {
            fromFileReader(new FileReader(file));
        }
    }

    public TraceFromJSONObjectFile(String file, boolean progress) throws IOException {
        this(new File(file), progress);
    }

    public TraceFromJSONObjectFile(Reader inputf) throws IOException {
        fromFileReader(inputf);
    }

    private void fromFileReader(Reader inputf) throws IOException {
        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        xs.createObjectInputStream(inputf);
        this.input = xs.createObjectInputStream(inputf);

    }

    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>() {
            TraceOperation nextElement = readOperation();

            @Override
            public boolean hasMoreElements() {
                return nextElement != null;
            }

            @Override
            public TraceOperation nextElement() {
                TraceOperation ret = nextElement;
                nextElement = readOperation();
                return ret;

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
                        Logger.getLogger(TraceFromJSONObjectFile.class.getName()).log(Level.SEVERE, null, ex);
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
