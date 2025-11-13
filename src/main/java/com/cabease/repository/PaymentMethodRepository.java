package com.cabease.repository;
import com.cabease.entity.PaymentMethod;
import com.cabease.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserAndIsActiveTrue(User user);
    List<PaymentMethod> findByUser(User user);
    Optional<PaymentMethod> findByUserAndIsDefaultTrue(User user);
    Optional<PaymentMethod> findByUserAndMethodType(User user, String methodType);
    Long countByUser(User user);
}
