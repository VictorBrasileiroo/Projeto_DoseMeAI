package com.dosemeai.DoseMeAI.controllers.medicine;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoRequest;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoResponse;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import com.dosemeai.DoseMeAI.services.medicine.MedicineService;
import com.dosemeai.DoseMeAI.utils.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medService;

    @GetMapping
    public ResponseModel<List<MedicineModel>> getAllMedicines() {
        try {
            var response = medService.getAll();
            return ResponseModel.ok(true, "Medicines retrieved successfully", response);
        } catch (Exception e){
            return ResponseModel.error(false, "Failed to retrieve medicines: " + e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseModel<MedicineModel> getMedicineByName(@PathVariable String name) {
        try {
            var response = medService.findMedicineByName(name);
            return ResponseModel.ok(true, "Medicine found successfully", response);
        } catch (Exception e){
            return ResponseModel.error(false, "Failed to find medicine: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseModel<MedicineDtoResponse> addMedicine(@RequestBody MedicineDtoRequest medDtoRequest){
        try {
            var response = medService.createMedicine(medDtoRequest);
            return ResponseModel.ok(true, "Medicine created successfully", response);
        } catch (Exception e){
            return ResponseModel.error(false, "Failed to create medicine: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseModel<MedicineDtoResponse> removeMedicine(@PathVariable UUID id){
        try {
            var response = medService.removeMedicine(id);
            return ResponseModel.ok(true, "Medicine removed successfully", response);
        } catch (Exception e){
            return ResponseModel.error(false, "Failed to remove medicine: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseModel<MedicineDtoResponse> updateMedicine(@PathVariable UUID id, @RequestBody MedicineDtoRequest medDtoRequest){
        try {
            var response = medService.updateMedicine(id, medDtoRequest);
            return ResponseModel.ok(true, "Medicine updated successfully", response);
        } catch (Exception e){
            return ResponseModel.error(false, "Failed to update medicine: " + e.getMessage());
        }
    }
}
