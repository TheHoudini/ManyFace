package core;

import java.io.BufferedReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;

import dao.AccountDAO;
import dbmapping.Account;
import dbmapping.User;


public class HttpUtil {

	public static String getBody(HttpServletRequest req){
		try{
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(req.getReader());
			String line;
			while( (line = reader.readLine()) != null)
				builder.append(line);
			return builder.toString();
		}catch(Exception e){
			return null;
		}
		
	}
	
	public static boolean checkUser(Account acc, User user) {
		boolean founded = false;
		for(User it : acc.getUserList()){
			if( (it.getAccount_id() == acc.getId()) && (it.getId() == user.getId()) ){
				founded = true;
				break;
			}
		}
		return founded;
	}
	
	public static Account getAccountByContext(AccountDAO accDAO) {
		String login = SecurityContextHolder.getContext().getAuthentication().getName();
		Account acc = accDAO.getAccountByLogin(login);
		return acc;
	}
	
	public static User getUserFromContext(AccountDAO accDAO, long userid){
		List<User> list = getAccountByContext(accDAO).getUserList();
			
		for(User user : list)
			if(user.getId() == userid) return user;
		return null;
	}
}
