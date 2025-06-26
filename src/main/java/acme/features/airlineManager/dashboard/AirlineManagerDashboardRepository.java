
package acme.features.airlineManager.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@Repository
public interface AirlineManagerDashboardRepository extends AbstractRepository {

	@Query("SELECT a FROM AirlineManager a")
	Collection<AirlineManager> findAirlineManagers();

	@Query("SELECT a FROM AirlineManager a WHERE a.id = :id")
	AirlineManager findAirlineManagerById(int id);

	@Query("SELECT COUNT(a) + 1 FROM AirlineManager a WHERE a.yearsOfExperience > (SELECT b.yearsOfExperience FROM AirlineManager b WHERE b.id = :id)")
	int calculateRanking(int id);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.flight.manager.id = :id AND l.status = :status")
	int countLegsByStatus(int id, LegStatus status);

	@Query("SELECT l FROM Leg l WHERE l.flight.manager.id = :id")
	Collection<Leg> findLegsByManagerId(int id);

	@Query("SELECT DISTINCT f.cost.currency FROM Flight f WHERE f.manager.id = :id")
	List<String> findCurrenciesByManagerId(int id);

	@Query("SELECT avg(f.cost.amount) FROM Flight f WHERE f.cost.currency = :currency and f.manager.id = :id")
	Double avgFlightCostByCurrency(int id, String currency);

	@Query("SELECT min(f.cost.amount) FROM Flight f WHERE f.cost.currency = :currency and f.manager.id = :id")
	Double minFlightCostByCurrency(int id, String currency);

	@Query("SELECT max(f.cost.amount) FROM Flight f WHERE f.cost.currency = :currency and f.manager.id = :id")
	Double maxFlightCostByCurrency(int id, String currency);

	@Query("SELECT stddev(f.cost.amount) FROM Flight f WHERE f.cost.currency = :currency and f.manager.id = :id")
	Double devFlightCostByCurrency(int id, String currency);
}
