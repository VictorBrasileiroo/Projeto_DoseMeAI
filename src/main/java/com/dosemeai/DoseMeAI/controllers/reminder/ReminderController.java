package com.dosemeai.DoseMeAI.controllers.reminder;

import com.dosemeai.DoseMeAI.domain.reminders.ReminderDto;
import com.dosemeai.DoseMeAI.services.reminder.ReminderService;
import com.dosemeai.DoseMeAI.utils.ResponseModel;
import com.dosemeai.DoseMeAI.services.auth.JwtService;
import com.dosemeai.DoseMeAI.repositories.users.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("api/reminders")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final JwtService jwtService;
    private final IUserRepository userRepository;

    @GetMapping("/my")
    public ResponseModel<List<ReminderDto>> getMyReminders(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            List<ReminderDto> response = reminderService.getRemindersByUserEmail(email);
            return ResponseModel.ok(true, "My Reminders successfully listed", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }


    @GetMapping
    public ResponseModel<List<ReminderDto>> getAll() {
        try {
            List<ReminderDto> response = reminderService.getAllReminders();
            return ResponseModel.ok(true, "Reminders successfully listed", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }

    @PostMapping
    public ResponseModel<ReminderDto> create(@RequestBody ReminderDto dto, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            ReminderDto dtoWithUserEmail = new ReminderDto(
                dto.message(),
                dto.remindAt(),
                dto.sent(),
                email,
                dto.nameMedicine()
            );

            ReminderDto response = reminderService.saveReminder(dtoWithUserEmail);
            return ResponseModel.ok(true, "Successfully created", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseModel<ReminderDto> update(@PathVariable UUID id, @RequestBody ReminderDto dto, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            String userEmail = jwtService.extractEmail(token);

            ReminderDto existingReminder = reminderService.getReminderById(id);
            if (!existingReminder.emailUser().equals(userEmail)) {
                return ResponseModel.error(false, "Access denied: You can only update your own reminders");
            }

            ReminderDto response = reminderService.updateReminder(id, dto);
            return ResponseModel.ok(true, "Successfully changed", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseModel<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            String userEmail = jwtService.extractEmail(token);

            ReminderDto existingReminder = reminderService.getReminderById(id);
            if (!existingReminder.emailUser().equals(userEmail)) {
                return ResponseModel.error(false, "Access denied: You can only delete your own reminders");
            }

            reminderService.deleteReminder(id);
            return ResponseModel.ok(true, "Successfully removed", null);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }
}
