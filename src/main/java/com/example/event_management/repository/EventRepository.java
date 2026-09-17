package com.example.event_management.repository;

import com.example.event_management.entity.Event;
import com.example.event_management.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT e FROM Event e
        WHERE (:search IS NULL OR
               LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:venue IS NULL OR
             LOWER(e.venue) LIKE LOWER(CONCAT('%', :venue, '%')))
        AND (:status IS NULL OR e.status = :status)
        """)
    Page<Event> searchEvents(
            @Param("search") String search,
            @Param("venue") String venue,
            @Param("status") EventStatus status,
            Pageable pageable
    );
    boolean existsByNameIgnoreCaseAndStartDateTimeAndVenueIgnoreCase(
            String name,
            LocalDateTime startDateTime,
            String venue
    );
    boolean existsByNameIgnoreCaseAndStartDateTimeAndVenueIgnoreCaseAndIdNot(
            String name,
            LocalDateTime startDateTime,
            String venue,
            Long id
    );
}