package gr.rating.service.services;

import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;

import java.text.ParseException;
import java.util.Date;

public interface RatingService {

    RatedEntityResult calculateOverallRating(String ratedEntity, Date specificDate) throws ParseException;

    Rating saveRating(Rating rating);

    void deleteOldRatings();
}
