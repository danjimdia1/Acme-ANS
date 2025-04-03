
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.task.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("SELECT t FROM Task t")
	Collection<Task> findAllTasks();

	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId ")
	Collection<Task> findAllTaskByTechnicianId(final int technicianId);

	@Query("SELECT t FROM Task t WHERE t.id = :id")
	Task findTaskById(int id);

	@Query("SELECT i FROM Involves i WHERE i.task.id = :id")
	Collection<Involves> findInvolvesByTaskId(int id);

}
