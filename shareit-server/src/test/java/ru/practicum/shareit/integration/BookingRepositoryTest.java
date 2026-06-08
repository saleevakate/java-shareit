package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);
        booking = bookingRepository.save(booking);
    }

    @Test
    void findByBookerId_shouldReturnBookings() {
        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByItemOwnerId_shouldReturnBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(owner.getId(), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(bookings).hasSize(1);
    }

    @Test
    void findByIdAndUser_shouldReturnBooking_whenUserIsBooker() {
        var found = bookingRepository.findByIdAndUser(booking.getId(), booker.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findByIdAndUser_shouldReturnBooking_whenUserIsOwner() {
        var found = bookingRepository.findByIdAndUser(booking.getId(), owner.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findByIdAndUser_shouldReturnEmpty_whenUserHasNoAccess() {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        var found = bookingRepository.findByIdAndUser(booking.getId(), otherUser.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByBookerIdAndStatus_shouldReturnFilteredBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(
                booker.getId(), StatusBooking.WAITING, Sort.by(Sort.Direction.DESC, "start"));
        assertThat(bookings).hasSize(1);
    }
}