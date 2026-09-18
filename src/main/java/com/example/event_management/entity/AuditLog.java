package com.example.event_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long eventId;

    private String eventName;

    private String action;

    private LocalDateTime timestamp;

    protected AuditLog() {
    }

    public AuditLog(
            Long eventId,
            String eventName,
            String action,
            LocalDateTime timestamp) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}