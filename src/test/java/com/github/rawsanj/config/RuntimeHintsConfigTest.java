package com.github.rawsanj.config;

import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeHintsConfigTest {

	@Test
	void registerHintsTest() {
		RuntimeHints hints = new RuntimeHints();
		new RuntimeHintsConfig.SerdeRuntimeHints().registerHints(hints, getClass().getClassLoader());
		assertThat(RuntimeHintsPredicates.serialization().onType(ChatMessage.class)).accepts(hints);
		assertThat(RuntimeHintsPredicates.serialization().onType(Message.class)).accepts(hints);;
	}
}
