package gr.rating.service.controllers;

import gr.rating.service.exception.ErrorMessage;
import gr.rating.service.exception.RatingsValidationException;
import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;
import gr.rating.service.services.RatingService;
import gr.rating.service.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/ratings")
public class RatingsController {

	private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);
	private static final DateTimeFormatter SPECIFIC_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	private final RatingService ratingService;

	public RatingsController(RatingService ratingService) {
		this.ratingService = ratingService;
	}

	@Operation(summary = "Create a new rating", description = "Returns the newly created entity")
	@PostMapping
	public ResponseEntity<Rating> createRating(@Valid @RequestBody Rating rating) throws RatingsValidationException {

		ValidationUtil.validateRatingInput(rating);

		logger.debug("creating entity with values : {}", rating);
		return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.saveRating(rating));
	}

	@Operation(summary = "Retrieve the rating value", description = "Based on rated entity name and optional specific date")
	@GetMapping
	public ResponseEntity<RatedEntityResult> getRating(
			@RequestParam("rated_entity") String ratedEntity,
			@RequestParam(value = "specificDate", required = false) String specificDateString) {

		logger.debug("getting entity with name : {}", ratedEntity);

		Date date = resolveSpecificDate(specificDateString);

		RatedEntityResult ratedEntityResult = ratingService.calculateOverallRating(ratedEntity, date);

		if (ratedEntityResult == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(ratedEntityResult);
	}

	private static Date resolveSpecificDate(String specificDateString) {
		if (specificDateString == null || specificDateString.isEmpty()) {
			return Date.from(Instant.now());
		}
		try {
			LocalDate localDate = LocalDate.parse(specificDateString, SPECIFIC_DATE_FORMAT);
			return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} catch (DateTimeParseException e) {
			logger.warn("The specific date is not valid : {}", specificDateString, e);
			return Date.from(Instant.now());
		}
	}

	@ExceptionHandler(RatingsValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorMessage> handleRatingsValidation(RatingsValidationException ex) {
		ErrorMessage message = new ErrorMessage(
				HttpStatus.BAD_REQUEST.value(),
				new Date(),
				ex.getMessage());
		return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorMessage> handleBindException(BindException ex) {
		String detail = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.collect(Collectors.joining("; "));
		ErrorMessage message = new ErrorMessage(
				HttpStatus.BAD_REQUEST.value(),
				new Date(),
				detail.isEmpty() ? "Validation failed" : detail);
		return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
	}

}
