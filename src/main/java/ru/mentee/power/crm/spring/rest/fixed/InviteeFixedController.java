package ru.mentee.power.crm.spring.rest.fixed;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ЗАДАНИЕ: Найдите все проблемы в этом контроллере используя чек-лист. Ожидается найти минимум 10
 * проблем из разных категорий.
 */
@RestController
@RequiredArgsConstructor
public class InviteeFixedController {

  private final InviteeService inviteeService;

  @GetMapping("/invitees")
  public ResponseEntity<List<InviteeResponse>> getInvitees() {
    return ResponseEntity.ok(inviteeService.getAllInvitees());
  }

  @GetMapping("/invitees/{id}")
  public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    InviteeResponse inviteeResponse = inviteeService.getInviteeById(id);
    if (inviteeResponse == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(inviteeResponse);
  }

  @PostMapping("/invitees")
  public ResponseEntity<InviteeResponse> create(@RequestBody CreateInviteeRequest request) {
    InviteeResponse response = inviteeService.createInvitee(request);
    URI location = URI.create("/api/leads/" + response.id());
    return ResponseEntity.created(location).body(response);
  }

  @DeleteMapping("/invitees/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    boolean result = inviteeService.deleteInviteeById(id);
    if (result) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
