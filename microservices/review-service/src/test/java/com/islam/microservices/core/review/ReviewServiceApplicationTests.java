package com.islam.microservices.core.review;

import com.islam.api.core.review.Review;
import com.islam.microservices.core.review.datalayer.ReviewEntity;
import com.islam.microservices.core.review.datalayer.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.datasource.url=jdbc:h2:mem:review-db"})
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests {

	private static final int PRODUCT_ID_OK = 1;
	private static final int REVIEWID = 1;
	@Autowired
	private ReviewRepository repository;

	@Autowired
	private WebTestClient client;

	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getReviewsByProductId() {
		int expectedNumReviews = 3;
		//add the product
		ReviewEntity entity1 = new ReviewEntity(PRODUCT_ID_OK, 1, "author-1", "subject-1", "content-1");
		repository.save(entity1);
		ReviewEntity entity2 = new ReviewEntity(PRODUCT_ID_OK, 2, "author-2", "subject-2", "content-2");
		repository.save(entity2);
		ReviewEntity entity3 = new ReviewEntity(PRODUCT_ID_OK, 3, "author-3", "subject-3", "content-3");
		repository.save(entity3);
		//make sure they are there
		assertEquals(3, repository.findByProductId(PRODUCT_ID_OK).size());
		client.get()
				.uri("/review?productId=" + PRODUCT_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedNumReviews)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OK);
	}
	@Test
	public void createReview() {
		// create the review
		//int productId, int reviewId, String author, String subject, String content, String serviceAddress
		Review review = new Review(PRODUCT_ID_OK, REVIEWID, "Author " + REVIEWID, "Subject " + REVIEWID, "Content " + REVIEWID, "SA");
		//execute the post
		client.post()
				.uri("/review")
				.body(just(review), Review.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
		//verify it's in the database
		assertEquals(1, repository.findByProductId(PRODUCT_ID_OK).size());
	}
	@Test
	public void deleteReviewsByProductId() {
		// create the review entity
		//int productId, int reviewId, String author, String subject, String content
		ReviewEntity entity = new ReviewEntity(PRODUCT_ID_OK, REVIEWID, "Author " + REVIEWID, "Subject " + REVIEWID, "Content ");
		//save it
		repository.save(entity);
		assertEquals(1, repository.findByProductId(PRODUCT_ID_OK).size());
		client.delete()
				.uri("/review?productId=" + PRODUCT_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody();
		assertEquals(0, repository.findByProductId(PRODUCT_ID_OK).size());
	}

}
