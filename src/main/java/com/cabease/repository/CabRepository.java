package com.cabease.repository;
import com.cabease.models.Cab;
import com.cabease.models.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface CabRepository extends JpaRepository<Cab, Long> {
    List<Cab> findByAvailableTrue();
    List<Cab> findByVehicleTypeAndAvailableTrue(VehicleType vehicleType);
    @Query("SELECT c FROM Cab c WHERE c.available = true AND c.capacity >= :capacity")
    List<Cab> findAvailableCabsByCapacity(@Param("capacity") Integer capacity);
    @Query("SELECT c FROM Cab c WHERE c.available = true AND c.vehicleType = :vehicleType AND c.capacity >= :capacity")
    List<Cab> findAvailableCabsByVehicleTypeAndCapacity(@Param("vehicleType") VehicleType vehicleType, @Param("capacity") Integer capacity);
    @Query("SELECT c FROM Cab c WHERE c.available = true AND " +
           "SQRT(POWER(c.currentLatitude - :lat, 2) + POWER(c.currentLongitude - :lng, 2)) <= :radius " +
           "ORDER BY SQRT(POWER(c.currentLatitude - :lat, 2) + POWER(c.currentLongitude - :lng, 2))")
    List<Cab> findAvailableCabsNearby(@Param("lat") BigDecimal latitude,
                                     @Param("lng") BigDecimal longitude,
                                     @Param("radius") BigDecimal radiusKm);
    @Query("SELECT c FROM Cab c WHERE c.available = true AND c.vehicleType = :vehicleType AND " +
           "SQRT(POWER(c.currentLatitude - :lat, 2) + POWER(c.currentLongitude - :lng, 2)) <= :radius " +
           "ORDER BY SQRT(POWER(c.currentLatitude - :lat, 2) + POWER(c.currentLongitude - :lng, 2))")
    List<Cab> findAvailableCabsByVehicleTypeNearby(@Param("vehicleType") VehicleType vehicleType,
                                                  @Param("lat") BigDecimal latitude,
                                                  @Param("lng") BigDecimal longitude,
                                                  @Param("radius") BigDecimal radiusKm);
    @Query("SELECT COUNT(c) FROM Cab c WHERE c.available = true AND c.vehicleType = :vehicleType")
    Long countAvailableCabsByVehicleType(@Param("vehicleType") VehicleType vehicleType);
    @Query("SELECT c FROM Cab c WHERE c.driver.id = :driverId")
    List<Cab> findByDriverId(@Param("driverId") Long driverId);
    List<Cab> findByVehicleType(VehicleType vehicleType);
    boolean existsByCabNumber(String cabNumber);
}
