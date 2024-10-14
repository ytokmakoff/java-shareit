package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingMapper;
import ru.practicum.shareitserver.booking.dto.BookingSaveDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingState;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.booking.BookingAccessDeniedException;
import ru.practicum.shareitserver.exception.booking.BookingConflictException;
import ru.practicum.shareitserver.exception.booking.BookingNotFoundException;
import ru.practicum.shareitserver.exception.booking.InvalidBookingPeriodException;
import ru.practicum.shareitserver.exception.item.ItemAccessDeniedException;
import ru.practicum.shareitserver.exception.item.ItemNotFoundException;
import ru.practicum.shareitserver.exception.item.ItemUnavailableException;
import ru.practicum.shareitserver.exception.user.UserNotFoundException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking save(Long bookerId, BookingSaveDto bookingSaveDto) {
        log.info("BookingServiceImpl: attempting to save booking for user with id: {}", bookerId);

        User booker = userRepository.findById(bookerId).orElseThrow(() -> {
            log.warn("BookingServiceImpl: user not found with id: {}", bookerId);
            return new UserNotFoundException("User with id " + bookerId + " not found");
        });
        log.info("BookingServiceImpl: found user with id: {}", bookerId);

        Item item = itemRepository.findById(bookingSaveDto.getItemId()).orElseThrow(() -> {
            log.warn("BookingServiceImpl: item not found with id: {}", bookingSaveDto.getItemId());
            return new ItemNotFoundException("Item with id " + bookingSaveDto.getItemId() + " not found");
        });
        log.info("BookingServiceImpl: found item with id: {}", bookingSaveDto.getItemId());

        if (!item.getAvailable()) {
            log.warn("BookingServiceImpl: item with id: {} is not available", item.getId());
            throw new ItemUnavailableException("Item is not available");
        }

        if (bookingSaveDto.getEnd().isBefore(bookingSaveDto.getStart())) {
            log.warn("BookingServiceImpl: invalid booking period for user with id: {} - end time is before start time", bookerId);
            throw new InvalidBookingPeriodException("End time cannot be before start time");
        }

        boolean hasConflictingBookings = bookingRepository.existsByItemAndPeriod(item.getId(), bookingSaveDto.getStart(), bookingSaveDto.getEnd());
        if (hasConflictingBookings) {
            log.warn("BookingServiceImpl: booking period conflicts with an existing booking for item with id: {}", item.getId());
            throw new BookingConflictException("Booking period conflicts with an existing booking");
        }

        Booking booking = new Booking(
                bookingSaveDto.getStart(),
                bookingSaveDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );

        Booking savedBooking = bookingRepository.save(booking);
        log.info("BookingServiceImpl: successfully saved booking with id: {}", savedBooking.getId());

        return savedBooking;
    }

    @Override
    @Transactional
    public Booking responseToRequest(long userId, long bookingId, boolean approved) {
        log.info("BookingServiceImpl: attempting to respond to booking request with id: {} by user with id: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("BookingServiceImpl: booking not found with id: {}", bookingId);
            return new BookingNotFoundException("Booking with id " + bookingId + " not found");
        });
        log.info("BookingServiceImpl: found booking with id: {}", bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            log.warn("BookingServiceImpl: user with id: {} is not the owner of the item with id: {}", userId, booking.getItem().getId());
            throw new ItemAccessDeniedException(
                    "User with id " + userId + " is not the owner of the item with id " + booking.getItem().getId());
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            log.warn("BookingServiceImpl: booking with id: {} is already processed with status: {}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking is already processed: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("BookingServiceImpl: updated booking with id: {} to status: {}", bookingId, booking.getStatus());

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("BookingServiceImpl: successfully responded to booking request with id: {} with approval status: {}", bookingId, approved);

        return updatedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingById(long userId, long bookingId) {
        log.info("BookingServiceImpl: attempting to find booking with id: {} for user with id: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("BookingServiceImpl: booking not found with id: {}", bookingId);
            return new BookingNotFoundException("booking with id " + bookingId + " not found");
        });
        log.info("BookingServiceImpl: found booking with id: {}", bookingId);

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (bookerId != userId && ownerId != userId) {
            log.warn("BookingServiceImpl: access denied for booking with id: {} for user with id: {}", bookingId, userId);
            throw new BookingAccessDeniedException(
                    "access denied for booking with id " + bookingId + " for user with id " + userId);
        }

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        log.info("BookingServiceImpl: successfully retrieved booking with id: {}", bookingId);

        return bookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllForUser(long userId, BookingState state) {
        log.info("BookingServiceImpl: attempting to find all bookings for user with id: {} and state: {}", userId, state);

        List<Booking> bookings = switch (state) {
            case ALL -> {
                log.info("BookingServiceImpl: fetching all bookings for user with id: {}", userId);
                yield bookingRepository.findAllByUserId(userId);
            }
            case CURRENT -> {
                log.info("BookingServiceImpl: fetching current bookings for user with id: {}", userId);
                yield bookingRepository.findCurrentByUserId(userId);
            }
            case PAST -> {
                log.info("BookingServiceImpl: fetching past bookings for user with id: {}", userId);
                yield bookingRepository.findPastByUserId(userId);
            }
            case FUTURE -> {
                log.info("BookingServiceImpl: fetching future bookings for user with id: {}", userId);
                yield bookingRepository.findFutureByUserId(userId);
            }
            case WAITING -> {
                log.info("BookingServiceImpl: fetching waiting bookings for user with id: {}", userId);
                yield bookingRepository.findWaitingByUserId(userId);
            }
            case REJECTED -> {
                log.info("BookingServiceImpl: fetching rejected bookings for user with id: {}", userId);
                yield bookingRepository.findRejectedByUserId(userId);
            }
        };

        log.info("BookingServiceImpl: successfully retrieved {} bookings for user with id: {}", bookings.size(), userId);

        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findReservations(long userId, BookingState state) {
        log.info("BookingServiceImpl: attempting to find reservations for user with id: {} and state: {}", userId, state);

        List<Booking> bookings = switch (state) {
            case ALL -> {
                log.info("BookingServiceImpl: fetching all reservations for user with id: {}", userId);
                yield bookingRepository.findAllReservationsByUserId(userId);
            }
            case CURRENT -> {
                log.info("BookingServiceImpl: fetching current reservations for user with id: {}", userId);
                yield bookingRepository.findCurrentReservationsByUserId(userId);
            }
            case PAST -> {
                log.info("BookingServiceImpl: fetching past reservations for user with id: {}", userId);
                yield bookingRepository.findPastReservationsByUserId(userId);
            }
            case FUTURE -> {
                log.info("BookingServiceImpl: fetching future reservations for user with id: {}", userId);
                yield bookingRepository.findFutureReservationsByUserId(userId);
            }
            case WAITING -> {
                log.info("BookingServiceImpl: fetching waiting reservations for user with id: {}", userId);
                yield bookingRepository.findWaitingReservationsByUserId(userId);
            }
            case REJECTED -> {
                log.info("BookingServiceImpl: fetching rejected reservations for user with id: {}", userId);
                yield bookingRepository.findRejectedReservationsByUserId(userId);
            }
        };

        if (bookings == null || bookings.isEmpty()) {
            log.warn("BookingServiceImpl: no reservations found for user with id: {}", userId);
            throw new BookingNotFoundException("No reservations found for user " + userId);
        }
        return bookings;
    }
}