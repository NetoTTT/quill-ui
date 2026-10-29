package com.quillui.action;

/**
 * Backend boundary for UI actions. Quill UI never mutates game state itself.
 *
 * <p>The payload type is owned by the consumer: use {@code QuillActionSink<String>} for commands,
 * or a project-specific enum/record for typed C2S packets.</p>
 *
 * <p>{@link QuillCommandSink} is the zero-dependency, command-first implementation. A mod that
 * prefers typed packets can provide another implementation and still use the same widgets.</p>
 *
 * @param <A> action payload type
 */
@FunctionalInterface
public interface QuillActionSink<A> {
	void execute(A action);
}
