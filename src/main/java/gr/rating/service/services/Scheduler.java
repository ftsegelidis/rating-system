package gr.rating.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("production")
@Component
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    RatingService ratingService;


   @Scheduled(fixedRateString = "${delete.old.ratings.time.interval}")
    public void runPeriodically() {

        try{

            ratingService.deleteOldRatings();

        }catch (Exception ex){
            logger.error("Could not delete ratings : " + ex.getMessage());
        }


    }
}
