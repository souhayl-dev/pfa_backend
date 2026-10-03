-- Booking platform schema, design v4: 22 tables (see docs/database-diagram-v4.svg).
--
-- Conventions
--   * ids are UUIDs stored as char(36)
--   * every datetime is UTC; listings carry their own timezone for display
--   * money is decimal(10,2); the currency lives on the listing and is copied into bookings
--   * enum columns are varchar without CHECK lists, so new values need no migration.
--     Only the two type discriminators are checked, because a new type needs a new table anyway.
--   * type tables (hotels, rooms...) link to their parent with a plain foreign key; the triggers
--     at the end of this file make sure the parent has the matching type.

-- ---------------------------------------------------------------------------------------------
-- Accounts
-- ---------------------------------------------------------------------------------------------

create table users (
    id                    char(36)     not null primary key,
    first_name            varchar(100) not null,
    last_name             varchar(100) not null,
    email                 varchar(255) not null,
    username              varchar(50),
    password_hash         varchar(255),
    phone                 varchar(30),
    gender                varchar(20),
    profile_image         varchar(500),
    is_active             boolean      not null default true,
    is_verified           boolean      not null default false,
    preferred_currency    varchar(3)   not null default 'EUR',
    notifications_enabled boolean      not null default true,
    created_at            datetime(6)  not null,
    updated_at            datetime(6)  not null,
    constraint uq_users_email unique (email),
    constraint uq_users_username unique (username)
);

create table user_roles (
    user_id char(36)    not null,
    role    varchar(20) not null,
    primary key (user_id, role),
    constraint fk_user_roles_user foreign key (user_id) references users (id) on delete cascade
);

-- Email verification and password reset links. Only the SHA-256 hash of a token is stored.
create table user_tokens (
    id         char(36)    not null primary key,
    user_id    char(36)    not null,
    token_hash varchar(64) not null,
    type       varchar(30) not null,
    expires_at datetime(6) not null,
    revoked    boolean     not null default false,
    created_at datetime(6) not null,
    constraint uq_user_tokens_hash unique (token_hash),
    constraint fk_user_tokens_user foreign key (user_id) references users (id) on delete cascade
);

create index idx_user_tokens_user_type on user_tokens (user_id, type);

-- The booking side of a user, created at their first booking.
create table clients (
    id          char(36)    not null primary key,
    user_id     char(36)    not null,
    nationality varchar(2),
    birth_date  date,
    created_at  datetime(6) not null,
    updated_at  datetime(6) not null,
    constraint uq_clients_user unique (user_id),
    constraint fk_clients_user foreign key (user_id) references users (id)
);

-- ---------------------------------------------------------------------------------------------
-- Providers and their teams
-- ---------------------------------------------------------------------------------------------

create table providers (
    id                        char(36)     not null primary key,
    company_name              varchar(200) not null,
    legal_name                varchar(200),
    tax_id                    varchar(50),
    verification_document_url varchar(500),
    description               varchar(4000),
    status                    varchar(20)  not null default 'PENDING',
    created_at                datetime(6)  not null,
    updated_at                datetime(6)  not null
);

create table provider_members (
    id          char(36)    not null primary key,
    provider_id char(36)    not null,
    user_id     char(36)    not null,
    role        varchar(20) not null,
    status      varchar(20) not null default 'ACTIVE',
    joined_at   datetime(6) not null,
    constraint uq_provider_members_user unique (provider_id, user_id),
    constraint fk_provider_members_provider foreign key (provider_id) references providers (id),
    constraint fk_provider_members_user foreign key (user_id) references users (id)
);

create index idx_provider_members_user on provider_members (user_id);

-- ---------------------------------------------------------------------------------------------
-- Listings: one shared table plus one table per type
-- ---------------------------------------------------------------------------------------------

