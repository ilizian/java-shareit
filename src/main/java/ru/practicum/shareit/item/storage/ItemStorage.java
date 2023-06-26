package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "SELECT * FROM items WHERE available = TRUE AND " +
            "(LOWER(description) LIKE '%' || ?1 || '%' OR LOWER(name) LIKE '%' || ?1 || '%')",
            nativeQuery = true)
    List<Item> search(String search);
}
