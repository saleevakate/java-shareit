package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);
    Optional<Item> findById(int id);
    List<Item> findAllByOwnerId(int ownerId);
    List<Item> findAll();
    Item update(Item item);
    void deleteById(int id);
    boolean existsById(int id);
}