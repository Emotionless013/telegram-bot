package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.Model.notification_task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface notificationRepository extends JpaRepository<notification_task, Long> {
    List<notification_task> findAllByTime(LocalDateTime time);

    @Scheduled(cron = "0 0/1 * * * *")
    default List<notification_task> findAllByCurrentTime(){
        return findAllByTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    };
}
