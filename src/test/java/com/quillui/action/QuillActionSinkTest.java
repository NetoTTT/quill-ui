package com.quillui.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuillActionSinkTest {
	private enum ConfigAction {
		NEXT_PAGE,
		PREVIOUS_PAGE
	}

	@Test
	void typedSinkReceivesTheConsumerActionType() {
		List<ConfigAction> recorded = new ArrayList<>();
		QuillActionSink<ConfigAction> sink = recorded::add;

		sink.execute(ConfigAction.NEXT_PAGE);

		assertEquals(List.of(ConfigAction.NEXT_PAGE), recorded);
	}
}
