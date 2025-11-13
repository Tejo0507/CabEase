package com.cabease.services;
import com.cabease.models.Cab;
import com.cabease.models.Driver;
import com.cabease.models.VehicleType;
import com.cabease.repository.CabRepository;
import com.cabease.repository.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
@Slf4j
public class CabService {
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private DriverRepository driverRepository;
    public List<Cab> findAllCabs() {
        return cabRepository.findAll();
    }
    public List<Cab> findAvailableCabs() {
        return cabRepository.findByAvailableTrue();
    }
    public List<Cab> findAvailableCabsByCapacity(Integer capacity) {
        return cabRepository.findAvailableCabsByCapacity(capacity);
    }
    public Optional<Cab> findById(Long id) {
        return cabRepository.findById(id);
    }
    public Cab saveCab(Cab cab) {
        if (cabRepository.existsByCabNumber(cab.getCabNumber())) {
            throw new RuntimeException("Cab number already exists");
        }
        log.info("Saving cab: {}", cab.getCabNumber());
        return cabRepository.save(cab);
    }
    public Cab updateCab(Cab cab) {
        log.info("Updating cab: {}", cab.getId());
        return cabRepository.save(cab);
    }
    public void deleteCab(Long id) {
        log.info("Deleting cab: {}", id);
        cabRepository.deleteById(id);
    }
    public Cab assignDriver(Long cabId, Long driverId) {
        Optional<Cab> cabOpt = cabRepository.findById(cabId);
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (cabOpt.isPresent() && driverOpt.isPresent()) {
            Cab cab = cabOpt.get();
            Driver driver = driverOpt.get();
            cab.setDriver(driver);
            log.info("Assigned driver {} to cab {}", driver.getName(), cab.getCabNumber());
            return cabRepository.save(cab);
        }
        throw new RuntimeException("Cab or Driver not found");
    }
    public Cab updateAvailability(Long cabId, Boolean available) {
        Optional<Cab> cabOpt = cabRepository.findById(cabId);
        if (cabOpt.isPresent()) {
            Cab cab = cabOpt.get();
            cab.setAvailable(available);
            log.info("Updated availability for cab {} to {}", cab.getCabNumber(), available);
            return cabRepository.save(cab);
        }
        throw new RuntimeException("Cab not found");
    }
    public Long countAvailableCabsByVehicleType(VehicleType vehicleType) {
        return cabRepository.countAvailableCabsByVehicleType(vehicleType);
    }
    public Long countAvailableCabsByVehicleTypeNearby(VehicleType vehicleType, BigDecimal latitude,
                                                     BigDecimal longitude, BigDecimal radiusKm) {
        List<Cab> nearbyCabs = cabRepository.findAvailableCabsByVehicleTypeNearby(
            vehicleType, latitude, longitude, radiusKm);
        return (long) nearbyCabs.size();
    }
    public List<Cab> findAvailableCabsNearby(BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) {
        return cabRepository.findAvailableCabsNearby(latitude, longitude, radiusKm);
    }
    public List<Cab> findByVehicleType(VehicleType vehicleType) {
        return cabRepository.findByVehicleType(vehicleType);
    }
    public List<Cab> findByVehicleTypeAndAvailableTrue(VehicleType vehicleType) {
        return cabRepository.findByVehicleTypeAndAvailableTrue(vehicleType);
    }
}
