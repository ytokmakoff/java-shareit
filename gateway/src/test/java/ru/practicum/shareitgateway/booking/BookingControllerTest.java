package ru.practicum.shareitgateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.booking.dto.BookingSaveDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 1L;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingClient bookingClient;

    @Test
    void save_ShouldReturnOk() throws Exception {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                1L,
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1)
        );

        when(bookingClient.save(anyLong(), any(BookingSaveDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingSaveDto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).save(any(), any());
    }

    @Test
    void responseToRequest_ShouldReturnOk() throws Exception {
        given(bookingClient.responseToRequest(anyLong(), anyLong(), anyBoolean()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).responseToRequest(USER_ID, BOOKING_ID, true);
    }

    @Test
    void findBookingById_ShouldReturnOk() throws Exception {
        given(bookingClient.findBookingById(anyLong(), anyLong()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findBookingById(USER_ID, BOOKING_ID);
    }

    @Test
    void findAllForUser_ShouldReturnOk() throws Exception {
        given(bookingClient.findAllForUser(anyLong(), any(BookingState.class)))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findAllForUser(USER_ID, BookingState.ALL);
    }

    @Test
    void findReservations_ShouldReturnOk() throws Exception {
        given(bookingClient.findReservations(anyLong(), any(BookingState.class)))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findReservations(USER_ID, BookingState.ALL);
    }
}
