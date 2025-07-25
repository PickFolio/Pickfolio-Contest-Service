package com.pickfolio.contest.domain.model;

import com.pickfolio.contest.constant.ContestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "contests")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestStatus status;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column(unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal virtualBudget;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false, updatable = false)
    private UUID creatorId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}