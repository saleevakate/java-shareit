package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private User otherUser;
    private Item item;
    private Booking booking;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking currentBooking;

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

        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

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

        pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(StatusBooking.APPROVED);
        pastBooking = bookingRepository.save(pastBooking);

        futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(5));
        futureBooking.setEnd(LocalDateTime.now().plusDays(6));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(StatusBooking.APPROVED);
        futureBooking = bookingRepository.save(futureBooking);

        currentBooking = new Booking();
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(StatusBooking.APPROVED);
        currentBooking = bookingRepository.save(currentBooking);
    }

    @Test
    void findByBookerId_shouldReturnBookingsSortedByStartDesc() {
        List<Booking> bookings = bookingRepository.findByBookerId(
                booker.getId(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b -> b.getBooker().getId() == booker.getId());
    }

    @Test
    void findByItemOwnerId_shouldReturnBookingsSortedByStartDesc() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(
                owner.getId(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b -> b.getItem().getOwner().getId() == owner.getId());
    }

    @Test
    void findCurrentByBooker_shouldReturnCurrentBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findCurrentByBooker(booker.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now));
    }

    @Test
    void findFutureByBooker_shouldReturnFutureBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findFutureByBooker(booker.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b -> b.getStart().isAfter(now));
    }

    @Test
    void findPastByBooker_shouldReturnPastBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findPastByBooker(booker.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b -> b.getEnd().isBefore(now));
    }

    @Test
    void findCurrentByOwner_shouldReturnCurrentBookingsForOwnerItems() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findCurrentByOwner(owner.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b ->
                b.getItem().getOwner().getId() == owner.getId() &&
                        b.getStart().isBefore(now) && b.getEnd().isAfter(now));
    }

    @Test
    void findFutureByOwner_shouldReturnFutureBookingsForOwnerItems() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findFutureByOwner(owner.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b ->
                b.getItem().getOwner().getId() == owner.getId() &&
                        b.getStart().isAfter(now));
    }

    @Test
    void findPastByOwner_shouldReturnPastBookingsForOwnerItems() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findPastByOwner(owner.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allMatch(b ->
                b.getItem().getOwner().getId() == owner.getId() &&
                        b.getEnd().isBefore(now));
    }

    @Test
    void findByIdAndUser_shouldReturnBooking_whenUserIsBooker() {
        Optional<Booking> found = bookingRepository.findByIdAndUser(booking.getId(), booker.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdAndUser_shouldReturnBooking_whenUserIsOwner() {
        Optional<Booking> found = bookingRepository.findByIdAndUser(booking.getId(), owner.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findByIdAndUser_shouldReturnEmpty_whenUserHasNoAccess() {
        Optional<Booking> found = bookingRepository.findByIdAndUser(booking.getId(), otherUser.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByIdAndUser_shouldReturnEmpty_whenBookingNotFound() {
        Optional<Booking> found = bookingRepository.findByIdAndUser(999, booker.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore_shouldReturnTrue() {
        LocalDateTime now = LocalDateTime.now();
        boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(), booker.getId(), StatusBooking.APPROVED, now);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore_shouldReturnFalse() {
        LocalDateTime now = LocalDateTime.now();
        boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                999, booker.getId(), StatusBooking.APPROVED, now);

        assertThat(exists).isFalse();
    }

    @Test
    void findLastBooking_shouldReturnLastCompletedBooking() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findLastBooking(item.getId(), now);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findLastBooking_shouldReturnEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findLastBooking(999, now);
        assertThat(bookings).isEmpty();
    }

    @Test
    void findNextBooking_shouldReturnNextUpcomingBooking() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findNextBooking(item.getId(), now);

        assertThat(bookings).isNotEmpty();

        Booking nextBooking = bookings.get(0);

        assertThat(nextBooking.getStart()).isAfter(now);
        assertThat(nextBooking.getStatus()).isEqualTo(StatusBooking.APPROVED);

        for (Booking b : bookings) {
            assertThat(b.getStart()).isAfterOrEqualTo(nextBooking.getStart());
        }
    }

    @Test
    void findNextBooking_shouldReturnEmptyList() {
        LocalDateTime now = LocalDateTime.now().plusYears(10);
        List<Booking> bookings = bookingRepository.findNextBooking(item.getId(), now);
        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndStatus_shouldReturnFilteredBookings() {
        List<Booking> waitingBookings = bookingRepository.findByBookerIdAndStatus(
                booker.getId(), StatusBooking.WAITING, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void findByBookerIdAndStatus_shouldReturnEmptyList_whenNoMatch() {
        List<Booking> rejectedBookings = bookingRepository.findByBookerIdAndStatus(
                booker.getId(), StatusBooking.REJECTED, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(rejectedBookings).isEmpty();
    }

    @Test
    void findByItemOwnerIdAndStatus_shouldReturnFilteredBookings() {
        List<Booking> approvedBookings = bookingRepository.findByItemOwnerIdAndStatus(
                owner.getId(), StatusBooking.APPROVED, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(approvedBookings).hasSize(3);
        assertThat(approvedBookings).allMatch(b -> b.getStatus() == StatusBooking.APPROVED);
    }

    @Test
    void findByItemOwnerIdAndStatus_shouldReturnEmptyList_whenNoMatch() {
        List<Booking> rejectedBookings = bookingRepository.findByItemOwnerIdAndStatus(
                owner.getId(), StatusBooking.REJECTED, Sort.by(Sort.Direction.DESC, "start"));

        assertThat(rejectedBookings).isEmpty();
    }

    @Test
    void save_shouldGenerateId() {
        Booking newBooking = new Booking();
        newBooking.setStart(LocalDateTime.now().plusDays(10));
        newBooking.setEnd(LocalDateTime.now().plusDays(12));
        newBooking.setItem(item);
        newBooking.setBooker(booker);
        newBooking.setStatus(StatusBooking.WAITING);

        Booking saved = bookingRepository.save(newBooking);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void delete_shouldRemoveBooking() {
        bookingRepository.delete(booking);
        Optional<Booking> found = bookingRepository.findById(booking.getId());
        assertThat(found).isEmpty();
    }
}