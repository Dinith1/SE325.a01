package se325.assignment01.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertInfoNotificationDTO;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.BookingMapper;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.ConcertSummaryMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;
import se325.assignment01.concert.service.mapper.SeatMapper;
import se325.assignment01.concert.service.mapper.UserMapper;
import se325.assignment01.concert.service.util.Subscription;
import se325.assignment01.concert.service.util.TheatreLayout;

/**
 * Class to implement a simple REST Web service for managing Concerts.
 * <p>
 * <p>
 * ConcertResource implements a WEB service with the following interface:
 * <p>
 * - GET <base-uri>/concerts/{id} : Retrieves a Concert based on its unique id.
 * The HTTP response message has a status code of either 200 or 404, depending
 * on whether the specified Concert is found.
 * <p>
 * - GET <base-uri>/concerts : Retrieves a list of all the Concerts. Returns an
 * HTTP code of 200.
 * <p>
 * - GET <base-uri>/concerts/summaries : Retrieves a list of Concert summaries.
 * Returns an HTTP code of 200.
 * <p>
 * - GET <base-uri>/performers/{id} : Retrieves a Performer based on its unique
 * id. The HTTP response message has a status code of either 200 or 404,
 * depending on whether the specified Performer is found.
 * <p>
 * - GET <base-uri>/performers : Retrieves a list of all the Performers. Returns
 * an HTTP code of 200.
 * <p>
 * - POST <base-uri>/login : Allows a user to log into the concert service.
 * Returns an HTTP code of 200 if successfully logged in, or 404 if the username
 * or password is incorrect.
 * <p>
 * - POST <base-uri>/bookings : Allows the user to make a booking for a concert.
 * Returns an HTTP code of 201 and URL to the location of the new booking when
 * successful, 404 when the user is not logged in, 400 when trying to book a
 * non-existent concert or non-existent date for a concert, or 403 when trying
 * to book a seat that has already been booked.
 * <p>
 * GET <base-uri>/bookings/{id} : Retrieves a booking based on its unique id.
 * Returns at HTTP code of 200 when successful, 404 when the user is not logged
 * in, 401 when the booking doesn't exist, or 403 when the user is trying to
 * access a booking not belonging to him.
 * <p>
 * GET <base-uri>/bookings : Retrieves a list of all the bookings of the user
 * making the request. Returns an HTTP code of 200 when successful or 401 when
 * the user is not logged in.
 * <p>
 * GET <base-uri>/seats/{date}?status=<BookingStatus> : Retrieves a list of
 * seats for a particular date that have the booking status specified. Returns
 * an HTTP code of 200.
 * <p>
 * POST <base-uri>/subscribe/concertInfo : Allows the user to subscribe to be
 * notified when a certain number of seats for a specified concert and date has
 * been booked. Returns an HTTP code of 200 when the number of seats booked
 * seats has already been passe, 404 when the user is not logged in, or 400 when
 * the concert doesn't exist or the date for that concert doesn't exist.
 * Susbscribers will be notified via the POST method postToSubs().
 * <p>
 */
