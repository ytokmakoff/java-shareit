package ru.practicum.shareitserver.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.dto.BookingDto;
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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    private User booker;
    private Item availableItem;
    private Booking booking;
    private User itemOwner;

    @BeforeEach
    void setUp() {
        itemOwner = new User();
        itemOwner.setName("Item Owner");
        itemOwner.setEmail("owner@example.com");
        itemOwner = userRepository.save(itemOwner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        availableItem = new Item();
        availableItem.setName("Available Item");
        availableItem.setDescription("An item that is available for booking");
        availableItem.setAvailable(true);
        availableItem.setOwner(itemOwner);
        availableItem = itemRepository.save(availableItem);
    }

    @Test
    void saveBooking_ShouldReturnOkStatus() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        Booking savedBooking = bookingService.save(booker.getId(), bookingSaveDto);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", savedBooking.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingSaveDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingSaveDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(availableItem.getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void saveBookingShouldReturnNotFoundException() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(UserNotFoundException.class, () -> bookingService.save(999L, bookingSaveDto));
    }

    @Test
    void saveBookingWhenItemNotAvailable() {
        Item unavailableItem = new Item();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("An item that is not available for booking");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(booker);
        unavailableItem = itemRepository.save(unavailableItem);

        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                unavailableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ItemUnavailableException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void saveBookingBookingInvalidPerion() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(InvalidBookingPeriodException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void saveBookingConflictingBookingTime() {
        BookingSaveDto bookingSaveDto1 = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.save(booker.getId(), bookingSaveDto1);

        BookingSaveDto bookingSaveDto2 = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1).plusHours(1),
                LocalDateTime.now().plusDays(1).plusHours(3)
        );

        assertThrows(BookingConflictException.class, () -> bookingService.save(booker.getId(), bookingSaveDto2));
    }

    @Test
    void saveBookingItemNotFound() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                999L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ItemNotFoundException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void responseToRequestApprove() {
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(LocalDateTime.now().plusDays(1));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(2));
        waitingBooking.setItem(availableItem);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        waitingBooking = bookingRepository.save(waitingBooking);

        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), waitingBooking.getId(), true);

        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(updatedBooking.getId()).isEqualTo(waitingBooking.getId());
    }


    @Test
    void responseToRequestReject() {
        Booking newBooking = new Booking();
        newBooking.setItem(availableItem);
        newBooking.setBooker(booker);
        newBooking.setStart(LocalDateTime.now().plusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(2));
        newBooking.setStatus(BookingStatus.WAITING);
        newBooking = bookingRepository.save(newBooking);

        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), newBooking.getId(), false);

        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(updatedBooking.getId()).isEqualTo(newBooking.getId());
    }


    @Test
    void responseToRequestUserNotOwner() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        User nonOwner = new User();
        nonOwner.setName("NonOwner");
        nonOwner.setEmail("nonowner@example.com");
        User savedNonOwner = userRepository.save(nonOwner);

        assertThrows(ItemAccessDeniedException.class, () -> bookingService.responseToRequest(savedNonOwner.getId(), booking.getId(), true));
    }

    @Test
    void responseToRequestAlreadyProcessed() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), booking.getId(), true);

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThrows(IllegalStateException.class, () -> bookingService.responseToRequest(itemOwner.getId(), booking.getId(), false));
    }


    @Test
    void responseToRequestBookingNotFound() {
        assertThrows(BookingNotFoundException.class, () -> bookingService.responseToRequest(booker.getId(), 999L, true));
    }

    @Test
    void findBookingByIdAsBooker() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        BookingDto bookingDto = bookingService.findBookingById(booker.getId(), booking.getId());

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(availableItem.getId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findBookingByIdAccessDenied() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        User savedUser = userRepository.save(anotherUser);

        assertThrows(BookingAccessDeniedException.class, () -> bookingService.findBookingById(savedUser.getId(), booking.getId()));
    }

    @Test
    void findBookingByIdNotFound() {
        long nonExistentBookingId = 999L;
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingById(booker.getId(), nonExistentBookingId));
    }

    @Test
    void findAllBookingsForUserAll() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().plusDays(1));
        pastBooking.setEnd(LocalDateTime.now().plusDays(2));
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.ALL);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllBookingsForUserCurrent() {
        Booking currentBooking = new Booking();
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        currentBooking.setItem(availableItem);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.CURRENT);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void findAllBookingsForUserPast() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findAllBookingsForUserFuture() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }


    @Test
    void findAllBookingsForUserWaiting() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findAllBookingsForUserRejected() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(LocalDateTime.now().plusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setItem(availableItem);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void findAllReservations() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.ALL);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findCurrentReservations() {
        Booking newBooking = new Booking();

        newBooking.setItem(availableItem);
        newBooking.setBooker(booker);
        newBooking.setStart(LocalDateTime.now().minusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(1));
        newBooking.setStatus(BookingStatus.WAITING);
        newBooking = bookingRepository.save(newBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.CURRENT);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(newBooking.getId());
    }


    @Test
    void findPastReservations() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findFutureReservations() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findWaitingReservations() {
        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findRejectedReservations() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(availableItem);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStart(LocalDateTime.now().plusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void findReservationsNoBookingsFound() {
        long userId = booker.getId();
        BookingState state = BookingState.ALL;

        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findReservations(userId, state),
                "Expected findReservations to throw, but it didn't"
        );

        assertThat(exception.getMessage()).isEqualTo("No reservations found for user " + userId);
    }
}