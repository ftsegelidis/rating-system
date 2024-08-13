package gr.rating.service.util;

import gr.rating.service.exception.RatingsValidationException;
import gr.rating.service.models.dto.Rating;

public class ValidationUtil {

    private ValidationUtil (){}

    /**
     * Validates the input based on some rules
     * @param rating
     * @throws RatingsValidationException
     */
    public static void validateRatingInput(Rating rating) throws RatingsValidationException {
        if(rating == null ||
                rating.getRatedEntity() == null ||
                rating.getRatedEntity().isEmpty() ||
                (rating.getGivenRating() < 0.0 || rating.getGivenRating() > 5.0) ||
                rating.getGivenRating() % 0.5 != 0) {

            throw new RatingsValidationException("You must provide a valid rating entity  with a given rating (0 to 5) ");

        }
    }
}
