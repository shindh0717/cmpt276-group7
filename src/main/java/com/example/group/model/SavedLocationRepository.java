package com.example.group.model;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SavedLocationRepository  extends JpaRepository<SavedLocation, Long>{
    
}
