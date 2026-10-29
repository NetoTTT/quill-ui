package com.quillui.motion;

/**
 * Converts wall-clock time between UI frames into a capped delta in seconds. A ticker belongs to
 * one screen or overlay; call {@link #tick()} once per rendered frame.
 */
public final class QuillTicker {
	private static final double MAX_DELTA_SECONDS = 0.1D;

	private long lastNanos;
	private boolean started;

	public float tick() {
		long now = System.nanoTime();
		if (!started) {
			started = true;
			lastNanos = now;
			return 0.0F;
		}

		double delta = (now - lastNanos) / 1_000_000_000.0D;
		lastNanos = now;
		return (float) Math.min(MAX_DELTA_SECONDS, Math.max(0.0D, delta));
	}

	public void reset() {
		started = false;
		lastNanos = 0L;
	}
}
