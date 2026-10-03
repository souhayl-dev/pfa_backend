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
@Table(name = "rooms")
public class RoomJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", length = 36)
    private UUID unitId;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 30)
    private UnitDetails.RoomType roomType;

    protected RoomJpaEntity() {
    }

    public RoomJpaEntity(UUID unitId, String roomNumber, UnitDetails.RoomType roomType) {
        this.unitId = unitId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
    }

    public UUID getUnitId() {
        return unitId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public UnitDetails.RoomType getRoomType() {
        return roomType;
    }
}
