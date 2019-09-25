package se325.assignment01.concert.service.util;

import javax.ws.rs.container.AsyncResponse;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;

/**
 * Represents a subscription that a user makes. The subscription is for the user
 * to be notified when a certain percentage of seats has been booked for a
 * particular concert on a particular date.
 * 
 * A Subscription is used to keep track of a ConcertInfoSubscriptionDTO object
 * and an AsyncResponse object. The ConcertInfoSubscriptionDTO object contains
 * information related to what concert the user wants to be notified about,
 * while the AsyncResponse object is used to return a response to the user when
 * ready.
 */
public class Subscription {

    ConcertInfoSubscriptionDTO subDto;
    AsyncResponse response;

    public Subscription(ConcertInfoSubscriptionDTO subDto, AsyncResponse response) {
        this.subDto = subDto;
        this.response = response;
    }

    public ConcertInfoSubscriptionDTO getSubDto() {
        return subDto;
    }

    public AsyncResponse getResponse() {
        return response;
    }

}