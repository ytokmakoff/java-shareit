package ru.practicum.shareitgateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
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

    public ResponseEntity<Object> save(Long userId, BookingSaveDto bookingSaveDto) {
        return post("", userId, bookingSaveDto);
    }

    public ResponseEntity<Object> responseToRequest(Long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> findBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllForUser(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );

        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> findReservations(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );

        return get("/owner?state={state}", userId, parameters);
    }
}