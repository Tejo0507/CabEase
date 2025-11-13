package com.cabease.repository;
import com.cabease.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    Optional<UserProfile> findByUserId(@Param("userId") Long userId);
    @Query("SELECT COUNT(up) > 0 FROM UserProfile up WHERE up.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
    @Query("SELECT up FROM UserProfile up WHERE up.membershipTier = :tier")
    List<UserProfile> findByMembershipTier(@Param("tier") String tier);
    @Query("SELECT up FROM UserProfile up WHERE LOWER(up.city) = LOWER(:city)")
    List<UserProfile> findByCity(@Param("city") String city);
    @Query("SELECT up FROM UserProfile up WHERE up.phoneNumber = :phone OR up.alternatePhone = :phone")
    Optional<UserProfile> findByPhoneNumber(@Param("phone") String phone);
    @Query("DELETE FROM UserProfile up WHERE up.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
