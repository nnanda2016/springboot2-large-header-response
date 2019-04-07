package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpClientCodec;
import reactor.netty.NettyPipeline;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/**
 * Spring config class.
 * 
 * @author Niranjan Nanda
 */
@Configuration
@EnableWebFlux
public class AppConfig { 
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
		
	@Bean(name = "demoWebClient")
	public WebClient buildDempWebClient(final ReactorResourceFactory resourceFactory) throws Exception {
		final ConnectionProvider connectionProvider = resourceFactory.getConnectionProvider();
		final LoopResources channelResources = resourceFactory.getLoopResources();

		// Custom configuration of timeouts.
		final HttpClient httpClient = HttpClient.create(connectionProvider)
			.tcpConfiguration(tcpClient -> 
				tcpClient
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					.doOnConnected(conn -> 
						conn
							.replaceHandler(NettyPipeline.HttpCodec, new HttpClientCodec(4096, 30000, 8192, false))
					)
					.runOn(channelResources)
			)
			;
		
		final ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
		
		return WebClient.builder()
                .baseUrl("http://localhost:5001")
                .defaultHeader("User-Agent", "demo-web-client")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(clientHttpConnector)
                .filter((request, next) ->
                	next.exchange(request)
                		.doOnNext(clientResponse -> {
                			final HttpStatus httpStatus = clientResponse.statusCode();
                			if (httpStatus.isError()) {
                				logger.error("Received error response. [Status: {}]", httpStatus);                				
                				throw new CustomException("Custom exception was raised because of 4xx or 5xx status.");
                			}
                		})
                )
                .build()
                ;
	}
}
