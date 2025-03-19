
package acme.entities.trackingLogs;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id != :currentId ORDER BY t.creationMoment DESC")
	Optional<TrackingLog> findLastTrackingLogByClaimId(@Param("claimId") int claimId, @Param("currentId") int currentId);

}
