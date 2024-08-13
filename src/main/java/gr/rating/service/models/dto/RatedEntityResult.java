package gr.rating.service.models.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatedEntityResult {

    private double overallRating;

    private int noOfRatings;

    private String calculationDate;

    private String ratedEntityName;
}
