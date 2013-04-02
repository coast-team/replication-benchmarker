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
package crdt.tree.orderedtree.renderer;

import collect.SimpleNode;
import crdt.CRDT;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class SizeJSonDoc extends SizeXMLDoc {

   
    public String gen(SimpleNode<String> in) {
        try {
            JSONObject xmlJSONObj = XML.toJSONObject("<?xml version=\"1.0\" ?>"+view(in));
            return xmlJSONObj.toString(0).replaceAll("\\s","");
        } catch (JSONException ex) {
            Logger.getLogger(SizeJSonDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public int serializ(CRDT m) throws IOException {
        return gen((SimpleNode<String>) m.lookup()).length();
    }
}
