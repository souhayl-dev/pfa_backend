#!/usr/bin/env bash
# End-to-end check of the API against a running backend loaded with the V2 seed data.
# Run: bash scripts/e2e-api-check.sh   (it creates bookings and a test provider; reset the DB for a clean state)
A=http://localhost:8080/api
PASS=0; FAIL=0

req() { # method path token [json] -> prints "status body"
  local m=$1 p=$2 t=$3 d=$4
  local args=(-s -o /tmp/body -w "%{http_code}" -X "$m" -H "Content-Type: application/json")
  [ -n "$t" ] && args+=(-H "Authorization: Bearer $t")
  [ -n "$d" ] && args+=(-d "$d")
  local code; code=$(curl "${args[@]}" "$A$p")
  echo "$code $(cat /tmp/body)"
}
check() { # name expected-status actual-output
  local got=${3%% *}
  if [ "$got" = "$2" ]; then PASS=$((PASS+1)); echo "  ok   $1 ($got)";
  else FAIL=$((FAIL+1)); echo "  FAIL $1: expected $2, got $got -> ${3#* }" | cut -c1-300; fi
}
field() { grep -oE "\"$1\":\"?[^\",}]*" <<<"$2" | head -1 | sed -E "s/^\"$1\":\"?//"; }
login() { field token "$(req POST /auth/login "" "{\"email\":\"$1\",\"password\":\"$2\"}")"; }
book() { echo "{\"unitId\":\"$1\",\"start\":\"$2\",\"end\":\"$3\",\"guestsCount\":$4}"; }

ATLAS=10000000-0000-0000-0000-000000000001
RIAD=20000000-0000-0000-0000-000000000001
ATLANTIC=20000000-0000-0000-0000-000000000002
ROOM101=30000000-0000-0000-0000-000000000001
DUSTER=30000000-0000-0000-0000-000000000004
TABLE=30000000-0000-0000-0000-000000000006
TOUR=30000000-0000-0000-0000-000000000009

CUSTOMER=$(login customer@bookly.com 'Password123!')
AMINA=$(login hotels@bookly.com 'Password123!')
SARA=$(login staff@bookly.com 'Password123!')
YOUSSEF=$(login cars@bookly.com 'Password123!')
ADMIN=$(login admin@bookly.com 'Admin123!')

echo "Public catalog"
R=$(req GET "/listings" ""); check "search without login" 200 "$R"
echo "  listings found: $(grep -o '"providerId"' <<<"$R" | wc -l)"
check "search hotels in marrakech" 200 "$(req GET "/listings?type=HOTEL&city=marrakech" "")"
R=$(req GET "/listings?minPrice=400&maxPrice=700&sort=PRICE_DESC" ""); check "search by price range" 200 "$R"
echo "  from 400 to 700, dearest first: $(grep -oE '"fromPrice":[0-9.]+' <<<"$R" | tr '\n' ' ')"
R=$(req GET "/listings?q=fatima&minRating=4" ""); check "search by text and rating" 200 "$R"
echo "  listings found: $(grep -o '"providerId"' <<<"$R" | wc -l)"
check "price range upside down" 400 "$(req GET "/listings?minPrice=900&maxPrice=400" "")"
check "unknown sort" 400 "$(req GET "/listings?sort=CHEAPEST" "")"
R=$(req GET "/listings/facets?city=marrakech" ""); check "facets" 200 "$R"
echo "  in marrakech: $(field total "$R") listings, prices $(field minPrice "$R") to $(field maxPrice "$R")"
R=$(req GET "/listings/$RIAD" ""); check "listing detail" 200 "$R"
echo "  hotel details present: $(grep -c '"hotel":{"stars":5' <<<"$R"), units: $(grep -o '"pricingUnit"' <<<"$R" | wc -l)"
check "reviews" 200 "$(req GET "/listings/$RIAD/reviews" "")"
R=$(req GET "/units/$ROOM101/quote?start=2026-11-10T00:00&end=2026-11-12T00:00&guests=2" "")
check "quote 2 nights" 200 "$R"; echo "  quote: $(field total "$R") MAD, available=$(field available "$R")"
R=$(req GET "/units/$ROOM101/quote?start=2025-01-01T00:00&end=2025-01-02T00:00&guests=1" "")
check "quote in the past" 200 "$R"; echo "  available=$(field available "$R") ($(field reason "$R"))"
R=$(req GET "/units/$ROOM101/quote?start=2026-11-10T00:00&end=2026-11-12T00:00&guests=3" "")
echo "  3 guests in a room for 2: available=$(field available "$R")"

echo "Booking a room"
B=$(book $ROOM101 2026-11-10T00:00 2026-11-12T00:00 2)
check "booking needs login" 403 "$(req POST /bookings "" "$B")"
R=$(req POST /bookings "$CUSTOMER" "$B"); check "customer books room 101" 201 "$R"
B1=$(field id "$R"); echo "  code $(field code "$R"), status $(field status "$R"), total $(field totalAmount "$R")"
check "same room same nights again" 409 "$(req POST /bookings "$CUSTOMER" "$B")"
check "owner cannot book own listing" 409 "$(req POST /bookings "$AMINA" "$B")"
check "back-to-back stay is fine" 201 "$(req POST /bookings "$CUSTOMER" "$(book $ROOM101 2026-11-12T00:00 2026-11-13T00:00 2)")"
check "too many guests" 409 "$(req POST /bookings "$CUSTOMER" "$(book $ROOM101 2026-12-01T00:00 2026-12-02T00:00 3)")"
check "date in the past" 409 "$(req POST /bookings "$CUSTOMER" "$(book $ROOM101 2025-01-01T00:00 2025-01-02T00:00 1)")"
check "unknown unit" 404 "$(req POST /bookings "$CUSTOMER" "$(book 39999999-0000-0000-0000-000000000000 2026-12-01T00:00 2026-12-02T00:00 1)")"

echo "Provider team handles it"
check "staff sees riad bookings" 200 "$(req GET "/manage/listings/$RIAD/bookings?status=PENDING" "$SARA")"
check "staff sees the other hotel too" 200 "$(req GET "/manage/listings/$ATLANTIC/bookings" "$SARA")"
check "another provider is refused" 403 "$(req GET "/manage/listings/$RIAD/bookings" "$YOUSSEF")"
R=$(req POST "/bookings/$B1/confirm" "$SARA"); check "staff confirms" 200 "$R"; echo "  status $(field status "$R"), client seen by team: $(field clientName "$R")"
check "confirm twice" 409 "$(req POST "/bookings/$B1/confirm" "$SARA")"
check "other provider cannot confirm" 403 "$(req POST "/bookings/$B1/confirm" "$YOUSSEF")"
check "no-show before it starts" 409 "$(req POST "/bookings/$B1/no-show" "$SARA")"
check "staff cannot create units" 403 "$(req POST "/manage/listings/$RIAD/units" "$SARA" '{"type":"ROOM","name":"x","basePrice":10,"capacity":1,"room":{"roomNumber":"9","roomType":"SINGLE"}}')"
R=$(req POST "/bookings/$B1/cancel" "$CUSTOMER" '{"reason":"Plans changed"}'); check "client cancels" 200 "$R"
R=$(req GET "/bookings/$B1/history" "$CUSTOMER"); check "history" 200 "$R"
echo "  history entries: $(grep -o toStatus <<<"$R" | wc -l), reason kept: $(grep -c 'Plans changed' <<<"$R")"

echo "Tours, cars, tables"
T() { echo "{\"unitId\":\"$TOUR\",\"start\":\"$1T00:00\",\"guestsCount\":$2}"; }
check "tour: 11 seats when 10 are left" 409 "$(req POST /bookings "$CUSTOMER" "$(T 2026-10-15 11)")"
R=$(req POST /bookings "$CUSTOMER" "$(T 2026-10-15 10)"); check "tour: 10 seats when 10 are left" 201 "$R"
echo "  10 people x 1800 = $(field totalAmount "$R") MAD"
check "tour: that date is now full" 409 "$(req POST /bookings "$CUSTOMER" "$(T 2026-10-15 1)")"
check "tour: next day is a new group" 201 "$(req POST /bookings "$CUSTOMER" "$(T 2026-10-16 12)")"
R=$(req POST /bookings "$CUSTOMER" "$(book $DUSTER 2026-11-01T10:00 2026-11-03T12:00 2)"); check "car rental" 201 "$R"
echo "  50h rental billed: $(field totalAmount "$R") MAD (3 days x 450)"
R=$(req POST /bookings "$CUSTOMER" "{\"unitId\":\"$TABLE\",\"start\":\"2026-11-05T20:00\",\"guestsCount\":4}"); check "restaurant table, no end time" 201 "$R"
echo "  4 guests x 250 = $(field totalAmount "$R") MAD"

echo "Reviews"
check "already reviewed booking" 409 "$(req POST /bookings/60000000-0000-0000-0000-000000000001/review "$CUSTOMER" '{"rating":4}')"
check "cannot review unfinished booking" 409 "$(req POST "/bookings/$B1/review" "$CUSTOMER" '{"rating":4}')"
check "rating 6 rejected" 400 "$(req PUT /reviews/63000000-0000-0000-0000-000000000001 "$CUSTOMER" '{"rating":6}')"
check "edit review" 200 "$(req PUT /reviews/63000000-0000-0000-0000-000000000001 "$CUSTOMER" '{"rating":3,"comment":"Nice but noisy"}')"
check "team replies" 200 "$(req POST /reviews/63000000-0000-0000-0000-000000000001/reply "$SARA" '{"text":"Sorry about the noise."}')"
check "other provider cannot reply" 403 "$(req POST /reviews/63000000-0000-0000-0000-000000000001/reply "$YOUSSEF" '{"text":"x"}')"
echo "  riad rating now: $(field ratingAvg "$(req GET "/listings/$RIAD" "")")"

echo "Sign-up and client profile"
EMAIL="owner$RANDOM@example.com"
R=$(req POST /auth/register "" "{\"firstName\":\"Omar\",\"lastName\":\"Test\",\"email\":\"$EMAIL\",\"password\":\"Secret123!\"}")
check "sign up" 201 "$R"; echo "  client profile right after sign-up: $(grep -c '"clientId":null' <<<"$R") (1 = none yet)"
OMAR=$(login "$EMAIL" 'Secret123!')
check "first booking" 201 "$(req POST /bookings "$OMAR" "$(book $ROOM101 2026-12-10T00:00 2026-12-11T00:00 1)")"
R=$(req GET /profile/me "$OMAR"); echo "  client profile after first booking: $(grep -c '"clientId":"' <<<"$R") (1 = created)"
check "set traveller details" 200 "$(req PUT /profile/me/client "$OMAR" '{"nationality":"MA","birthDate":"1990-03-04"}')"
check "bad country code" 400 "$(req PUT /profile/me/client "$OMAR" '{"nationality":"Morocco"}')"

echo "Becoming a provider"
R=$(req POST /providers "$OMAR" '{"companyName":"Omar Travel"}'); check "register provider" 201 "$R"; P=$(field id "$R"); echo "  status $(field status "$R")"
check "listed in switch menu" 200 "$(req GET /providers/mine "$OMAR")"
HOTEL='{"type":"HOTEL","name":"Dar Omar","city":"Fes","countryCode":"MA","timezone":"Africa/Casablanca","hotel":{"stars":3,"checkInTime":"15:00","checkOutTime":"11:00"}}'
R=$(req POST "/providers/$P/listings" "$OMAR" "$HOTEL"); check "create draft hotel" 201 "$R"; L=$(field id "$R")
check "stars 7 rejected" 400 "$(req POST "/providers/$P/listings" "$OMAR" "${HOTEL/\"stars\":3/\"stars\":7}")"
check "hotel without details" 400 "$(req POST "/providers/$P/listings" "$OMAR" '{"type":"HOTEL","name":"x","city":"Fes","countryCode":"MA","timezone":"Africa/Casablanca","currency":"EUR"}')"
check "draft is not public" 404 "$(req GET "/listings/$L" "")"
check "cannot go live while pending" 409 "$(req POST "/manage/listings/$L/activate" "$OMAR")"
check "non-admin cannot approve" 403 "$(req POST "/admin/providers/$P/approve" "$OMAR")"
check "admin sees pending providers" 200 "$(req GET /admin/providers "$ADMIN")"
check "admin approves" 200 "$(req POST "/admin/providers/$P/approve" "$ADMIN")"
check "go live" 200 "$(req POST "/manage/listings/$L/activate" "$OMAR")"
check "tour unit in a hotel" 409 "$(req POST "/manage/listings/$L/units" "$OMAR" '{"type":"TOUR","name":"x","basePrice":10,"capacity":4,"tour":{"durationDays":1,"steps":[]}}')"
R=$(req POST "/manage/listings/$L/units" "$OMAR" '{"type":"ROOM","name":"Garden room","basePrice":60,"capacity":2,"room":{"roomNumber":"1","roomType":"DOUBLE"}}')
check "add room" 201 "$R"; U=$(field id "$R")
check "update room price" 200 "$(req PUT "/manage/units/$U" "$OMAR" '{"type":"ROOM","name":"Garden room","basePrice":70,"capacity":2,"room":{"roomNumber":"1","roomType":"DOUBLE"}}')"
check "new listing is public" 200 "$(req GET "/listings/$L" "")"
AGENCY='{"type":"TRAVEL_AGENCY","name":"Omar Tours","city":"Fes","countryCode":"MA","timezone":"Africa/Casablanca","travelAgency":{"licenseNumber":"AGV-FES-1"}}'
R=$(req POST "/providers/$P/listings" "$OMAR" "$AGENCY"); check "create travel agency" 201 "$R"; AG=$(field id "$R")
TOURJSON='{"type":"TOUR","name":"Fes and Middle Atlas","basePrice":120,"capacity":8,"tour":{"durationDays":2,"steps":[{"stepOrder":1,"dayNumber":1,"city":"Ifrane"},{"stepOrder":2,"dayNumber":2,"city":"Azrou"}]}}'
R=$(req POST "/manage/listings/$AG/units" "$OMAR" "$TOURJSON"); check "add tour with steps" 201 "$R"
echo "  steps stored: $(grep -o '"stepOrder"' <<<"$R" | wc -l)"
check "tour step beyond duration" 400 "$(req POST "/manage/listings/$AG/units" "$OMAR" "${TOURJSON/\"dayNumber\":2/\"dayNumber\":5}")"

echo "Photos"
R=$(req POST "/manage/listings/$L/photos" "$OMAR" '{"url":"/uploads/dar-omar.jpg"}'); check "listing photo" 201 "$R"; PH=$(field id "$R")
check "unit photo" 201 "$(req POST "/manage/units/$U/photos" "$OMAR" '{"url":"/uploads/garden-room.jpg"}')"
check "another provider cannot delete it" 403 "$(req DELETE "/manage/photos/$PH" "$YOUSSEF")"
check "delete photo" 204 "$(req DELETE "/manage/photos/$PH" "$OMAR")"

echo "Team"
check "staff cannot add members" 403 "$(req POST "/providers/$ATLAS/members" "$SARA" "{\"email\":\"$EMAIL\",\"role\":\"STAFF\"}")"
check "unknown email" 409 "$(req POST "/providers/$ATLAS/members" "$AMINA" '{"email":"nobody@example.com","role":"STAFF"}')"
R=$(req POST "/providers/$ATLAS/members" "$AMINA" "{\"email\":\"$EMAIL\",\"role\":\"STAFF\"}"); check "owner adds an existing user" 201 "$R"; M=$(field id "$R")
check "add twice" 409 "$(req POST "/providers/$ATLAS/members" "$AMINA" "{\"email\":\"$EMAIL\",\"role\":\"STAFF\"}")"
check "new member sees team bookings" 200 "$(req GET "/manage/listings/$RIAD/bookings" "$OMAR")"
check "promote to manager" 200 "$(req PATCH "/members/$M/role" "$AMINA" '{"role":"MANAGER"}')"
check "team list" 200 "$(req GET "/providers/$ATLAS/members" "$AMINA")"
check "last owner cannot leave" 409 "$(req DELETE "/members/11000000-0000-0000-0000-000000000001" "$AMINA")"
check "remove member" 204 "$(req DELETE "/members/$M" "$AMINA")"
check "removed member loses access" 403 "$(req GET "/manage/listings/$RIAD/bookings" "$OMAR")"

echo
echo "passed $PASS, failed $FAIL"