@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static List<Subscription> subscriptions = new ArrayList<>();

    @GET
    @Path("concerts/{id}")
    public Response getConcert(@PathParam("id") long id) {
        LOGGER.debug("getConcert(): Getting concert for id: " + id);

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Concert concert = em.find(Concert.class, id);

            em.getTransaction().commit();

            if (concert == null) {
                LOGGER.debug("getConcert(): No concert with id: " + id + " exists");
                return Response.status(Status.NOT_FOUND).build();
            }

            LOGGER.debug("getConcert(): Found concert with id: " + id);
            return Response.ok(ConcertMapper.toDto(concert)).build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("concerts")
    public Response getAllConcerts() {
        LOGGER.debug("getAllConcerts(): Getting all concerts");

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            List<Concert> concerts = em.createQuery("select c from Concert c", Concert.class).getResultList();

            List<ConcertDTO> dtos = new ArrayList<>();
            for (Concert c : concerts) {
                dtos.add(ConcertMapper.toDto(c));
            }

            LOGGER.debug("getAllConcerts(): Found " + dtos.size() + " concerts");

            GenericEntity<List<ConcertDTO>> entity = new GenericEntity<List<ConcertDTO>>(dtos) {
            };

            return Response.ok(entity).build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("concerts/summaries")
    public Response getConcertSummaries() {
        LOGGER.debug("getConcertSummaries(): Getting concert summaries");

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            List<Concert> concerts = em.createQuery("select c from Concert c", Concert.class).getResultList();

            LOGGER.debug("getConcertSummaries(): Found " + concerts.size() + " concerts");

            List<ConcertSummaryDTO> summaries = new ArrayList<>();
            for (Concert c : concerts) {
                summaries.add(ConcertSummaryMapper.toConcertSummary(c));
            }

            GenericEntity<List<ConcertSummaryDTO>> entity = new GenericEntity<List<ConcertSummaryDTO>>(summaries) {
            };

            return Response.ok(entity).build();

        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @GET
    @Path("performers/{id}")
    public Response getPerformer(@PathParam("id") long id) {
        LOGGER.debug("getPerfomer(): Getting performer for id: " + id);

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Performer performer = em.find(Performer.class, id);

            em.getTransaction().commit();

            if (performer == null) {
                LOGGER.debug("getPerformer(): No performer with id: " + id + " exists");
                return Response.status(Status.NOT_FOUND).build();
            }

            LOGGER.debug("getPerformer(): Found performer with id: " + id);
            return Response.ok(PerformerMapper.toDto(performer)).build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("performers")
    public Response getAllPerformers() {
        LOGGER.debug("getAllPerformers(): Getting all performers");

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            List<Performer> performers = em.createQuery("select c from Performer c", Performer.class).getResultList();

            LOGGER.debug("getAllPerformers(): Found " + performers.size() + " performers");

            List<PerformerDTO> performerDTOs = new ArrayList<>();
            for (Performer p : performers) {
                performerDTOs.add(PerformerMapper.toDto(p));
            }

            GenericEntity<List<PerformerDTO>> entity = new GenericEntity<List<PerformerDTO>>(performerDTOs) {
            };

            return Response.ok(entity).build();

        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @POST
    @Path("login")
    public Response login(UserDTO dto) {
        LOGGER.debug("login(): Logging in for user: " + dto.getUsername() + " and password: " + dto.getPassword());

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            List<User> users = em.createQuery("select u from User u where u.username = :username", User.class)
                    .setParameter("username", dto.getUsername()).getResultList();

            // Incorrect username
            if (users.isEmpty()) {
                LOGGER.debug("login(): Username " + dto.getUsername() + " not found");
                return Response.status(Status.UNAUTHORIZED).build();
            }

            // There must be only a single user (assuming usernames are unique)
            User user = users.get(0);

            // Incorrect password
            if (!dto.getPassword().equals(user.getPassword())) {
                LOGGER.debug("login(): Incorrect password");
                return Response.status(Status.UNAUTHORIZED).build();
            }

            // Correct username and password, so generate a token, add it to the USER table
            // and return as a cookie
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            em.merge(user);
            em.getTransaction().commit();

            LOGGER.debug("login(): Login was successfull");
            NewCookie authCookie = new NewCookie("auth", token);
            return Response.ok(UserMapper.toDto(user)).cookie(authCookie).build();

        } finally {
            em.close();
        }
    }

    @POST
    @Path("bookings")
    public Response makeBooking(BookingRequestDTO dto, @CookieParam("auth") Cookie token) {
        LOGGER.debug("makeBooking(): Making booking for concert with id: " + dto.getConcertId() + " on date: "
                + dto.getDate().toString());

        // User is not logged in (no auth cookie in request)
        if (token == null) {
            LOGGER.debug("makeBooking(): Not logged in");
            return Response.status(Status.UNAUTHORIZED).build();
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Concert concert = em.find(Concert.class, dto.getConcertId());

            // Concert doesn't exist
            if (concert == null) {
                LOGGER.debug("makeBooking(): Concert with id: " + dto.getConcertId() + " does not exist");
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Concert isn't scheduled on the specified date
            if (!concert.getDates().contains(dto.getDate())) {
                LOGGER.debug("makeBooking(): Concert not scheduled on " + dto.getDate().toString());
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Find all existing bookings for the same concert and date
            // Also set the lock mode to OPTIMISTIC, to prevent a client overriding another
            // client's booking when both bookings are done at the same time
            List<Booking> matchingBookings = em
                    .createQuery("select b from Booking b where b.concertId = :id and b.date = :date", Booking.class)
                    .setParameter("id", dto.getConcertId()).setParameter("date", dto.getDate())
                    .setLockMode(LockModeType.OPTIMISTIC).getResultList();

            // Find all the seats that have already been booked
            List<String> bookedSeatLabels = new ArrayList<>();
            for (Booking b : matchingBookings) {
                for (Seat s : b.getSeats()) {
                    bookedSeatLabels.add(s.getLabel());
                }
            }

            // Check if any of the requested seats have already been booked
            for (String bookedSeat : bookedSeatLabels) {
                for (String requestedSeat : dto.getSeatLabels()) {
                    // At least of of the seats is already booked
                    if (bookedSeat.equals(requestedSeat)) {
                        LOGGER.debug("makeBooking(): Can't book an already booked seat");
                        return Response.status(Status.FORBIDDEN).build();
                    }
                }
            }

            // None of the seats are booked, so book them
            List<Seat> seats = em
                    .createQuery("select s from Seat s where s.date = :date and s.label in :labels", Seat.class)
                    .setParameter("date", dto.getDate()).setParameter("labels", dto.getSeatLabels()).getResultList();

            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", token.getValue().toString()).getSingleResult();

            Booking newBooking = new Booking(dto.getConcertId(), dto.getDate(), seats, user.getId());
            em.persist(newBooking);

            // Update isBooked for all seats in database (isBooked is set inside Booking
            // constructor)
            for (Seat s : seats) {
                em.merge(s);
            }

            em.getTransaction().commit();

            // Notify any subscribers if necessary
            if (!subscriptions.isEmpty()) {
                LOGGER.debug("makeBooking(): Checking to notify subscribers");

                matchingBookings.add(newBooking);

                // Find all the seats that match the booking
                List<Seat> bookedSeats = new ArrayList<>();
                for (Booking b : matchingBookings) {
                    bookedSeats.addAll(b.getSeats());
                }

                // Notify subscribers if their seat threshold is passed
                List<Subscription> subs = new ArrayList<>();

                for (Subscription sub : subscriptions) {
                    if ((sub.getSubDto().getConcertId() == dto.getConcertId())
                            && (sub.getSubDto().getDate().equals(dto.getDate()))) {
                        if (sub.getSubDto().getPercentageBooked() < ((100 * bookedSeats.size())
                                / TheatreLayout.NUM_SEATS_IN_THEATRE)) {
                            LOGGER.debug("makeBooking(): Threshold reached: Notifying subscriber");
                            subs.add(sub);
                        }
                    }
                }
                if (!subs.isEmpty()) {
                    postToSubs(subs, TheatreLayout.NUM_SEATS_IN_THEATRE - bookedSeats.size());
                }
            }

            LOGGER.debug("makeBooking(): Successfully made booking: " + newBooking.toString());
            NewCookie tokenCookie = new NewCookie(token);
            return Response.created(URI.create("concert-service/bookings/" + newBooking.getId())).cookie(tokenCookie)
                    .build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("bookings/{id}")
    public Response getBooking(@PathParam("id") Long id, @CookieParam("auth") Cookie token) {
        LOGGER.debug("getBooking(): Getting booking with id: " + id);

        // User is not logged in (no auth cookie in request)
        if (token == null) {
            LOGGER.debug("getBooking(): Not logged in");
            return Response.status(Status.UNAUTHORIZED).build();
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Booking booking = em.find(Booking.class, id);

            // Booking with that id doesn't exist
            if (booking == null) {
                LOGGER.debug("getBooking(): No booking with id: " + id + " exists");
                return Response.status(Status.NOT_FOUND).build();
            }

            // Assume user exists
            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", token.getValue().toString()).getSingleResult();

            // User is trying to access a booking not belonging to him
            if (!booking.getUserId().equals(user.getId())) {
                LOGGER.debug("getBooking(): Cannot access another user's booking");
                return Response.status(Status.FORBIDDEN).build();
            }

            return Response.ok(BookingMapper.toDto(booking)).build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("bookings")
    public Response getAllBookings(@CookieParam("auth") Cookie token) {

        // User is not logged in (no auth cookie in request)
        if (token == null) {
            LOGGER.debug("getBooking(): Not logged in");
            return Response.status(Status.UNAUTHORIZED).build();
        }

        LOGGER.debug("getBooking(): Getting all bookings for user with token: " + token.getValue());

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // Assume user exists
            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", token.getValue().toString()).getSingleResult();

            List<Booking> bookings = em.createQuery("select b from Booking b where b.userId = :id", Booking.class)
                    .setParameter("id", user.getId()).getResultList();

            List<BookingDTO> dtos = new ArrayList<>();
            for (Booking b : bookings) {
                dtos.add(BookingMapper.toDto(b));
            }

            GenericEntity<List<BookingDTO>> entity = new GenericEntity<List<BookingDTO>>(dtos) {
            };

            return Response.ok(entity).build();

        } finally {
            em.close();
        }
    }

    @GET
    @Path("seats/{date}")
    public Response getSeats(@PathParam("date") String date, @QueryParam("status") BookingStatus status) {
        LOGGER.debug("getSeats(): Getting " + status.toString() + " seats for date: " + date + " that are " + status);

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            LocalDateTime ldt = LocalDateTime.parse(date, DATE_FORMATTER);
            List<Seat> seats;

            if ((status != null) && (status != BookingStatus.Any)) {
                // Get only booked/non-booked seats for the date
                boolean isBooked = (status == BookingStatus.Booked);

                seats = em.createQuery("select s from Seat s where s.date = :date and s.isBooked = :status", Seat.class)
                        .setParameter("date", ldt).setParameter("status", isBooked).getResultList();

            } else {
                // Get all seats for the date
                seats = em.createQuery("select s from Seat s where s.date = :date", Seat.class)
                        .setParameter("date", ldt).getResultList();
            }

            List<SeatDTO> dtos = new ArrayList<>();
            for (Seat c : seats) {
                dtos.add(SeatMapper.toDto(c));
            }

            LOGGER.debug("getSeats(): Found " + dtos.size() + " " + status.toString() + " seats");

            GenericEntity<List<SeatDTO>> entity = new GenericEntity<List<SeatDTO>>(dtos) {
            };

            return Response.ok(entity).build();

        } finally {
            em.close();
        }
    }

    @POST
    @Path("subscribe/concertInfo")
    public void subscribe(ConcertInfoSubscriptionDTO dto, @CookieParam("auth") Cookie token,
            @Suspended AsyncResponse resp) {
        LOGGER.debug("subscribe(): Attempting to subscribe");

        // User is not logged in (no auth cookie in request)
        if (token == null) {
            LOGGER.debug("subscribe(): Not logged in");
            resp.resume(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            Concert concert = em.find(Concert.class, dto.getConcertId());

            // Specified concert doesn't exist
            if (concert == null) {
                LOGGER.debug("subscribe(): Concert with id: " + dto.getConcertId() + " does not exists");
                resp.resume(Response.status(Response.Status.BAD_REQUEST).build());
            }

            // Concert isn't on the specified date
            if (!concert.getDates().contains(dto.getDate())) {
                LOGGER.debug("subscribe(): Concert is not on date: " + dto.getDate().toString());
                resp.resume(Response.status(Response.Status.BAD_REQUEST).build());
            }

            // Find all bookings for that concert and date
            List<Booking> matchingBookings = em
                    .createQuery("select b from Booking b where b.concertId = :id and b.date = :date", Booking.class)
                    .setParameter("id", dto.getConcertId()).setParameter("date", dto.getDate()).getResultList();
.
            // Find all booked seats for that concert and date
            List<Seat> bookedSeats = new ArrayList<>();
            for (Booking b : matchingBookings) {
                bookedSeats.addAll(b.getSeats());
            }
            LOGGER.debug("subscribe(): Number of booked seats: " + bookedSeats.size());
            LOGGER.debug("subscribe(): Threshold: " + dto.getPercentageBooked() + "%");

            if (dto.getPercentageBooked() < ((100 * bookedSeats.size()) / TheatreLayout.NUM_SEATS_IN_THEATRE)) {
                LOGGER.debug("subscribe(): Threshold reached: Notifying subscriber");
                ConcertInfoNotificationDTO infoDto = new ConcertInfoNotificationDTO(
                        TheatreLayout.NUM_SEATS_IN_THEATRE - bookedSeats.size());
                resp.resume(Response.ok(infoDto).build());
                return;
            }

            // Otherwise add to subscriber list to notify later
            LOGGER.debug("subscribe(): Successfully subscribed");
            subscriptions.add(new Subscription(dto, resp));
        } finally {
            em.close();
        }
    }

    @POST
    public void postToSubs(List<Subscription> subs, int numSeatsRemaining) {
        LOGGER.debug("postToSubs(): Notifying " + subs.size() + " subscribers");

        synchronized (subscriptions) {
            for (Subscription sub : subs) {
                LOGGER.debug("postToSubs(): Notifying sub for concert id: " + sub.getSubDto().getConcertId()
                        + " and threshold: " + sub.getSubDto().getPercentageBooked() + "%");

                ConcertInfoNotificationDTO dto = new ConcertInfoNotificationDTO(numSeatsRemaining);
                sub.getResponse().resume(Response.ok(dto).build());

                subscriptions.remove(sub);
            }
        }
    }

}
