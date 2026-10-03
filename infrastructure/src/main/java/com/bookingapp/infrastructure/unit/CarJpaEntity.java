package com.bookingapp.infrastructure.unit;

import com.bookingapp.domain.unit.UnitDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "cars")
public class CarJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", length = 36)
    private UUID unitId;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitDetails.CarCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitDetails.Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private UnitDetails.FuelType fuelType;

    @Column(nullable = false)
    private int doors;

    @Column(name = "has_ac", nullable = false)
    private boolean hasAc;

    @Column(name = "plate_number", nullable = false, length = 20)
    private String plateNumber;

    @Column(name = "mileage_limit_km")
    private Integer mileageLimitKm;

    protected CarJpaEntity() {
    }

    public CarJpaEntity(UUID unitId, String brand, String model, int year, UnitDetails.CarCategory category,
                        UnitDetails.Transmission transmission, UnitDetails.FuelType fuelType, int doors,
                        boolean hasAc, String plateNumber, Integer mileageLimitKm) {
        this.unitId = unitId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.category = category;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.doors = doors;
        this.hasAc = hasAc;
        this.plateNumber = plateNumber;
        this.mileageLimitKm = mileageLimitKm;
    }

    public UUID getUnitId() {
        return unitId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public UnitDetails.CarCategory getCategory() {
        return category;
    }

    public UnitDetails.Transmission getTransmission() {
        return transmission;
    }

    public UnitDetails.FuelType getFuelType() {
        return fuelType;
    }

    public int getDoors() {
        return doors;
    }

    public boolean isHasAc() {
        return hasAc;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public Integer getMileageLimitKm() {
        return mileageLimitKm;
    }
}
