package core;

import static core.HttpUtil.getUserFromContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.AccountDAO;
import dao.DialogDAO;
import dao.UserDAO;
import dbmapping.Dialog;
import dbmapping.Message;
import dbmapping.User;
@Controller
@RequestMapping("api/v1/message")
public class MessageController {
	
	public void setDialogDAO(DialogDAO dialogDAO) {
		this.dialogDAO = dialogDAO;
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
	
	

	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<Message> getDialogMessages(
			@PathVariable(name="id") Long userToId,
			@RequestHeader("user-id") Long userFromId, 
			@RequestHeader(name="send_time",required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date date,
			@RequestHeader(name="count",required=false) Long count,
			@RequestHeader(name="reversed",required = false) Boolean reversed,
			@RequestHeader(name="readed",required = false) Boolean readed,
			HttpServletResponse res  ) throws IOException{

		User mainUser = getUserFromContext(accDAO, userFromId);
		User receiver = userDAO.getById(userToId, User.class);
		if(mainUser == null ||  receiver == null){
			res.sendError(406);
			return null;
		}
		Dialog dlg = dialogDAO.getDialogWithUsers(mainUser, receiver);
		if(dlg==null){
			res.sendError(406);
			return null;
		}


		if(reversed==null)
			reversed = new Boolean(false);
		List<Message> list = dialogDAO.getMessagesFromDialog(dlg, date, count, reversed.booleanValue(),readed);
		return list;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.POST)
	@ResponseBody
	@Transactional
	public Date sendMessage(
			@PathVariable(name="id") Long userToId,
			@RequestHeader("user-id") Long userFromId,
			@RequestBody String message,
			HttpServletResponse res  ) throws IOException{
		User userFrom = getUserFromContext(accDAO, userFromId);
		User userTo = userDAO.getById(userToId, User.class);
		if(userFrom == null ||  userTo == null){
			res.sendError(406);
			return null;
		}
		
		Dialog dlg = dialogDAO.getDialogWithUsers(userFrom, userTo);
		if(dlg == null)
			dlg = dialogDAO.addDialog( userFrom, userTo);
		return dialogDAO.addMessageToDialog(dlg, message, userFrom);
	}



	@RequestMapping(value="/{id}/accept",method=RequestMethod.PUT)
	@ResponseBody
	@Transactional
	public void acceptMessages(
			@PathVariable(name="id") Long userToId,
			@RequestBody(required = false) long[] ids,
			@RequestHeader(name="send_time",required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date date,
			@RequestHeader("user-id") Long userFromId,
			HttpServletResponse res  ) throws IOException{

		User userFrom = getUserFromContext(accDAO, userFromId);
		User userTo = userDAO.getById(userToId, User.class);
		if(userFrom == null ||  userTo == null){
			res.sendError(406);
			return;
		}

		Dialog dlg = dialogDAO.getDialogWithUsers(userFrom, userTo);
		if(dlg== null)
			return;
		dialogDAO.acceptMessages(dlg,userFrom,ids,date,false);


	}


	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<Message> getAllMessages(
			@RequestHeader("user-id") Long userFromid, 
			@RequestHeader(name="send_time",required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date date,
			@RequestHeader(name="count",required=false) Long count,
			@RequestHeader(name="reversed",required = false) Boolean reversed,
			@RequestHeader(name="readed",required = false) Boolean readed,
			HttpServletResponse res  ) throws IOException{
		User userFrom = getUserFromContext(accDAO,userFromid);
		if(userFrom == null){
			res.sendError(406);
			return null;
		}
		
		if(reversed==null)
			reversed = new Boolean(false);

		return dialogDAO.getAllMessages(userFrom, date, count, reversed.booleanValue(),readed);
	}
	
}
