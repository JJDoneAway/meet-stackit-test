package de.hoehne.stackit.test.randoms;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class RandomPersonCreator {

	@Value("${meet_stackit.service.name}")
	private String serviceURL;

	@Scheduled(fixedDelay = 30_000, initialDelay = 1_000)
	void creatPersonFier() {

		try {
			ExecutorService pool = Executors.newFixedThreadPool(50);
			List<Callable<String>> callables = new ArrayList<>();
			for(int i = 0; i < 500; i++) {
				callables.add(new Callable<String>() {

					@Override
					public String call() throws Exception {
						log.debug("start to creat a new person");
						return fire(serviceURL);
					}
				});
			}
			pool.invokeAll(callables);
		} catch (Exception e) {
			log.error("Got System Exception while creating demo persons", e);
		}
	}

	private static String fire(String url) {

		String name = RandomStringUtils.randomAlphabetic(5, 15);
		URI uri = URI.create("%s/person?name=%s".formatted(url, name));
		return WebClient.create().post().uri(uri).exchangeToMono(response -> {
			if (response.statusCode().equals(HttpStatus.OK)) {
				log.info("Created new person {}", name);
				return response.bodyToMono(String.class);
			} else if (response.statusCode().is4xxClientError()) {
				log.warn("Got Client Error while creating a new person {}", name);
				return Mono.just("Error response");
			} else if (response.statusCode().is5xxServerError()) {
				log.warn("Got Server Error while creating a new person {}", name);
				return Mono.just("Error response");
			} else {
				log.error("Got unexpected Error while creating new person {}", name);
				return response.createException().flatMap(Mono::error);
			}
		}).block();

	}
}
