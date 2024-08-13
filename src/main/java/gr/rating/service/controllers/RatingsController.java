package gr.rating.service.controllers;

import gr.rating.service.exception.ErrorMessage;
import gr.rating.service.exception.RatingsValidationException;
import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.models.dto.Rating;
import gr.rating.service.services.RatingService;
import gr.rating.service.util.ValidationUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(path = "/ratings")
public class RatingsController {

    private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);

    private final RatingService ratingService;

    public RatingsController (RatingService ratingService) {
        this.ratingService = ratingService;
    }


    @ApiOperation(value ="Create a new rating" , notes = "Return the newly created entity")
    @PostMapping
    public ResponseEntity<Rating> createRating (@Valid @RequestBody Rating rating) throws RatingsValidationException {

        //Perform validations
        ValidationUtil.validateRatingInput(rating);

        logger.debug("creating entity with values : " + rating.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.saveRating(rating));
    }

    @ApiOperation(value ="Retrieves the rating value" , notes = "Based on rated entity name and (optional ) specific date")
    @GetMapping
    public ResponseEntity<RatedEntityResult> getRating (@RequestParam("rated_entity") String ratedEntity,@RequestParam(value = "specificDate",required=false) String specificDateString) throws ParseException {
        logger.debug("getting entity with name : " + ratedEntity);

        Date date = null;
        if(specificDateString == null || specificDateString.isEmpty()){
            //today
            date = new Date();
        }else{
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                date = format.parse(specificDateString);
           
            } catch (ParseException e) {
                logger.error("The specific date is not valid : " + e.getMessage());
                date = new Date();
            }
        }

      

        RatedEntityResult ratedEntityResult = ratingService.calculateOverallRating(ratedEntity,date);

        if(ratedEntityResult == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ratedEntityResult);
    }


    @ExceptionHandler({ RatingsValidationException.class ,BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(
            RatingsValidationException ex) {

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

    }


}
