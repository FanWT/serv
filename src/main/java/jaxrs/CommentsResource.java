package jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Comments;
import model.Courses;
import model.Users;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

@Path("/comments")
public class CommentsResource {
	@GET
	@Path("{course_id}/{user_id}")
	@Produces("text/plain")
	public Response getComments(@PathParam("course_id")String course_id, @PathParam("user_id")String user_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Comments as a where a.user_id = ?";
		List<Comments> list = se.createQuery(sql).setParameter(0, user_id).list();
		if(list.isEmpty())
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><comments>";
		int length = list.size();
		for(int i=0; i<length; ++i){
			if(list.get(i).getCourse_id().equals(course_id)){
				
			String comment_content = list.get(i).getComment_content();
			Date comment_date = list.get(i).getComment_date();
			int comment_state = list.get(i).getComment_state();
			Double comment_rate = list.get(i).getComment_rate();
			int comment_id = list.get(i).getComment_id();			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String comment_time_str = sdf.format(comment_date);
			
			result += "<comment><comment_id>"+comment_id+"</comment_id><course_id>" +course_id+"</course_id> <comment_state>"
					+comment_state+"</comment_state><user_id>"+user_id+"</user_id><comment_content>"+comment_content
					+"</comment_content><comment_date>"+comment_time_str+"</comment_date><comment_rate>"
					+comment_rate+"</comment_rate></comment>";
			}
		}
		result += "</comments>";

		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("user/{user_id}")
	@Produces("text/plain")
	public Response getCommentByUserId(@PathParam("user_id")String user_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Comments as a where a.user_id = ? order by a.comment_date desc";
		Query query = se.createQuery(sql);
		List<Comments> list = query.setParameter(0, user_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><comments></comments>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><comments>";
		int lenght = list.size();
		for(int i = 0; i<lenght; ++i){
			Comments comment = list.get(i);
			
			int comment_state = comment.getComment_state();
			if(comment_state!=-1){
			int comment_id = comment.getComment_id();
			String comment_content = comment.getComment_content();
			String course_id = comment.getCourse_id();
			Date comment_date = comment.getComment_date();
			Double comment_rate = comment.getComment_rate();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");   
			String comment_time_str = sdf.format(comment_date);
			String comment_rate_str = comment_rate.toString();
			
			result += "<comment><comment_id>"
					+comment_id+"</comment_id><course_id>" +course_id+"</course_id> <comment_state>"+comment_state+"</comment_state><user_id>"
					+user_id+"</user_id><comment_content>"+comment_content+"</comment_content><comment_date>"
					+comment_time_str+"</comment_date><comment_rate>"+comment_rate_str+"</comment_rate></comment>";
			}
		}
		result += "</comments>";
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	
	
	@GET
	@Path("courseid/{course_id}")
	@Produces("text/plain")
	public Response listComments(@Context HttpServletRequest request, @PathParam("course_id")String course_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Comments as a where a.course_id = ? order by a.comment_date desc";
		Query query = se.createQuery(sql);
		String start = request.getHeader("start");
		query.setFirstResult((Integer.parseInt(start))*10); 
		query.setMaxResults(10);
		List<Comments> list = query.setParameter(0, course_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><comments></comments>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><comments>";
		int lenght = list.size();
		for(int i = 0; i<lenght; ++i){
			Comments comment = list.get(i);
			
			int comment_state = comment.getComment_state();
			if(comment_state!=-1){
			int comment_id = comment.getComment_id();
			String comment_content = comment.getComment_content();
			Date comment_date = comment.getComment_date();
			String user_id = comment.getUser_id();
			
			Double comment_rate = comment.getComment_rate();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");   
			String comment_time_str = sdf.format(comment_date);
			String comment_rate_str = comment_rate.toString();
			
			result += "<comment><comment_id>"
					+comment_id+"</comment_id><course_id>" +course_id+"</course_id> <comment_state>"+comment_state+"</comment_state><user_id>"
					+user_id+"</user_id><comment_content>"+comment_content+"</comment_content><comment_date>"
					+comment_time_str+"</comment_date><comment_rate>"+comment_rate_str+"</comment_rate></comment>";
			}
		}
		result += "</comments>";
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();		
	}
	
	@POST
	@Produces("text/plain")
	public Response createComment(@Context HttpServletRequest request) throws IOException, DocumentException, ParseException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		InputStream in = request.getInputStream();
		Scanner sc = new Scanner(in, "utf-8");
		String input = sc.useDelimiter("\\A").next();
		sc.close();
		
		Document dom = DocumentHelper.parseText(input);
		Element root=dom.getRootElement();//这里获得最外层元素，至少要3层标签，可以在xml最外面加一个<root>
		
		String comment_content = root.elementText("comment_content");
		String comment_date_str = root.elementText("comment_date");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date comment_date = sdf.parse(comment_date_str);
		String comment_rate = root.elementText("comment_rate");
		String comment_state = root.elementText("comment_state");
		String course_id = root.elementText("course_id");
		String user_id = root.elementText("user_id");
		
		Users user = (Users)se.get(Users.class, user_id);
		if(user.getState()==0)
			return Response.status(403).entity("you are banned").type(MediaType.TEXT_PLAIN).build();
		
		Comments comment = new Comments();
		comment.setComment_content(comment_content);
		comment.setComment_date(comment_date);
		comment.setComment_rate(Double.parseDouble(comment_rate));
		comment.setComment_state(Integer.parseInt(comment_state));
		comment.setCourse_id(course_id);
		comment.setUser_id(user_id);
		
		se.save(comment);
		int comment_id = comment.getComment_id();
		
		Courses course = (Courses)se.get(Courses.class, course_id);
		int course_comment_num = course.getCourse_comment_num();
		double course_rate = course.getCourse_rate();
		course.setCourse_comment_num(course_comment_num+1);
		course.setCourse_rate((course_rate*course_comment_num+Double.parseDouble(comment_rate))/(course_comment_num+1));
		se.update(course);
		
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +"<comment><comment_id>"
				+comment_id+"</comment_id><course_id>" +course_id+"</course_id> <comment_state>"+comment_state+"</comment_state><user_id>"
				+user_id+"</user_id><comment_content>"+comment_content+"</comment_content><comment_date>"
				+comment_date_str+"</comment_date><comment_rate>"+comment_rate+"</comment_rate></comment>";
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@DELETE
	@Path("{comment_id}")
	@Produces("text/plain")
	public Response deleteComments(@PathParam("comment_id")String comment_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Comments comment = (Comments)se.get(Comments.class, Integer.parseInt(comment_id));
		if(comment==null)
			return Response.status(404).entity("error").build();
		double comment_rate = comment.getComment_rate();
		String course_id = comment.getCourse_id();
		se.delete(comment);
		
		Courses course = (Courses)se.get(Courses.class, course_id);
		int course_comment_num = course.getCourse_comment_num();
		double course_rate = course.getCourse_rate();
		course.setCourse_comment_num(course_comment_num-1);
		if(course_comment_num==1)
			course.setCourse_rate(0);
		else
			course.setCourse_rate((course_rate*course_comment_num-comment_rate)/(course_comment_num-1));
		se.update(course);
		
		tran.commit();
		se.close();
		sf.close();	
		
		return Response.status(204).entity("delete successfully").type(MediaType.TEXT_PLAIN).build();
	}
	
	@PUT
	@Path("{comment_id}")
	@Produces("text/plain")
	public Response updateComments(@PathParam("comment_id")int comment_id, @Context HttpServletRequest request) throws Exception{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		InputStream in = request.getInputStream();
		Scanner sc = new Scanner(in, "utf-8");
		String input = sc.useDelimiter("\\A").next();
		sc.close();
		
		Document dom = DocumentHelper.parseText(input);
		Element root=dom.getRootElement();//这里获得最外层元素，至少要3层标签，可以在xml最外面加一个<root>
		
		String comment_content = root.elementText("comment_content");
		String comment_date_str = root.elementText("comment_date");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date comment_date = sdf.parse(comment_date_str);
		String comment_rate = root.elementText("comment_rate");
		String comment_state = root.elementText("comment_state");
		String course_id = root.elementText("course_id");
		String user_id = root.elementText("user_id");
		
		Comments comment = (Comments)se.get(Comments.class, comment_id);
		if(comment==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		double comment_rate_former = comment.getComment_rate();
		comment.setComment_content(comment_content);
		comment.setComment_date(comment_date);
		comment.setComment_rate(Double.parseDouble(comment_rate));
		comment.setComment_state(Integer.parseInt(comment_state));
		comment.setCourse_id(course_id);
		comment.setUser_id(user_id);
		
		se.update(comment);
		
		Courses course = (Courses)se.get(Courses.class, course_id);
		int course_comment_num = course.getCourse_comment_num();
		double course_rate = course.getCourse_rate();
		course.setCourse_rate((course_rate*course_comment_num+Double.parseDouble(comment_rate)-comment_rate_former)
							/course_comment_num);
		se.update(course);
		
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +"<comment><comment_id>"
				+comment_id+"</comment_id><course_id>" +course_id+"</course_id> <comment_state>"+comment_state+"</comment_state><user_id>"
				+user_id+"</user_id><comment_content>"+comment_content+"</comment_content><comment_date>"
				+comment_date_str+"</comment_date><comment_rate>"+comment_rate+"</comment_rate></comment>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
}
