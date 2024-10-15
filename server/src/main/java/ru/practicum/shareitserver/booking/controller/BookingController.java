package ru.practicum.shareitserver.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingSaveDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingState;
import ru.practicum.shareitserver.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public Booking save(@RequestHeader(X_SHARER_USER_ID) Long userId,
                        @RequestBody BookingSaveDto bookingSaveDto) {
        log.info("BookingController: save is called by userId={}, itemId={}", userId, bookingSaveDto.getItemId());
        Booking booking = bookingService.save(userId, bookingSaveDto);
        log.info("BookingController: booking saved successfully for userId={}, bookingId={}", userId, booking.getId());
        return booking;
    }

    @PatchMapping("{bookingId}")
    public Booking responseToRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        log.info("BookingController: responseToRequest is called by userId={} for bookingId={}, approved={}", userId, bookingId, approved);
        Booking booking = bookingService.responseToRequest(userId, bookingId, approved);
        log.info("BookingController: responseToRequest successful for bookingId={}, newStatus={}", bookingId, booking.getStatus());
        return booking;
    }

    @GetMapping("{bookingId}")
    public BookingDto findBookingById(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                      @PathVariable long bookingId) {
        log.info("BookingController: findBookingById is called by userId={} for bookingId={}", bookerId, bookingId);
        BookingDto bookingDto = bookingService.findBookingById(bookerId, bookingId);
        log.info("BookingController: findBookingById successful for bookingId={}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<Booking> findAllForUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                        @RequestParam BookingState state) {
        log.info("BookingController: findAllForUser is called by userId={} with state={}", userId, state);
        List<Booking> bookingList = bookingService.findAllForUser(userId, state);
        log.info("BookingController: findAllForUser successful for userId={}, totalBookings={}", userId, bookingList.size());
        return bookingList;
    }

    @GetMapping("owner")
    public List<Booking> findReservations(@RequestHeader(X_SHARER_USER_ID) long userId,
                                          @RequestParam BookingState state) {
        log.info("BookingController: findReservations is called by ownerId={} with state={}", userId, state);
        List<Booking> reservationsList = bookingService.findReservations(userId, state);
        log.info("BookingController: findReservations successful for ownerId={}, totalReservations={}", userId, reservationsList.size());
        return reservationsList;
    }
}