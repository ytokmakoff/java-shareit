package ru.practicum.shareitgateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingSaveDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                       @RequestBody @Valid BookingSaveDto bookingSaveDto) {
        log.info("save booking {}, userId={}", bookingSaveDto, userId);
        return bookingClient.save(userId, bookingSaveDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> responseToRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                    @PathVariable long bookingId,
                                                    @RequestParam boolean approved) {
        log.info("responseToRequest userId={} bookingId={}, approved={}", userId, bookingId, approved);
        return bookingClient.responseToRequest(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                  @PathVariable long bookingId) {
        log.info("findBookingById userId={} bookingId={}", userId, bookingId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("findAllForUser userId={} state={}", userId, state);
        return bookingClient.findAllForUser(userId, state);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> findReservations(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("findReservations ownerId={} state={}", userId, state);
        return bookingClient.findReservations(userId, state);
    }
}