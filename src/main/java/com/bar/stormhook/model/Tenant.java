package com.bar.stormhook.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = {"endpoints", "events"})
@Entity
@Table(name = "tenants",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tenant_api_key", columnNames = "api_key")
        })
public class Tenant {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    // In production, consider storing a hash instead of raw apiKey
    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    // In production, manage via a secrets manager / KMS
    @Column(name = "signing_secret", nullable = false)
    private String signingSecret;

    @Builder.Default
    @Column(name = "rate_limit_rps", nullable = false)
    private Integer rateLimitRps = 10;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endpoint> endpoints;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;
}

