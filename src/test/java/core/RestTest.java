package core;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import dbmapping.User;
import security.Encryptor;


@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration( locations = {"file:/d:/jdev/manyface/src/main/webapp/WEB-INF/manyface-servlet.xml","file:/d:/jdev/manyface/src/main/webapp/WEB-INF/spring-security-test.xml"})
public class RestTest {


	
	@Rule
	public RestDocumentation restDoc = new RestDocumentation("target/generated-snippets");

	private MockMvc mock;
	
	
	@Inject
	private WebApplicationContext context;
	
	
	@Before
	public void setUp() throws Exception {
		mock = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(springSecurity())
				.apply(documentationConfiguration(this.restDoc)) 
				.build();

	}

	@Test
	public void test01AuthAddTestOk() throws Exception {
		String user = "test";
		String password = "test";
		this.mock.perform(post("/api/v1/auth")
			.header("Login", user)
			.header("Password", password)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()) 
			.andDo(document("AuthAdd/ok"));


		this.mock.perform(post("/api/v1/auth")
				.header("Login", "test2")
				.header("Password", "test2")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void test02AuthAddTestMissHeader() throws Exception {

		this.mock.perform(post("/api/v1/auth")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotAcceptable()) 
			.andDo(document("AuthAdd/miss"));
	}
	
	@Test
	public void test03AuthAddExist() throws Exception {
		this.mock.perform(post("/api/v1/auth")
			.header("Login", "test")
			.header("Password", "test")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict()) 
			.andDo(document("AuthAdd/conflict"));
	}
	
	
	@Test 
	public void test04AuthTokenOkTest() throws Exception {
		String user = "test";
		String password = "test";
		this.mock.perform(get("/api/v1/auth")
			.header("Login", user)
			.header("Password", password)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()) 
			.andExpect(header().string("AccessToken", Encryptor.encrypt(user+";"+password) ))
			.andDo(document("AuthGet/ok"));
	}
	
	@Test 
	public void test05AuthTokenNotAcceptTest() throws Exception {
		String user = ";buntu-em;";
		String password = ";rabotyagi!?";
		this.mock.perform(get("/api/v1/auth")
			.header("Login", user)
			.header("Password", password)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotAcceptable()) 
			.andDo(document("AuthGet/error"));
	}
	
	public String createToken(String user, String password) throws Exception{
		return Encryptor.encrypt(user+";"+password);
	}

	
	@Test
	public void test06UserGetByIdOkTest() throws Exception{
		
		String user = "test";
		String password = "test";
		this.mock.perform(get("/api/v1/user/1")
			.header("AccessToken",createToken(user, password))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("UserGetById/ok" ))
			.andReturn().getResponse();
	}
	
	@Test
	public void test07UserAddOkTest() throws Exception{
		
		String login = "test";
		String password = "test";
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		user.setUsername("testUser");
		user.setDescription("test description");
		
		this.mock.perform(post("/api/v1/user")
			.header("AccessToken",createToken(login, password))
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(user))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("UserAdd/ok" ))
			.andReturn().getResponse();


		user.setUsername("newUser");
		this.mock.perform(post("/api/v1/user")
				.header("AccessToken",createToken("test2", "test2"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(user))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());


		user.setUsername("userExample");
		this.mock.perform(post("/api/v1/user")
				.header("AccessToken",createToken(login, password))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(user))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	public void test07UserAddPhoto() throws Exception {

		this.mock.perform(post("/api/v1/user/1/photo")
				.header("AccessToken",createToken("test", "test"))
				.header("height",50)
				.header("width",50)
				.header("format","jpg")
				.contentType(MediaType.APPLICATION_JSON)
				.content("Some image data here")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("UserPhotoAdd/ok" ));
	}


	@Test
	public void test07UserGetPhoto() throws Exception {

		this.mock.perform(get("/api/v1/user/1/photo")
				.header("AccessToken",createToken("test", "test"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("UserPhotoGet/ok" ));
	}


    @Test
    public void test08UserGetAllOkTest() throws Exception{

        String user = "test";
        String password = "test";
        this.mock.perform(get("/api/v1/user")
                .header("AccessToken",createToken(user, password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("UserGetAll/ok" ))
                .andReturn().getResponse();
    }
	
	@Test
	public void test09UserUpdateOkTest() throws Exception{
		
		String login = "test";
		String password = "test";
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		user.setUsername("testUser");
		user.setDescription("test description");
		
		this.mock.perform(put("/api/v1/user/1")
			.header("AccessToken",createToken(login, password))
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(user))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("UserUpdate/ok" ))
			.andReturn().getResponse();
	}
	
	@Test
	public void test10ContactsGetOkTest() throws Exception{
		String login = "test";
		String password = "test";
		
		this.mock.perform(get("/api/v1/contact")
			.header("AccessToken",createToken(login, password))
			.header("user-id",1)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("Contact/ok" ))
			.andReturn().getResponse();
		
		
	}
	


	
	@Test
	public void test11MessagesSendOkTest() throws Exception{
		String login = "test";
		String password = "test";
		
		this.mock.perform(post("/api/v1/message/2")
			.header("AccessToken",createToken(login, password))
			.header("user-id",1)
			.content("Hello my friend!")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("MessageSend/ok" ))
			.andReturn().getResponse();

		this.mock.perform(post("/api/v1/message/1")
				.header("AccessToken",createToken("test2", "test2"))
				.header("user-id",2)
				.content("hi!")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		this.mock.perform(post("/api/v1/message/2")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.content("what a nice day to use this api!")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void test12MessagesGetAllOkTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/message")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("MessagesAll/ok" ))
				.andReturn().getResponse();


	}
	@Test
	public void test13MessageGetOkTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/message/2")
			.header("AccessToken",createToken(login, password))
			.header("user-id",1)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("MessageGet/ok" ))
			.andReturn().getResponse();
	}

	@Test
	public void test133MessageAfterDateTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/message/2")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.header("send_time","2015-01-01 23:59:59")
				.header("count",3)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("MessagesDate/ok" ))
				.andReturn().getResponse();

	}


	@Test
	public void test133MessagesBeforeDateTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/message/2")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.header("send_time","2018-01-01 23:59:59")
				.header("count",3)
				.header("reversed",true)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("MessagesDateBefore/ok" ))
				.andReturn().getResponse();

	}

	@Test
	public void test14SearchIdOkTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/search/id/1")
				.header("AccessToken",createToken(login, password))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("SearchID/ok" ))
				.andReturn().getResponse();
	}

	@Test
	public void test15SearchUsernameOkTest() throws Exception{
		String login = "test";
		String password = "test";

		this.mock.perform(get("/api/v1/search/username/user")
				.header("AccessToken",createToken(login, password))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("SearchUsername/ok" ))
				.andReturn().getResponse();
	}

	@Test
	public void test16MessagesAccept() throws Exception{

		String login = "test";
		String password = "test";

		this.mock.perform(put("/api/v1/message/2/accept")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.header("send_time","2017-01-01 23:59:59")
				.header("reversed",true)
				.contentType(MediaType.APPLICATION_JSON)
				.content("[1,2,3,4]")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("AcceptMessages/ok" ))
				.andReturn().getResponse();

		this.mock.perform(get("/api/v1/message/2")
				.header("AccessToken",createToken(login, password))
				.header("user-id",1)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("AcceptMessageResult/ok" ))
				.andReturn().getResponse();

	}



}
