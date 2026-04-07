package br.com.alugueldecarros.domain.repository;

import br.com.alugueldecarros.domain.model.BaseEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractInMemoryRepository<T extends BaseEntity> {

    private final Map<Long, T> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> listAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(BaseEntity::getId))
                .toList();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
