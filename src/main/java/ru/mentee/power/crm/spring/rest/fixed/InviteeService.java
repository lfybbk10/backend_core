package ru.mentee.power.crm.spring.rest.fixed;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteeService {
  private final InviteeRepository inviteeRepository;
  private final InviteeMapper inviteeMapper;

  public List<InviteeResponse> getAllInvitees() {
    return inviteeRepository.findAll().stream()
        .map(inviteeMapper::toResponse)
        .collect(Collectors.toList());
  }

  public InviteeResponse getInviteeById(UUID id) {
    return inviteeRepository.findById(id).map(inviteeMapper::toResponse).orElse(null);
  }

  public InviteeResponse createInvitee(CreateInviteeRequest request) {
    Invitee invitee = inviteeMapper.toEntity(request);
    inviteeRepository.save(invitee);
    return inviteeMapper.toResponse(invitee);
  }

  public boolean deleteInviteeById(UUID id) {
    if (inviteeRepository.existsById(id)) {
      inviteeRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
