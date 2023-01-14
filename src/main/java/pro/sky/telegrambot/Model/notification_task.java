package pro.sky.telegrambot.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class notification_task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private LocalDateTime time;
    private String text;

    public Long getChatId() {
        return chatId;
    }

    public notification_task(Long chatId, LocalDateTime time, String text) {
        this.chatId = chatId;
        this.time = time;
        this.text = text;
    }


    public notification_task() {

    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
