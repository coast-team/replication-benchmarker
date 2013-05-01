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
package jbenchmarker.twotier;

import crdt.CRDT;
import crdt.Factory;
import jbenchmarker.jupiter.OTModel;

/**
 * A factory to create two-tier architecture clients and replicas.
 * @author urso
 */
public class EdgeFactory<T> implements Factory<Client> {
    final private Factory<OTModel<T>> otDocumentFactory;
    final private Factory<CRDT<T>> crdtFactory;
    private CoreReplica<T> currentReplica; 
    private int numberClient = 0, coreNumber = 0;
    final private int clientPerCore;

    public EdgeFactory(Factory<OTModel<T>> otdocf, Factory<CRDT<T>> crdtf, int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Number of client per core replica must be strictly positive.");
        }
        this.otDocumentFactory = otdocf;
        this.crdtFactory = crdtf;
        this.currentReplica = new CoreReplica<T>(otdocf.create(), crdtf.create());
        this.numberClient = 0;
        this.clientPerCore = max;
    }
    
    @Override
    public Client create() {
        ++numberClient;
        if (numberClient == clientPerCore) {
            ++coreNumber;
            currentReplica = new CoreReplica<T>(otDocumentFactory.create(), crdtFactory.create());
            currentReplica.setReplicaNumber(coreNumber);
            numberClient = 0;
        }
        return new Client(otDocumentFactory.create(), currentReplica);
    }
    
    
}
