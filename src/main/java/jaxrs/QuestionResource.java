package jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import model.Questions;
import model.Tags;
import model.Users;
import model.Tag_question;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

@Path("/questions")
public class QuestionResource {
	@GET
	@Produces("text/plain")
	@Path("question_id/{question_id}")
	public Response getQuestionById(@PathParam("question_id")int question_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Questions question = (Questions)se.get(Questions.class, question_id);
		tran.commit();
		se.close();
		sf.close();	
		if(question==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
				
		Date question_time = question.getQuestion_time();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		String question_time_str = sdf.format(question_time);
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><question><question_id>"+question.getQuestion_id()
						+"</question_id><question_answer_num>"+question.getQuestion_answer_num()
						+"</question_answer_num><question_content>"+question.getQuestion_content()
						+"</question_content><question_tag1>"+question.getQuestion_tag1()+"</question_tag1><question_tag2>"				
						+question.getQuestion_tag2()+"</question_tag2><question_tag3>"+question.getQuestion_tag3()
						+"</question_tag3><question_title>"+question.getQuestion_title()+"</question_title><question_time>"
						+question_time_str+"</question_time><user_id>"+question.getUser_id()+"</user_id></question>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	
	@GET
	@Produces("text/xml")
	public Response getQuestion(@Context HttpServletRequest request){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String hql = "from Questions as a order by a.question_id desc";
		Query query = se.createQuery(hql);
		String start = request.getHeader("start");
		System.out.println(start);
		query.setFirstResult((Integer.parseInt(start))*10); 
		query.setMaxResults(10);
		List<Questions> list = query.list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions>";
		int length = list.size();
		for(int i=0; i<length; ++i){
			String question_content = list.get(i).getQuestion_content();
			Date question_time = list.get(i).getQuestion_time();
			String question_tag1 = list.get(i).getQuestion_tag1();
			String question_tag2 = list.get(i).getQuestion_tag2();
			String question_tag3 = list.get(i).getQuestion_tag3();
			String user_id = list.get(i).getUser_id();
			String question_title = list.get(i).getQuestion_title();
			int question_answer_num = list.get(i).getQuestion_answer_num();
			int question_id = list.get(i).getQuestion_id();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String question_time_str = sdf.format(question_time);
			
			result += "<question><question_id>"+question_id+"</question_id> <question_content>"
					+question_content+"</question_content> <question_time>"
					+question_time_str+"</question_time><question_tag1>"
					+question_tag1+ "</question_tag1><question_tag2>"
					+question_tag2+"</question_tag2><question_tag3>"
					+question_tag3+"</question_tag3><user_id>"
					+user_id+"</user_id><question_title>"
					+question_title+"</question_title><question_answer_num>"
					+question_answer_num+"</question_answer_num></question>";
		}
		result += "</questions>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@POST
	@Produces("text/plain")
	public Response createQuestion(@Context HttpServletRequest request) throws IOException, DocumentException, ParseException{
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

		String question_content = root.elementText("question_content");
		String question_tag1 = root.elementText("question_tag1");
		String question_tag2 = root.elementText("question_tag2");
		String question_tag3 = root.elementText("question_tag3");
		String question_time_str = root.elementText("question_time");
		String user_id = root.elementText("user_id");
		String question_title = root.elementText("question_title");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date question_time = sdf.parse(question_time_str);
		
		Users user = (Users)se.get(Users.class, user_id);
		if(user.getState()==0)
			return Response.status(403).entity("you are banned").type(MediaType.TEXT_PLAIN).build();
		
		Questions question = new Questions();
		question.setQuestion_content(question_content);
		question.setQuestion_tag1(question_tag1);
		question.setQuestion_tag2(question_tag2);
		question.setQuestion_tag3(question_tag3);
		question.setQuestion_time(question_time);
		question.setUser_id(user_id);	
		question.setQuestion_answer_num(0);
		question.setQuestion_title(question_title);
		se.save(question);	
		
		int question_id = question.getQuestion_id();
		if(!question_tag1.isEmpty()){
			String sql = "from Tags as a where a.tag_content =:tag" ;
			Tag_question tag_question = new Tag_question();
			@SuppressWarnings("unchecked")
			List<Tags> list = se.createQuery(sql).setParameter("tag", question_tag1).list();
			if(list.isEmpty()){
				Tags tag = new Tags();
				tag.setTag_content(question_tag1);
				se.save(tag);							
				tag_question.setQuestion_id(question_id);
				tag_question.setTag_id(tag.getTag_id());
				se.save(tag_question);
			}
			else{
				tag_question.setQuestion_id(question_id);
				tag_question.setTag_id(list.get(0).getTag_id());
				se.save(tag_question);
			}
		}
		
		if(!question_tag2.isEmpty()){
			String sql = "from Tags as a where a.tag_content =:tag" ;
			Tag_question tag_question2 = new Tag_question();
			@SuppressWarnings("unchecked")
			List<Tags> list = se.createQuery(sql).setParameter("tag", question_tag2).list();
			if(list.isEmpty()){
				Tags tag = new Tags();
				tag.setTag_content(question_tag2);
				se.save(tag);							
				tag_question2.setQuestion_id(question_id);
				tag_question2.setTag_id(tag.getTag_id());
				se.save(tag_question2);
			}
			else{
				tag_question2.setQuestion_id(question_id);
				tag_question2.setTag_id(list.get(0).getTag_id());
				se.save(tag_question2);
			}
		}
		
		if(!question_tag3.isEmpty()){
			String sql = "from Tags as a where a.tag_content =:tag" ;
			Tag_question tag_question3 = new Tag_question();
			@SuppressWarnings("unchecked")
			List<Tags> list = se.createQuery(sql).setParameter("tag", question_tag3).list();
			if(list.isEmpty()){
				Tags tag = new Tags();
				tag.setTag_content(question_tag3);
				se.save(tag);							
				tag_question3.setQuestion_id(question_id);
				tag_question3.setTag_id(tag.getTag_id());
				se.save(tag_question3);
			}
			else{
				tag_question3.setQuestion_id(question_id);
				tag_question3.setTag_id(list.get(0).getTag_id());
				se.save(tag_question3);
			}
		}
		
		
		tran.commit();
		
		se.close();
		sf.close();
		
		String result =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
				+"<question><question_id>"+question_id+"</question_id> <question_content>"
				+question_content+"</question_content> <question_time>"
				+question_time_str+"</question_time><question_tag1>"
				+question_tag1+ "</question_tag1><question_tag2>"
				+question_tag2+"</question_tag2><question_tag3>"
				+question_tag3+"</question_tag3><user_id>"
				+user_id+"</user_id><question_title>"
				+question_title+"</question_title><question_answer_num>"
				+0+"</question_answer_num></question>";
		
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("keyword/{keyword}")
	@Produces("text/plain")
	public Response getQuestionsByTitle(@PathParam("keyword")String keyword) throws IOException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String keyword2 = URLDecoder.decode(keyword, "utf-8");
		String sql = "from Questions as a where a.question_title like :keyword1 order by a.question_id desc";
		Query query = se.createQuery(sql);
		query.setParameter("keyword1", "%"+keyword2+"%");
		List<Questions> list = query.list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions>";
		int length = list.size();
		for(int i=0; i<length; ++i){
			String question_content = list.get(i).getQuestion_content();
			Date question_time = list.get(i).getQuestion_time();
			String question_tag1 = list.get(i).getQuestion_tag1();
			String question_tag2 = list.get(i).getQuestion_tag2();
			String question_tag3 = list.get(i).getQuestion_tag3();
			String user_id = list.get(i).getUser_id();
			String question_title = list.get(i).getQuestion_title();
			int question_answer_num = list.get(i).getQuestion_answer_num();
			int question_id = list.get(i).getQuestion_id();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String question_time_str = sdf.format(question_time);
			
			result += "<question><question_id>"+question_id+"</question_id> <question_content>"
					+question_content+"</question_content> <question_time>"
					+question_time_str+"</question_time><question_tag1>"
					+question_tag1+ "</question_tag1><question_tag2>"
					+question_tag2+"</question_tag2><question_tag3>"
					+question_tag3+"</question_tag3><user_id>"
					+user_id+"</user_id><question_title>"
					+question_title+"</question_title><question_answer_num>"
					+question_answer_num+"</question_answer_num></question>";
		}
		result += "</questions>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("tag/{keyword}")
	@Produces("text/plain")
	public Response getQuestionByTag(@PathParam("keyword")String keyword) throws IOException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String keyword2 = URLDecoder.decode(keyword, "utf-8");
		String sql = "from Tags as a where a.tag_content = ?";
		List<Tags> list = se.createQuery(sql).setParameter(0, keyword2).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
					.type(MediaType.TEXT_PLAIN).build();
		
		int tag_id = list.get(0).getTag_id();
		String sql2 = "from Tag_question as a where a.tag_id = ?";
		List<Tag_question> list2 = se.createQuery(sql2).setParameter(0, tag_id).list();
		if(list2.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
					.type(MediaType.TEXT_PLAIN).build();
				
		int length = list2.size();
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions>";
		for(int i = 0; i<length; ++i){
			Questions question = (Questions)se.get(Questions.class, list2.get(i).getQuestion_id());
			if(question==null)
				return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
						.type(MediaType.TEXT_PLAIN).build();
			
			int question_id = question.getQuestion_id();
			String question_content = question.getQuestion_content();
			Date question_time = question.getQuestion_time();
			String tag1 = question.getQuestion_tag1();
			String tag2 = question.getQuestion_tag2();
			String tag3 = question.getQuestion_tag3();
			String user_id = question.getUser_id();
			String question_title = question.getQuestion_title();
			int question_answer_num = question.getQuestion_answer_num();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String question_time_str = sdf.format(question_time);
			
			result += 	"<question><question_id>"
						+question_id+"</question_id> <question_content>"
						+question_content+"</question_content> <question_time>"
						+question_time_str+"</question_time><question_tag1>"
						+tag1+ "</question_tag1><question_tag2>"
						+tag2+"</question_tag2><question_tag3>"
						+tag3+"</question_tag3><user_id>"
						+user_id+"</user_id><question_title>"
						+question_title+"</question_title><question_answer_num>"
						+question_answer_num+"</question_answer_num></question>";
		}
		result += "</questions>";
		tran.commit();
		
		se.close();
		sf.close();
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET 
	@Path("user/{user_id}")
	@Produces("text/plain")
	public Response getQuestionsByUserid(@PathParam("user_id")String user_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Questions as a where a.user_id =? order by a.question_id desc";
		List<Questions> list = se.createQuery(sql).setParameter(0, user_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions></questions>")
					.type(MediaType.TEXT_PLAIN).build();
		
		int length = list.size();
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><questions>";
		for(int i = 0; i<length; ++i){
			Questions question = list.get(i);
			
			int question_id = question.getQuestion_id();
			String question_content = question.getQuestion_content();
			Date question_time = question.getQuestion_time();
			String tag1 = question.getQuestion_tag1();
			String tag2 = question.getQuestion_tag2();
			String tag3 = question.getQuestion_tag3();
			String question_title = question.getQuestion_title();
			int question_answer_num = question.getQuestion_answer_num();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String question_time_str = sdf.format(question_time);
			
			result += 	"<question><question_id>"
						+question_id+"</question_id> <question_content>"
						+question_content+"</question_content> <question_time>"
						+question_time_str+"</question_time><question_tag1>"
						+tag1+ "</question_tag1><question_tag2>"
						+tag2+"</question_tag2><question_tag3>"
						+tag3+"</question_tag3><user_id>"
						+user_id+"</user_id><question_title>"
						+question_title+"</question_title><question_answer_num>"
						+question_answer_num+"</question_answer_num></question>";
		}
		result += "</questions>";
		tran.commit();
		
		se.close();
		sf.close();
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
		
	@DELETE
	@Path("{question_id}")
	@Produces("text/plain")
	public Response deleteQuestion(@PathParam("question_id")int question_id ){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Questions question = (Questions)se.get(Questions.class, question_id);
		if(question==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		se.delete(question);
		
		
		tran.commit();
		se.close();
		sf.close();	
			
		return Response.status(204).entity("delete successfully").type(MediaType.TEXT_PLAIN).build();
	}
	
	@PUT 
	@Path("{question_id}")
	@Produces("text/plain")
	public Response updateQuestions(@PathParam("question_id")int question_id, @Context HttpServletRequest request) throws Exception{
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

		String question_content = root.elementText("question_content");
		String question_tag1 = root.elementText("question_tag1");
		String question_tag2 = root.elementText("question_tag2");
		String question_tag3 = root.elementText("question_tag3");
		String question_time_str = root.elementText("question_time");
		String user_id = root.elementText("user_id");
		String question_title = root.elementText("question_title");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date question_time = sdf.parse(question_time_str);
		
		Questions question = (Questions)se.get(Questions.class, question_id);
		if(question==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		question.setQuestion_content(question_content);
		question.setQuestion_tag1(question_tag1);
		question.setQuestion_tag2(question_tag2);
		question.setQuestion_tag3(question_tag3);
		question.setQuestion_time(question_time);
		question.setUser_id(user_id);	
		question.setQuestion_title(question_title);
		se.update(question);
		
		tran.commit();		
		se.close();
		sf.close();
		
		String result =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
				+"<question><question_id>"+question_id+"</question_id> <question_content>"
				+question_content+"</question_content> <question_time>"
				+question_time_str+"</question_time><question_tag1>"
				+question_tag1+ "</question_tag1><question_tag2>"
				+question_tag2+"</question_tag2><question_tag3>"
				+question_tag3+"</question_tag3><user_id>"
				+user_id+"</user_id><question_title>"
				+question_title+"</question_title><question_answer_num>"
				+0+"</question_answer_num></question>";
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
}
