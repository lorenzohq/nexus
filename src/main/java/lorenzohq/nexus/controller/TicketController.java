package lorenzohq.nexus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.AssignTicketDTO;
import lorenzohq.nexus.dto.CreateTicketDTO;
import lorenzohq.nexus.dto.MessageDTO;
import lorenzohq.nexus.dto.TicketDTO;
import lorenzohq.nexus.dto.UpdateTicketDTO;
import lorenzohq.nexus.security.UserPrincipal;
import lorenzohq.nexus.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ticket")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketDTO> raiseTicket(
            @Valid @RequestBody CreateTicketDTO body,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.raiseTicket(body, principal.getUser()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketDTO>> getMyTickets(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ticketService.getMyTickets(principal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(ticketService.getTicketById(id, principal.getUser()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketDTO body
    ) {
        return ResponseEntity.ok(ticketService.updateTicket(id, body));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketDTO> assignTicket(
            @PathVariable UUID id,
            @Valid @RequestBody AssignTicketDTO body
    ) {
        return ResponseEntity.ok(ticketService.assignTicket(id, body));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(new MessageDTO("Ticket deleted"));
    }
}
