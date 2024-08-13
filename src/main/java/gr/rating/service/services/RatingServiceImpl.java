package gr.rating.service.services;

import gr.rating.service.mappers.DbMappers;
import gr.rating.service.mappers.DtoMappers;
import gr.rating.service.models.db.Rating;
import gr.rating.service.models.dto.RatedEntityResult;
import gr.rating.service.repositories.RatingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class RatingServiceImpl implements  RatingService{

    private final RatingsRepository ratingsRepository;
    private static final Map<Integer,Double> individualAgeRatingMap = new HashMap<>();
    private static final Map<Double,Double> individualRatingMap = new HashMap<>();

    private static final int INITIAL_WEIGHTED_RATING_VALUE = 100;
    private static final int MAX_RATING_DAYS = 100;
    private static final int ONE_HUNDRED = 100;
    private static final int FIFTY = 50;
    private static final int TWENTY = 20;


    @Value("${days.to.delete.ratings.threshold}")
    private int daysToDeleteRating;

    public RatingServiceImpl(RatingsRepository ratingsRepository){
        this.ratingsRepository = ratingsRepository;
    }



    /**
     *  Calculates the overall rating of a rated entity
     * @param ratedEntity
     * @return
     * @throws ParseException
     */
    @Cacheable(value = "overallRatingCache", key = "#ratedEntity.concat('-').concat(#specificDate.toString())")
    @Override
    public RatedEntityResult calculateOverallRating(String ratedEntity, Date specificDate) throws ParseException {

        //Get all available ratings for the specific entity name
        List<Rating> ratings =
                ratingsRepository.findAllRatingsByRatedEntity(ratedEntity);

        if(ratings.isEmpty()){
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        LocalDate localDateToday = specificDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        double overalRatingSum = 0.0;
        int validRatingsCounter = 0;

        //iterate through ratings
        for(Rating rating : ratings){

            LocalDate localDateCreatedAt = rating.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysBetween = Math.abs(ChronoUnit.DAYS.between(localDateToday, localDateCreatedAt));

            //Ignore dates longer than 100 days
            if(daysBetween > MAX_RATING_DAYS){
                break;
            }

            //get values from the three different calculation factors
            double individualRating = calculateBasedOnIndividual(rating.getGivenRating());
            double ageRating = calculateBasedOnAge((int) daysBetween);
            double raterRating = calculateBasedOnRater(rating.getRater());

            overalRatingSum += (INITIAL_WEIGHTED_RATING_VALUE * individualRating * ageRating * raterRating);
            validRatingsCounter ++;

        }

        //Construct the final object
        RatedEntityResult ratedEntityResult = new RatedEntityResult();

        //check divide by zero
        if(validRatingsCounter != 0){
            ratedEntityResult.setOverallRating(overalRatingSum / validRatingsCounter / TWENTY);
        }

        ratedEntityResult.setCalculationDate(dateFormat.format(specificDate));
        ratedEntityResult.setNoOfRatings(validRatingsCounter);
        ratedEntityResult.setRatedEntityName(ratedEntity);

        return ratedEntityResult;
    }

    @Override
    public gr.rating.service.models.dto.Rating saveRating(gr.rating.service.models.dto.Rating rating) {
        return  DtoMappers.fromDbModel(this.ratingsRepository.save(DbMappers.fromDtoModel(rating)));
    }

    @Override
    public void deleteOldRatings() {

        LocalDate thresholdDate = LocalDateTime.now().minusDays(daysToDeleteRating).toLocalDate();

        Date thresholdDateAsDate = Date.from(thresholdDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        ratingsRepository.deleteOldRatings(thresholdDateAsDate);
    }


    /**
     *
     * @param rater ( null if the rater is absent)
     * @return The factor value for the rater
     */
    public double calculateBasedOnRater(String rater){

        if(rater == null || rater.isEmpty()){
            return 0.1;
        }

        return 1.0;

    }

    /**
     * Calculates the factor values constructing a precalculated  map
     * @return the factor value
     */
    public  double calculateBasedOnIndividual (double givenRating){

        return  individualRatingMap.get(givenRating);
    }

    /**
     *  Calculates the factor values,in a precalculated  map, for the range (0-100)
     * @return the factor value
     */
    public double calculateBasedOnAge(int daysBetween){

        return  individualAgeRatingMap.get(daysBetween);
    }




//fill in the maps with the precalculated values
    static {

        //age factor
        double ratingValue;
        for(int i=0; i<=ONE_HUNDRED ; i++){

            if(i == 0){
                ratingValue = 1.0 ;
            }else if(i<=FIFTY){
                ratingValue = 1- ((i*0.5) / FIFTY );
            }else if (i<ONE_HUNDRED){
                ratingValue = (i*0.5) / FIFTY ;
            }else {
                ratingValue = 0.0;
            }

            BigDecimal bd =  BigDecimal.valueOf(ratingValue).setScale(2, RoundingMode.HALF_UP);
            individualAgeRatingMap.put( i, bd.doubleValue() );
        }


        //date factor
        //create a 0.5 step
        for(double i=0.0; i<=5.0 ; i = i+ 0.5){
            individualRatingMap.put( i, (i*2 / 10.0));
        }
    }

}
