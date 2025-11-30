package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-flows")
public class CashFlowController {

    private final CashFlowService cashFlowService;

    @Autowired
    protected AuthContext authContext;

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    // Menambahkan Cash Flow baru
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createCashFlow(@RequestBody CashFlow reqCashFlow) {

        // Validasi input manual (mirip TodoController)
        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Tipe tidak valid", null));
        } else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Label tidak valid", null));
        } else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Jumlah (Amount) harus lebih dari 0", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow newCashFlow = cashFlowService.createCashFlow(
                authUser.getId(),
                reqCashFlow.getType(),
                reqCashFlow.getSource(),
                reqCashFlow.getLabel(),
                reqCashFlow.getAmount(),
                reqCashFlow.getDescription()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil dibuat",
                Map.of("id", newCashFlow.getId())));
    }

    // Mendapatkan semua cash flow dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CashFlow>>>> getAllCashFlows(
            @RequestParam(required = false) String search) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Daftar cash flow berhasil diambil",
                Map.of("cashFlows", cashFlows)));
    }

    // Mendapatkan cash flow berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, CashFlow>>> getCashFlowById(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), id);
        if (cashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil diambil",
                Map.of("cashFlow", cashFlow)));
    }

    // Memperbarui cash flow berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashFlow>> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow reqCashFlow) {

        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Tipe tidak valid", null));
        } else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Label tidak valid", null));
        } else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Jumlah (Amount) harus lebih dari 0", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow updatedCashFlow = cashFlowService.updateCashFlow(
                authUser.getId(),
                id,
                reqCashFlow.getType(),
                reqCashFlow.getSource(),
                reqCashFlow.getLabel(),
                reqCashFlow.getAmount(),
                reqCashFlow.getDescription()
        );

        if (updatedCashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Data cash flow berhasil diperbarui", null));
    }

    // Menghapus cash flow berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCashFlow(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = cashFlowService.deleteCashFlow(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil dihapus",
                null));
    }
}