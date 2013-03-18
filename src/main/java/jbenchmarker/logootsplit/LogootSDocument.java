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
package jbenchmarker.logootsplit;


import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;



public class LogootSDocument<T> implements Document{
    
    private List<LogootSElement> elements;
    private List<List<T>> view;
   
    private int clock;
    private int maxId;
    private int replicaNumber;
    
    public LogootSDocument(int max){
        this.maxId=max;
        elements=new ArrayList<LogootSElement>();
        view=new ArrayList<List<T>>();
        clock=0;
        
        LogootSIdentifier lowest=new LogootSIdentifier(0, 0);
        LogootSIdentifier greatest=new LogootSIdentifier(max, 0);
        
        List<LogootSIdentifier> ll=new ArrayList<LogootSIdentifier>();
        ll.add(lowest);
        
        List<LogootSIdentifier> gl=new ArrayList<LogootSIdentifier>();
        gl.add(greatest);
        
        
        elements.add(new LogootSElement(ll, 0));
        elements.add(new LogootSElement(gl, 0));
        
        List l1=new ArrayList<T>();
        l1.add(null);
        List l2=new ArrayList<T>();
        l2.add(null);
        view.add(l1);
        view.add(l2);
    }
    
    public int clockIncrement() {
        return ++this.clock;
    }
    
    public int max(){
        return maxId;
    }
    
    public void setReplicaNumber(int i){
        replicaNumber = i;
    }
    

    private Position getPositionFromCarret(int start) {
        int i = 0;
        int index = 1;
        while (i +view.get(index).size() <= start) {
            i = i + view.get(index).size();
            index++;
        }
        return new Position(index, start - i);
    }

    private Position[] getPositionFromCarret(int start, int end) {
        Position[] res = new Position[2];
        int i = 0;
        int index = 1;
        while (i + view.get(index).size() <= start) {
            i = i + view.get(index).size();
            index++;
        }
        res[0] = new Position(index, start - i);

        while (i + view.get(index).size() < end) {
            i = i + view.get(index).size();
            index++;
        }
        res[1] = new Position(index, end - i);
        
        return res;

    }

    public List<SequenceMessage> generateInsertion(SequenceOperation so) {
        int start=so.getPosition();
        Position position = getPositionFromCarret(start);
        List<SequenceMessage> list = new ArrayList<SequenceMessage>();
        if (position.offset == 0) {
            LogootSElement el =new LogootSElement(elements.get(position.index - 1), view.get(position.index - 1).size()-1);
            list.add(new LogootSInsertion(so,el, elements.get(position.index), so.getContent(), this, replicaNumber));
        } else {
            list.add(new LogootSSplit(so,elements.get(position.index), position.offset));
            list.add(new LogootSInsertion(so,elements.get(position.index), position.offset, so.getContent(), this,replicaNumber));
        }
        return list;

    }

    public List<SequenceMessage> generateDeletion(SequenceOperation so) {
        int start=so.getPosition();
        int end=start+so.getLenghOfADel();
        Position[] positions = getPositionFromCarret(start, end);
        List<SequenceMessage> list = new ArrayList<SequenceMessage>();
        int n = positions[0].index;
        int o = positions[0].offset;
        while (n != positions[1].index) {
            list.add(new LogootSDeletion(so,elements.get(n), o, view.get(n).size()));
            o = 0;
            n++;
        }
        list.add(new LogootSDeletion(so,elements.get(n), o, positions[1].offset));
        return list;
    }
    
    public int IndexOf(LogootSElement el,boolean insert){
        int min=0;
        int max=elements.size()-1;
        int medium;
        while(min<=max){
            medium=(min+max)/2;
            switch(elements.get(medium).compareTo(el)/(elements.get(medium).compareTo(el)== 0 ? 1 : Math.abs(elements.get(medium).compareTo(el)))){
                case -1 :
                    min=medium+1;
                    break;
                case 0 :
                    return medium;
                case 1 : 
                    max=medium -1;
                    break;
            }
        }
        return insert ? min : -1;
    }
    
    public List<Integer> getAllLike(LogootSElement el){
        List<Integer> list = new ArrayList<Integer>();
        LogootSElement minEl=el.origin();
        LogootSElement maxEl=el.origin();
        maxEl.getIdAt(maxEl.size()-1).setOffset(maxId);
        int min=IndexOf(minEl,true);
        int max=IndexOf(maxEl,true);
        for(int i=min;i<max;i++){
            if(elements.get(i).size()==el.size()){
                list.add(i);
            }
        }   
        return list;
    }
   
    public List<T> get(int index){
        return view.get(index);
    }
    
    public LogootSElement getEl(int index){
        return elements.get(index);
    }
    
    public void add(LogootSElement el, List<T> content){
        int index=IndexOf(el, true);
        elements.add(index, el);
        view.add(index, content);
    }
    
    public void remove(int index){
        elements.remove(index);
        view.remove(index);
    }
    
    public void delete(int index, int start, int end){
        List<T> s=view.get(index);
        LogootSElement el=elements.get(index);
        
        if(start==0){//end !=s.length too
            //String ns=s.substring(end);
            List<T> ns=new ArrayList<T>(s.subList(end,s.size()));
            LogootSElement nel=new LogootSElement(el, end);
            view.remove(index);
            elements.remove(index);
            int nindex=IndexOf(nel, true);
            view.add(nindex, ns);
            elements.add(nindex, nel);
        }
        else{//start!=0
            if(end==s.size()){
                //String ns=s.substring(0, start);
                List<T> ns=new ArrayList<T>(s.subList(0, start));
                view.set(index,ns);  
            }
            else{
                //String ns1=s.substring(0, start);
                //String ns2=s.substring(end);
                List<T> ns1=new ArrayList<T>(s.subList(0, start));
                List<T> ns2=new ArrayList<T>(s.subList(end, s.size()));
                LogootSElement nel=new LogootSElement(el,end);
                int nindex=IndexOf(nel, true);
                view.set(index, ns1);
                view.add(nindex, ns2);
                elements.add(nindex, nel);
            }
        }
        

    }
    
    
    @Override
    public String view() {
        StringBuilder sb = new StringBuilder();
        for (int i=1;i<view.size()-1;i++){
            for(int j=0;j<view.get(i).size();j++){
                sb.append(view.get(i).get(j));
            }
        }
        return sb.toString();
    }

    @Override
    public void apply(Operation op) {
        ((LogootSOperation)op).apply(this);
    }
    
    public String elements(){
        String s ="";
        for(int i=0;i<elements.size();i++){
            s=s+elements.get(i)+"*"+view.get(i)+"\n";
        }
        return s;
    }
   
    
    public int size(){
        return elements.size();
    }

    @Override
    public int viewLength() {
        return view().length();
    }
    
}
class Position {

    int index;
    int offset;

    public Position(int index, int offset) {
        this.index = index;
        this.offset = offset;
    }
}