package ru.mentee.power.crm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class H2ConsoleTest {

  @Test
  @Disabled
  void keepAppRunning() throws Exception {
    System.out.println("Приложение запущено. Открой http://localhost:8081/h2-console");

    Thread.sleep(10 * 60 * 1000); // 10 минут
  }
}
