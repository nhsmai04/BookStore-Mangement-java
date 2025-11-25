package org.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_request_items")
@Data
public class BorrowRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_request_id", nullable = false)
    private BorrowRequest borrowRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_version_id", nullable = false)
    private BookVersion bookVersion;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "day_return")
    private LocalDateTime dayReturn;

    @Column(name = "day_expired", nullable = false)
    private LocalDateTime dayExpired;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
