# Booking Platform - Backend

Multi-vendor booking marketplace: hotels, restaurants, guides, travel agencies (tours and transfers)
and car rental agencies. The data model is drawn in `../docs/database-diagram.svg` and documented
in `../docs/database-design.html`.

## Architecture

Clean / hexagonal architecture as four Maven modules, each only depending on the ones to its left:

```
domain  <-  application  <-  infrastructure  <-  api
```

| Module | Depends on | Contains |
|---|---|---|
| `domain` | nothing (plain Java) | Entities, value objects, business rules, repository **interfaces** (ports). No Spring, no JPA. |
| `application` | `domain` | Use cases: one interface and one service per feature, with their commands and views. Still framework-free; transactions go through the `UnitOfWork` port. |
| `infrastructure` | `domain`, `application` | Adapters: JPA entities, Spring Data repositories, mappers, security (JWT, password hashing), file storage, email. |
| `api` | all three | Spring Boot application: REST controllers with their request and response classes, security config, error handling, the composition root (`shared/config/UseCaseConfig`), Flyway migrations. |

**Why this shape:** the domain and application modules compile without Spring on the classpath, so
every business rule is unit-tested with plain JUnit and no database.

### Packages: one folder per feature

Inside each module, code is grouped by feature, with one class per file:

```
com.bookingapp.<module>/
├── auth, profile      sign-up, login, the user's own account        (application, api)
├── user, client       accounts and client profiles                  (domain, infrastructure)
├── provider, team     businesses and their members
├── listing            listings and their 5 types
├── unit               bookable units: rooms, cars, tables, guide services, transfers, tours
├── photo              photos of listings and units
├── catalog            the public search and quotes                  (application, api)
├── booking            bookings, status history, availability, pricing
├── review             reviews and replies
├── admin              platform administration                       (application, api)
└── shared             exceptions, ports, security, config, error handling
```

For example, the booking feature is `domain/booking` (the rules), `application/booking` (the use
case), `infrastructure/booking` (the tables) and `api/booking` (the endpoints).

### Core domain model

- **Accounts** - one `User` per person. Signing up creates only the account. A `Client` profile is
  created at the user's first booking, and a `ProviderMember` row when they create a business or
  are added to a team. `UserRole` only holds platform roles (`ADMIN`).
- **Providers** - a `Provider` is a business (`PENDING` until an admin approves it) with a team:
  `OWNER`, `MANAGER` or `STAFF`. Owners and managers add people by the email of an existing account.
  Every active member sees all the provider's listings; only owners and managers change them.
- **Listings** - a place a client can visit and review: `HOTEL`, `RESTAURANT`, `GUIDE`,
  `TRAVEL_AGENCY` or `CAR_RENTAL_AGENCY`. Shared fields live in `listings`; each type has its own
  table sharing the primary key.
- **Bookable units** - what a client books inside a listing: `ROOM`, `CAR`, `TABLE`,
  `GUIDE_SERVICE`, `TRANSPORT` or `TOUR`. The type decides the availability rule and what the base
  price is charged per:

  | Unit | Offered by | Availability | Price per |
  |---|---|---|---|
  | `ROOM` | hotel | one booking at a time | night |
  | `CAR` | car rental agency | one booking at a time | started 24 hours |
  | `GUIDE_SERVICE` | guide | one booking at a time | started 24 hours |
  | `TRANSPORT` | travel agency | one booking at a time | trip |
  | `TABLE` | restaurant | guests present at the same time fit in the capacity | guest |
  | `TOUR` | travel agency | bookings starting the same day form one group up to the capacity | guest |

- **Bookings** - one `Booking` is one client reserving one unit for one period, with the price copied
  at booking time. Status: `PENDING -> CONFIRMED -> COMPLETED`, with `CANCELLED` and `NO_SHOW`; every
  change is saved to `booking_status_history`. One `Review` per completed booking.

### Integrity

- **In the database:** foreign keys, CHECK constraints, and triggers that stop a type row on the
  wrong parent (a hotel row on a restaurant listing, a car in a hotel) or a type change after creation.
- **In the code:** rules that span rows or depend on time. Notably, a booking locks the unit row
  (`findByIdForUpdate`) before checking availability, so two requests can't book the same room.

### Not built yet

Payments, seasonal prices, blocked days, amenities, opening hours and email invitations are out of
scope for now. Refresh tokens are not issued: access tokens last 24 hours, so a deactivated user
keeps access until theirs expires.

## Running locally

Requires Docker.

```bash
docker compose up -d
```

This starts MySQL (port 3307), phpMyAdmin (port 8081) and the API (port 8080), which builds the
project and runs the jar. Swagger UI is at `http://localhost:8080/swagger-ui.html`.

On startup Flyway applies `V1__init.sql` (schema), `V2__seed_data.sql` (development data) and the
photo seeds (`V3`, `V4`: links to files on Wikimedia Commons, not copies), then
Hibernate validates every entity against the schema. Seed accounts use `Password123!`, except
`admin@bookly.com` which uses `Admin123!`.

The migration creates triggers. With binary logging on, MySQL only allows that for a non-admin user
when `log_bin_trust_function_creators` is enabled, which `docker-compose.yml` does.

Set `BOOKING_JWT_SECRET` to a real secret outside of local development.

### Emails

Sign-up sends a link to verify the address, and "forgot password" sends a link to choose a new one.
Both open a page of the website (`APP_FRONTEND_URL`, by default `http://localhost:5173`).

