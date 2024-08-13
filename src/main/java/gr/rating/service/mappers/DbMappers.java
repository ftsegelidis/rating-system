package gr.rating.service.mappers;

import gr.rating.service.models.db.Rating;

import java.time.Instant;
import java.util.Date;

public final class DbMappers {

    private DbMappers() {
        throw new IllegalStateException("Utility class");
    }

    public static Rating fromDtoModel (gr.rating.service.models.dto.Rating rating) {
        if (rating == null) {
            return null;
        }

        var o = new Rating();

        o.setGivenRating(rating.getGivenRating());
        o.setRatedEntity(rating.getRatedEntity());
        o.setRater(rating.getRater());
        o.setCreatedAt(Date.from(Instant.now()));

        return o;
    }
}
