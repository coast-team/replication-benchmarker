/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package testMain;

import jbenchmarker.Main;
import org.junit.Ignore;
import org.junit.Test;
/**
 *
 * @author score
 */
public class mainSimulatorTest {        
   
    @Ignore
    @Test
    public void mainTest() throws Exception {
        String logoot = "jbenchmarker.logoot.LogootFactory";
        //String rga = "jbenchmarker.rga.RGAFactory";
        //String wootH = "jbenchmarker.woot.WootFactories$WootHFactory";
        String trace = "../../traces/xml/SerieDoc2.xml";
        String[] args = new String[]{logoot, trace, "5", "2", "10"};
        Main mn = new Main();
        mn.main(args);
    }
    
    @Test
    public void playAndReplay() {
        
    }
}
