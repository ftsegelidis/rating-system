package gr.rating.service;

import gr.rating.service.controllers.RatingsController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("local")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class ServiceApplicationTests {

	@Autowired
	private RatingsController ratingsController;

	@Test
	void contextLoads() {
		assertThat(ratingsController).isNotNull();
	}

}
