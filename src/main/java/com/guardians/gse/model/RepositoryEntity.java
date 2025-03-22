package com.guardians.gse.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Table(name = "repositories", indexes = { @Index(name = "idx_language", columnList = "language") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryEntity {

    @Id
    private Integer id; // GitHub Repository ID
    private String name;
    @Column(name = "full_name", length = 512)
    private String fullName;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_name")
    private String ownerName;

    private String language;
    private int stars;

    private int forks;
    @Column(name = "url", length = 512)
    private String url;

    @Column(name = "last_updated")
    private Instant lastUpdated;
}
