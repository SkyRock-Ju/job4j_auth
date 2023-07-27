package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final PersonService personService;
    private BCryptPasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    @PostMapping("/sign-up")
    public ResponseEntity<Person> signUp(@RequestBody Person person) {
        if (person.getPassword() == null || person.getLogin() == null) {
            throw new NullPointerException("Login or Password is empty or null");
        }
        if (person.getLogin().length() < 6) {
            throw new IllegalArgumentException("Login should have at least 6 letters");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password should have at least 6 letters");
        }
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
    public ResponseEntity<Void> update(@RequestBody Person person) {
        if (person.getPassword() == null || person.getLogin() == null) {
            throw new NullPointerException("Login or Password is empty or null");
        }
        if (personService.updatePerson(person).isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePersonFields(@PathVariable int id, @RequestBody Map<String, String> fields) {
        if (personService.updatePersonByFields(id, fields).isEmpty()) {
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

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void illegalArgumentExceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}