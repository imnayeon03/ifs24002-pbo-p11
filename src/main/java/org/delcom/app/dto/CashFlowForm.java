package org.delcom.app.dto;

import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CashFlowForm {

    private UUID id;

    @NotBlank(message = "Tipe harus dipilih")
    private String type;

    @NotBlank(message = "Sumber dana harus dipilih")
    private String source;

    @NotBlank(message = "Label harus diisi")
    private String label;

    @NotNull(message = "Nominal harus diisi")
    @Min(value = 1, message = "Nominal harus lebih dari 0")
    private Integer amount;

    private String description;
    
    // Untuk validasi hapus
    private String confirmLabel;

    public CashFlowForm() {
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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
    
    public String getConfirmLabel() { return confirmLabel; }
    public void setConfirmLabel(String confirmLabel) { this.confirmLabel = confirmLabel; }
}