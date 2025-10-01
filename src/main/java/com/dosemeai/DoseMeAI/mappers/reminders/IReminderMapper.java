package com.dosemeai.DoseMeAI.mappers.reminders;

import com.dosemeai.DoseMeAI.domain.reminders.ReminderDto;
import com.dosemeai.DoseMeAI.domain.reminders.ReminderModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IReminderMapper {

    @Mapping(source = "user.email", target = "emailUser")
    @Mapping(source = "medicine.name", target = "nameMedicine")
    ReminderDto toDto(ReminderModel reminderModel);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    ReminderModel toModel(ReminderDto reminderDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateModelFromDto(ReminderDto reminderDto, @MappingTarget ReminderModel reminderModel);
}
