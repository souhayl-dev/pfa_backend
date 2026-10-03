-- Development seed data.
-- Passwords: admin@bookly.com -> Admin123! | every other account -> Password123!
--
-- Id prefixes: 0 users, 1 providers, 11 members, 2 listings, 3 units, 5 clients,
--              6 bookings, 62 status history, 63 reviews

insert into users (id, first_name, last_name, email, username, password_hash, is_verified, created_at, updated_at) values
    ('00000000-0000-0000-0000-000000000001', 'Bookly', 'Admin', 'admin@bookly.com', 'admin',
     '$2b$12$nk581l1zNdYZol8GoxkQ3eOU8tibPpS6U6r7tRvqe3CXtWh8uuEj6', true, now(6), now(6)),
    ('00000000-0000-0000-0000-000000000002', 'Amina', 'Benali', 'hotels@bookly.com', 'amina',
     '$2b$12$dm860ThxHh6qMAQFg0dLkenScz.yZp14zd9N6KUOsqCSgLZ9UEuVG', true, now(6), now(6)),
    ('00000000-0000-0000-0000-000000000003', 'Youssef', 'Alaoui', 'cars@bookly.com', 'youssef',
     '$2b$12$dm860ThxHh6qMAQFg0dLkenScz.yZp14zd9N6KUOsqCSgLZ9UEuVG', true, now(6), now(6)),
    ('00000000-0000-0000-0000-000000000004', 'Fatima', 'Idrissi', 'tours@bookly.com', 'fatima',
     '$2b$12$dm860ThxHh6qMAQFg0dLkenScz.yZp14zd9N6KUOsqCSgLZ9UEuVG', true, now(6), now(6)),
    ('00000000-0000-0000-0000-000000000005', 'Test', 'Customer', 'customer@bookly.com', 'customer',
     '$2b$12$dm860ThxHh6qMAQFg0dLkenScz.yZp14zd9N6KUOsqCSgLZ9UEuVG', true, now(6), now(6)),
    ('00000000-0000-0000-0000-000000000006', 'Sara', 'Tazi', 'staff@bookly.com', 'sara',
     '$2b$12$dm860ThxHh6qMAQFg0dLkenScz.yZp14zd9N6KUOsqCSgLZ9UEuVG', true, now(6), now(6));

insert into user_roles (user_id, role) values
    ('00000000-0000-0000-0000-000000000001', 'ADMIN');

insert into clients (id, user_id, nationality, birth_date, created_at, updated_at) values
    ('50000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 'FR', '1994-05-12',
     now(6), now(6));

-- Providers and teams ---------------------------------------------------------------------------

insert into providers (id, company_name, legal_name, tax_id, description, status, created_at, updated_at) values
    ('10000000-0000-0000-0000-000000000001', 'Atlas Riads', 'Atlas Riads SARL', 'MA-IF-4471023',
     'Riads and boutique hotels in Marrakech and Casablanca.', 'APPROVED', now(6), now(6)),
    ('10000000-0000-0000-0000-000000000002', 'Youssef Location', 'Youssef Location SARL', 'MA-IF-5580311',
     'Car rental with airport and city-centre branches.', 'APPROVED', now(6), now(6)),
    ('10000000-0000-0000-0000-000000000003', 'Fatima Excursions', 'Fatima Excursions SARL', 'MA-IF-6692845',
     'Tours, guides and a restaurant in the Marrakech region.', 'APPROVED', now(6), now(6));

insert into provider_members (id, provider_id, user_id, role, status, joined_at) values
    ('11000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000002', 'OWNER', 'ACTIVE', now(6)),
    ('11000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000006', 'STAFF', 'ACTIVE', now(6)),
    ('11000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000003', 'OWNER', 'ACTIVE', now(6)),
    ('11000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000004', 'OWNER', 'ACTIVE', now(6));

-- Listings ----------------------------------------------------------------------------------------

