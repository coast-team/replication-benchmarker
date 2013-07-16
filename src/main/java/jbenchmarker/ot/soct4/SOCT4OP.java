package jbenchmarker.ot.soct4;

import java.util.List;
import java.util.logging.Logger;
import jbenchmarker.ot.soct4.TimestampAlreadyAssignedException;

import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

import jbenchmarker.ot.soct4.common.ID;

public class SOCT4OP<T> implements crdt.Operation<SOCT4OP<T>> {

	protected static final int NO_TIMESTAMP = Integer.MIN_VALUE;
	protected static final int NO_POSITION = -1;

	private static final long serialVersionUID = 1L;
	private ID siteID;
	private int timestamp;
	private SequenceOperation<T> op;
	private boolean sendToObservers;

	private static Logger logger = Logger.getLogger(SOCT4OP.class.getName());

	public SOCT4OP() {

	}

	public SOCT4OP(ID siteID, int timestamp, SequenceOperation<T> op) {
		this.siteID = siteID;
		this.timestamp = timestamp;
		this.op = op;
		this.sendToObservers = false;
	}

	public SOCT4OP(ID siteID, int timestamp, SequenceOperation<T> op,
			boolean sendToObservers) {
		this.siteID = siteID;
		this.timestamp = timestamp;
		this.op = op;
		this.sendToObservers = sendToObservers;
	}

	public SOCT4OP<T> clone() {
		return new SOCT4OP<T>(this.siteID.clone(), this.timestamp,
				this.op.clone());
	}

	public void updateTimestamp(int incomingTimestamp)
			throws TimestampAlreadyAssignedException {
		if (this.timestamp >= 0) {
			logger.warning("Timestamp already assigned to operation with timestamp "
					+ timestamp);
			throw new TimestampAlreadyAssignedException();
		}
		this.timestamp = incomingTimestamp;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public ID getSiteId() {
		return this.siteID;
	}

	public OpType getOpType() {
		return op.getType();
	}

	public String toString() {
		return "{" + op.getType() + " " + op.getPosition() + " "
				+ op.getContentAsString() + " " + "}";
	}

	public int getPos() {
		return op.getPosition();
	}

	public List<T> getAtom() {
		return op.getContent();
	}

	public String getAtomAsString() {
		return op.getContentAsString();
	}

	public SequenceOperation<T> getSequenceOperation() {
		return op;
	}

	public boolean sendToObservers() {
		return sendToObservers;
	}

}
