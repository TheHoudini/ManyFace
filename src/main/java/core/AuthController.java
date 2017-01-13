package core;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.AccountDAO;
import dbmapping.Account;
import security.AuthTokenFilter;
import security.Encryptor;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

	AccountDAO accDAO;
	
	
	
	AuthController(){
	}
	
	@Autowired
	AuthController(AccountDAO dao){
		this.accDAO = dao;
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public void AuthUser(HttpServletRequest request, HttpServletResponse response){
		try{
			Account acc = accDAO.getAccountByLogin(request.getHeader("login"));
			if(acc == null){
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "�������� ����� ��� ������");
				return;
			}
			if(!(acc.getPassword().equals(request.getHeader("Password")))){
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "�������� ����� ��� ������");
				return;
			}
			
			String accessToken = Encryptor.encrypt(acc.getLogin() + ";" + acc.getPassword());
			response.setHeader(AuthTokenFilter.TOKEN_HEADER, accessToken);
			return;
		}catch(Exception e){
			try {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,"������������ ����� ��� ������");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	@Transactional
	public void AddUser(HttpServletRequest req, HttpServletResponse res){

		String login = req.getHeader("Login");
		String password =  req.getHeader("Password");


		try{
			if( (login==null) || (password==null) ){
				res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "������������ ����� ��� ������");
				return;
			}
			
			Account acc = accDAO.getAccountByLogin(login);
			if(acc != null){
				res.sendError(HttpServletResponse.SC_CONFLICT, "������������ � ����� ������� ��� ����������");
				return;
			}
			acc = new Account(login,password);
			accDAO.save(acc);			
			String accessToken = Encryptor.encrypt(acc.getLogin() + ";" + acc.getPassword());
			res.setHeader(AuthTokenFilter.TOKEN_HEADER, accessToken);
			
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
	}
}
