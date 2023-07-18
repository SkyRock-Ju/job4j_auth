package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
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
}
