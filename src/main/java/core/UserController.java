package core;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbmapping.Photo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import dao.AccountDAO;
import dao.UserDAO;
import dbmapping.Account;
import dbmapping.User;

@RestController
@RequestMapping(value="/api/v1/user")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,property="test")
public class UserController {

	AccountDAO accDAO;
	UserDAO userDAO;
	
	@Inject
	public UserController(AccountDAO accDAO, UserDAO userDAO) {
		this.accDAO = accDAO;
		this.userDAO = userDAO;

	}
	
	
	
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	@ResponseBody
	public List<User> getUserList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Account acc = getAccountByContext();
		List<User> list = acc.getUserList();
        System.out.print("USER LIST: " + list.size());
		return list;
	}

	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public User getUserData( @PathVariable(name="id") Long userId)
	{
		return userDAO.getById(userId, User.class);
	}


	private Account getAccountByContext() {
		String login = SecurityContextHolder.getContext().getAuthentication().getName();
		Account acc = accDAO.getAccountByLogin(login);
		return acc;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@Transactional
	@ResponseBody
	public User addUser(HttpServletRequest req, HttpServletResponse res , @RequestBody User user) throws IOException{
		Account acc = getAccountByContext();
		if(acc.getUserList().size() >= 3){
			res.sendError(HttpServletResponse.SC_CONFLICT,"Превышено максимально кол-во пользователей");
			return null;
		}
		

		user.setId(0);
		user.setAccount_id(acc.getId());
		userDAO.save(user);
		return user;
	}
	

	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	@Transactional
	@ResponseBody
	public void updateUser(
			HttpServletRequest req,
			HttpServletResponse res, 
			@RequestBody User user,
			@PathVariable(name="id") Long userId) throws IOException{
		Account acc = getAccountByContext();
		user.setId(userId);
		if( (!checkUser(acc,user)) ){
			res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,"Неверный id пользователя");
			return;
		}
		user.setAccount_id(acc.getId());
		
		userDAO.merge(user);
	}

	@RequestMapping(method=RequestMethod.DELETE)
	@Transactional
	@ResponseBody
	public void deleteUser(HttpServletRequest req, HttpServletResponse res) throws IOException{
		String id = req.getHeader("UserId");
		if(id==null){
			res.sendError(406);
			return;
		}
		Account acc = getAccountByContext();
		User user = userDAO.getById(Long.valueOf(id).longValue(), User.class);
		if(!checkUser(acc, user)){
			res.sendError(406);
			return;
		}
		userDAO.remove(user);
	}


	private boolean checkUser(Account acc, User user) {
		boolean founded = false;
		for(User it : acc.getUserList()){
			if( (it.getAccount_id() == acc.getId()) && (it.getId() == user.getId()) ){
				founded = true;
				break;
			}
		}
		return founded;
	}




	@RequestMapping(value="/{id}/photo",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public byte[] getPhoto( @PathVariable(name="id") Long userId ,
						  HttpServletResponse res) throws IOException {
		User user = userDAO.getById(userId, User.class);
		if (user == null){
			res.sendError(404,"User not founded");
			return null;
		}
		byte[] photo = user.getPhoto();
		if(photo == null) {
			res.sendError(404,"User have no photo");
			return null;
		}
		else {
			return photo;
		}
	}


	@RequestMapping(value="/{id}/photo",method=RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void addPhoto(@PathVariable(name="id") Long userId ,
						 @RequestBody byte[] data,
						 HttpServletResponse res) throws IOException {
		User user = userDAO.getById(userId, User.class);
		if (user == null){
			res.sendError(404);
			return;
		}
		user.setPhoto(data);

		userDAO.merge(user);
	}

}






