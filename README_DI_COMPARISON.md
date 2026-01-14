# Сравнение: new внутри vs DI через конструктор
                            
## BAD: new InMemoryLeadRepository() внутри класса


```java
public class LeadService {
    // Тесная связанность!
    private final LeadRepository repository = new InMemoryLeadRepository();
}
```