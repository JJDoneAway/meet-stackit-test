package de.hoehne.stackit.test.randoms;

import java.net.URI;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@EnableScheduling
@Slf4j
public class RandomPersonPager {

	@Value("${meet_stackit.service.name}")
	private String serviceURL;

	private Pattern MY_PATTERN = Pattern.compile("\"size\":\\s*[0-9]+");

	private int currentPage = 0;

	@Scheduled(fixedDelay = 30_000, initialDelay = 1_000)
	@Async
	void pagePersons() {
		log.info("Start to page now.");
		try {
			String result = "";
			log.info("Query page {}", currentPage);
			result = fire(currentPage);

			Matcher m = MY_PATTERN.matcher(result);
			while (m.find()) {
				String s = m.group();
				log.info(s);
			}

			if (result.contains("\"empty\":false")) {
				currentPage = currentPage + 1;
			} else {
				currentPage = 0;
			}
		} catch (Exception e) {
			log.error("Got System Exception while creating demo persons", e);
		}
		log.info("Paging done. Go to sleep.");
	}

	private String fire(Integer page) {

		URI uri = URI.create("%s/person/page/%s".formatted(serviceURL, page));
		return WebClient.create().get().uri(uri).exchangeToMono(response -> {
			if (response.statusCode().equals(HttpStatus.OK)) {
				log.info("selected page {}", page);
				return response.bodyToMono(String.class);
			} else if (response.statusCode().is4xxClientError()) {
				log.warn("Got Client Error while selecting page {}, Reason is {}", page,
						response.statusCode().getReasonPhrase());
				return Mono.just("Error response");
			} else if (response.statusCode().is5xxServerError()) {
				log.warn("Got Server Error while selecting page {}, Reason is {}", page,
						response.statusCode().getReasonPhrase());
				return Mono.just("Error response");
			} else {
				log.error("Got unexpected Error while selecting page {}", page);
				return response.createException().flatMap(Mono::error);
			}
		}).retryWhen(Retry.backoff(3, Duration.ofSeconds(2))).block();

	}
}
