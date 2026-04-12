package gr.rating.service.services;

import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;

import java.util.Date;

public interface RatingService {

	RatedEntityResult calculateOverallRating(String ratedEntity, Date specificDate);

	Rating saveRating(Rating rating);

	void deleteOldRatings();
}