insert into listings (id, provider_id, type, name, description, address, city, country_code, latitude, longitude,
                      timezone, currency, phone, status, rating_avg, reviews_count, created_at, updated_at) values
    ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'HOTEL',
     'Riad Jardin Secret', 'Traditional riad with a patio, a fountain and a rooftop terrace over the medina.',
     '12 Derb El Hammam', 'Marrakech', 'MA', 31.631800, -7.989800, 'Africa/Casablanca', 'EUR',
     '+212524000001', 'ACTIVE', 5.00, 1, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'HOTEL',
     'Hotel Atlantic Casablanca', 'Modern hotel near the Corniche, breakfast included.',
     '5 Boulevard de la Corniche', 'Casablanca', 'MA', 33.595000, -7.668500, 'Africa/Casablanca', 'EUR',
     '+212522000002', 'ACTIVE', 0, 0, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'CAR_RENTAL_AGENCY',
     'Youssef Location - Marrakech Airport', 'Pick up your car right at Menara airport arrivals.',
     'Aeroport Marrakech Menara', 'Marrakech', 'MA', 31.606900, -8.036300, 'Africa/Casablanca', 'EUR',
     '+212524000003', 'ACTIVE', 0, 0, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000002', 'CAR_RENTAL_AGENCY',
     'Youssef Location - Casablanca Centre', 'City-centre branch near the old medina.',
     '48 Boulevard Mohammed V', 'Casablanca', 'MA', 33.591900, -7.617600, 'Africa/Casablanca', 'EUR',
     '+212522000004', 'ACTIVE', 0, 0, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000003', 'RESTAURANT',
     'La Terrasse des Epices', 'Traditional Moroccan restaurant on a terrace in the heart of the medina.',
     '15 Souk Cherifia', 'Marrakech', 'MA', 31.630400, -7.987100, 'Africa/Casablanca', 'EUR',
     '+212524000005', 'ACTIVE', 0, 0, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000003', 'GUIDE',
     'Fatima - Atlas Mountain Guide', 'Certified mountain guide for the High Atlas and its valleys.',
     null, 'Marrakech', 'MA', null, null, 'Africa/Casablanca', 'EUR',
     '+212661000006', 'ACTIVE', 0, 0, now(6), now(6)),
    ('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000003', 'TRAVEL_AGENCY',
     'Fatima Excursions', 'Multi-day tours and airport transfers from Marrakech.',
     '22 Avenue Mohammed VI', 'Marrakech', 'MA', 31.617900, -8.010300, 'Africa/Casablanca', 'EUR',
     '+212524000007', 'ACTIVE', 0, 0, now(6), now(6));

insert into hotels (listing_id, stars, check_in_time, check_out_time) values
    ('20000000-0000-0000-0000-000000000001', 5, '14:00:00', '12:00:00'),
    ('20000000-0000-0000-0000-000000000002', 4, '15:00:00', '11:00:00');

insert into car_rental_agencies (listing_id, license_number, min_driver_age, deposit_amount) values
    ('20000000-0000-0000-0000-000000000003', 'LOC-MRK-2019-114', 21, 500.00),
    ('20000000-0000-0000-0000-000000000004', 'LOC-CAS-2021-087', 21, 500.00);

insert into restaurants (listing_id, cuisine_type) values
    ('20000000-0000-0000-0000-000000000005', 'MOROCCAN');

insert into guides (listing_id, years_experience) values
    ('20000000-0000-0000-0000-000000000006', 12);

insert into travel_agencies (listing_id, license_number) values
    ('20000000-0000-0000-0000-000000000007', 'AGV-MRK-2015-032');

-- Bookable units ------------------------------------------------------------------------------------

insert into bookable_units (id, listing_id, type, name, description, base_price, capacity, created_at, updated_at)
values
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'ROOM',
     'Patio Double Room', 'Double room opening onto the patio.', 85.00, 2, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'ROOM',
     'Terrace Suite', 'Suite with a private terrace.', 140.00, 3, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'ROOM',
     'Sea View Double Room', 'Double room facing the ocean.', 65.00, 2, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000003', 'CAR',
     'Dacia Duster Automatic', 'Comfortable SUV, ideal for the Atlas.', 45.00, 5, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000004', 'CAR',
     'Renault Clio Manual', 'Economical city car.', 28.00, 5, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000005', 'TABLE',
     'Terrace seating', 'Price per guest for the set menu.', 25.00, 40, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000006', 'GUIDE_SERVICE',
     'Full-day Atlas hike', 'Private guided day in the Ourika valley, up to 8 people.', 90.00, 8, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000007', 'TRANSPORT',
     'Marrakech airport transfer', 'Minibus with driver, airport to medina.', 30.00, 7, now(6), now(6)),
    ('30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000007', 'TOUR',
     'Atlas and Valleys', 'Across the High Atlas to Ait Benhaddou, Ouarzazate and the Dades valley.',
     180.00, 12, now(6), now(6));

insert into rooms (unit_id, room_number, room_type) values
    ('30000000-0000-0000-0000-000000000001', '101', 'DOUBLE'),
    ('30000000-0000-0000-0000-000000000002', '201', 'SUITE'),
    ('30000000-0000-0000-0000-000000000003', '305', 'DOUBLE');

