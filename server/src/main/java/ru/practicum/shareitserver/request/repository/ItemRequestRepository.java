package ru.practicum.shareitserver.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByUserId(Long userId);

    @Query("select i from Item i where i.requestId = ?1")
    List<Item> findItemsByRequestId(Long requestId);

    @Query("select ir from ItemRequest ir where ir.id != ?1 order by ir.created desc")
    List<ItemRequest> findAllByUserIdNot(Long userId);

    @Query("select i from Item i where i.requestId in ?1")
    List<Item> findItemsByRequestIds(List<Long> requestIds);

}
