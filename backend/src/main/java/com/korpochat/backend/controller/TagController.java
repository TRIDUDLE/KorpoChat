package com.korpochat.backend.controller;

import com.korpochat.backend.entity.Tag;
import com.korpochat.backend.service.TagService;
import com.korpochat.backend.dto.TagRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody TagRequest request) {
        Tag created = tagService.createTag(request.getName());
        return ResponseEntity.ok(created);
    }
}
