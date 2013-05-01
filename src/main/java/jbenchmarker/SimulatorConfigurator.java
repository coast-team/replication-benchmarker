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
package jbenchmarker;

import collect.HashMapSet;
import crdt.CRDT;
import crdt.Factory;
import java.util.Set;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import org.reflections.Reflections;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class SimulatorConfigurator {

    HashMapSet<String, Object> elementFromType;
    JFrame mainWin;
    JTree jtree;
    Box config;
    
    public void scan(String pack) {
    }

    public SimulatorConfigurator() {
        
        mainWin = new JFrame("SimulatorConfigurator");
        
        Box b= Box.createHorizontalBox();
        mainWin.add(b);
        jtree = new JTree();
        JPanel confpanel=new JPanel();
        config=Box.createVerticalBox();
        confpanel.add(config);
        
        
        
    }

    void parameters(Class c){
        
    }
            
    public static void main(String args[]) {

        try {


            /*
             * System.out.println("hello !"); List<String> ls =
             * lireListService("jbenchmarker");
             *
             * for (String s : ls) { System.out.println("->" + s);
            }
             */
            //System.exit(0);
            Reflections reflections = new Reflections("");
            Set<Class<? extends CRDT>> subTypes = reflections.getSubTypesOf(CRDT.class);
            
            for (Class<? extends CRDT> c : subTypes) {
                System.out.println("" + c.getName());

            }

            //switch case()


            System.exit(0);


            Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[0]).newInstance();


        } catch (Exception ex) {
            ex.printStackTrace(System.err);

        }



    }

    
}
