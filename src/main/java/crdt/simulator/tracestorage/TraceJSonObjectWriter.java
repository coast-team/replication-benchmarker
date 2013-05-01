/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator.tracestorage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import crdt.simulator.CausalSimulator;
import crdt.simulator.TraceOperation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class TraceJSonObjectWriter implements TraceStore{
    ObjectOutputStream writer;
    
    public TraceJSonObjectWriter(String filename) throws IOException {
        this(new FileWriter(filename));
    }
    
    public TraceJSonObjectWriter(File filename) throws IOException {
        this(new FileWriter(filename));
    }
    
    public TraceJSonObjectWriter(FileWriter fileWriter) throws IOException {
        XStream xs= new XStream(new JsonHierarchicalStreamDriver());
        
        writer= xs.createObjectOutputStream(fileWriter);
        
    }
    
    @Override
    public void storeOp(TraceOperation op) {
        try {
            writer.writeObject(op);
        } catch (IOException ex) {
            Logger.getLogger(CausalSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TraceJSonObjectWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
