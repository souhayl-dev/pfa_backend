package com.bookingapp.api.shared.config;

import com.bookingapp.application.admin.AdminService;
import com.bookingapp.application.admin.AdminUseCase;
import com.bookingapp.application.auth.AuthService;
import com.bookingapp.application.auth.AuthUseCase;
import com.bookingapp.application.booking.BookingService;
import com.bookingapp.application.booking.BookingUseCase;
import com.bookingapp.application.catalog.CatalogService;
import com.bookingapp.application.catalog.CatalogUseCase;
import com.bookingapp.application.catalog.PublicListings;
import com.bookingapp.application.listing.ListingManagementService;
import com.bookingapp.application.listing.ListingManagementUseCase;
import com.bookingapp.application.listing.ListingViewAssembler;
import com.bookingapp.application.photo.PhotoService;
import com.bookingapp.application.photo.PhotoUseCase;
import com.bookingapp.application.profile.ProfileService;
import com.bookingapp.application.profile.ProfileUseCase;
import com.bookingapp.application.provider.ProviderService;
import com.bookingapp.application.provider.ProviderUseCase;
import com.bookingapp.application.review.ReviewService;
import com.bookingapp.application.review.ReviewUseCase;
import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.EmailSender;
import com.bookingapp.application.shared.port.PasswordHasher;
import com.bookingapp.application.shared.port.TokenGenerator;
import com.bookingapp.application.shared.port.TokenIssuer;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.application.team.TeamService;
import com.bookingapp.application.team.TeamUseCase;
import com.bookingapp.application.unit.UnitManagementService;
import com.bookingapp.application.unit.UnitManagementUseCase;
import com.bookingapp.domain.booking.BookingRepository;
import com.bookingapp.domain.client.ClientRepository;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.photo.PhotoRepository;
import com.bookingapp.domain.provider.ProviderRepository;
import com.bookingapp.domain.review.ReviewRepository;
import com.bookingapp.domain.team.ProviderMemberRepository;
import com.bookingapp.domain.unit.BookableUnitRepository;
import com.bookingapp.domain.user.UserRepository;
import com.bookingapp.domain.user.UserTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: use-case classes are plain Java (no Spring annotations) so the
 * application module stays framework-free. Wiring them to their port implementations
 * happens here, at the outermost layer.
 */
@Configuration
public class UseCaseConfig {

    // Shared helpers ----------------------------------------------------------------------------

    @Bean
    public ProviderAccess providerAccess(ProviderMemberRepository members, ListingRepository listings,
                                         BookableUnitRepository units) {
        return new ProviderAccess(members, listings, units);
    }

    @Bean
    public PublicListings publicListings(ListingRepository listings, ProviderRepository providers) {
        return new PublicListings(listings, providers);
    }

    @Bean
    public ListingViewAssembler listingViewAssembler(ProviderRepository providers, PhotoRepository photos,
                                                     BookableUnitRepository units) {
        return new ListingViewAssembler(providers, photos, units);
    }

    // Accounts ----------------------------------------------------------------------------------

    @Bean
    public AuthUseCase authUseCase(UserRepository users, UserTokenRepository tokens, ClientRepository clients,
                                   PasswordHasher passwordHasher, TokenIssuer tokenIssuer,
                                   TokenGenerator tokenGenerator, EmailSender emailSender, UnitOfWork unitOfWork) {
        return new AuthService(users, tokens, clients, passwordHasher, tokenIssuer, tokenGenerator, emailSender,
                unitOfWork);
    }

    @Bean
    public ProfileUseCase profileUseCase(UserRepository users, ClientRepository clients,
                                         PasswordHasher passwordHasher, UnitOfWork unitOfWork) {
        return new ProfileService(users, clients, passwordHasher, unitOfWork);
    }

    @Bean
    public AdminUseCase adminUseCase(ProviderRepository providers, UserRepository users, ClientRepository clients) {
        return new AdminService(providers, users, clients);
    }

    // Providers ---------------------------------------------------------------------------------

    @Bean
    public ProviderUseCase providerUseCase(ProviderRepository providers, ProviderMemberRepository members,
                                           ProviderAccess access, UnitOfWork unitOfWork) {
        return new ProviderService(providers, members, access, unitOfWork);
    }

    @Bean
    public TeamUseCase teamUseCase(ProviderMemberRepository members, UserRepository users, ProviderAccess access,
                                   UnitOfWork unitOfWork) {
        return new TeamService(members, users, access, unitOfWork);
    }

    @Bean
    public ListingManagementUseCase listingManagementUseCase(ListingRepository listings,
                                                             ProviderRepository providers, ProviderAccess access,
                                                             ListingViewAssembler views, UnitOfWork unitOfWork) {
        return new ListingManagementService(listings, providers, access, views, unitOfWork);
    }

    @Bean
    public UnitManagementUseCase unitManagementUseCase(BookableUnitRepository units, PhotoRepository photos,
                                                       ProviderAccess access, UnitOfWork unitOfWork) {
        return new UnitManagementService(units, photos, access, unitOfWork);
    }

    @Bean
    public PhotoUseCase photoUseCase(PhotoRepository photos, ProviderAccess access) {
        return new PhotoService(photos, access);
    }

    // Catalog, bookings and reviews -------------------------------------------------------------

    @Bean
    public CatalogUseCase catalogUseCase(ListingRepository listings, BookableUnitRepository units,
                                         BookingRepository bookings, PublicListings publicListings,
                                         ListingViewAssembler views) {
        return new CatalogService(listings, units, bookings, publicListings, views);
    }

    @Bean
    public BookingUseCase bookingUseCase(BookingRepository bookings, BookableUnitRepository units,
                                         ListingRepository listings, ClientRepository clients, UserRepository users,
                                         ReviewRepository reviews, PublicListings publicListings,
                                         ProviderAccess access, UnitOfWork unitOfWork) {
        return new BookingService(bookings, units, listings, clients, users, reviews, publicListings, access,
                unitOfWork);
    }

    @Bean
    public ReviewUseCase reviewUseCase(ReviewRepository reviews, BookingRepository bookings,
                                       BookableUnitRepository units, ListingRepository listings,
                                       ClientRepository clients, UserRepository users,
                                       PublicListings publicListings, ProviderAccess access,
                                       UnitOfWork unitOfWork) {
        return new ReviewService(reviews, bookings, units, listings, clients, users, publicListings, access,
                unitOfWork);
    }
}
