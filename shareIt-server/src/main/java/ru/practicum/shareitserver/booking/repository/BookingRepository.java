package ru.practicum.shareitserver.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareitserver.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 " +
           "ORDER BY b.start DESC")
    List<Booking> findAllByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP BETWEEN b.start AND b.end " +
           "ORDER BY b.start DESC")
    List<Booking> findCurrentByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP > b.end " +
           "ORDER BY b.start DESC")
    List<Booking> findPastByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP < b.start " +
           "ORDER BY b.start DESC")
    List<Booking> findFutureByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND b.status = 'WAITING' " +
           "ORDER BY b.start DESC")
    List<Booking> findWaitingByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND b.status = 'REJECTED' " +
           "ORDER BY b.start DESC")
    List<Booking> findRejectedByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "ORDER BY b.start DESC")
    List<Booking> findAllReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP BETWEEN b.start AND b.end " +
           "ORDER BY b.start")
    List<Booking> findCurrentReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP > b.end " +
           "ORDER BY b.start")
    List<Booking> findPastReservationsByUserId(long userId);


    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP < b.start " +
           "ORDER BY b.start")
    List<Booking> findFutureReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND b.status = 'WAITING' " +
           "ORDER BY b.start")
    List<Booking> findWaitingReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND b.status = 'REJECTED' " +
           "ORDER BY b.start")
    List<Booking> findRejectedReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.id = ?1 AND b.booker.id = ?2 AND b.end < CURRENT TIMESTAMP")
    Optional<Booking> findByItemIdAndBookerIdAndEndBeforeNow(long itemId, long bookerId);

    @Query("SELECT COUNT(b) > 0 " +
           "FROM Booking b " +
           "WHERE b.item.id = ?1 AND " +
           "(b.start < ?3 AND b.end > ?2)")
    Boolean existsByItemAndPeriod(long itemId, LocalDateTime start, LocalDateTime end);
}