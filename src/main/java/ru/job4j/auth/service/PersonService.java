package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> updatePerson(Person person) {
        if (!personRepository.existsById(person.getId())) {
            return Optional.empty();
        }
        return Optional.of(personRepository.save(person));
    }

    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }
}
