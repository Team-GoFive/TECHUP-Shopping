package com.kt.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenAIClientConfiguration {

	private final OpenAIProperties openAIProperties;

	@Bean
	public OpenAIClient openAiClient() {
		RestClient restClient = RestClient.builder()
			.defaultHeader("Authorization", "Bearer" + openAIProperties.apiKey())
			.build();
		RestClientAdapter adapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
		return factory.createClient(OpenAIClient.class);
	}
}
