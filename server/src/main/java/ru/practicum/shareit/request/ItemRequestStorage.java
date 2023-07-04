package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIsNot(User user, Pageable pageable);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long userId);

}
