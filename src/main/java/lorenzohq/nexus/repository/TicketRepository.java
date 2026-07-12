package lorenzohq.nexus.repository;

import lorenzohq.nexus.entity.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Tickets, UUID> {
    List<Tickets> findByUser_Id(UUID userId);
}
