package ru.practicum.shareitgateway.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.booking.dto.BookingSaveDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;

import java.util.function.Supplier;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        String serverUrl = "http://localhost:8080";

        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory((Supplier<ClientHttpRequestFactory>) any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient(serverUrl, restTemplateBuilder);
    }

    @Test
    void saveBooking_ShouldReturnResponseEntityWithOkStatus() {
        Long userId = 1L;
        BookingSaveDto bookingSaveDto = new BookingSaveDto();

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = bookingClient.save(userId, bookingSaveDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void responseToRequest_ShouldReturnResponseEntityWithOkStatus() {
        Long userId = 1L;
        long bookingId = 123L;
        boolean approved = true;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = bookingClient.responseToRequest(userId, bookingId, approved);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/" + bookingId + "?approved={approved}"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), anyMap());
    }

    @Test
    void findBookingById_ShouldReturnResponseEntityWithOkStatus() {
        Long userId = 1L;
        long bookingId = 123L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/" + bookingId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = bookingClient.findBookingById(userId, bookingId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/" + bookingId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findAllForUser_ShouldReturnResponseEntityWithOkStatus() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = bookingClient.findAllForUser(userId, state);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap());
    }

    @Test
    void findReservations_ShouldReturnResponseEntityWithOkStatus() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/owner?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(mockResponse);


        ResponseEntity<Object> response = bookingClient.findReservations(userId, state);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/owner?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap());
    }
}
