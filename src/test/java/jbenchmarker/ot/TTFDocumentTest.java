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
package jbenchmarker.ot;

import jbenchmarker.ot.ttf.TTFVisibilityChar;
import jbenchmarker.ot.ttf.TTFDocument;
import java.util.ArrayList;
import java.util.Arrays;
import jbenchmarker.ot.ttf.TTFChar;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oster
 */
public class TTFDocumentTest {

    @Test
    public void testView() {
        TTFDoc model = new TTFDoc(c('a'), h('b'), h('c'), c('d'), h('e'));
        assertEquals("ad", model.view());
    }
    
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
        assertEquals(1, model.viewToModel(1));
        assertEquals(3, model.viewToModel(2));

        model = new TTFDoc(h('a'), c('b'), c('c'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(2, model.viewToModel(1));
        assertEquals(3, model.viewToModel(2));

        model = new TTFDoc(c('a'), c('b'), h('c'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(1, model.viewToModel(1));
        assertEquals(2, model.viewToModel(2));

        model = new TTFDoc(c('a'), h('b'), h('c'), c('d'));
        assertEquals(0, model.viewToModel(0));
        assertEquals(1, model.viewToModel(1));
        assertEquals(4, model.viewToModel(2));
    }


    // enable internal data-model injection
    private class TTFDoc extends TTFDocument {
        public TTFDoc() {
            super();
        }

        public TTFDoc(TTFChar... cs) {
            super();
            this.model.addAll(Arrays.asList(cs));
        }
    }

    // helpers

    // generate a visible TTFChar
    private static TTFChar c(char c) {
        return new TTFVisibilityChar(c);
    }

    // generate an unvisible TTFChar
    private static TTFChar h(char c) {
        TTFVisibilityChar ch = new TTFVisibilityChar(c);
        ch.hide();
        return ch;
    }
}
