package com.dosemeai.DoseMeAI.domain.reminders;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import com.dosemeai.DoseMeAI.domain.users.UserModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "reminders")
public class ReminderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_reminder"))
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "id_medicine", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_medicine_reminder"))
    private MedicineModel medicine;

    private String message;
    private LocalDateTime remindAt;
    private boolean sent;
}
