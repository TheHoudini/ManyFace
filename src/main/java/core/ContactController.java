package core;

import static core.HttpUtil.getUserFromContext;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.AccountDAO;
import dao.DialogDAO;
import dao.UserDAO;
import dbmapping.Dialog;
import dbmapping.User;

@Controller
@RequestMapping("api/v1/contact")
public class ContactController {

	public void setDialogDAO(DialogDAO dialogDAO) {
		this.dialogDAO = dialogDAO;
	}

	public AccountDAO getAccDAO() {
		return accDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}



	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setAccDAO(AccountDAO accDAO) {
		this.accDAO = accDAO;
	}

	@Inject
	private AccountDAO accDAO;
	
	@Inject
	private UserDAO userDAO;
	
	
	@Inject 
	private DialogDAO dialogDAO;
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<User> getDialogMessages(@RequestHeader("user-id") Long userId ){
		User user = getUserFromContext(accDAO, userId);
		if(user == null)
			return null;
		List<Dialog> list = dialogDAO.getDialogsWithUser(user);
		List<User> userList = new ArrayList<User>();
		for(Dialog dlg : list){
			if(dlg.getFirstUser().getId() == user.getId())
				userList.add(dlg.getSecondUser());
			else 
				userList.add(dlg.getFirstUser());
		}
		return userList;
	}
	
}
