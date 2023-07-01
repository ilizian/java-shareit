package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.user.dto.UserDtoBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void init() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5), 1L, 1L);
        bookingDtoResponse = new BookingDtoResponse(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(5), new UserDtoBooking(), Status.WAITING, new ItemDtoBooking());
    }

    @Test
    void creatBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDtoResponse);
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDtoResponse);
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void getBookingsOfUserTest() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoResponse));
        mockMvc.perform(get("/bookings?from=1&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void getForOwnerTest() throws Exception {
        when(bookingService.getForOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoResponse));
        mockMvc.perform(get("/bookings/owner?from=1&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class));
    }
}
