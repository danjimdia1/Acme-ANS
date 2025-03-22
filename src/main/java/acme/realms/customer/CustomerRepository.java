
package acme.realms.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CustomerRepository extends AbstractRepository {

	@Query("SELECT c FROM Customer c WHERE c.identifier = :identifier AND c.id != :customerId")
	Optional<Customer> findByIdentifierAnNotCustomerId(String identifier, int customerId);
}
