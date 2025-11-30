package org.delcom.app.views;

import java.util.UUID;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cash-flows")
public class CashFlowView {

    private final CashFlowService cashFlowService;

    public CashFlowView(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    // Helper untuk cek auth (sama seperti di TodoView)
    private User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken || authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @GetMapping
    public String listCashFlows(@RequestParam(required = false) String search, Model model) {
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";
        model.addAttribute("auth", authUser);

        // Ambil data list
        var cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        model.addAttribute("cashFlows", cashFlows);

        // Hitung total untuk dashboard
        Long totalIncome = cashFlowService.getTotalIncome(authUser.getId());
        Long totalExpense = cashFlowService.getTotalExpense(authUser.getId());
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", totalIncome - totalExpense);

        // Form untuk modal tambah
        model.addAttribute("cashFlowForm", new CashFlowForm());

        return ConstUtil.TEMPLATE_PAGES_CASHFLOW_INDEX;
    }

    @PostMapping("/add")
    public String postAddCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Data tidak valid: " + result.getFieldError().getDefaultMessage());
            redirectAttributes.addFlashAttribute("addModalOpen", true);
            return "redirect:/cash-flows";
        }

        CashFlow cf = cashFlowService.createCashFlow(
                authUser.getId(),
                form.getType(),
                form.getSource(),
                form.getLabel(),
                form.getAmount(),
                form.getDescription()
        );

        if (cf == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan data");
        } else {
            redirectAttributes.addFlashAttribute("success", "Data berhasil ditambahkan");
        }

        return "redirect:/cash-flows";
    }

    @PostMapping("/edit")
    public String postEditCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm form,
                                   RedirectAttributes redirectAttributes) {
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        if (form.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID tidak valid");
            return "redirect:/cash-flows";
        }

        CashFlow updated = cashFlowService.updateCashFlow(
                authUser.getId(),
                form.getId(),
                form.getType(),
                form.getSource(),
                form.getLabel(),
                form.getAmount(),
                form.getDescription()
        );

        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui data");
            redirectAttributes.addFlashAttribute("editModalOpen", true);
            redirectAttributes.addFlashAttribute("editModalId", form.getId());
        } else {
            redirectAttributes.addFlashAttribute("success", "Data berhasil diperbarui");
        }

        return "redirect:/cash-flows";
    }

    @PostMapping("/delete")
    public String postDeleteCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm form,
                                     RedirectAttributes redirectAttributes) {
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        if (form.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID tidak valid");
            return "redirect:/cash-flows";
        }

        // Cek data existing untuk validasi konfirmasi
        CashFlow existing = cashFlowService.getCashFlowById(authUser.getId(), form.getId());
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "Data tidak ditemukan");
            return "redirect:/cash-flows";
        }

        // Validasi konfirmasi label (mirip todo confirm title)
        if (form.getConfirmLabel() == null || !form.getConfirmLabel().equals(existing.getLabel())) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi Label tidak sesuai");
            redirectAttributes.addFlashAttribute("deleteModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteModalId", form.getId());
            return "redirect:/cash-flows";
        }

        boolean status = cashFlowService.deleteCashFlow(authUser.getId(), form.getId());
        if (status) {
            redirectAttributes.addFlashAttribute("success", "Data berhasil dihapus");
        } else {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus data");
        }

        return "redirect:/cash-flows";
    }
}