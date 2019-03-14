package ch.uzh.ifi.seal.soprafs19.service;
import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Test
    public void fetchUsers() throws Exception {
        this.mvc.perform(get("/users")).andExpect(status().is(200));

        User testUser = new User();
        testUser.setUsername("fetchUsers");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        this.mvc.perform(get("/users")).andExpect(status().is(200));
    }

    @Test
    public void createUser() throws Exception {
        //new user
        this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"createUser\", \"password\": \"testPassword\"}"))
                .andExpect(status().is(201));

        //already existing username+password
        this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"createUser\", \"password\": \"testPassword\"}"))
                .andExpect(status().is(409));

        //already existing username
        this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"createUser\", \"password\": \"newPassword\"}"))
                .andExpect(status().is(409));
    }

    @Test
    public void fetchUser() throws Exception {
        User testUser = new User();
        testUser.setUsername("fetchUser");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        //valid user
        this.mvc.perform(get("/users/1")).andExpect(status().is(200));

        //invalid user
        this.mvc.perform(get("/users/0")).andExpect(status().is(404));
    }

    @Test
    public void loginUser() throws Exception {
        User testUser = new User();
        testUser.setUsername("loginUser");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        //proper login
        this.mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"loginUser\", \"password\": \"testPassword\"}"))
                .andExpect(status().is(200));

        //wrong password
        this.mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"loginUser\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().is(404));

        //username doesn't exist
        this.mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"loginUser111111\", \"password\": \"testPassword1111\"}"))
                .andExpect(status().is(404));

    }

    @Test
    public void updateUser() throws Exception {
        User testUser = new User();
        testUser.setUsername("updateUser");
        testUser.setPassword("testPassword");
        User user = userService.createUser(testUser);

        //successfully changed username
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateUser111\", \"id\": " +  user.getId() + ", \"token\": \"" + user.getToken() + "\" , \"password\": \"testPassword\"}"))
                .andExpect(status().is(204));

        //successfully changed birthday
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"birthday\": \"1990-10-10\", \"id\": " +  user.getId() + ", \"token\": \"" + user.getToken() + "\" , \"password\": \"testPassword\"}"))
                .andExpect(status().is(204));

        //username already exists
        User newUser = userService.createUser(testUser);
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateUser\", \"id\": " +  user.getId() + ", \"token\": \"" + user.getToken() + "\" , \"password\": \"testPassword\"}"))
                .andExpect(status().is(400));

        //id doesn't exist
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateUser222\", \"id\": 0, \"token\": \"" + user.getToken() + "\" , \"password\": \"testPassword\"}"))
                .andExpect(status().is(404));

        //invalid token but right password
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateUser111\", \"id\": " +  user.getId() + ", \"token\": \" wrongToken \" , \"password\": \"testPassword\"}"))
                .andExpect(status().is(401));

        //invalid password but right token
        this.mvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateUser111\", \"id\": " +  user.getId() + ", \"token\": \"" + user.getToken() + "\" , \"password\": \"wrongPassword\"}"))
                .andExpect(status().is(401));
    }
}
