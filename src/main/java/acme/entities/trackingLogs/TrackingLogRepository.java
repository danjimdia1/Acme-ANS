
package acme.entities.trackingLogs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.creationMoment ASC")
	List<TrackingLog> findAllTrackingLogsByClaimId(int claimId);

	@Query("SELECT t.status FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.creationMoment DESC")
	List<TrackingLogStatus> findLastStatus(int claimId);

}
