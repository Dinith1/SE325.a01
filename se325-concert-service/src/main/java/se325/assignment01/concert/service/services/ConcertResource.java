package se325.assignment01.concert.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.ConcertSummaryMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;
import se325.assignment01.concert.service.mapper.UserMapper;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

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
    @Path("/login")
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

            LOGGER.debug("login(): Login was successfull");
            NewCookie authCookie = new NewCookie("auth", token);
            return Response.ok(UserMapper.toDto(user)).cookie(authCookie).build();

        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }
}
