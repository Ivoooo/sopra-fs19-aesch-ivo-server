package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.Date;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate(new Date());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public Boolean userExistsByUsername(String username){
        //returns true if it already exists!
        return this.userRepository.existsByUsername(username);
    }

    public Boolean userExistsById(long userId) {
        //returns true if it exists!
        return this.userRepository.existsById(userId);
    }

    public boolean correctPassword(String username, String password) {
        //returns true if the Password is correct!
        if (this.userRepository.existsByUsername(username)) {
            User user = this.userRepository.findByUsername(username);
            return password.equals(user.getPassword());
        }
        return false;
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User getUserById(long userId) {
        return this.userRepository.findById(userId);
    }

    public void updateUser(User user) {
        //check if user already exists
        if (!userExistsById(user.getId())) {
            return;
        }

        //update necessary parts
        User x = getUserById(user.getId());
        if (user.getUsername() != null) {
            x.setUsername(user.getUsername());
        }
        if (user.getBirthday() != null) {
            x.setBirthday(user.getBirthday());
        }

        //actually update
        this.userRepository.save(x);
    }

    public boolean checkUser(User toCheck) {
        //checks the users Token and Password for validity
        //returns true if both are correct!
        if (this.userRepository.existsByToken(toCheck.getToken())) {
            User usr = this.userRepository.findByToken(toCheck.getToken());
            String pw = usr.getPassword();
            return pw.equals(toCheck.getPassword());
        }
        return false;
    }
}
