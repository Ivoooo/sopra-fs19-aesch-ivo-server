package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        this.userRepository.deleteAll();

        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.ONLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }

    @Test
    public void userExistsById() {
        this.userRepository.deleteAll();

        User user1 = new User();
        user1.setUsername("user1Username");
        user1.setPassword("user1Password");
        User usr = userService.createUser(user1);
        long wrongid = 654645646;

        Assert.assertFalse(userService.userExistsById(wrongid));
        Assert.assertTrue(userService.userExistsById(usr.getId()));
    }

    @Test
    public void userExistsByUsername() {
        this.userRepository.deleteAll();

        User user1 = new User();
        user1.setUsername("user1Username");
        user1.setPassword("user1Password");
        User usr = userService.createUser(user1);
        String wrong = "asdflja";

        Assert.assertFalse(userService.userExistsByUsername(wrong));
        Assert.assertTrue(userService.userExistsByUsername(usr.getUsername()));
    }

    @Test
    public void correctPassword() {
        this.userRepository.deleteAll();

        Assert.assertFalse(userService.correctPassword("testUsername", "testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        userService.createUser(testUser);

        Assert.assertFalse(userService.correctPassword("testUsername", "testUsername"));
        Assert.assertFalse(userService.correctPassword("testPassword", "testPassword"));
        Assert.assertFalse(userService.correctPassword("11", "22"));
        Assert.assertFalse(userService.correctPassword("testPassword", "testUsername"));

        Assert.assertTrue(userService.correctPassword("testUsername", "testPassword"));
    }

    @Test
    public void getUserByUsername() {
        this.userRepository.deleteAll();

        Assert.assertNull(userService.getUserByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNull(userService.getUserByUsername("asdf"));
        Assert.assertEquals(createdUser, userService.getUserByUsername(createdUser.getUsername()));
    }

    @Test
    public void getUserById() {
        this.userRepository.deleteAll();

        long wrong = 21342323;
        Assert.assertNull(userService.getUserById(wrong));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNull(userService.getUserById(wrong));
        Assert.assertEquals(createdUser, userService.getUserById(createdUser.getId()));
    }

    @Test
    public void check() {
        this.userRepository.deleteAll();

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertTrue(userService.checkUser(createdUser));

        testUser.setPassword("testPassword");
        testUser.setUsername("wrongUsername");
        Assert.assertTrue(userService.checkUser(createdUser));


        createdUser.setPassword("wrongPassword");
        Assert.assertFalse(userService.checkUser(createdUser));

        testUser.setPassword("testPassword");
        testUser.setToken("wrongToken");
        Assert.assertFalse(userService.checkUser(createdUser));

        testUser.setPassword("wrongPassword");
        testUser.setToken("wrongToken");
        Assert.assertFalse(userService.checkUser(createdUser));
    }

    @Test
    public void updateUser() {
        this.userRepository.deleteAll();

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        //trying to update with wrong token
        String token = createdUser.getToken();
        createdUser.setToken("wrongToken");
        userService.updateUser(createdUser);
        createdUser.setToken(token);
        Assert.assertEquals(createdUser, userService.getUserByUsername(createdUser.getUsername()));

        //updating user normally
        Date date = new Date(2000,10,15);
        createdUser.setBirthday(date);
        createdUser.setUsername("newUsername");
        userService.updateUser(createdUser);
        Assert.assertEquals(createdUser, userService.getUserById(createdUser.getId()));

        //update to already taken
        User createdUser2 = userService.createUser(testUser);

        createdUser.setUsername("testUsername");
        userService.updateUser(createdUser);
        createdUser.setUsername("newUsername");
        Assert.assertEquals(createdUser, userService.getUserById(createdUser.getId()));
    }
}
