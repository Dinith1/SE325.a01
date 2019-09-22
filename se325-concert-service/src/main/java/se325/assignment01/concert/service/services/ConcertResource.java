package se325.assignment01.concert.service.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.mapper.ConcertSummaryMapper;

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
                // throw new WebApplicationException(Response.Status.NOT_FOUND);
                return Response.status(Status.NOT_FOUND).build();
            }

            LOGGER.debug("getConcert(): Found concert with id: " + id);
            return Response.ok(concert).build();

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

            LOGGER.debug("getAllConcerts(): Found " + concerts.size() + " concerts");

            GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {
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
            em.close();
        }

    }

}