create table listings (
    id            char(36)      not null primary key,
    provider_id   char(36)      not null,
    type          varchar(30)   not null,
    name          varchar(200)  not null,
    description   varchar(4000),
    address       varchar(255),
    city          varchar(100)  not null,
    country_code  varchar(2)    not null,
    latitude      decimal(9, 6),
    longitude     decimal(9, 6),
    timezone      varchar(64)   not null,
    currency      varchar(3)    not null,
    phone         varchar(30),
    email         varchar(255),
    status        varchar(20)   not null default 'DRAFT',
    rating_avg    decimal(3, 2) not null default 0,
    reviews_count int           not null default 0,
    created_at    datetime(6)   not null,
    updated_at    datetime(6)   not null,
    deleted_at    datetime(6),
    constraint fk_listings_provider foreign key (provider_id) references providers (id),
    constraint chk_listings_type
        check (type in ('HOTEL', 'RESTAURANT', 'GUIDE', 'TRAVEL_AGENCY', 'CAR_RENTAL_AGENCY')),
    constraint chk_listings_latitude check (latitude between -90 and 90),
    constraint chk_listings_longitude check (longitude between -180 and 180),
    constraint chk_listings_rating check (rating_avg between 0 and 5),
    constraint chk_listings_reviews_count check (reviews_count >= 0)
);

create index idx_listings_provider on listings (provider_id);
create index idx_listings_search on listings (city, type, status);

create table hotels (
    listing_id     char(36) not null primary key,
    stars          int,
    check_in_time  time,
    check_out_time time,
    constraint fk_hotels_listing foreign key (listing_id) references listings (id),
    constraint chk_hotels_stars check (stars between 1 and 5)
);

create table restaurants (
    listing_id   char(36) not null primary key,
    cuisine_type varchar(50),
    constraint fk_restaurants_listing foreign key (listing_id) references listings (id)
);

create table guides (
    listing_id       char(36) not null primary key,
    years_experience int,
    constraint fk_guides_listing foreign key (listing_id) references listings (id),
    constraint chk_guides_experience check (years_experience >= 0)
);

create table travel_agencies (
    listing_id     char(36)     not null primary key,
    license_number varchar(100) not null,
    constraint fk_travel_agencies_listing foreign key (listing_id) references listings (id)
);

create table car_rental_agencies (
    listing_id     char(36)       not null primary key,
    license_number varchar(100)   not null,
    min_driver_age int            not null default 21,
    deposit_amount decimal(10, 2) not null default 0,
    constraint fk_car_rental_agencies_listing foreign key (listing_id) references listings (id),
    constraint chk_car_rental_agencies_age check (min_driver_age between 18 and 99),
    constraint chk_car_rental_agencies_deposit check (deposit_amount >= 0)
);

-- ---------------------------------------------------------------------------------------------
-- Bookable units: what a client books inside a listing
-- ---------------------------------------------------------------------------------------------

create table bookable_units (
    id          char(36)       not null primary key,
    listing_id  char(36)       not null,
    type        varchar(30)    not null,
    name        varchar(200)   not null,
    description varchar(2000),
    base_price  decimal(10, 2) not null,
    capacity    int            not null,
    is_active   boolean        not null default true,
    created_at  datetime(6)    not null,
    updated_at  datetime(6)    not null,
    deleted_at  datetime(6),
    constraint fk_bookable_units_listing foreign key (listing_id) references listings (id),
    constraint chk_bookable_units_type
        check (type in ('ROOM', 'CAR', 'TABLE', 'GUIDE_SERVICE', 'TRANSPORT', 'TOUR')),
    constraint chk_bookable_units_price check (base_price >= 0),
    constraint chk_bookable_units_capacity check (capacity > 0)
);

create index idx_bookable_units_listing on bookable_units (listing_id);

create table rooms (
    unit_id     char(36)    not null primary key,
    room_number varchar(20) not null,
    room_type   varchar(30) not null,
    constraint fk_rooms_unit foreign key (unit_id) references bookable_units (id)
);

-- The number of seats is the unit's capacity.
create table cars (
    unit_id          char(36)    not null primary key,
    brand            varchar(50) not null,
    model            varchar(50) not null,
    year             int         not null,
    category         varchar(20) not null,
    transmission     varchar(20) not null,
    fuel_type        varchar(20) not null,
    doors            int         not null,
    has_ac           boolean     not null default true,
    plate_number     varchar(20) not null,
    mileage_limit_km int,
    constraint fk_cars_unit foreign key (unit_id) references bookable_units (id),
    constraint chk_cars_year check (year >= 1950),
    constraint chk_cars_doors check (doors > 0),
    constraint chk_cars_mileage check (mileage_limit_km > 0)
);

