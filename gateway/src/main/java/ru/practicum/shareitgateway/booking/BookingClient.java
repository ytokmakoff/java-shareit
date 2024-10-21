package ru.practicum.shareitgateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.booking.dto.Booking;
import ru.practicum.shareitgateway.booking.dto.BookingDto;
import ru.practicum.shareitgateway.booking.dto.BookingSaveDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;
import ru.practicum.shareitgateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Booking> save(Long userId, BookingSaveDto bookingSaveDto) {
        ResponseEntity<Object> response = post("", userId, bookingSaveDto);

        Booking booking = (Booking) response.getBody();

        return new ResponseEntity<>(booking, response.getStatusCode());
    }

    public ResponseEntity<Booking> responseToRequest(Long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        ResponseEntity<Object> response = patch("/" + bookingId + "?approved={approved}", userId, parameters, null);

        Booking booking = (Booking) response.getBody();

        return new ResponseEntity<>(booking, response.getStatusCode());
    }

    public ResponseEntity<BookingDto> findBookingById(long userId, long bookingId) {
        ResponseEntity<Object> response = get("/" + bookingId, userId);

        BookingDto bookingDto = (BookingDto) response.getBody();

        return new ResponseEntity<>(bookingDto, response.getStatusCode());
    }

    public ResponseEntity<Booking> findAllForUser(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );

        ResponseEntity<Object> response = get("?state={state}", userId, parameters);

        Booking booking = (Booking) response.getBody();

        return new ResponseEntity<>(booking, response.getStatusCode());
    }

    public ResponseEntity<Booking> findReservations(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );
        ResponseEntity<Object> response = get("/owner?state={state}", userId, parameters);

        Booking booking = (Booking) response.getBody();

        return new ResponseEntity<>(booking, response.getStatusCode());
    }
}