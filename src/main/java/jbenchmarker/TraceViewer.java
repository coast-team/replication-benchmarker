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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TraceViewer extends JFrame {

    JTextArea text;
    static int windows = 0;

    public TraceViewer(String file) {
        windows++;
        text = new JTextArea(5, 20);
        
        this.setTitle("TraceViwer " + (file == null ? "" : " [" + file + "]"));
        //text.setWrapStyleWord(true);
        this.setBounds(0, 0, 500, 500);
        JScrollPane bar = new JScrollPane(text);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                windows--;
                if (windows==0){
                    System.exit(0);
                }
            }
        });
        //bar.add(text);
        this.add(bar);
        if (file != null) {
            this.open(file);
        }
        this.setVisible(true);

    }

    public void open(String file) {
        StringBuilder str = new StringBuilder();
        ObjectInputStream objs;
        try {

            objs = new ObjectInputStream(new FileInputStream(file));
            System.out.println("" + objs.available());
            try {
                while (true) {
                    Object obj = objs.readObject();
                    str.append(obj.toString());
                    str.append("\n----------------------------------------------\n");
                }
            } catch (EOFException ex) {//Normal...
            }
            objs.close();

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }


        text.append(str.toString());
        text.updateUI();
    }

    public void save(String file) throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(text.getText().getBytes());
        fout.close();
    }

    public static void main(String... args) {
        for (String arg : args) {
            new TraceViewer(arg);
        }
    }
}