create index idx_cars_plate on cars (plate_number);

create table transports (
    unit_id      char(36)    not null primary key,
    vehicle_type varchar(30) not null,
    constraint fk_transports_unit foreign key (unit_id) references bookable_units (id)
);

-- A tour is priced per person (the unit's base_price) and takes groups up to the unit's capacity.
create table tours (
    unit_id       char(36) not null primary key,
    duration_days int      not null,
    constraint fk_tours_unit foreign key (unit_id) references bookable_units (id),
    constraint chk_tours_duration check (duration_days > 0)
);

create table tour_steps (
    tour_id     char(36)      not null,
    step_order  int           not null,
    day_number  int           not null,
    city        varchar(100)  not null,
    description varchar(1000),
    primary key (tour_id, step_order),
    constraint fk_tour_steps_tour foreign key (tour_id) references tours (unit_id) on delete cascade,
    constraint chk_tour_steps_day check (day_number >= 1)
);

-- A photo belongs to a listing or to a unit: exactly one of the two is set.
create table photos (
    id         char(36)     not null primary key,
    listing_id char(36),
    unit_id    char(36),
    url        varchar(500) not null,
    sort_order int          not null,
    created_at datetime(6)  not null,
    constraint fk_photos_listing foreign key (listing_id) references listings (id) on delete cascade,
    constraint fk_photos_unit foreign key (unit_id) references bookable_units (id) on delete cascade,
    constraint chk_photos_owner check ((listing_id is null) <> (unit_id is null))
);

create index idx_photos_listing on photos (listing_id, sort_order);
create index idx_photos_unit on photos (unit_id, sort_order);

-- ---------------------------------------------------------------------------------------------
-- Bookings and reviews
-- ---------------------------------------------------------------------------------------------

-- One booking is one client reserving one unit for one period. The listing is the unit's listing.
-- unit_price, total_amount and currency are copied at booking time.
create table bookings (
    id               char(36)       not null primary key,
    code             varchar(20)    not null,
    client_id        char(36)       not null,
    unit_id          char(36)       not null,
    status           varchar(20)    not null,
    start_at         datetime(6)    not null,
    end_at           datetime(6)    not null,
    guests_count     int            not null,
    unit_price       decimal(10, 2) not null,
    total_amount     decimal(10, 2) not null,
    currency         varchar(3)     not null,
    special_requests varchar(1000),
    created_at       datetime(6)    not null,
    updated_at       datetime(6)    not null,
    constraint uq_bookings_code unique (code),
    constraint fk_bookings_client foreign key (client_id) references clients (id),
    constraint fk_bookings_unit foreign key (unit_id) references bookable_units (id),
    constraint chk_bookings_period check (end_at > start_at),
    constraint chk_bookings_guests check (guests_count > 0),
    constraint chk_bookings_unit_price check (unit_price >= 0),
    constraint chk_bookings_total check (total_amount >= 0)
);

create index idx_bookings_client on bookings (client_id, created_at);
create index idx_bookings_unit_period on bookings (unit_id, start_at, end_at);

-- changed_by is null for changes made by the system (for example automatic completion)
create table booking_status_history (
    id          char(36)     not null primary key,
    booking_id  char(36)     not null,
    from_status varchar(20),
    to_status   varchar(20)  not null,
    changed_by  char(36),
    reason      varchar(500),
    changed_at  datetime(6)  not null,
    constraint fk_booking_status_history_booking foreign key (booking_id) references bookings (id),
    constraint fk_booking_status_history_user foreign key (changed_by) references users (id)
);

create index idx_booking_status_history_booking on booking_status_history (booking_id, changed_at);

-- A reply is all-or-nothing: text, author and time are set together.
create table reviews (
    id         char(36)      not null primary key,
    booking_id char(36)      not null,
    rating     int           not null,
    comment    varchar(2000),
    reply      varchar(2000),
    replied_by char(36),
    replied_at datetime(6),
    created_at datetime(6)   not null,
    updated_at datetime(6)   not null,
    constraint uq_reviews_booking unique (booking_id),
    constraint fk_reviews_booking foreign key (booking_id) references bookings (id),
    constraint fk_reviews_replied_by foreign key (replied_by) references users (id),
    constraint chk_reviews_rating check (rating between 1 and 5),
    constraint chk_reviews_reply check (
        (reply is null and replied_by is null and replied_at is null)
        or (reply is not null and replied_by is not null and replied_at is not null))
);

-- ---------------------------------------------------------------------------------------------
-- Type checks
--
-- The type tables use plain foreign keys, so these triggers are what stops a hotel row on a
-- restaurant listing, a car inside a hotel, or a type being changed after creation.
-- ---------------------------------------------------------------------------------------------

delimiter $$

create trigger trg_listings_type_fixed before update on listings for each row
begin
    if new.type <> old.type then
        signal sqlstate '45000' set message_text = 'the type of a listing cannot change';
    end if;
end$$

create trigger trg_hotels_type before insert on hotels for each row
begin
    if (select type from listings where id = new.listing_id) <> 'HOTEL' then
        signal sqlstate '45000' set message_text = 'hotels rows need a HOTEL listing';
    end if;
end$$

create trigger trg_restaurants_type before insert on restaurants for each row
begin
    if (select type from listings where id = new.listing_id) <> 'RESTAURANT' then
        signal sqlstate '45000' set message_text = 'restaurants rows need a RESTAURANT listing';
    end if;
end$$

create trigger trg_guides_type before insert on guides for each row
begin
    if (select type from listings where id = new.listing_id) <> 'GUIDE' then
        signal sqlstate '45000' set message_text = 'guides rows need a GUIDE listing';
    end if;
end$$

create trigger trg_travel_agencies_type before insert on travel_agencies for each row
begin
    if (select type from listings where id = new.listing_id) <> 'TRAVEL_AGENCY' then
        signal sqlstate '45000' set message_text = 'travel_agencies rows need a TRAVEL_AGENCY listing';
    end if;
end$$

create trigger trg_car_rental_agencies_type before insert on car_rental_agencies for each row
begin
    if (select type from listings where id = new.listing_id) <> 'CAR_RENTAL_AGENCY' then
        signal sqlstate '45000' set message_text = 'car_rental_agencies rows need a CAR_RENTAL_AGENCY listing';
    end if;
end$$

-- Which unit types each kind of listing may offer.
create trigger trg_bookable_units_listing_type before insert on bookable_units for each row
begin
    declare listing_type varchar(30);
    select type into listing_type from listings where id = new.listing_id;
    if not ((new.type = 'ROOM' and listing_type = 'HOTEL')
            or (new.type = 'TABLE' and listing_type = 'RESTAURANT')
            or (new.type = 'GUIDE_SERVICE' and listing_type = 'GUIDE')
            or (new.type in ('TRANSPORT', 'TOUR') and listing_type = 'TRAVEL_AGENCY')
            or (new.type = 'CAR' and listing_type = 'CAR_RENTAL_AGENCY')) then
        signal sqlstate '45000' set message_text = 'this kind of listing cannot offer this unit type';
    end if;
end$$

create trigger trg_bookable_units_fixed before update on bookable_units for each row
begin
    if new.type <> old.type or new.listing_id <> old.listing_id then
        signal sqlstate '45000' set message_text = 'the type and listing of a unit cannot change';
    end if;
end$$

create trigger trg_rooms_type before insert on rooms for each row
begin
    if (select type from bookable_units where id = new.unit_id) <> 'ROOM' then
        signal sqlstate '45000' set message_text = 'rooms rows need a ROOM unit';
    end if;
end$$

create trigger trg_cars_type before insert on cars for each row
begin
    if (select type from bookable_units where id = new.unit_id) <> 'CAR' then
        signal sqlstate '45000' set message_text = 'cars rows need a CAR unit';
    end if;
end$$

create trigger trg_transports_type before insert on transports for each row
begin
    if (select type from bookable_units where id = new.unit_id) <> 'TRANSPORT' then
        signal sqlstate '45000' set message_text = 'transports rows need a TRANSPORT unit';
    end if;
end$$

create trigger trg_tours_type before insert on tours for each row
begin
    if (select type from bookable_units where id = new.unit_id) <> 'TOUR' then
        signal sqlstate '45000' set message_text = 'tours rows need a TOUR unit';
    end if;
end$$

delimiter ;
