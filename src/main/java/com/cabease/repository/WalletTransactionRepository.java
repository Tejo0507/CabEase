package com.cabease.repository;
import com.cabease.models.User;
import com.cabease.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<WalletTransaction> findTop10ByUserOrderByCreatedAtDesc(User user);
    Optional<WalletTransaction> findByTransactionId(String transactionId);
}
