package lorenzohq.nexus.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.AssignTicketDTO;
import lorenzohq.nexus.dto.CreateTicketDTO;
import lorenzohq.nexus.dto.TicketDTO;
import lorenzohq.nexus.dto.UpdateTicketDTO;
import lorenzohq.nexus.entity.Role;
import lorenzohq.nexus.entity.Tickets;
import lorenzohq.nexus.entity.User;
import lorenzohq.nexus.exception.ApplicationException;
import lorenzohq.nexus.exception.ForbiddenException;
import lorenzohq.nexus.exception.NotFoundException;
import lorenzohq.nexus.repository.TicketRepository;
import lorenzohq.nexus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private TicketDTO toDTO(Tickets ticket) {
        return new TicketDTO(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUser().getId(),
                ticket.getUser().getName(),
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null,
                ticket.getCreatedAt()
        );
    }

    @Transactional
    public TicketDTO raiseTicket(@Valid CreateTicketDTO body, User currentUser) {
        Tickets ticket = new Tickets();
        ticket.setUser(currentUser);
        ticket.setSubject(body.subject());
        ticket.setDescription(body.description());

        Tickets savedTicket = ticketRepository.save(ticket);

        mailService.sendTicketCreatedEmail(savedTicket);

        return toDTO(savedTicket);
    }

    public List<TicketDTO> getMyTickets(User currentUser) {
        return ticketRepository.findByUser_Id(currentUser.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TicketDTO getTicketById(UUID ticketId, User currentUser) {
        Tickets ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        if (currentUser.getRole() != Role.ADMIN && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have access to this ticket");
        }

        return toDTO(ticket);
    }

    @Transactional
    public TicketDTO updateTicket(UUID ticketId, @Valid UpdateTicketDTO body) {
        Tickets ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        if (body.status() != null) {
            ticket.setStatus(body.status());
        }
        if (body.priority() != null) {
            ticket.setPriority(body.priority());
        }

        Tickets savedTicket = ticketRepository.save(ticket);
        return toDTO(savedTicket);
    }

    @Transactional
    public TicketDTO assignTicket(UUID ticketId, @Valid AssignTicketDTO body) {
        Tickets ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        User agent = userRepository.findById(body.agentId())
                .orElseThrow(() -> new NotFoundException("Agent not found"));

        if (agent.getRole() != Role.ADMIN) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Ticket can only be assigned to an admin user");
        }

        ticket.setAssignedTo(agent);
        Tickets savedTicket = ticketRepository.save(ticket);
        return toDTO(savedTicket);
    }

    @Transactional
    public void deleteTicket(UUID ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new NotFoundException("Ticket not found");
        }
        ticketRepository.deleteById(ticketId);
    }
}
