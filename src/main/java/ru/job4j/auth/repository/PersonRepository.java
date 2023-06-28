package ru.job4j.auth.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.model.Person;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    @Query("SELECT u FROM Person u")
    List<Person> findAll();
}
