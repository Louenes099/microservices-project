package com.islam.microservices.core.recommendation;

import com.islam.api.core.recommendation.Recommendation;
import com.islam.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.islam.microservices.core.recommendation.datalayer.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class RecommendationServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 113;
	private static final String PRODUCT_ID_INVALID_STRING = "not-integer";
	private static final int PRODUCT_ID_INVALID_NEGATIVE_VALUE = -1;
	private static final int RECOMMENDATION_ID = 1;


	@Autowired
	private WebTestClient client;

	@Autowired
	private RecommendationRepository repository;

	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getRecommendationsByProductId() {


		//add the recommendations
		RecommendationEntity entity1 = new RecommendationEntity(PRODUCT_ID_OKAY, 1, "author-1", 1, "content-1");
		repository.save(entity1);
		RecommendationEntity entity2 = new RecommendationEntity(PRODUCT_ID_OKAY, 2, "author-2", 1, "content-2");
		repository.save(entity2);
		RecommendationEntity entity3 = new RecommendationEntity(PRODUCT_ID_OKAY, 3, "author-3", 1, "content-3");
		repository.save(entity3);

		//make sure they are there
		assertEquals(3, repository.findByProductId(PRODUCT_ID_OKAY).size());


		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OKAY);


	}


	@Test
	public void createRecommendation() {

		Recommendation recommendation = new Recommendation(PRODUCT_ID_OKAY, RECOMMENDATION_ID, "author-1", 1, "content-1", "SA");

		client.post()
				.uri("/recommendation")
				.body(just(recommendation), Recommendation.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());

	}

	@Test
	public void deleteRecommendation() {

		RecommendationEntity entity = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_ID, "author-1", 1, "content-1");
		repository.save(entity);

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.delete()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody();

		assertEquals(0, repository.findByProductId(PRODUCT_ID_OKAY).size());


	}

	@Test
	public void getRecommendationsMissingParameter() {

		client.get()
				.uri("/recommendation")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
	}

	@Test
	public void getRecommendationInvalidParameter() {

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	public void getRecommendationsNotFound() {

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);

	}

	@Test
	public void getRecommendationsInvalidParameterNegativeValue() {

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_NEGATIVE_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID_NEGATIVE_VALUE);
	}


	@Test
	void contextLoads() {
	}
}