package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Runs as an offline process.
 * 
 * @author Niranjan Nanda
 */
@Component
public class ServiceInvoker {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);
	
	private static final String SERVICE_URL = "/api/mock/largeHeader/";
	
	private final WebClient demoWebClient;
	
	@Autowired
	public ServiceInvoker(@Qualifier("demoWebClient") final WebClient demoWebClient) {
		this.demoWebClient = demoWebClient;
	}
	
	public void start() {	
		this.invokeLargeHeaderService()
			.subscribe();
	}
	
	private Mono<Void> invokeLargeHeaderService() {
		return this.demoWebClient.get()
			.uri(SERVICE_URL)
			.exchange()
			.onErrorResume(t -> {
				logger.error("[demoWebClient] Exception while invoking API: '{}' ... ", SERVICE_URL, t);
				return Mono.empty();
			})
			.flatMap(clientResponse -> {
				logger.info("Response received. [Status: {}]", clientResponse.rawStatusCode());
				return clientResponse.bodyToMono(String.class)
						.flatMap(strResponse -> {
							logger.info("String response -> {}", strResponse);
							return Mono.empty();
						});
			})
		;
	}
}
