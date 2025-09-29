package com.dosemeai.DoseMeAI.repositories.medicine;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IMedicineRepository extends JpaRepository<MedicineModel, UUID> {
    Optional<MedicineModel> findByName(String name);
}
