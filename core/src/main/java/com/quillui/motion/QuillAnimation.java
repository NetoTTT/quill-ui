package com.quillui.motion;

/**
 * A small time-based animation driven by explicit frame deltas. The animation owns its progress;
 * widgets can update it in their render pass and derive opacity, offsets or sizes from {@link #value()}.
 */
public final class QuillAnimation {
	private float durationSeconds;
	private float elapsedSeconds;
	private float from;
	private float to = 1.0F;
	private QuillCurve curve = QuillCurves.EASE_IN_OUT;
	private boolean running;
	private boolean looping;

	public QuillAnimation(float durationSeconds) {
		this.durationSeconds = Math.max(0.0F, durationSeconds);
	}

	public QuillAnimation play(float from, float to) {
		this.from = from;
		this.to = to;
		this.elapsedSeconds = 0.0F;
		this.running = true;
		if (effectiveDuration() <= 0.0F) {
			running = false;
		}
		return this;
	}

	public QuillAnimation playTo(float target) {
		return play(value(), target);
	}

	public QuillAnimation stop() {
		running = false;
		return this;
	}

	public QuillAnimation jumpTo(float value) {
		from = value;
		to = value;
		elapsedSeconds = 0.0F;
		running = false;
		return this;
	}

	public void update(float deltaSeconds) {
		if (!running) {
			return;
		}

		float duration = effectiveDuration();
		if (duration <= 0.0F) {
			elapsedSeconds = 0.0F;
			running = false;
			return;
		}

		elapsedSeconds += Math.max(0.0F, deltaSeconds);
		if (elapsedSeconds < duration) {
			return;
		}

		if (looping) {
			elapsedSeconds %= duration;
			return;
		}

		elapsedSeconds = duration;
		running = false;
	}

	public float value() {
		float duration = effectiveDuration();
		if (duration <= 0.0F) {
			return to;
		}
		float progress = Math.max(0.0F, Math.min(1.0F, elapsedSeconds / duration));
		float eased = curve == null ? progress : curve.transform(progress);
		return from + (to - from) * eased;
	}

	public float progress() {
		float duration = effectiveDuration();
		return duration <= 0.0F
				? 1.0F
				: Math.max(0.0F, Math.min(1.0F, elapsedSeconds / duration));
	}

	public boolean running() {
		return running;
	}

	public boolean finished() {
		return !running && elapsedSeconds >= effectiveDuration();
	}

	public QuillAnimation setDuration(float durationSeconds) {
		this.durationSeconds = Math.max(0.0F, durationSeconds);
		return this;
	}

	public float duration() {
		return effectiveDuration();
	}

	public QuillAnimation setCurve(QuillCurve curve) {
		this.curve = curve == null ? QuillCurves.LINEAR : curve;
		return this;
	}

	public QuillAnimation loop(boolean looping) {
		this.looping = looping;
		return this;
	}

	public QuillAnimation reset() {
		elapsedSeconds = 0.0F;
		running = false;
		return this;
	}

	private float effectiveDuration() {
		return QuillMotion.reducedMotion() ? 0.0F : durationSeconds;
	}
}
