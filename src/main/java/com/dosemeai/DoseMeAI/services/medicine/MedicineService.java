package com.dosemeai.DoseMeAI.services.medicine;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoRequest;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoResponse;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import com.dosemeai.DoseMeAI.mappers.medicine.IMedicineMapper;
import com.dosemeai.DoseMeAI.repositories.medicine.IMedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final IMedicineMapper medMapper;
    private final IMedicineRepository medRepo;

    public List<MedicineModel> getAll() {
        return medRepo.findAll();
    }

    public MedicineModel findMedicineByName(String name){
        return medRepo.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
    }

    public MedicineModel findMedicineById(UUID id){
        return medRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
    }

    public MedicineDtoResponse createMedicine(MedicineDtoRequest dto){
        var medicine = medMapper.toModel(dto);
        var savedMedicine = medRepo.save(medicine);
        return medMapper.toDtoResponse(savedMedicine);
    }

    public MedicineDtoResponse removeMedicine(UUID id){
        var medicine = medRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));

        medRepo.delete(medicine);
        return medMapper.toDtoResponse(medicine);
    }

    public MedicineDtoResponse updateMedicine(UUID id, MedicineDtoRequest dto){
        var medicine = medRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));

        medMapper.updateModelFromDto(dto, medicine);
        var updatedMedicine = medRepo.save(medicine);
        return medMapper.toDtoResponse(updatedMedicine);
    }
}
