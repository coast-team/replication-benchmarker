package jbenchmarker.logoot;

import java.util.*;

public class LogootIdentifier implements Comparable<LogootIdentifier> {

    final private ArrayList<Component> id;

    public LogootIdentifier(int capacity) {
        id = new ArrayList<Component>(capacity);
    }

    public ArrayList<Component> getID() {
        return id;
    }

    public Component getComponentAt(int position) {
        return id.get(position);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootIdentifier other = (LogootIdentifier) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public void addComponent(Component cp) {
        id.add(cp);
    }

    public int length() {
        return id.size();
    }

    public String toString() {
        String ligneIdentif = "";
        for (Component c : id) {
            ligneIdentif += c.toString();
        }
        return ligneIdentif;
    }

    /**
     * Returns O if j > index().
     **/
    long getDigitAt(int index) {
        if (index >= this.length()) {
            return 0;
        } else {
            return id.get(index).getDigit();
        }
    }

    /**
     * Digits of this identifier until index included (filled by 0s if index >= length()) 
     */
    public List<Long> digits(int index) {
        List<Long> l = new ArrayList<Long>();
        for (int i = 0; i <= index; i++) {
            if (i >= id.size()) {
                l.add(0L);
            } else {
                l.add(id.get(i).getDigit());
            }
        }
        return l;
    }

    public int compareTo(LogootIdentifier t) {
        int m = Math.min(id.size(), t.id.size());
        for (int i = 0; i < m; i++) {
            int c = id.get(i).compareTo(t.id.get(i));
            if (c != 0) {
                return c;
            }
        }
        return id.size() - t.id.size();
    }

    @Override
    public LogootIdentifier clone() {
        LogootIdentifier o = new LogootIdentifier(id.size());
        for (Component c : id) {
            o.id.add(c.clone());
        }
        return o;
    }
}