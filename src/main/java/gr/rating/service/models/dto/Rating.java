package gr.rating.service.models.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Rating {
	@DecimalMin("0.0")
	@DecimalMax("5.0")
	private double givenRating;

	@NotBlank
	private String ratedEntity;

	private String rater;

	private Date createdAt;
}
