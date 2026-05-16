package com.korpochat.backend.repository;

import com.korpochat.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    List<Tag> findAllByNameIn(Collection<String> names);
    List<Tag> findAllByOrderByNameAsc();
}
