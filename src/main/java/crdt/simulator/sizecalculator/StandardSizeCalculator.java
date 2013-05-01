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
 
package crdt.simulator.sizecalculator;

import crdt.CRDT;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class StandardSizeCalculator implements SizeCalculator{
    private final boolean overhead;

    public StandardSizeCalculator(boolean overhead) {
        this.overhead = overhead;
    }
    
    public static int sizeOf(Object o) throws IOException{
        int size;
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteOutput);
         stream.writeObject(o);
          stream.flush();
        byteOutput.flush();
        size= byteOutput.size();

        //System.out.println("replica :"+m.getReplicaNumber()+" has "+byteOutput.size()+" byte");

        stream.close();
        byteOutput.close();
        //System.out.println("After: replica :"+m.getReplicaNumber()+" has "+byteOutput.size()+" byte");
        return size;
    }
    @Override
     public int serializ(CRDT m) throws IOException {
        
        if (overhead) {
            return (sizeOf(m));
        } else {
            return sizeOf(m.lookup());
        }
       
    }
}
