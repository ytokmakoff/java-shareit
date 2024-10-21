package ru.practicum.shareitserver.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.item.ItemNotFoundException;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
public class ItemServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    private User itemOwner;
    private Item availableItem;

    @BeforeEach
    void setUp() {
        itemOwner = new User();
        itemOwner.setName("Owner");
        itemOwner.setEmail("owner@example.com");
        em.persist(itemOwner);

        availableItem = new Item();
        availableItem.setName("Test Item");
        availableItem.setDescription("Test Description");
        availableItem.setAvailable(true);
        availableItem.setOwner(itemOwner);
        em.persist(availableItem);
    }

    @Test
    void saveItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Item savedItem = itemService.save(itemDto, itemOwner.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item foundItem = query.setParameter("id", savedItem.getId()).getSingleResult();

        assertThat(foundItem.getName(), equalTo(itemDto.getName()));
        assertThat(foundItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(foundItem.getOwner().getId(), equalTo(itemOwner.getId()));
    }

    @Test
    void updateItem() {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Updated Item");
        updateItemDto.setDescription("Updated Description");

        Item updatedItem = itemService.update(availableItem.getId(), updateItemDto, itemOwner.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item foundItem = query.setParameter("id", updatedItem.getId()).getSingleResult();

        assertThat(foundItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(foundItem.getDescription(), equalTo(updateItemDto.getDescription()));
    }

    @Test
    void findById() {
        ItemWithCommentsDto foundItemDto = itemService.findById(availableItem.getId());

        assertThat(foundItemDto.getId(), equalTo(availableItem.getId()));
        assertThat(foundItemDto.getName(), equalTo(availableItem.getName()));
        assertThat(foundItemDto.getDescription(), equalTo(availableItem.getDescription()));
    }

    @Test
    void allItemsFromUser() {
        List<ItemWithBookingDateDto> items = itemService.allItemsFromUser(itemOwner.getId());

        assertThat(items.get(0).getId(), equalTo(availableItem.getId()));
    }

    @Test
    void searchItems() {
        List<ItemDto> items = itemService.search("Test");

        assertThat(items.get(0).getName(), containsString("Test"));
    }

    @Test
    void searchWithBlankText() {
        String blankSearchText = " ";

        List<ItemDto> result = itemService.search(blankSearchText);

        assertThat(result, empty());
    }

    @Test
    void saveComment_ItemNotFound() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        Comment comment = new Comment();
        comment.setText("This is a test comment");

        assertThatThrownBy(() -> itemService.saveComment(comment, 999L, user.getId()))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item with id 999 not found");
    }

    @Test
    void findById_ItemNotFound() {
        long nonExistentItemId = 999L;

        assertThatThrownBy(() -> itemService.findById(nonExistentItemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item with id " + nonExistentItemId + " not found");
    }
}
