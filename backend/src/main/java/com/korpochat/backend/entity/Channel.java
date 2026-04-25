package com.korpochat.backend.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    @Column(name = "channel_type", nullable = false, length = 20)
    private String channelType = "PUBLIC"; // lub "PRIVATE"

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    // Mapowanie tabeli channel_members
    @ManyToMany
    @JoinTable(
            name = "channel_members",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getChannelType() { return channelType; }


    public String getDescription() {
        return description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<User> getMembers() {
        return members;
    }

    // --- SETTERY ---

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    // Pomocnicza metoda do dodawania pojedynczego użytkownika (dobra praktyka przy Set)
    public void addMember(User user) {
        this.members.add(user);
    }
}