package com.bookingapp.domain.team;

/**
 * What a team member may do for their provider.
 *
 * <pre>
 *                                   OWNER  MANAGER  STAFF
 * edit company info                   x
 * add and remove members              x      x (staff only)
 * create and edit listings and units  x      x
 * confirm and cancel bookings         x      x       x
 * reply to reviews                    x      x       x
 * </pre>
 */
public enum MemberRole {
    OWNER,
    MANAGER,
    STAFF;

    public boolean canManageProvider() {
        return this == OWNER;
    }

    public boolean canManageListings() {
        return this == OWNER || this == MANAGER;
    }

    public boolean canManageTeam() {
        return this == OWNER || this == MANAGER;
    }

    /** Managers can only add or remove staff; owners can manage anyone. */
    public boolean canAssign(MemberRole role) {
        return this == OWNER || (this == MANAGER && role == STAFF);
    }
}
