package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);
        return cashFlowRepository.save(cashFlow);
    }

    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return cashFlowRepository.findByUserIdAndKeyword(userId, search);
        }
        return cashFlowRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public CashFlow getCashFlowById(UUID userId, UUID id) {
        return cashFlowRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    @Transactional
    public CashFlow updateCashFlow(UUID userId, UUID id, String type, String source, String label, Integer amount, String description) {
        CashFlow existing = getCashFlowById(userId, id);
        if (existing != null) {
            existing.setType(type);
            existing.setSource(source);
            existing.setLabel(label);
            existing.setAmount(amount);
            existing.setDescription(description);
            return cashFlowRepository.save(existing);
        }
        return null;
    }

    @Transactional
    public boolean deleteCashFlow(UUID userId, UUID id) {
        CashFlow existing = getCashFlowById(userId, id);
        if (existing != null) {
            cashFlowRepository.delete(existing);
            return true;
        }
        return false;
    }
    
    // Helper untuk ringkasan di UI
    public Long getTotalIncome(UUID userId) {
        Long total = cashFlowRepository.sumIncomeByUserId(userId);
        return total != null ? total : 0L;
    }

    public Long getTotalExpense(UUID userId) {
        Long total = cashFlowRepository.sumExpenseByUserId(userId);
        return total != null ? total : 0L;
    }
}