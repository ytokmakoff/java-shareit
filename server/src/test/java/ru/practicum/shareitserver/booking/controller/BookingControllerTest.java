package ru.practicum.shareitserver.booking.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingState;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.exception.booking.BookingAccessDeniedException;
import ru.practicum.shareitserver.exception.booking.BookingConflictException;
import ru.practicum.shareitserver.exception.booking.BookingNotFoundException;
import ru.practicum.shareitserver.exception.booking.InvalidBookingPeriodException;
import ru.practicum.shareitserver.exception.controller.ExceptionHandlingController;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mvc;

    private User owner;
    private User booker;
    private Booking booking;
    private Item item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();

        owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                booker,
                BookingStatus.WAITING
        );

        bookingDto = new BookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
    }

    @Test
    void saveBooking_ShouldReturnOkStatus() throws Exception {
        when(bookingService.save(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header(X_SHARER_USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void responseToRequest_ShouldReturnOkStatus() throws Exception {
        long userId = 2L;
        long bookingId = booking.getId();
        boolean approved = true;

        when(bookingService.responseToRequest(userId, bookingId, approved))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(X_SHARER_USER_ID, userId)
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void findBookingById_ShouldReturnOkStatus() throws Exception {
        long bookerId = 2L;
        long bookingId = booking.getId();

        when(bookingService.findBookingById(bookerId, bookingId))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(X_SHARER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void findAllBookingsForUser_ShouldReturnOkStatus() throws Exception {
        long userId = 2L;
        BookingState state = BookingState.ALL;

        when(bookingService.findAllForUser(userId, state))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .param("state", state.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }

    @Test
    void findReservations_ShouldReturnOkStatus() throws Exception {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        when(bookingService.findReservations(userId, state))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, userId)
                        .param("state", state.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }

    @Test
    void findBookingById_ShouldReturnForbiddenStatus_WhenAccessDenied() throws Exception {
        User notOwner = new User();
        notOwner.setId(2L);
        notOwner.setName("notOwner");
        notOwner.setEmail("notOwner@email.com");
        when(bookingService.findBookingById(notOwner.getId(), booking.getId()))
                .thenThrow(new BookingAccessDeniedException(
                        "access denied for booking with id " + booking.getId() + " for user with id " + notOwner.getId()
                ));

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", notOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "access denied for booking with id " + booking.getId()
                        + " for user with id " + notOwner.getId()));
    }

    @Test
    void findBookingById_ShouldReturnNotFoundStatus_WhenBookingNotFound() throws Exception {
        long nonExistentBookingId = 999L;
        when(bookingService.findBookingById(booker.getId(), nonExistentBookingId))
                .thenThrow(new BookingNotFoundException("booking with id " + nonExistentBookingId + " not found"));

        // Perform the request
        mvc.perform(get("/bookings/{bookingId}", nonExistentBookingId)
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expecting a 404 status
                .andExpect(jsonPath("$.message").value("booking with id " + nonExistentBookingId + " not found")); // Expecting the error message
    }

    @Test
    void saveBooking_ShouldReturnConflictStatus_WhenBookingConflict() throws Exception {
        when(bookingService.save(any(), any()))
                .thenThrow(new BookingConflictException("booking conflict"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("booking conflict"));
    }

    @Test
    void saveBooking_ShouldReturnBadRequestStatus_WhenInvalidBookingPeriod() throws Exception {
        when(bookingService.save(any(), any()))
                .thenThrow(new InvalidBookingPeriodException("InvalidBookingPeriodException"));

        mvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(booking))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("InvalidBookingPeriodException"));
    }
}
