package com.example.likelion14th_hackathon.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_infos")
public class MaterialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @Column(name = "care_summary", columnDefinition = "TEXT")
    private String careSummary;

    @Column(name = "cleaning_method", columnDefinition = "TEXT")
    private String cleaningMethod;

    @Column(name = "storage_method", columnDefinition = "TEXT")
    private String storageMethod;

    @Column(name = "avoid_list", columnDefinition = "TEXT")
    private String avoidList;

    @Column(name = "water_warning", columnDefinition = "TEXT")
    private String waterWarning;

    @Column(name = "repair_recommendation", columnDefinition = "TEXT")
    private String repairRecommendation;

    protected MaterialInfo() {
    }

    public MaterialInfo(
            String materialName,
            String careSummary,
            String cleaningMethod,
            String storageMethod,
            String avoidList,
            String waterWarning,
            String repairRecommendation
    ) {
        this.materialName = materialName;
        this.careSummary = careSummary;
        this.cleaningMethod = cleaningMethod;
        this.storageMethod = storageMethod;
        this.avoidList = avoidList;
        this.waterWarning = waterWarning;
        this.repairRecommendation = repairRecommendation;
    }

    public Long getId() {
        return id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getCareSummary() {
        return careSummary;
    }

    public String getCleaningMethod() {
        return cleaningMethod;
    }

    public String getStorageMethod() {
        return storageMethod;
    }

    public String getAvoidList() {
        return avoidList;
    }

    public String getWaterWarning() {
        return waterWarning;
    }

    public String getRepairRecommendation() {
        return repairRecommendation;
    }
}
