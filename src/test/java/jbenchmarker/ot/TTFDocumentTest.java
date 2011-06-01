package jbenchmarker.ot;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oster
 */
public class TTFDocumentTest {

    @Test
    public void testViewToModel() {
        TTFDoc model;

        model = new TTFDoc();
        assertEquals(0, model.viewToModel(0));

        model = new TTFDoc(c('a'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(1, model.viewToModel(1));

        model = new TTFDoc(c('a'), c('b'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(1, model.viewToModel(1));
        assertEquals(2, model.viewToModel(2));

        model = new TTFDoc(c('a'), h('b'), c('c'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(2, model.viewToModel(1));
        assertEquals(3, model.viewToModel(2));

        model = new TTFDoc(h('a'), c('b'), c('c'));
        assertEquals(1, model.viewToModel(0));
        assertEquals(2, model.viewToModel(1));
        assertEquals(3, model.viewToModel(2));

        model = new TTFDoc(c('a'), c('b'), h('c'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(1, model.viewToModel(1));
        assertEquals(3, model.viewToModel(2));

        model = new TTFDoc(c('a'), h('b'), h('c'), c('d'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(3, model.viewToModel(1));
        assertEquals(4, model.viewToModel(2));
    }


    // enable internal data-model injection
    private class TTFDoc extends TTFDocument {
        public TTFDoc() {
            super();
        }

        public TTFDoc(TTFChar... cs) {
            super();
            this.model = new ArrayList<TTFChar>();
            for (TTFChar c : cs)
                this.model.add(c);
        }
    }

    // helpers

    // generate a visible TTFChar
    private static TTFChar c(char c) {
        return new TTFChar(c);
    }

    // generate an unvisible TTFChar
    private static TTFChar h(char c) {
        TTFChar ch = new TTFChar(c);
        ch.hide();
        return ch;
    }
}
