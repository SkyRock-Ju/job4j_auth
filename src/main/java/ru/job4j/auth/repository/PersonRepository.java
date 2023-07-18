package ru.job4j.auth.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.auth.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    @Query("SELECT u FROM Person u")
    List<Person> findAll();

    @Query("SELECT u FROM Person u WHERE u.login = :login")
    Optional<Person> findByLogin(@Param("login") String login);
}
