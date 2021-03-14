package com.yaprofi.software_engineering.repository;

import com.yaprofi.software_engineering.model.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 14.03.2021
 */
public interface NotesRepository extends CrudRepository<Note, Long> {
    Optional<Note> findById(Long id);
    List<Note> findAll();
}
