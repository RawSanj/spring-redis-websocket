package com.github.rawsanj.config;

import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.model.Message;
import com.github.rawsanj.model.Platform;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;

@Component
@ImportRuntimeHints(RuntimeHintsConfig.SerdeRuntimeHints.class)
public class RuntimeHintsConfig  {

	static class SerdeRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.serialization()
				.registerType(ChatMessage.class)
				.registerType(Message.class)
				.registerType(Platform.class);
		}
	}
}
