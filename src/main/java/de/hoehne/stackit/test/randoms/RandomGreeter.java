package de.hoehne.stackit.test.randoms;

import java.net.URI;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@EnableScheduling
@Slf4j
public class RandomGreeter{
	
	private Random random = new Random();

	@Value("${meet_stackit.service.name}")
	private String serviceURL;

	@Scheduled(fixedDelay = 1_000, initialDelay = 1_000)
	void greet() {
		long waiting = random.nextLong(1000);
		try {
			Thread.sleep(waiting);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		URI uri = URI.create("%s/hello?name=%s".formatted(serviceURL, RandomStringUtils.randomAlphabetic(5, 15)));
		try {
			WebClient.create().get().uri(uri)
					.exchangeToMono(response -> {
						if (response.statusCode().equals(HttpStatus.OK)) {
							log.info("Triggered new greeting event after {} ms on {}", waiting, uri);
							return response.bodyToMono(String.class);
						} else if (response.statusCode().is4xxClientError()) {
							log.warn("Got Client Error while calling greeting service after {} ms on {}", waiting, uri);
							return Mono.just("Error response");
						} else if (response.statusCode().is5xxServerError()) {
							log.warn("Got Server Error while calling greeting service after {} ms on {}", waiting, uri);
							return Mono.just("Error response");
						} else {
							log.error("Got unexpected Error while calling greeting service after {} ms on {}", waiting, uri);
							return response.createException().flatMap(Mono::error);
						}
					}).block();

		} catch (Exception e) {
			log.error("Got System Exception while calling demo service on " + uri, e);
		}
	}
}
