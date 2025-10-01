package com.dosemeai.DoseMeAI.repositories.reminders;

import com.dosemeai.DoseMeAI.domain.reminders.ReminderModel;
import com.dosemeai.DoseMeAI.domain.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IReminderRepository extends JpaRepository<ReminderModel, UUID> {
    List<ReminderModel> findBySentFalseAndRemindAtBefore(LocalDateTime dateTime);
    List<ReminderModel> findByUser(UserModel user);
}
