package core;


import dao.AccountDAO;
import dao.UserDAO;
import dbmapping.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/search")
public class SearchController {

    AccountDAO accDAO;
    UserDAO userDAO;

    @Inject
    public SearchController(AccountDAO accDAO, UserDAO userDAO) {
        this.accDAO = accDAO;
        this.userDAO = userDAO;
    }


    @RequestMapping(value="/id/{id}",method= RequestMethod.GET)
    @ResponseBody
    @Transactional
    public User getUserData(@PathVariable(name="id") Long userId)
    {
        return userDAO.getById(userId, User.class);
    }

    @RequestMapping(value="/username/{username}",method=RequestMethod.GET)
    @ResponseBody
    @Transactional
    public List<User> getUserData(@PathVariable(name="username") String username)
    {
        return userDAO.findUsers(username);
    }
}
