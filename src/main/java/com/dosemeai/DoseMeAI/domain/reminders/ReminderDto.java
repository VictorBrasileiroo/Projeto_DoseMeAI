package com.dosemeai.DoseMeAI.domain.reminders;

public record ReminderDto(
        String message,
        String remindAt,
        boolean sent,
        String emailUser,
        String nameMedicine
) {
}
