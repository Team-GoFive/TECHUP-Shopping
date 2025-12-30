package com.kt.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class OpenAIClientConfiguration {

	@Bean
	public OpenAIClient openAiClient() {
		RestClient restClient = RestClient.builder().baseUrl("https://api.openai.com/v1").build();
		RestClientAdapter adapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
		return factory.createClient(OpenAIClient.class);
	}
}
