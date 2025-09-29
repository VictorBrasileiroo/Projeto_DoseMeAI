package com.dosemeai.DoseMeAI.controllers.medicine;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import com.dosemeai.DoseMeAI.services.medicine.MedicineService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medService;

    @GetMapping
    public ResponseEntity<List<MedicineModel>> getAllMedicines() {
        try {
            var response = medService.getAll();
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
