package com.dosemeai.DoseMeAI.services.reminder;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import com.dosemeai.DoseMeAI.domain.reminders.ReminderDto;
import com.dosemeai.DoseMeAI.domain.reminders.ReminderModel;
import com.dosemeai.DoseMeAI.domain.users.UserModel;
import com.dosemeai.DoseMeAI.mappers.reminders.IReminderMapper;
import com.dosemeai.DoseMeAI.repositories.medicine.IMedicineRepository;
import com.dosemeai.DoseMeAI.repositories.reminders.IReminderRepository;
import com.dosemeai.DoseMeAI.repositories.users.IUserRepository;
import com.dosemeai.DoseMeAI.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ConditionalOnIssuerLocationJwtDecoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final IReminderMapper mapper;
    private final IReminderRepository repoReminder;
    private final EmailService emailService;
    private final IUserRepository repoUser;
    private final IMedicineRepository repoMedicine;

    @Scheduled(fixedDelay = 60_000)
    @ConditionalOnProperty(name = "reminder.scheduling.enabled", havingValue = "true")
    public void sendPendingReminders(){
        var now = LocalDateTime.now();
        List<ReminderModel> pendingReminders = repoReminder.findBySentFalseAndRemindAtBefore(now);

        if(pendingReminders.isEmpty()) return;

        for(ReminderModel reminder : pendingReminders){
            try{
                sendReminderNotification(reminder);
                reminder.setSent(true);
                repoReminder.save(reminder);
                System.out.println("Reminder sent successfully to: " + reminder.getUser().getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send reminder to: " + reminder.getUser().getEmail() +
                                 ". Error: " + e.getMessage());
                if (e.getMessage().contains("domain is not verified")) {
                    System.err.println("Domain not verified in Resend. Consider using a verified domain or mock email service for testing.");
                }
            }
        }
    }

    private void sendReminderNotification(ReminderModel reminder){
        String subject = "Reminder of Medicine " + reminder.getMedicine().getName();
        emailService.sendEmail(
                reminder.getUser().getEmail(),
                subject,
                "Olá " + reminder.getUser().getUsername() + ",\n\n" +
                        "Este é um lembrete para tomar seu medicamento.\n\n" +
                        "Informações do usuário:\n" +
                        "Nome: " + reminder.getUser().getUsername() + "\n" +
                        "Email: " + reminder.getUser().getEmail() + "\n\n" +
                        "Informações do remédio:\n" +
                        "Nome: " + reminder.getMedicine().getName() + "\n" +
                        "Descrição: " + reminder.getMedicine().getDescription() + "\n" +
                        "Dosagem: " + reminder.getMedicine().getDosage() + "\n" +
                        "Horário para tomar: " + reminder.getRemindAt() + "\n\n" +
                        "Mensagem adicional: " + reminder.getMessage()
        );
    }

    public ReminderDto saveReminder(ReminderDto reminderDto){
        ReminderModel reminderModel = mapper.toModel(reminderDto);

        UserModel user = repoUser.findByEmail(reminderDto.emailUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MedicineModel medicine = repoMedicine.findByName(reminderDto.nameMedicine())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        reminderModel.setUser(user);
        reminderModel.setMedicine(medicine);

        ReminderModel saved = repoReminder.save(reminderModel);
        return mapper.toDto(saved);
    }

    public List<ReminderDto> getAllReminders(){
        List<ReminderModel> models = repoReminder.findAll();
        return models.stream().map(mapper::toDto).toList();
    }

    public ReminderDto getReminderById(UUID id){
        ReminderModel model = repoReminder.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        return mapper.toDto(model);
    }

    public List<ReminderDto> getRemindersByUserEmail(String email){
        UserModel user = repoUser.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ReminderModel> models = repoReminder.findByUser(user);
        return models.stream().map(mapper::toDto).toList();
    }

    public Void deleteReminder(UUID id){
        ReminderModel reminder = repoReminder.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        repoReminder.delete(reminder);
        return null;
    }

    public ReminderDto updateReminder(UUID id, ReminderDto dto){
        ReminderModel reminder = repoReminder.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (dto.emailUser() != null && !dto.emailUser().equals(reminder.getUser().getEmail())) {
            UserModel user = repoUser.findByEmail(dto.emailUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            reminder.setUser(user);
        }

        if (dto.nameMedicine() != null && !dto.nameMedicine().equals(reminder.getMedicine().getName())) {
            MedicineModel medicine = repoMedicine.findByName(dto.nameMedicine())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));
            reminder.setMedicine(medicine);
        }

        mapper.updateModelFromDto(dto, reminder);

        ReminderModel updated = repoReminder.save(reminder);
        return mapper.toDto(updated);
    }
}

