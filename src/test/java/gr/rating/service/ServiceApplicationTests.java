package gr.rating.service;

import gr.rating.service.controllers.RatingsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("local")
@SpringBootTest
class ServiceApplicationTests {

	@Autowired
	private RatingsController ratingsController;

	@Test
	void contextLoads() {
		assertThat(ratingsController).isNotNull();
	}

}
