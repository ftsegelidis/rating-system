package gr.rating.service.service;

import gr.rating.service.models.db.Rating;
import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.repositories.RatingsRepository;
import gr.rating.service.services.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTests {

	@Mock
	private RatingsRepository ratingsRepository;

	@InjectMocks
	private RatingServiceImpl ratingService;

	@Test
	void calculateIndividualRating() {

		var factor1 = ratingService.calculateBasedOnIndividual(5.0);
		var factor2 = ratingService.calculateBasedOnIndividual(4.0);
		var factor3 = ratingService.calculateBasedOnIndividual(0.0);

		assertThat(factor1).isEqualTo(1.0);
		assertThat(factor2).isEqualTo(0.8);
		assertThat(factor3).isEqualTo(0.0);

	}

	@Test
	void calculateAgeRating() {
		var factor1 = ratingService.calculateBasedOnAge(50);
		var factor2 = ratingService.calculateBasedOnAge(100);
		var factor3 = ratingService.calculateBasedOnAge(0);

		assertThat(factor1).isEqualTo(0.5);
		assertThat(factor2).isEqualTo(0.0);
		assertThat(factor3).isEqualTo(1.0);

	}

	@Test
	void calculateRaterRating() {
		var factor1 = ratingService.calculateBasedOnRater(null);
		var factor2 = ratingService.calculateBasedOnRater("rater1");

		assertThat(factor1).isEqualTo(0.1);
		assertThat(factor2).isEqualTo(1.0);

	}

	@Test
	void calculateOverallRatingWithNullRatedEntity() {

		String ratedEntity = null;

		given(ratingsRepository.findAllRatingsByRatedEntity(ratedEntity))
				.willReturn(Collections.emptyList());

		var ratingList = ratingService.calculateOverallRating(ratedEntity, new Date());

		assertThat(ratingList).isNull();
	}

	@Test
	void calculateOverallRatingWithAValidRatedEntity() throws Exception {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date asOfDate = df.parse("2024-04-15 12:00:00");

		List<Rating> ratings = new ArrayList<>();
		Rating rating1 = new Rating();
		rating1.setRatedEntity(null);
		rating1.setGivenRating(4.5);
		rating1.setCreatedAt(df.parse("2024-04-01 13:10:00"));

		ratings.add(rating1);

		Rating rating2 = new Rating();
		rating2.setRatedEntity("entity2");
		rating2.setGivenRating(3.5);
		rating2.setCreatedAt(df.parse("2024-04-01 14:00:00"));

		ratings.add(rating2);

		Rating rating3 = new Rating();
		rating3.setRatedEntity("entity3");
		rating3.setGivenRating(4);
		rating3.setCreatedAt(df.parse("2023-11-01 13:10:00"));

		given(ratingsRepository.findAllRatingsByRatedEntity("some_entity"))
				.willReturn(List.of(rating1, rating2, rating3));

		RatedEntityResult ratedEntityResult = ratingService.calculateOverallRating("some_entity", asOfDate);

		assertThat(ratedEntityResult).isNotNull();
		assertEquals(2, ratedEntityResult.getNoOfRatings());
	}

}
