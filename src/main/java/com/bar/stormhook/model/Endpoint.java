package com.bar.stormhook.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = {"tenant"})
@Entity
@Table(name = "endpoints",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_endpoint_tenant_url", columnNames = {"tenant_id", "url"})
        })
public class Endpoint {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_endpoint_tenant"))
    private Tenant tenant;

    @Column(nullable = false, columnDefinition = "text")
    private String url;

    @Builder.Default
    @Column(name = "timeout_ms", nullable = false)
    private Integer timeoutMs = 5000;

    @Builder.Default
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 5;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

