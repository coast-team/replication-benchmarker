/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt.simulator.sizecalculator;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class MemSizeCalculatorTest {

    public MemSizeCalculatorTest() {
    }

    @Test
    public void testSomeMethod() {

        String str = "hello";
        Random rnd = new Random();
        String str_t[] = new String[50];
        long empty = MemSizeCalculator.sizeOf(str_t);
        for (int i = 0; i < str_t.length; i++) {
            str_t[i] = str;
        }
        long fullSame = MemSizeCalculator.sizeOf(str_t);

        for (int i = 0; i < str_t.length; i++) {
            byte toto[] = new byte[5];
            rnd.nextBytes(toto);
            str_t[i] = new String(toto);
        }
        long full = MemSizeCalculator.sizeOf(str_t);
        assertTrue("not deep", empty < fullSame);
        assertTrue("impossible", fullSame < full);
        System.out.println(empty + " " + fullSame + " " + full);
    }

    @Test
    public void nameAsNoEffect() {
        Ioezjofiezjfoezuahfuizehfizeuahfzefbzefbaeizuhfizeuhiezuhfuizehfiuzeifuzehiufezhuizefhze a = new Ioezjofiezjfoezuahfuizehfizeuahfzefbzefbaeizuhfizeuhiezuhfuizehfiuzeifuzehiufezhuizefhze();
        Doh b = new Doh();
        long as = MemSizeCalculator.sizeOf(a);
        long bs = MemSizeCalculator.sizeOf(b);
        assertEquals(bs, as);
       
    }

    @Test
    public void nameAsEffect() throws IOException {
        Ioezjofiezjfoezuahfuizehfizeuahfzefbzefbaeizuhfizeuhiezuhfuizehfiuzeifuzehiufezhuizefhze a = new Ioezjofiezjfoezuahfuizehfizeuahfzefbzefbaeizuhfizeuhiezuhfuizehfiuzeifuzehiufezhuizefhze();
        Doh b = new Doh();
        long as = StandardSizeCalculator.sizeOf(a);
        long bs = StandardSizeCalculator.sizeOf(b);
        assertTrue("Serialisation take the name of class",bs!= as);
        
    }

    static class Ioezjofiezjfoezuahfuizehfizeuahfzefbzefbaeizuhfizeuhiezuhfuizehfiuzeifuzehiufezhuizefhze implements Serializable {

        int i = 0;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    static class Doh implements Serializable {

        int i = 0;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}
