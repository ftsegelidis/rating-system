package gr.rating.service.controller;

import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;
import gr.rating.service.services.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class RatingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RatingService ratingService;




    @Test
    void testCreateValidRating() throws Exception {

        Rating rating = new Rating();
        rating.setRatedEntity("entity");
        rating.setGivenRating(5.0);

        when(ratingService.saveRating(any(Rating.class))).thenReturn(rating);

        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ratedEntity\": \"entity\", \"givenRating\": 5.0}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ratedEntity").value(rating.getRatedEntity()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.givenRating").value(rating.getGivenRating()));
    }


    @Test
    void testCreateInValidRating() throws Exception {

        Rating rating = new Rating();
        rating.setRatedEntity("entity");
        rating.setGivenRating(5.5);


        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ratedEntity\": \"entity\", \"givenRating\": 5.5}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void testValidGetRating() throws Exception {

        RatedEntityResult ratedEntityResult = new RatedEntityResult();
        ratedEntityResult.setOverallRating(1.7);
        ratedEntityResult.setNoOfRatings(3);
        ratedEntityResult.setRatedEntityName("property_3742");

        mockMvc.perform(MockMvcRequestBuilders.get("/ratings")
                        .param("rated_entity", "property_3742")
                        .param("specificDate", "2020/11/04")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.overallRating").value(ratedEntityResult.getOverallRating()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.noOfRatings").value(ratedEntityResult.getNoOfRatings()));
    }


    @Test
    void testInvalidGetRating() throws Exception {



        mockMvc.perform(MockMvcRequestBuilders.get("/ratings")
                        .param("rated_entity", "property_xxx")
                        .param("specificDate", "2024/11/04")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}
