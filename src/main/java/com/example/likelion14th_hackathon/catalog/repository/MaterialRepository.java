package com.example.likelion14th_hackathon.catalog.repository;

import com.example.likelion14th_hackathon.catalog.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByMaterialName(String materialName);
}
