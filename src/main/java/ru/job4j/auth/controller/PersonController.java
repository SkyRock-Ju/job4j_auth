package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.marker.Operation;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.model.PersonDTO;
import ru.job4j.auth.service.PersonService;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;
    private BCryptPasswordEncoder encoder;

    @PostMapping("/sign-up")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> signUp(@RequestBody @Valid Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        var savedPerson = personService.savePerson(person);
        if (savedPerson.isEmpty()) {
            return new ResponseEntity<>(
                    person,
                    HttpStatus.CONFLICT
            );
        }
        return new ResponseEntity<>(
                savedPerson.orElseThrow(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return personService.findAllPersons();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = personService.findPersonById(id);
        if (person.isEmpty()) {
            throw new NoSuchElementException(String.format("User with id=%s doesn't exist", id));
        }
        return new ResponseEntity<Person>(
                person.get(),
                HttpStatus.OK
        );
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@RequestBody @Valid Person person) {
        if (personService.updatePerson(person).isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> updatePersonFields(@PathVariable int id, @RequestBody @Valid PersonDTO personDTO) {
        if (personService.updatePersonByFields(id, personDTO).isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (personService.deletePersonById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}