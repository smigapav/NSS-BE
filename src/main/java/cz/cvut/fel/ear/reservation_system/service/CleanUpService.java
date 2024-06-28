package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.exception.InvalidApiKeyException;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
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

    /**
     * API key for authentication.
     */
    @Value("${apiKey}")
    private String apiKey;

    /**
     * Deletes all not paid reservations that are less than one day from now.
     *
     * @param apiKey the API key for authentication
     * @throws InvalidApiKeyException if the provided API key is invalid
     */
    @Transactional
    public void deleteNotPaidReservationsLessThanOneDayFromNow(String apiKey) {
        if (apiKey != null && !apiKey.equals(this.apiKey)) {
            throw new InvalidApiKeyException(HttpStatus.UNAUTHORIZED, "Invalid API key");
        }
        LocalDateTime oneDayFromNow = LocalDateTime.now().plusDays(1);
        List<Reservation> reservations = reservationDao.findByStatusAndDateFromBefore(ReservationStatus.NOT_PAID, oneDayFromNow);
        reservationDao.deleteAll(reservations);
        System.out.println("Deleted " + reservations.size() + " not paid reservations less than one day from now.");
    }
}
