package org.delcom.app.repositories;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {

    @Query("SELECT c FROM CashFlow c WHERE c.userId = :userId AND (lower(c.label) LIKE lower(concat('%', :keyword, '%')) OR lower(c.description) LIKE lower(concat('%', :keyword, '%'))) ORDER BY c.createdAt DESC")
    List<CashFlow> findByUserIdAndKeyword(@Param("userId") UUID userId, @Param("keyword") String keyword);

    List<CashFlow> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<CashFlow> findByIdAndUserId(UUID id, UUID userId);
    
    // Untuk menghitung total (Opsional tapi berguna untuk UI)
    @Query("SELECT SUM(c.amount) FROM CashFlow c WHERE c.userId = :userId AND c.type = 'PEMASUKAN'")
    Long sumIncomeByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(c.amount) FROM CashFlow c WHERE c.userId = :userId AND c.type = 'PENGELUARAN'")
    Long sumExpenseByUserId(@Param("userId") UUID userId);
}