insert into cars (unit_id, brand, model, year, category, transmission, fuel_type, doors, has_ac, plate_number,
                  mileage_limit_km) values
    ('30000000-0000-0000-0000-000000000004', 'Dacia', 'Duster', 2023, 'SUV', 'AUTOMATIC', 'DIESEL', 5, true,
     '12345-A-6', 300),
    ('30000000-0000-0000-0000-000000000005', 'Renault', 'Clio', 2022, 'COMPACT', 'MANUAL', 'PETROL', 5, true,
     '67890-B-1', null);

insert into transports (unit_id, vehicle_type) values
    ('30000000-0000-0000-0000-000000000008', 'MINIBUS');

insert into tours (unit_id, duration_days) values
    ('30000000-0000-0000-0000-000000000009', 3);

insert into tour_steps (tour_id, step_order, day_number, city, description) values
    ('30000000-0000-0000-0000-000000000009', 1, 1, 'Ait Benhaddou',
     'Tizi n''Tichka pass, then a guided visit of the ksar.'),
    ('30000000-0000-0000-0000-000000000009', 2, 2, 'Ouarzazate', 'Kasbah Taourirt, then on to the Dades gorges.'),
    ('30000000-0000-0000-0000-000000000009', 3, 3, 'Marrakech', 'Road back through the valleys.');

-- Bookings ------------------------------------------------------------------------------------------
-- Times are UTC. Marrakech is UTC+1, so a 14:00 check-in is stored as 13:00.

insert into bookings (id, code, client_id, unit_id, status, start_at, end_at, guests_count, unit_price,
                      total_amount, currency, special_requests, created_at, updated_at) values
    -- a finished stay: 2 nights in room 101
    ('60000000-0000-0000-0000-000000000001', 'BK-7Q2M4X', '50000000-0000-0000-0000-000000000001',
     '30000000-0000-0000-0000-000000000001', 'COMPLETED', '2026-09-10 13:00:00', '2026-09-12 11:00:00', 2,
     85.00, 170.00, 'EUR', 'Late arrival around 22:00.', '2026-08-20 10:00:00', '2026-09-12 11:00:00'),
    -- an upcoming car rental: 3 days
    ('60000000-0000-0000-0000-000000000002', 'BK-3R8K1P', '50000000-0000-0000-0000-000000000001',
     '30000000-0000-0000-0000-000000000004', 'CONFIRMED', '2026-10-20 09:00:00', '2026-10-23 09:00:00', 2,
     45.00, 135.00, 'EUR', null, '2026-09-25 09:00:00', '2026-09-25 12:00:00'),
    -- a tour request for 2 people, waiting for the agency: 15 to 17 October
    ('60000000-0000-0000-0000-000000000003', 'BK-9T5W2C', '50000000-0000-0000-0000-000000000001',
     '30000000-0000-0000-0000-000000000009', 'PENDING', '2026-10-14 23:00:00', '2026-10-17 23:00:00', 2,
     180.00, 360.00, 'EUR', null, '2026-09-29 16:00:00', '2026-09-29 16:00:00');

insert into booking_status_history (id, booking_id, from_status, to_status, changed_by, reason, changed_at) values
    ('62000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', null, 'PENDING',
     '00000000-0000-0000-0000-000000000005', null, '2026-08-20 10:00:00'),
    ('62000000-0000-0000-0000-000000000002', '60000000-0000-0000-0000-000000000001', 'PENDING', 'CONFIRMED',
     '00000000-0000-0000-0000-000000000006', null, '2026-08-20 14:30:00'),
    ('62000000-0000-0000-0000-000000000003', '60000000-0000-0000-0000-000000000001', 'CONFIRMED', 'COMPLETED',
     null, 'Stay ended', '2026-09-12 11:00:00'),
    ('62000000-0000-0000-0000-000000000004', '60000000-0000-0000-0000-000000000002', null, 'PENDING',
     '00000000-0000-0000-0000-000000000005', null, '2026-09-25 09:00:00'),
    ('62000000-0000-0000-0000-000000000005', '60000000-0000-0000-0000-000000000002', 'PENDING', 'CONFIRMED',
     '00000000-0000-0000-0000-000000000003', null, '2026-09-25 12:00:00'),
    ('62000000-0000-0000-0000-000000000006', '60000000-0000-0000-0000-000000000003', null, 'PENDING',
     '00000000-0000-0000-0000-000000000005', null, '2026-09-29 16:00:00');

insert into reviews (id, booking_id, rating, comment, reply, replied_by, replied_at, created_at, updated_at) values
    ('63000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', 5,
     'Beautiful riad, very quiet, and the breakfast on the terrace was wonderful.',
     'Thank you for staying with us, we hope to welcome you again!', '00000000-0000-0000-0000-000000000002',
     '2026-09-14 09:00:00', '2026-09-13 18:00:00', '2026-09-14 09:00:00');
