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
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDTMessage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author score
 */
public class DiagSequence extends JPanel implements Runnable, Viewer {

    

    class ERMessage {

        private VectorClock emitor;
        private VectorClock receptor;
        private int e;
        private int r;
        private CRDTMessage m;

        public ERMessage(VectorClock emitor, VectorClock receptor, int e, int r, CRDTMessage m) {
            this.emitor =(VectorClock) emitor.clone();
            this.receptor =(VectorClock) receptor.clone();
            this.e = e;
            this.r = r;
            this.m = m;
        }

        /**
         * @return the emitor
         */
        public VectorClock getEmitor() {
            return emitor;
        }

        /**
         * @return the receptor
         */
        public VectorClock getReceptor() {
            return receptor;
        }

        /**
         * @return the e
         */
        public int getE() {
            return e;
        }

        /**
         * @return the r
         */
        public int getR() {
            return r;
        }

        /**
         * @return the m
         */
        public CRDTMessage getM() {
            return m;
        }
    }
    
    
    
    
    
    
    
    
    
    
    LinkedList<ERMessage> listMessage;
    JFrame window;
    int nbSite = 10;
    Thread th;
    int offsetX = 20;
    int offsetY;
    
    
    
    
    
    @Override
    public void run() {
        try {
            while (true) {

                Thread.sleep(200);
                Dimension dim = new Dimension(offsetX + 200 * nbSite, 30 + 25 * listMessage.size());
                //System.out.println(30+10*transit.size());
                //this.setMinimumSize(dim);
                this.setPreferredSize(dim);
                //this.setSize(dim);
                this.updateUI();

            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    JFrame ViewerWindow(JComponent jc) {
        JFrame jf = new JFrame();
        jf.setTitle("View");
        jf.setSize(600, 400);
        //this.setContentPane(new JScrollPane(this.getContentPane()));
        JScrollPane js = new JScrollPane(jc);
        //js.add(this);
        jf.getContentPane().add(js);
         
        jf.setVisible(true);
        return jf;
    }

    DiagSequence(int nbSite) {
        this.setBackground(Color.WHITE);
        this.nbSite = nbSite;
        listMessage = new LinkedList<ERMessage>();
        
        //th.start();
    }

    @Override
    public void addMessage(VectorClock emitor, VectorClock receptor, int e, int r, CRDTMessage m) {
        listMessage.add(new ERMessage(emitor, receptor, e, r, m));
    }

    
     @Override
    public synchronized void paintComponent(Graphics g){
        super.paintComponent(g);
        int ecart,tmp=0;
        int offsetx=offsetX;
        int offsety=offsetY;
        int width=this.getWidth();
        Iterator<ERMessage> it;
        ERMessage tmess;
        super.paintComponent(g);
        
        ecart = (int) (((double) width-offsetx) / (nbSite));
        //offsetx=ecart/2+offsetx;
        tmp=offsetx;
        for (int a=0;a<nbSite;a++){
               g.drawString(Integer.toString(a+1), tmp,20);
               g.drawLine(tmp,25 , tmp, offsety);
               tmp+=ecart;
        }
        it=listMessage.iterator();

        while(it.hasNext()){
            tmess=it.next();
            g.drawLine(tmess.getE()*ecart+offsetx, offsety, tmess.getR()*ecart+offsetx, offsety+15);
            g.drawString(tmess.getM().toString(),tmess.getR()*ecart+offsetx , offsety+25);
            //System.out.println("dep="+tmess.getSiteDep()+" arr="+tmess.getSiteAr());
            for (int a=offsetx;a<this.getWidth();a+=ecart){
                g.drawLine(a,offsety , a, offsety+25);
            }
            offsety+=25;
        }
        
     }
     @Override
    public void clear() {
        this.listMessage.clear();
    }
    public void affiche(){
        window = ViewerWindow(this);
        th = new Thread(this);
    }
    
}
