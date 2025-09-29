package com.dosemeai.DoseMeAI.mappers.medicine;

import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoRequest;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineDtoResponse;
import com.dosemeai.DoseMeAI.domain.medicine.MedicineModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IMedicineMapper {
    MedicineModel toModel(MedicineDtoRequest medicineDtoRequest);
    MedicineDtoRequest toDtoRequest(MedicineModel medicineModel);
    MedicineDtoResponse toDtoResponse(MedicineModel medicineModel);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(MedicineDtoRequest medicineDtoRequest, @MappingTarget MedicineModel medicineModel);
}
