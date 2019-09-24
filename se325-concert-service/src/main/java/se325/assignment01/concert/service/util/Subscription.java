package se325.assignment01.concert.service.util;

import javax.ws.rs.container.AsyncResponse;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;

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