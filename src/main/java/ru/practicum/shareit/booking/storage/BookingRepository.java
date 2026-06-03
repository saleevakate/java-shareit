package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerId(Integer bookerId, Sort sort);

    List<Booking> findByItemOwnerId(Integer ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentByBooker(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByBooker(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByBooker(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentByOwner(@Param("ownerId") Integer ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByOwner(@Param("ownerId") Integer ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByOwner(@Param("ownerId") Integer ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND " +
            "(b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUser(@Param("bookingId") Integer bookingId, @Param("userId") Integer userId);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Integer itemId, Integer bookerId, StatusBooking status, LocalDateTime endBefore);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' AND b.start < :now ORDER BY b.start DESC")
    List<Booking> findLastBooking(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);
}