package com.korpochat.backend.service;

import com.korpochat.backend.entity.Tag;
import com.korpochat.backend.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final ChannelService channelService;

    public TagService(TagRepository tagRepository, ChannelService channelService) {
        this.tagRepository = tagRepository;
        this.channelService = channelService;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Tag createTag(String rawName) {
        String normalized = normalizeTagName(rawName);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Tag name nie może być pusty");
        }
        if (tagRepository.existsByName(normalized)) {
            throw new IllegalArgumentException("Tag już istnieje: " + normalized);
        }

        Tag tag = new Tag();
        tag.setName(normalized);
        tag.setDisplayName(rawName.trim());
        Tag saved = tagRepository.save(tag);

        channelService.createChannelIfMissing("#" + normalized,
                "Kanał dla tagu " + saved.getDisplayName(),
                "PUBLIC");

        return saved;
    }

    @Transactional
    public Tag createTagIfMissing(String rawName) {
        String normalized = normalizeTagName(rawName);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Tag name nie może być pusty");
        }
        return tagRepository.findByName(normalized).orElseGet(() -> createTag(rawName));
    }

    public String validateAndFormatTags(Collection<String> rawTags) {
        if (rawTags == null || rawTags.isEmpty()) {
            return null;
        }

        Set<String> normalized = rawTags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> normalizeTagName(tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (normalized.isEmpty()) {
            return null;
        }

        List<Tag> existing = tagRepository.findAllByNameIn(normalized);
        Set<String> existingNames = existing.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<String> unknown = normalized.stream()
                .filter(tag -> !existingNames.contains(tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!unknown.isEmpty()) {
            throw new IllegalArgumentException("Nieznane tagi: " + String.join(", ", unknown));
        }

        return String.join(",", normalized);
    }

    public Set<String> normalizeTagNames(Collection<String> rawTags) {
        if (rawTags == null) {
            return Set.of();
        }
        return rawTags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(this::normalizeTagName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeTagName(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}
