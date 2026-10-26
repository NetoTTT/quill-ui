package com.quillui.action;

/**
 * Backend boundary for UI actions. Quill UI never mutates game state itself.
 *
 * <p>{@link QuillCommandSink} is the zero-dependency, command-first implementation. A mod that
 * prefers typed C2S packets can provide another implementation and still use the same widgets.</p>
 */
public interface QuillActionSink {
	void execute(String action);
}
