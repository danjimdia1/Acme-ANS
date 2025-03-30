
package acme.realms.crewMember;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CrewMemberRepository extends AbstractRepository {

	@Query("SELECT c FROM CrewMember c WHERE c.employeeCode = :employeeCode AND c.id != :crewMemberId")
	Optional<CrewMember> findByEmployeeCode(String employeeCode, int crewMemberId);

}
