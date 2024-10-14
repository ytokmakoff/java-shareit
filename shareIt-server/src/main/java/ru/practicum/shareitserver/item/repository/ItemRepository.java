package ru.practicum.shareitserver.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemWithBookingDateDto;
import ru.practicum.shareitserver.item.dto.ItemWithCommentsDto;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT new ru.practicum.shareitserver.item.dto.ItemDto(i.id, i.name, i.description, i.available, i.requestId) " +
           "FROM Item i " +
           "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
           "AND i.available = TRUE")
    List<ItemDto> search(String text);

    @Query("SELECT new ru.practicum.shareitserver.item.dto.ItemWithBookingDateDto(i.id, i.name, i.description, i.available," +
           "(SELECT MAX(b.start) FROM Booking b WHERE b.item.id = i.id AND b.end < CURRENT_TIMESTAMP)," +
           "(SELECT MIN(b.start) FROM Booking b WHERE b.item.id = i.id AND b.start > CURRENT_TIMESTAMP)) " +
           "FROM Item i JOIN i.owner u WHERE u.id = ?1 ")
    List<ItemWithBookingDateDto> allItemsFromUser(long userId);

    @Query("SELECT new ru.practicum.shareitserver.item.dto.ItemWithCommentsDto(i.id, i.name, i.description, i.available, " +
           "(SELECT MIN(b.start) FROM Booking b WHERE b.item.id = i.id AND b.start > CURRENT_TIMESTAMP), " +
           "(SELECT MIN(b.start) FROM Booking b WHERE b.item.id = i.id AND b.start > CURRENT_TIMESTAMP), " +
           "null) " +
           "FROM Item i " +
           "WHERE i.id = ?1")
    Optional<ItemWithCommentsDto> findWithCommentsById(long id);
}