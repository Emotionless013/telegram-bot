package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.Model.notification_task;
import pro.sky.telegrambot.repository.notificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final notificationRepository repository;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(notificationRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        String reg = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
        Pattern pattern = Pattern.compile(reg);

        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Long chatId = update.message().chat().id();
            if (update.message().text().equals("/start")) {
                SendMessage message = new SendMessage(chatId, "Hello!");
                SendResponse response = telegramBot.execute(message);
            }
            Matcher matcher = pattern.matcher(update.message().text());
            if (matcher.matches()) {
                String dateString = matcher.group(1);
                String item = matcher.group(3);
                LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                notification_task task = new notification_task(chatId, date, item);
                repository.save(task);
                SendMessage message = new SendMessage(chatId, "Saved");
                SendResponse response = telegramBot.execute(message);
            }
        // Process your updates here
    });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
}
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotification() {
        List<notification_task> base = repository.findAllByCurrentTime();
        for (int i = 0; i < base.size(); i++) {
            Long chatId = base.get(i).getChatId();
            String messageText = base.get(i).getText();
            SendMessage message = new SendMessage(chatId, messageText);
            SendResponse response = telegramBot.execute(message);
        }
    }

}
