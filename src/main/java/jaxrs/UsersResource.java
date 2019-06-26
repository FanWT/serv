package jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Users;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;


// The Java class will be hosted at the URI path //"/helloworld"
@Path("/users")
public class UsersResource {
	
    // The Java method will process HTTP GET requests
	@GET
	@Path("test/{userid}")
	@Produces ("text/html")
    public String Test(@PathParam("userid")String userid){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Users user = (Users)se.get(Users.class, userid);
		if(user==null)
			return "<html>helloworld</html>";
		String name = user.getUser_name();
		int state = user.getState();
		String user_dept = user.getUser_dept();
		String user_major = user.getUser_major();
		
		tran.commit();
		se.close();
		sf.close();	
		
		return "<html> " + "<title>" + "Hello Android" + "</title>"
	 	+ "<body><h1>" +name + "</body></h1>" + 
	"</html> ";
	}
	
	@GET
	@Path("{userid}")
	@Produces ("text/plain")
    public Response getUser(@PathParam("userid")String userid){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Users user = (Users)se.get(Users.class, userid);
		if(user==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		String name = user.getUser_name();
		int state = user.getState();
		String user_dept = user.getUser_dept();
		String user_major = user.getUser_major();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +"<user><id>"
				+userid+"</id><name>" +name+"</name> <state>"+state+"</state><user_dept>"
				+user_dept+"</user_dept><user_major>"+user_major+"</user_major></user>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@DELETE
	@Path("{user_id}")
	@Produces("text/plain")
	public Response deleteUser(@Context HttpServletRequest request){

		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String user_id = request.getHeader("user_id");
		Users user = (Users)se.get(Users.class, user_id);
		if(user==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		se.delete(user);
		tran.commit();
		se.close();
		sf.close();
		
		return Response.status(204).entity("delete successfully").type(MediaType.TEXT_PLAIN).build();
	}
	
	
	@POST
	@Produces("text/plain")
	public Response createUser(@Context HttpServletRequest request) throws IOException, DocumentException{
		
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		InputStream in = request.getInputStream();
		Scanner sc = new Scanner(in, "utf-8");
		String input = sc.useDelimiter("\\A").next();
		sc.close();
		
		Document dom = DocumentHelper.parseText(input);
		Element root=dom.getRootElement();
		String user_id = root.elementText("id");
		String user_name = root.elementText("name");
		String user_dept = root.elementText("user_dept");
		String user_major = root.elementText("user_major");
		
		Users user = (Users)se.get(Users.class, user_id);
		if(user!=null){
			String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +"<user><id>"
					+user.getUser_id()+"</id><name>" +user.getUser_name()+"</name> <state>"+user.getState()+"</state><user_dept>"
			+user.getUser_dept()+"</user_dept><user_major>"+user.getUser_major()+"</user_major></user>";
	
			return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
		}
		
		Users user_temp = new Users();
		user_temp.setState(1);
		user_temp.setUser_dept(user_dept);
		user_temp.setUser_id(user_id);
		user_temp.setUser_major(user_major);
		user_temp.setUser_name(user_name);
		se.save(user_temp);
		tran.commit();
		
		se.close();
		sf.close();	
				
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +"<user><id>"
						+user_temp.getUser_id()+"</id><name>" +user_temp.getUser_name()+"</name> <state>"+user_temp.getState()+"</state><user_dept>"
				+user_temp.getUser_dept()+"</user_dept><user_major>"+user_temp.getUser_major()+"</user_major></user>";
		
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	/*@PUT
	@Path("update/{userid}")
	@Produces("text/plain")
	public String updateUser(@FormParam("user_name") String user_name, @FormParam("state")int state, @FormParam("user_id")String user_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		Users user = (Users)se.load(Users.class, user_id);
		
		user.setUser_name(user_name);
		user.setState(state);
			
		se.update(user);
		tran.commit();
		
		se.close();
		sf.close();
		
		return "update success";
	}*/

}