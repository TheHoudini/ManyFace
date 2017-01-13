package security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class AuthTokenFilter extends AbstractAuthenticationProcessingFilter{


	public static String TOKEN_HEADER = "AccessToken";
	
	private AuthenticationManager authMgr;
	
	
	public  AuthTokenFilter(String filter, AuthenticationManager authMgr) {
		super(filter);
		this.authMgr = authMgr;
		super.setAuthenticationManager(new NoOpAuthenticationManager());
		super.setAuthenticationSuccessHandler(new AuthSuccessHandler());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {
		String token = req.getHeader(TOKEN_HEADER);

		if(token==null)
			token ="";
		
		return parseToken(token);
	}
	
	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) 
    		throws IOException, ServletException {
			super.successfulAuthentication(request, response, chain, authResult);
			chain.doFilter(request, response);
    }


	private Authentication parseToken(String token)
	{
		try {
			String encryptedToken = Encryptor.decrypt(token);
			String[] tokenData = encryptedToken.split(";");
			return authMgr.authenticate(new UsernamePasswordAuthenticationToken(tokenData[0],tokenData[1]));
		} catch (Exception e) {
			Authentication auth = new UsernamePasswordAuthenticationToken("", "");
			auth.setAuthenticated(false);
			return auth;
		}
		
	}


}
