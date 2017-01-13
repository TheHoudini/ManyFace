package security;


import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import dao.AccountDAO;

import org.springframework.security.core.GrantedAuthority;

import dbmapping.Account;

@Component
public class TokenAuthManager implements AuthenticationManager {

	AccountDAO accDAO;
	TokenAuthManager(){
	}
	
	@Autowired
	TokenAuthManager(AccountDAO dao){
		this.accDAO = dao;
	}
	
	@Override
	public Authentication authenticate(Authentication data) throws AuthenticationException {
		String login = data.getName();
		String password = data.getCredentials().toString();
		Account acc = accDAO.getAccountByLogin(login);
		if( (acc == null) ||  (!(acc.getPassword().equals(password)))  ){
			data.setAuthenticated(false);
			return data;
		}
		SimpleGrantedAuthority grantedAuth = new SimpleGrantedAuthority("ROLE_USER");
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		authorities.add(grantedAuth);
		return new UsernamePasswordAuthenticationToken(login,password,authorities);
	}

}
