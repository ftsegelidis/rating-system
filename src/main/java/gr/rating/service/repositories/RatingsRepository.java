package gr.rating.service.repositories;

import gr.rating.service.models.db.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
public interface RatingsRepository extends JpaRepository<Rating, Long> {


    @Query(value ="SELECT * FROM RATINGS  WHERE RATED_ENTITY = :ratedEntity", nativeQuery = true)
    List<Rating> findAllRatingsByRatedEntity(@Param("ratedEntity") String ratedEntity);

    @Modifying
    @Query("delete from  Rating e where e.createdAt < :thresholdDate")
    void deleteOldRatings(Date thresholdDate);

}
