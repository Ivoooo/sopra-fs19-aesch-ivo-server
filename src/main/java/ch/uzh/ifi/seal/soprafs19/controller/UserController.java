package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

/* original:
    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    } */
    @PostMapping("/users")
    ResponseEntity<User> createUser(@RequestBody User newUser) {
        if(this.service.userExistsByUsername(newUser.getUsername())){
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(this.service.createUser(newUser), HttpStatus.CREATED);
    }

    @PostMapping(value = "/users/login")
    ResponseEntity<User> login(@RequestBody User test){
        String username = test.getUsername();
        String password = test.getPassword();

        if (this.service.userExistsByUsername(username))
            if (this.service.correctPassword(username, password)) {
                return new ResponseEntity<>(this.service.getUserByUsername(username), HttpStatus.OK);
            }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/users/{userId}")
    ResponseEntity<User> getUserProfile(@RequestBody long userId) {
        if (this.service.userExistsById(userId)) {
            return new ResponseEntity<>(this.service.getUserById(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/users/{userId}")
    ResponseEntity<User> editUserProfile(@RequestBody User user) {
        if (this.service.userExistsById(user.getId())) {
            //since the username could've been change we need to do this to find the original username
            String username = this.service.getUserById(user.getId()).getUsername();

            if (this.service.correctPassword(username, user.getPassword())) {
                this.service.updateUser(user);
                //success
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            //if a user wants to edit someone else's profile
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //if the userId doesn't exist
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


/* From online guide how to do it
    @GetMapping("/{isbn}")

    public ResponseEntity<Book> getBook(@PathVariable("isbn") String isbn) {

        return bookRepository.findByIsbn(isbn)

                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))

                .orElseThrow(() -> new BookNotFoundException(isbn));

    }

*/

}
