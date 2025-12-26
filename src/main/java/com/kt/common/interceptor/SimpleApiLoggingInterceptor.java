package com.kt.common.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleApiLoggingInterceptor implements ClientHttpRequestInterceptor {
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws
		IOException {

		log.info("[External API Call] [{}] {} Headers: {} -d {} Body Length: {}",
			request.getMethod(),
			request.getURI(),
			request.getHeaders(),
			new String(body, StandardCharsets.UTF_8),
			body.length
		);

		return execution.execute(request, body);
	}
}
