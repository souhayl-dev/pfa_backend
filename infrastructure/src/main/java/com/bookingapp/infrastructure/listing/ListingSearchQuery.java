package com.bookingapp.infrastructure.listing;

import com.bookingapp.domain.listing.ListingFacets;
import com.bookingapp.domain.listing.ListingSearchCriteria;
import com.bookingapp.domain.listing.ListingSort;
import com.bookingapp.domain.listing.ListingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The public search. Its filters are optional and its facets each leave one filter out, so the
 * query text is assembled here instead of being one fixed query on the Spring Data repository.
 * Every value still goes in as a bound parameter.
 */
@Component
public class ListingSearchQuery {

    private enum Filter { TYPE, CITY, PRICE }

    /** Only listings a client may see: active, not deleted, and owned by an approved provider. */
    private static final String VISIBLE = """
            l.status = com.bookingapp.domain.listing.ListingStatus.ACTIVE
              and l.deletedAt is null
              and exists (select p.id from ProviderJpaEntity p
                          where p.id = l.providerId
                            and p.status = com.bookingapp.domain.provider.ProviderStatus.APPROVED)
            """;

    /** The "from" price of a listing: its cheapest bookable unit, null when it has none. */
    private static final String FROM_PRICE = """
            (select min(u.basePrice) from BookableUnitJpaEntity u
              where u.listingId = l.id and u.active = true and u.deletedAt is null)""";

    private static final char ESCAPE = '!';

    @PersistenceContext
    private EntityManager entityManager;

    public List<ListingJpaEntity> page(ListingSearchCriteria criteria) {
        Query query = query("select l from ListingJpaEntity l", criteria, null, " order by " + order(criteria.sort()));
        query.setFirstResult(criteria.page() * criteria.size());
        query.setMaxResults(criteria.size());
        @SuppressWarnings("unchecked")
        List<ListingJpaEntity> listings = query.getResultList();
        return listings;
    }

    public ListingFacets facets(ListingSearchCriteria criteria) {
        long total = (Long) query("select count(l) from ListingJpaEntity l", criteria, null, "").getSingleResult();

        Map<ListingType, Long> types = new EnumMap<>(ListingType.class);
        for (Object[] row : rows(query("select l.type, count(l) from ListingJpaEntity l", criteria, Filter.TYPE,
                " group by l.type"))) {
            types.put((ListingType) row[0], (Long) row[1]);
        }

        Map<String, Long> cities = new LinkedHashMap<>();
        for (Object[] row : rows(query("select l.city, count(l) from ListingJpaEntity l", criteria, Filter.CITY,
                " group by l.city order by l.city"))) {
            cities.put((String) row[0], (Long) row[1]);
        }

        // One price per listing; the bounds are their smallest and largest.
        List<BigDecimal> prices = fromPrices(criteria);
        BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(null);
        BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(null);
        return new ListingFacets(total, types, cities, minPrice, maxPrice);
    }

    @SuppressWarnings("unchecked")
    private List<BigDecimal> fromPrices(ListingSearchCriteria criteria) {
        String select = "select min(u.basePrice) from BookableUnitJpaEntity u, ListingJpaEntity l";
        Query query = query(select, criteria, Filter.PRICE,
                " and u.listingId = l.id and u.active = true and u.deletedAt is null group by l.id");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    private static List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    /** @param ignored the filter a facet leaves out, or null to apply them all */
    private Query query(String select, ListingSearchCriteria criteria, Filter ignored, String tail) {
        StringBuilder jpql = new StringBuilder(select).append(" where ").append(VISIBLE);
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (criteria.type() != null && ignored != Filter.TYPE) {
            jpql.append(" and l.type = :type");
            parameters.put("type", criteria.type());
        }
        if (criteria.city() != null && ignored != Filter.CITY) {
            jpql.append(" and lower(l.city) = lower(:city)");
            parameters.put("city", criteria.city());
        }
        if (criteria.countryCode() != null) {
            jpql.append(" and l.countryCode = :countryCode");
            parameters.put("countryCode", criteria.countryCode());
        }
        if (criteria.text() != null) {
            jpql.append(" and (lower(l.name) like :text escape '!' or lower(l.city) like :text escape '!')");
            parameters.put("text", "%" + escapeLike(criteria.text().toLowerCase()) + "%");
        }
        if (ignored != Filter.PRICE) {
            if (criteria.minPrice() != null) {
                jpql.append(" and ").append(FROM_PRICE).append(" >= :minPrice");
                parameters.put("minPrice", criteria.minPrice());
            }
            if (criteria.maxPrice() != null) {
                jpql.append(" and ").append(FROM_PRICE).append(" <= :maxPrice");
                parameters.put("maxPrice", criteria.maxPrice());
            }
        }
        if (criteria.minRating() != null && criteria.minRating().signum() > 0) {
            jpql.append(" and l.reviewsCount > 0 and l.ratingAvg >= :minRating");
            parameters.put("minRating", criteria.minRating());
        }

        Query query = entityManager.createQuery(jpql.append(tail).toString());
        parameters.forEach(query::setParameter);
        return query;
    }

    /** The id closes every order, so pages never overlap when two listings tie. */
    private static String order(ListingSort sort) {
        return switch (sort) {
            case RECOMMENDED -> "l.ratingAvg * least(l.reviewsCount, 5) desc, l.name asc, l.id asc";
            case PRICE_ASC -> FROM_PRICE + " asc nulls last, l.name asc, l.id asc";
            case PRICE_DESC -> FROM_PRICE + " desc nulls last, l.name asc, l.id asc";
            case RATING -> "l.ratingAvg desc, l.reviewsCount desc, l.name asc, l.id asc";
            case NAME -> "l.name asc, l.id asc";
        };
    }

    private static String escapeLike(String text) {
        StringBuilder escaped = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '%' || c == '_' || c == ESCAPE) {
                escaped.append(ESCAPE);
            }
            escaped.append(c);
        }
        return escaped.toString();
    }
}
