/*package com.korpochat.backend.repository;

import com.korpochat.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    // Wyszukiwanie pojedynczego działu po jego nazwie
    Optional<Department> findByName(String name);

    // Wyszukiwanie wielu działów na podstawie listy nazw (tagów)
    List<Department> findByNameIn(List<String> names);
}*/