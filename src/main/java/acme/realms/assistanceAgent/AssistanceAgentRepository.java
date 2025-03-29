
package acme.realms.assistanceAgent;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentRepository extends AbstractRepository {

	@Query("SELECT a FROM AssistanceAgent a WHERE a.employeeCode = :code AND a.id != :assistanceAgentId")
	Optional<AssistanceAgent> findByCode(String code, int assistanceAgentId);

}
