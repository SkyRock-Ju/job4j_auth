package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class PersonService implements UserDetailsService {

    private final PersonRepository personRepository;

    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public Optional<Person> savePerson(Person person) {
        return Optional.of(personRepository.save(person));
    }

    public Optional<Person> findPersonByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public Optional<Person> updatePerson(Person person) {
        if (!personRepository.existsById(person.getId())) {
            return Optional.empty();
        }
        return Optional.of(personRepository.save(person));
    }

    public boolean deletePersonById(int id) {
        if (personRepository.findById(id).isPresent()) {
            personRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = personRepository.findByLogin(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.get().getLogin(), user.get().getPassword(), emptyList());
    }

    public Optional<Person> updatePersonByFields(int id, Map<String, String> fields) {
        var person = personRepository.findById(id);
        if (person.isEmpty()) {
            throw new NoSuchElementException(String.format("User with id=%s doesn't exist", id));
        }
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Person.class, key);
            if (field == null) {
                throw new NullPointerException(String.format("field %s is empty", key));
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, person.get(), value);
        });
        return Optional.of(personRepository.save(person.get()));
    }
}
