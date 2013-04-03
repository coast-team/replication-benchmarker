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
package jbenchmarker.trace.json.moulinette.attributs;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author romain
 */
public class DelContenu implements XMLObjetInterface,Serializable {

    private HashMap<Integer,String> delText;

    public DelContenu() {
    }

    public HashMap<Integer, String> getDelText() {
        return delText;
    }

    public void setDelText(HashMap<Integer, String> delText) {
        this.delText = delText;
    }
    
    
    @Override
    public StringBuffer toStringXML() {
        StringBuffer b = new StringBuffer("");
        b.append("<delContenu>").append(delText).append("\n").append("</delContenu>");
        return b;
    }
}
