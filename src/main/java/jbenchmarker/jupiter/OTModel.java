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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.jupiter;

import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 * Operational transformation model. 
 * Includes data, operation generation and transformation functions.
 * @author urso
 */
public interface OTModel<T> {
    
    /**
     * Model view.
     */
    public T lookup();

    /**
     * Generates OT operations corresponding to a user local operation.
     * @param local user operation
     * @return corresponding ot operations
     */
    public List<OTOperation> generate(LocalOperation local);

    /**
     * Cross transforms a remote operation with a local operation.
     * Modifies the local operation. Return the transformed remote operation. 
     * @param msg remote operation
     * @param op local operation
     * @return remote transformed
     */
    public OTOperation transform(OTOperation msg, OTOperation op);

    /**
     * Applies a (remote) operation.
     * @param msg remote operation
     */
    public void apply(OTOperation msg);
}
