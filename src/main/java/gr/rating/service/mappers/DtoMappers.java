package gr.rating.service.mappers;

import gr.rating.service.models.db.Rating;

public final class DtoMappers {


    private DtoMappers() {
        throw new IllegalStateException("Utility class");
    }

    public static gr.rating.service.models.dto.Rating fromDbModel (Rating rating) {
        if (rating == null) {
            return null;
        }

        var o = new gr.rating.service.models.dto.Rating();

        o.setCreatedAt(rating.getCreatedAt());
        o.setGivenRating(rating.getGivenRating());
        o.setRatedEntity(rating.getRatedEntity());
        o.setRater(rating.getRater());

        return o;
    }
}
