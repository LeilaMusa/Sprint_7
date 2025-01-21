package api.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data // Автоматически генерирует геттеры, сеттеры, toString, equals и hashCode
@AllArgsConstructor // Генерирует конструктор со всеми полями
@NoArgsConstructor // Генерирует конструктор по умолчанию
public class Courier {
    private String login;
    private String password;
    private String firstName;
}