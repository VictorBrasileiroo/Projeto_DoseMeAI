package com.dosemeai.DoseMeAI.controllers.reminder;

import com.dosemeai.DoseMeAI.domain.reminders.ReminderDto;
import com.dosemeai.DoseMeAI.services.reminder.ReminderService;
import com.dosemeai.DoseMeAI.utils.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("api/reminders")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;

    @GetMapping("/{id}")
    public ResponseModel<ReminderDto> getById(@PathVariable UUID id) {
        try {
            ReminderDto response = reminderService.getReminderById(id);
            return ResponseModel.ok(true, "Reminder listed Successfuly.", response);
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
    public ResponseModel<ReminderDto> create(@RequestBody ReminderDto dto) {
        try {
            ReminderDto response = reminderService.saveReminder(dto);
            return ResponseModel.ok(true, "Successfully created", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseModel<ReminderDto> update(@PathVariable UUID id, @RequestBody ReminderDto dto) {
        try {
            ReminderDto response = reminderService.updateReminder(id, dto);
            return ResponseModel.ok(true, "Successfully changed", response);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseModel<Void> delete(@PathVariable UUID id) {
        try {
            reminderService.deleteReminder(id);
            return ResponseModel.ok(true, "Successfully removed", null);
        } catch (Exception e) {
            return ResponseModel.error(false, e.getMessage());
        }
    }
}