Without a mail server the emails are written to the API log (`docker compose logs -f api`), link
included. To send them for real, for example to a Mailtrap inbox, copy `.env.example` to `.env`
beside `docker-compose.yml`, fill in `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME` and `MAIL_PASSWORD`,
then run `docker compose up -d api`. The log says at start-up which of the two is in use.

To build and test without running: `mvn clean install` (JDK 21), or through Docker:

```bash
docker run --rm -v "$PWD:/app" -v booking_m2:/root/.m2 -w /app maven:3.9-eclipse-temurin-21 mvn clean install
```

`scripts/e2e-api-check.sh` exercises the whole API against a running backend with fresh seed data.

## API surface

Times sent by clients (`start`, `end`) are local to the listing, without a timezone, e.g.
`2026-11-10T14:00`; responses give UTC instants plus the listing's `timezone`.

**Public** (no sign-in)

```
GET   /api/listings?type=&city=&country=&q=&minPrice=&maxPrice=&minRating=&sort=&page=&size=
                                                         active listings of approved providers
GET   /api/listings/facets?(the same filters)            total, and the count beside each filter
GET   /api/listings/{id}                                 details, photos, bookable units
GET   /api/listings/{id}/reviews
GET   /api/units/{id}/quote?start=&end=&guests=          price and availability, without booking
```

Search: `q` is looked for in the name and the city, ignoring case and accents. `minPrice` and
`maxPrice` compare with the listing's "from" price (its cheapest bookable unit), so they leave out
listings that have none; `minRating` leaves out listings without a review. `sort` is `RECOMMENDED`
(the default), `PRICE_ASC`, `PRICE_DESC`, `RATING` or `NAME`. In the facets, `types`, `cities` and the
price bounds each ignore their own filter and apply the others.

A quote for a period that has already started answers `available: false` with the reason, as a
booking for it would be refused.

**Accounts**

```
POST  /api/auth/register                { firstName, lastName, email, password }
POST  /api/auth/login                   { email, password } -> { token, user }
POST  /api/auth/verify-email            { token }
POST  /api/auth/resend-verification     signed in: a new link, the earlier ones stop working
POST  /api/auth/request-password-reset  { email }
POST  /api/auth/reset-password          { token, newPassword }

GET   /api/profile/me
PATCH /api/profile/me                   { firstName, lastName, username, phone, gender,
                                          preferredCurrency, notificationsEnabled, profileImage }
PUT   /api/profile/me/client            { nationality, birthDate }
POST  /api/profile/me/change-password   { currentPassword, newPassword }
POST  /api/uploads                      multipart file -> { url }
```

**Bookings and reviews** (signed in)

```
POST  /api/bookings                     { unitId, start, end, guestsCount, specialRequests } -> PENDING
GET   /api/bookings/mine | /api/bookings/{id} | /api/bookings/{id}/history
POST  /api/bookings/{id}/cancel         { reason }   client until it starts, or the provider's team
POST  /api/bookings/{id}/confirm | /no-show          provider's team
POST  /api/bookings/{id}/review         { rating, comment }   client, once the booking is COMPLETED
PUT   /api/reviews/{id}                 { rating, comment }
POST  /api/reviews/{id}/reply           { text }     provider's team
```

How the period is read per unit: rooms use the dates of `start`/`end` with the hotel's check-in and
check-out times; a tour only needs `start` (its date) and lasts its number of days; tables default
to 2 hours and transfers to 1 hour when `end` is missing; cars and guide services need both.

**Providers and teams** (signed in, checked against membership)

```
POST  /api/providers                    become a provider (PENDING, caller is OWNER)
GET   /api/providers/mine               the "switch account" list
GET   /api/providers/{id} | PUT /api/providers/{id}
GET   /api/providers/{id}/members
POST  /api/providers/{id}/members       { email, role }   an existing account
PATCH /api/members/{id}/role            { role }
POST  /api/members/{id}/suspend | /reactivate   |   DELETE /api/members/{id}
```

**Provider dashboard** (the whole team reads; owners and managers write)

```
POST  /api/providers/{id}/listings      { type, name, city, countryCode, timezone, currency, ...,
                                          hotel | restaurant | guide | travelAgency | carRentalAgency: {...} }
GET   /api/providers/{id}/listings
GET|PUT|DELETE /api/manage/listings/{id}
POST  /api/manage/listings/{id}/activate | /deactivate
GET   /api/manage/listings/{id}/bookings?status=
POST  /api/manage/listings/{id}/units   { type, name, basePrice, capacity, room | car | transport | tour: {...} }
PUT|DELETE /api/manage/units/{id}       POST /activate | /deactivate
POST  /api/manage/listings/{id}/photos | /api/manage/units/{id}/photos   { url }
DELETE /api/manage/photos/{id}
```

**Admin** (`ADMIN` role)

```
GET   /api/admin/providers?status=PENDING
POST  /api/admin/providers/{id}/approve | /reject | /suspend
GET   /api/admin/users     POST /api/admin/users/{id}/deactivate | /activate
```

Signed-in endpoints expect `Authorization: Bearer <token>`. Errors: `400` invalid input, `403` not
allowed for this user, `404` not found (also used for listings the public may not see), `409` a
business rule such as a taken email, a booked room or a final booking status.

Confirmed bookings are moved to `COMPLETED` every 15 minutes once they have ended.
