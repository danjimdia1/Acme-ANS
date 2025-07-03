
package acme.features.crewMember.activityLog;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface FlightCMActivityLogRepository extends AbstractRepository {

	@Query("SELECT al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("SELECT al from ActivityLog al where al.flightAssignment.id = :id")
	Collection<ActivityLog> findActivityLogsByMasterId(int id);

	@Query("SELECT fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("SELECT al.flightAssignment from ActivityLog al where al.id = :id")
	FlightAssignment findFlightAssignmentByActivityLogId(int id);

	@Query("SELECT case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.leg.scheduledArrival < :currentMoment")
	boolean associatedWithCompletedLeg(int id, Date currentMoment);

	@Query("SELECT CASE when COUNT(cm) > 0 THEN true ELSE false END FROM CrewMember cm WHERE cm.id = :id")
	boolean existsCrewMember(int id);

	@Query("SELECT case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedByActivityLogId(int id);

	@Query("SELECT case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.scheduledArrival < :currentMoment")
	boolean flightAssignmentAssociatedWithCompletedLeg(int id, Date currentMoment);

	@Query("SELECT case when COUNT(al) > 0 then true ELSE false END FROM ActivityLog al WHERE al.id = :id")
	boolean existsActivityLog(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.leg.scheduledArrival < :currentMoment and fa.id = :flightAssignmentId")
	boolean isFlightAssignmentCompleted(Date currentMoment, int flightAssignmentId);

	@Query("SELECT case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedById(int id);

	@Query("SELECT count(al) > 0 from ActivityLog al where al.id = :activityLogId and al.flightAssignment.crewMember.id = :crewMemberId")
	boolean thatActivityLogIsOf(int activityLogId, int crewMemberId);

	@Query("SELECT case when COUNT(fa) > 0 THEN true ELSE false END FROM FlightAssignment fa WHERE fa.id = :id")
	boolean existsFlightAssignment(int id);

}
