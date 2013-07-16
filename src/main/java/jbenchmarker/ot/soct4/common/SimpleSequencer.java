package jbenchmarker.ot.soct4.common;

import java.util.concurrent.atomic.AtomicInteger;


public class SimpleSequencer {

	private AtomicInteger counter;

	private static SimpleSequencer instance;

	private SimpleSequencer() {
		this.counter = new AtomicInteger();
		this.counter.addAndGet(-1);
	}

	private int next() {
		int next = counter.incrementAndGet();
		return next;
	}

	public int ticket() {
		return next();
	}

	public static SimpleSequencer getInstance() {
		if (instance == null)
			instance = new SimpleSequencer();
		return instance;
	}

}
