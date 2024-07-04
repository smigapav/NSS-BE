package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.exception.InvalidApiKeyException;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Pipeline;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.DeleteNotPaidReservationsFilter;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.GenericLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for cleaning up reservations.
 * This class provides methods to delete not paid reservations that are less than one day from now.
 */
@Service
@RequiredArgsConstructor
public class CleanUpService {

    /**
     * DAO for accessing reservation data.
     */
    private final ReservationDao reservationDao;

    private final DeleteNotPaidReservationsFilter deleteNotPaidReservationsFilter;


    /**
     * API key for authentication.
     */
    @Value("${apiKey}")
    private String apiKey;

    /**
     * Deletes all not paid reservations that are less than one day from now.
     *
     * @param inputApiKey the API key for authentication
     * @throws InvalidApiKeyException if the provided API key is invalid
     */
    @Transactional
    public void deleteNotPaidReservationsLessThanOneDayFromNow(String inputApiKey) {
        if (inputApiKey != null && !inputApiKey.equals(this.apiKey)) {
            throw new InvalidApiKeyException(HttpStatus.UNAUTHORIZED, "Invalid API key");
        }
        Pipeline<String> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("Starting deletion of not paid reservations less than one day from now", CleanUpService.class.getName(), "deleteNotPaidReservationsLessThanOneDayFromNow"));
        pipeline.addFilter(deleteNotPaidReservationsFilter);
        pipeline.execute(inputApiKey);
    }
}
