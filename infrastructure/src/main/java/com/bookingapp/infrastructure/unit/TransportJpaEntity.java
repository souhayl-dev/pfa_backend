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
@Table(name = "transports")
public class TransportJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", length = 36)
    private UUID unitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private UnitDetails.VehicleType vehicleType;

    protected TransportJpaEntity() {
    }

    public TransportJpaEntity(UUID unitId, UnitDetails.VehicleType vehicleType) {
        this.unitId = unitId;
        this.vehicleType = vehicleType;
    }

    public UUID getUnitId() {
        return unitId;
    }

    public UnitDetails.VehicleType getVehicleType() {
        return vehicleType;
    }
}
