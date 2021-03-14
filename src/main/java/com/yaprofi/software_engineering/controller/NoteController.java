package com.yaprofi.software_engineering.controller;

import com.yaprofi.software_engineering.model.Note;
import com.yaprofi.software_engineering.repository.NotesRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 14.03.2021
 */
@RestController
@RequestMapping("notes")
public class NoteController {

    public static final String ERROR = "Some error occured!";
    public static final String SUCCESS = "Success!";

    @Value("${note.title.length}")
    private Integer titleLength;

    @Autowired
    private NotesRepository notesRepository;

    @GetMapping(value = "/all")
    @ApiOperation(value = "Получение списка всех заметок",
            notes = "В ответе ожидается полный список заметок в формате JSON")
    public List<Note> list() {
        return notesRepository.findAll();
    }

    @GetMapping
    @ApiOperation(value = "Получение списка всех заметок, удовлетворяющих поисковому запросу",
            notes = "В ответе ожидается полный список заметок в формате JSON или список заметок, удовлетворяющих поисковому запросу в формате JSON")
    public List<Note> listByQuery(@RequestParam String query) {
        List<Note> all = notesRepository.findAll();
        if (query.isBlank()) {
            return all;
        }
        List<Note> res = new ArrayList<>();
        for (Note note : all) {
            if (!note.getTitle().isBlank() && note.getTitle().contains(query)
                    || !note.getContent().isBlank() && note.getContent().contains(query)) {
                res.add(note);
            }
        }
        return res;
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Получение заметки по ее идентификатору",
            notes = "ID необходимой заметки передается как path-параметр: например: /notes/1\n" +
                    "В ответе ожидается конкретная заметка в формате JSON")
    public Note getOne(@PathVariable Long id) {
        Optional<Note> res = notesRepository.findById(id);
        return res.orElse(null);
    }

    @PostMapping(produces = "application/json")
    @ApiOperation(value = "Добавление заметки с возможностью указания заголовка (title) и текста заметки (content)",
            notes = "В запросе передается body в формате JSON\n" +
                    "В ответе ожидается новая заметка в формате JSON")
    public Note create(@RequestBody Note newNote) {
        createTitle(newNote);
        notesRepository.save(newNote);
        return newNote;
    }

    private void createTitle(Note newNote) {
        if (newNote.getTitle().isBlank()) {
            newNote.setTitle(newNote.getContent().substring(0, titleLength));
        }
    }

    @PutMapping(value = "{id}", produces = "application/json")
    @ApiOperation(value = "Редактирование заметки по ее идентификатору",
            notes = "ID заметки, которую необходимо отредактировать, передается как path-параметр: например: /notes/1")
    public String update(@PathVariable Long id, @RequestBody Note newNote) {
        Optional<Note> editedNoteO = notesRepository.findById(id);
        if (editedNoteO.isPresent()) {
            Note editedNote = editedNoteO.get();
            editedNote.setTitle(newNote.getTitle());
            editedNote.setContent(newNote.getContent());
            notesRepository.save(editedNote);
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    @DeleteMapping(value = "{id}", produces = "application/json")
    @ApiOperation(value = "Удаление заметки по ее идентификатору",
            notes = "ID заметки, которую необходимо удалить, передается как path-параметр, например: /notes/1")
    public void delete(@PathVariable Long id) {
        notesRepository.deleteById(id);
    }
}
