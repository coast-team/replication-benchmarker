package jbenchmarker.ot.soct4;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

import jbenchmarker.ot.soct4.common.SimpleSequencer;
import jbenchmarker.ot.soct4.common.StringID;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.Replica;

/**
 * This algorithms requires two steps of communication to execute a local
 * operation. First it gets a timestamp and only after the message is sent to
 * other nodes.
 * 
 * The reply from a local execution is the timestamp request
 * 
 * @author balegas
 * 
 * @param <T>
 */
public class SOCT4Algorithm<T extends Serializable & Comparable<T>> implements
		Replica<SOCT4Algorithm> {

	protected Logger log = Logger.getLogger(SOCT4Algorithm.class.getName());
	PriorityBlockingQueue<SOCT4OP<T>> remoteQueue;

	private List<SequenceOperation<T>> atomList;
	private SOCT4OP<T>[] history;
	private int historyPointer, deliveryPointer;

	private StringID siteID;
	private int replicaNumber;

	private boolean continueProc;
	private boolean active;

	private int generatedIds;

	private SimpleSequencer sequencer = SimpleSequencer.getInstance();

	// The identifier of the last operation delivered on this site
	int nOps;

	@SuppressWarnings("unchecked")
	public SOCT4Algorithm(StringID siteID, boolean dumpState) {
		this.active = true;
		this.siteID = siteID;
		this.history = new SOCT4OP[2];
		this.historyPointer = -1;
		this.deliveryPointer = -1;
		this.nOps = -1;

		Comparator<SOCT4OP<T>> comparator = new Comparator<SOCT4OP<T>>() {
			@Override
			public int compare(SOCT4OP<T> o1, SOCT4OP<T> o2) {
				// Not safe, can be > MAX_INT. However it is unlikely
				return o1.getTimestamp() - o2.getTimestamp();
			}
		};
		this.remoteQueue = new PriorityBlockingQueue<SOCT4OP<T>>(1, comparator);

		this.atomList = new LinkedList<SequenceOperation<T>>();
	}

	@Override
	public CRDTMessage applyLocal(LocalOperation op)
			throws PreconditionException {
		CRDTMessage opWithTs = localExecution((SequenceOperation<T>) op, true);
		return opWithTs;
	}

	/**
	 * Assumes sequential execution
	 * 
	 * @param op
	 * @param sendToObservers
	 * @return Timestamp request
	 */

	public synchronized CRDTMessage localExecution(SequenceOperation<T> op,
			boolean sendToObservers) {
		processOperation(op);
		checkHistorySize();
		int ts = sequencer.ticket();
		SOCT4OP<T> soct4op = new SOCT4OP<T>(this.siteID, ts, op,
				sendToObservers);
		history[++historyPointer] = soct4op;

		// TODO: Message is only broadcast when all previous timestamps of local operations
		// have integrated
		return new OperationBasedOneMessage(soct4op);

	}

	protected void processOperation(SequenceOperation<T> op) {
		processOperation(op, atomList);
	}

	private void processOperation(SequenceOperation<T> op,
			List<SequenceOperation<T>> atomList) {
		switch (op.getType()) {
		case insert:
			atomList.add(op.getPosition(), op);
			break;
		case delete:
			atomList.remove(op.getPosition());
			break;
		case noop:
			break;
		default:
			log.log(Level.WARNING, "Operation not supported " + op.getType());
			break;

		}

	}

	// TODO: code that broadcasts the message based on timestamp order
	/**
	 * private synchronized void deferredBroadCast() { if (localQueue.size() >
	 * 0) { if (localQueue.peek().getTimestamp() == nOps + 1)
	 * multicastSOCT4Msg(new SOCT4SOCT4Message(localQueue.poll())); } }
	 **/

	private synchronized void sequentialReception(SOCT4OP<T> soct4op) {
		remoteQueue.add(soct4op);
		while (remoteQueue.size() > 0) {
			if (remoteQueue.peek().getTimestamp() - 1 == nOps) {
				try {
					integration(remoteQueue.poll());
				} catch (OperationTranspositionException e) {
					log.log(Level.WARNING,
							"Exception while integrating operations");
					e.printStackTrace();
				}
				nOps++;
			} else {
				break;
			}
		}
	}

	private void integration(SOCT4OP<T> op)
			throws OperationTranspositionException {
		synchronized (history) {
			if (!op.getSiteId().equals(siteID)) {
				checkHistorySize();
				for (int j = historyPointer; j > deliveryPointer; j--) {
					history[j + 1] = history[j];
				}
				history[op.getTimestamp()] = op;
				historyPointer++;
				deliveryPointer++;
				for (int j = deliveryPointer + 1; j <= historyPointer; j++) {
					SOCT4OP<T> opL = history[j];
					history[j] = transposeForward(op, opL);
					op = transposeForward(opL, op);
				}
				processOperation(op.getSequenceOperation());
			} else
				deliveryPointer++;
		}

		// This is for OT/CRDT integration
		/**
		 * SOCT4Message msg = new SOCT4Message(op.getSequenceOperation()); if
		 * (op.sendToObservers())
		 * synchronousNotifyObservers(SOCT4Event.SOCT4_INTEGRATION_FINISHED
		 * ,msg);
		 **/

		// TODO: after integrating a message, the algorithm checks if the next
		// local operation can be broadcast
		// deferredBroadCast();
	}

	private SOCT4OP<T> transposeForward(SOCT4OP<T> soct4op1, SOCT4OP<T> soct4op2)
			throws OperationTranspositionException {
		SOCT4OP<T> newOp;
		SequenceOperation<T> newSequenceOp;
		SequenceOperation<T> op1 = soct4op1.getSequenceOperation();
		SequenceOperation<T> op2 = soct4op2.getSequenceOperation();

		if (op1.getType() == OpType.insert && op2.getType() == OpType.insert) {
			if (op1.getPosition() < op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(OpType.insert,
						op2.getPosition() + 1, 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			} else if (op1.getPosition() > op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(OpType.insert,
						op2.getPosition(), 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			} else {
				if (op1.getContentAsString()
						.compareTo(op2.getContentAsString()) == 0) {
					newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
							SOCT4OP.NO_TIMESTAMP, SequenceOperation.noop());
				} else {
					newSequenceOp = new SequenceOperation<T>(OpType.insert,
							op2.getPosition(), 0, op2.getContent());
					newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
							SOCT4OP.NO_TIMESTAMP, newSequenceOp);
				}

			}
		} else if (op1.getType() == OpType.delete
				&& op2.getType() == OpType.delete) {
			if (op1.getPosition() < op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(OpType.delete,
						op2.getPosition() - 1, 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			} else if (op1.getPosition() > op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(OpType.delete,
						op2.getPosition(), 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);

			} else {
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, SequenceOperation.noop());
			}
		} else if (op1.getType() == OpType.insert
				&& op2.getType() == OpType.delete) {
			if (op1.getPosition() < op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(op2.getType(),
						op2.getPosition() + 1, 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			} else {
				newSequenceOp = new SequenceOperation<T>(op2.getType(),
						op2.getPosition(), 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			}
		} else if (op1.getType() == OpType.delete
				&& op2.getType() == OpType.insert) {
			if (op1.getPosition() < op2.getPosition()) {
				newSequenceOp = new SequenceOperation<T>(op2.getType(),
						op2.getPosition() - 1, 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			} else {
				newSequenceOp = new SequenceOperation<T>(op2.getType(),
						op2.getPosition(), 0, op2.getContent());
				newOp = new SOCT4OP<T>(soct4op2.getSiteId(),
						SOCT4OP.NO_TIMESTAMP, newSequenceOp);
			}
		} else {
			throw new OperationTranspositionException();
		}
		return newOp;
	}

	private void checkHistorySize() {
		synchronized (history) {
			if (historyPointer + 1 == history.length) {
				history = Arrays.copyOf(history, history.length * 2);
			}
		}
	}

	public void stopNode() {
		continueProc = false;
	}

	/**
	 * TODO: This method generates a new atomlist from the operation history.
	 * (The atomlist has unordered operations)
	 * 
	 * @return
	 */
	public String getLocalAtomSequenceToString() {
		List<SequenceOperation<T>> atoms = new LinkedList();
		StringBuilder string = new StringBuilder();
		// for (Atom<T> a : atomList) {
		// string.append(a.toString());
		// }
		for (SOCT4OP<T> op : history) {
			if (op == null)
				break;
			processOperation(op.getSequenceOperation(), atoms);
		}

		for (SequenceOperation<T> a : atoms) {
			string.append(a.getContentAsString());
		}

		return string.toString();
	}

	public LinkedList<SOCT4OP<T>> dumpAtomList() {
		LinkedList<SOCT4OP<T>> output = new LinkedList<SOCT4OP<T>>();
		for (int i = 0; i <= historyPointer; i++) {
			SOCT4OP<T> hi = history[i];
			output.add(hi);
		}
		return output;
	}

	public void toggleBroadCast() {
		active = !active;
	}

	@Override
	public void applyRemote(CRDTMessage msg) {
		SOCT4OP op = (SOCT4OP) ((OperationBasedOneMessage) msg).getOperation();
		sequentialReception(op);

	}

	@Override
	public int getReplicaNumber() {
		return replicaNumber;
	}

	@Override
	public void setReplicaNumber(int replicaNumber) {
		this.replicaNumber = replicaNumber;
	}

	// TODO: Does this have to be implemented?
	@Override
	public SOCT4Algorithm lookup() {
		return null;
	}

}