package org.delcom.app.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime; // Menggunakan LocalDateTime agar konsisten dengan Todo
import java.util.UUID;

@Entity
@Table(name = "cash_flows")
public class CashFlow {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", nullable = false)
    private String type; // PEMASUKAN / PENGELUARAN

    @Column(name = "source", nullable = false)
    private String source; // Cash / Bank / E-Wallet

    @Column(name = "label", nullable = false)
    private String label; // Gaji, Makan, Listrik, dll

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CashFlow() {}

    public CashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        this.userId = userId;
        this.type = type;
        this.source = source;
        this.label = label;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}