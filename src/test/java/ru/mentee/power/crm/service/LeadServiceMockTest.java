package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceMockTest {

    @Mock
    private LeadRepository mockRepository;

    private LeadService service;

    @BeforeEach
    void setUp() {
        service = new LeadService(mockRepository);
    }

    @Test
    void shouldCallRepositorySave_whenAddingNewLead() {
        // Given: Repository возвращает пустой Optional (email уникален)
        when(mockRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // When: настраиваем save чтобы возвращал переданный Lead
        when(mockRepository.save(any(Lead.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: вызываем бизнес-метод
        Lead result = service.addLead("new@example.com", "Company", "NEW");

        // Then: проверяем что Repository.save() был вызван ровно 1 раз
        verify(mockRepository, times(1)).save(any(Lead.class));

        // Then: проверяем результат
        assertThat(result.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldNotCallSave_whenEmailExists() {
        // Given: Repository возвращает существующий Lead
        Lead existingLead = new Lead(
                UUID.randomUUID(),
                "existing@example.com",
                "Existing Company",
                "CONVERTED",
                LocalDateTime.now()
        );
        when(mockRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingLead));

        // When/Then: ожидаем исключение
        assertThatThrownBy(() ->
                service.addLead("existing@example.com", "New Company", "NEW")
        ).isInstanceOf(IllegalStateException.class);

        // Then: save() НЕ должен быть вызван
        verify(mockRepository, never()).save(any(Lead.class));
    }

    @Test
    void shouldCallFindByEmail_beforeSave() {
        // Given
        when(mockRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(mockRepository.save(any(Lead.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.addLead("test@example.com", "Company", "NEW");

        // Then: проверяем порядок вызовов
        var inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findByEmail("test@example.com");
        inOrder.verify(mockRepository).save(any(Lead.class));
    }
}