package gr.rating.service.controller;

import gr.rating.service.controllers.RatingsController;
import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;
import gr.rating.service.services.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingsController.class)
class RatingsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RatingService ratingService;

	@Test
	void testCreateValidRating() throws Exception {

		Rating rating = new Rating();
		rating.setRatedEntity("entity");
		rating.setGivenRating(5.0);

		when(ratingService.saveRating(any(Rating.class))).thenReturn(rating);

		mockMvc.perform(post("/ratings")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"ratedEntity\": \"entity\", \"givenRating\": 5.0}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ratedEntity").value(rating.getRatedEntity()))
				.andExpect(jsonPath("$.givenRating").value(rating.getGivenRating()));
	}

	@Test
	void testCreateInValidRating() throws Exception {

		mockMvc.perform(post("/ratings")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"ratedEntity\": \"entity\", \"givenRating\": 5.5}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testValidGetRating() throws Exception {

		RatedEntityResult ratedEntityResult = new RatedEntityResult();
		ratedEntityResult.setOverallRating(1.7);
		ratedEntityResult.setNoOfRatings(3);
		ratedEntityResult.setRatedEntityName("property_3742");

		when(ratingService.calculateOverallRating(eq("property_3742"), any(Date.class)))
				.thenReturn(ratedEntityResult);

		mockMvc.perform(get("/ratings")
						.param("rated_entity", "property_3742")
						.param("specificDate", "2020/11/04")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.overallRating").value(ratedEntityResult.getOverallRating()))
				.andExpect(jsonPath("$.noOfRatings").value(ratedEntityResult.getNoOfRatings()));
	}

	@Test
	void testInvalidGetRating() throws Exception {

		when(ratingService.calculateOverallRating(eq("property_xxx"), any(Date.class)))
				.thenReturn(null);

		mockMvc.perform(get("/ratings")
						.param("rated_entity", "property_xxx")
						.param("specificDate", "2024/11/04")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

}
