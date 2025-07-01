
package acme.features.crewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.crewMember.AvailabilityStatus;
import acme.realms.crewMember.CrewMember;

@Repository
public interface FlightCMAssignmentRepository extends AbstractRepository {

	@Query("SELECT f from FlightAssignment f where f.id = ?1")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("SELECT fa from FlightAssignment fa where fa.leg.scheduledArrival < :currentMoment and fa.crewMember.id = :crewMemberId")
	Collection<FlightAssignment> findAllFAByCompletedLeg(Date currentMoment, int crewMemberId);

	@Query("SELECT fa from FlightAssignment fa where fa.leg.scheduledArrival >= :currentMoment and fa.crewMember.id = :crewMemberId")
	Collection<FlightAssignment> findAllFAByPlannedLeg(Date currentMoment, int crewMemberId);

	@Query("SELECT l FROM Leg l WHERE l.flight.draftMode = false")
	Collection<Leg> findAllLegs();

	@Query("SELECT l from Leg l where l.id = ?1")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l where l.draftMode = false")
	Collection<Leg> findAllLegsPublish();

	@Query("SELECT f.leg from FlightAssignment f where f.crewMember.id = ?1")
	Collection<Leg> findLegsByCrewMemberId(int id);

	@Query("SELECT fa.leg from FlightAssignment fa where fa.crewMember.id = :id")
	Collection<Leg> findLegsByCrewMember(int id);

	@Query("SELECT cm FROM CrewMember cm WHERE cm.id = ?1")
	CrewMember findCrewMemberById(int id);

	@Query("SELECT cm FROM CrewMember cm")
	Collection<CrewMember> findAllCrewMembers();

	@Query("SELECT cm from CrewMember cm where cm.availabilityStatus = :availabilityStatus")
	Collection<CrewMember> findCrewMembersByAvailability(AvailabilityStatus availabilityStatus);

	@Query("SELECT al from ActivityLog al where al.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int flightAssignmentId);

	@Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END FROM CrewMember cm WHERE cm.id = :id")
	boolean existsCrewMemberById(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.scheduledArrival < :currentMoment")
	boolean isFlightAssignmentAssociatedWithCompletedLeg(int id, Date currentMoment);

	@Query("SELECT count(fa) > 0 from FlightAssignment fa where fa.duty = :duty and fa.leg.id = :legId")
	boolean existsCrewMemberWithDutyInLeg(int legId, Duty duty);

	@Query("SELECT count(fa) > 0 from FlightAssignment fa where fa.id = :flightAssignmentId and fa.crewMember.id = :crewMemberId")
	boolean isFlightAssignmentOfCrewMember(int flightAssignmentId, int crewMemberId);

	@Query("SELECT CASE when count(l) > 0 THEN true else false END from Leg l where l.id = :id")
	boolean existsLeg(int id);

	@Query("SELECT case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :flightAssignmentId and fa.leg.scheduledArrival < :currentMoment")
	boolean isLegCompletedByFlightAssignment(int flightAssignmentId, Date currentMoment);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.id = :flightAssignmentId and fa.crewMember.id = :crewMemberId")
	boolean thatFlightAssignmentIsOf(int flightAssignmentId, int crewMemberId);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :flightAssignmentId and fa.leg.scheduledArrival < :currentMoment")
	boolean areLegsCompletedByFlightAssignament(int flightAssignmentId, Date currentMoment);

	@Query("SELECT CASE WHEN COUNT(fa) > 0 THEN true ELSE false END FROM FlightAssignment fa WHERE fa.id = :id")
	boolean existsFlightAssignment(int id);

}
