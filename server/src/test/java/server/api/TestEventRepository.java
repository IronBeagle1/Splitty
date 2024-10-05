package server.api;

import commons.Event;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.EventRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class TestEventRepository implements EventRepository {

    private List<Event> events = new ArrayList<>();
    private List<String> calledMethods = new ArrayList<>();

    //stop missing javadoc method check
    @Override
    public void flush() {

    }

    @Override
    public <S extends Event> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Event> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Event> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Event getOne(String s) {
        return null;
    }

    @Override
    public Event getById(String s) {
        return null;
    }

    @Override
    public Event getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Event> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Event> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Event> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Event> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Event, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Event> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Event entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Event> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Event> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public <S extends Event> List<S> saveAll(Iterable<S> entities) {
        return null;
    }
    //resume missing javadoc method check

    /**
     * Save entity to the repository
     * @param entity Entity to be saved
     * @return Saved entity
     * @param <S> Type of the entity
     */
    @Override
    public <S extends Event> S save(S entity) {
        calledMethods.add("save");
        events.add(entity);
        return entity;
    }

    /**
     * Find entity by ID
     * @param s String of entity ID
     * @return Optional of the found or not found event
     */
    @Override
    public Optional<Event> findById(String s) {
        calledMethods.add("findById");
        Event event = null;
        for(int i = 0; i < events.size(); i++) {
            if(events.get(i).getId().equals(s)) {
                event = events.get(i);
                break;
            }
        }
        return Optional.ofNullable(event);
    }

    /**
     * Find all events
     * @return All events
     */
    @Override
    public List<Event> findAll() {
        return events;
    }
}
