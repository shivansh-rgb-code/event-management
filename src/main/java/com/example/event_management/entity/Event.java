package com.example.event_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Start date and time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    private LocalDateTime endDateTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;

    protected Event() {
    }

    public Long getId() {
        return id;
    }

    public EventStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getVenue() {
        return venue;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}