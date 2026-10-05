-- The platform has one currency: the Moroccan dirham. Until now a listing chose its own, and every
-- existing one chose EUR, so the amounts entered in euros are converted at 10 MAD for 1 EUR, a round
-- rate that keeps the prices plausible.
--
-- listings.currency and bookings.currency stay: an amount is stored with its currency, and the code
-- now always writes MAD there. users.preferred_currency goes: nothing ever read it.

update bookable_units u
  join listings l on l.id = u.listing_id
   set u.base_price = u.base_price * 10
 where l.currency = 'EUR';

update car_rental_agencies c
  join listings l on l.id = c.listing_id
   set c.deposit_amount = c.deposit_amount * 10
 where l.currency = 'EUR';

update bookings
   set unit_price = unit_price * 10, total_amount = total_amount * 10
 where currency = 'EUR';

update bookings set currency = 'MAD';
update listings set currency = 'MAD';

alter table users drop column preferred_currency;
