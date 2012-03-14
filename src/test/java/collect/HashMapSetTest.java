/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author moi
 */
public class HashMapSetTest {

    <T> ArrayList<T> createList(T... nodes) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : nodes) {
            list.add(t);
        }
        return list;
    }

    
    public HashMapSet fill() {
        HashMapSet<String, String> hsm = new HashMapSet<String, String>();
        hsm.put("Disney", "Le roi lion");
        hsm.put("DreamWorks", "Monstre et compagnie");
        hsm.put("Disney", "Blanche Neige");
        hsm.put("DreamWorks", "Le chat Poté");
        hsm.put("Disney", "Aladin");
        hsm.put("DreamWorks", "Shrek");
        hsm.put("Disney", "Atlantis");
        return hsm;
    }

    @Test 
    public void nullEntry(){
        HashMapSet<String, String> hsm = new HashMapSet<String, String>();
        hsm.put(null, "bonjour");
        hsm.put(null, "a");
        hsm.put(null, "tous");
        hsm.put("toto", "bibi");
        Set s=hsm.getAll(null);
        assertTrue(s.contains("bonjour"));
        assertTrue(s.contains("a"));
        assertTrue(s.contains("tous"));
        assertFalse(s.contains("bibi"));
        assertTrue(hsm.getOne("toto").equals("bibi"));
        
        
    }
    
    @Test
    public void ajoutdesupprimeplusieurclef() {

        HashMapSet<String, String> hsm = fill();


        assertTrue(hsm.getAll("Disney").contains("Le roi lion"));
        assertTrue(hsm.getAll("Disney").contains("Aladin"));
        assertTrue(hsm.getAll("Disney").contains("Blanche Neige"));
        assertTrue(hsm.getAll("DreamWorks").contains("Monstre et compagnie"));
        assertTrue(hsm.getAll("DreamWorks").contains("Shrek"));
        assertTrue(hsm.getAll("DreamWorks").contains("Le chat Poté"));
        assertTrue(hsm.getAll("Disney").contains("Atlantis"));

        List l = createList("Monstre et compagnie", "Shrek", "Le chat Poté");
        List l2 = createList("Le roi lion", "Atlantis", "Blanche Neige");


        assertTrue(hsm.removeAll("DreamWorks").containsAll(l));

        assertTrue(hsm.getAll("Disney").contains("Le roi lion"));
        assertTrue(hsm.getAll("Disney").contains("Aladin"));
        assertTrue(hsm.getAll("Disney").contains("Blanche Neige"));
        assertTrue(hsm.getAll("Disney").contains("Atlantis"));
        assertEquals(hsm.getAll("DreamWorks"),null);
        

        assertTrue(hsm.remove("Disney", "Aladin"));
        assertTrue(hsm.getAll("Disney").contains("Le roi lion"));
        assertTrue(hsm.getAll("Disney").contains("Blanche Neige"));
        assertTrue(hsm.getAll("Disney").contains("Atlantis"));
        assertEquals(hsm.getAll("DreamWorks"),null);
        assertFalse(hsm.getAll("Disney").contains("Aladin"));

        /*
         * assertTrue(hsm.values().containsAll(l2));
         */
        /*
         * hsm.removeAll("DreamWorks") hsm.remove("Disney", "Aladin")
         */
    }

    @Test
    public void valuesTest() {
        HashMapSet<String, String> hsm = fill();
        ArrayList l = createList("Monstre et compagnie", "Shrek", "Le chat Poté", "Aladin");
        ArrayList<String> l2 = createList("Le roi lion", "Atlantis", "Blanche Neige");
        ArrayList<String> l3 = ((ArrayList) l2.clone());
        ArrayList<String> l4 = ((ArrayList) l2.clone());
        l3.addAll(l2);
        l4.add("Aladin");


        assertTrue(hsm.values().containsAll(l3));
        hsm.removeAll("DreamWorks");
        assertTrue(hsm.values().containsAll(l4));

        hsm.remove("Disney", "Aladin");
        assertTrue(hsm.values().containsAll(l2));



    }

    @Test
    public void containsTest() {

        HashMapSet<String, String> hsm = fill();
        assertTrue(hsm.contains("Le roi lion"));
        assertTrue(hsm.contains("Aladin"));
        assertTrue(hsm.contains("Blanche Neige"));
        assertTrue(hsm.contains("Monstre et compagnie"));
        assertTrue(hsm.contains("Shrek"));
        assertTrue(hsm.contains("Le chat Poté"));
        assertTrue(hsm.contains("Atlantis"));





        hsm.removeAll("DreamWorks");
        assertTrue(hsm.contains("Le roi lion"));
        assertTrue(hsm.contains("Aladin"));
        assertTrue(hsm.contains("Blanche Neige"));
        assertTrue(hsm.contains("Atlantis"));
        assertFalse(hsm.contains("Monstre et compagnie"));
        assertFalse(hsm.contains("Shrek"));
        assertFalse(hsm.contains("Le chat Poté"));


        hsm.remove("Disney", "Aladin");
        assertTrue(hsm.contains("Le roi lion"));
        assertFalse(hsm.contains("Aladin"));
        assertTrue(hsm.contains("Blanche Neige"));
        assertTrue(hsm.contains("Atlantis"));
        assertFalse(hsm.contains("Monstre et compagnie"));
        assertFalse(hsm.contains("Shrek"));
        assertFalse(hsm.contains("Le chat Poté"));


    }

    @Test
    public void containsValueTest() {

        HashMapSet<String, String> hsm = fill();
        assertTrue(hsm.containsValue("Disney", "Le roi lion"));
        assertTrue(hsm.containsValue("Disney", "Aladin"));
        assertTrue(hsm.containsValue("Disney", "Blanche Neige"));
        assertTrue(hsm.containsValue("DreamWorks", "Monstre et compagnie"));
        assertTrue(hsm.containsValue("DreamWorks", "Shrek"));
        assertTrue(hsm.containsValue("DreamWorks", "Le chat Poté"));
        assertTrue(hsm.containsValue("Disney", "Atlantis"));

        hsm.removeAll("DreamWorks");
        assertTrue(hsm.containsValue("Disney", "Le roi lion"));
        assertTrue(hsm.containsValue("Disney", "Aladin"));
        assertTrue(hsm.containsValue("Disney", "Blanche Neige"));
        assertTrue(hsm.containsValue("Disney", "Atlantis"));
        assertFalse(hsm.containsValue("DreamWorks", "Monstre et compagnie"));
        assertFalse(hsm.containsValue("DreamWorks", "Shrek"));
        assertFalse(hsm.containsValue("DreamWorks", "Le chat Poté"));


        hsm.remove("Disney", "Aladin");
        assertTrue(hsm.containsValue("Disney", "Le roi lion"));
        assertTrue(hsm.containsValue("Disney", "Blanche Neige"));
        assertTrue(hsm.containsValue("Disney", "Atlantis"));
        assertFalse(hsm.containsValue("DreamWorks", "Monstre et compagnie"));
        assertFalse(hsm.containsValue("DreamWorks", "Shrek"));
        assertFalse(hsm.containsValue("DreamWorks", "Le chat Poté"));
        assertFalse(hsm.containsValue("Disney", "Aladin"));

    }

    @Test
    public void KeySetTest() {
        HashMapSet<String, String> hsm = fill();
        List l3 = createList("Disney", "DreamWorks");

        assertTrue(hsm.keySet().containsAll(l3));
        hsm.removeAll("DreamWorks");
        hsm.contains("Disney");

    }

    @Test
    public void containsKey() {

        HashMapSet<String, String> hsm = fill();

        assertTrue(hsm.containsKey("Disney"));
        assertTrue(hsm.containsKey("DreamWorks"));

        hsm.removeAll("Disney");
        assertTrue(hsm.containsKey("DreamWorks"));

    }

    @Test
    public void containsSize() {

        HashMapSet<String, String> hsm = fill();
        assertEquals(hsm.size(), 7);

        hsm.removeAll("DreamWorks");

        assertEquals(hsm.size(), 4);

        hsm.remove("Disney", "Aladin");

        assertEquals(hsm.size(), 3);

    }
}
