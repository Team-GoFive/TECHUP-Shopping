package com.kt.config;

import com.kt.util.EncryptUtil;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EncryptUtilInitializer {

	private final Environment env;

	public EncryptUtilInitializer(Environment env) {
		this.env = env;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadEncryptKey() {
		String key = env.getProperty("aes.key");
		EncryptUtil.loadKey(key);
	}

}
