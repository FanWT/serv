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

import model.Answers;
import model.Questions;
import model.Users;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

@Path("answers")
public class AnswersResource {
	@SuppressWarnings("unchecked")
	@GET
	@Path("question_id/{question_id}")
	@Produces("text/plain")
	public Response getAnswersByQuestionId(@PathParam("question_id")int question_id, @Context HttpServletRequest request){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Answers as a where a.question_id = :question_id order by a.answer_time desc";
		Query query = se.createQuery(sql);
		String start = request.getHeader("start");
		query.setFirstResult((Integer.parseInt(start))*10); 
		query.setMaxResults(10);
		
		List<Answers> list = query.setParameter("question_id", question_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><answers></answers>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><answers>";
		int length = list.size();
		for(int i= 0; i<length; ++i){
			int answer_id = list.get(i).getAnswer_id();
			String answer_content = list.get(i).getAnswer_content();
			String user_id = list.get(i).getUser_id();
			Date answer_time = list.get(i).getAnswer_time();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String answer_time_str = sdf.format(answer_time);
			result += "<answer><answer_id>"+answer_id+"</answer_id><user_id>"
						+user_id+"</user_id><question_id>"+question_id+"</question_id><answer_content>"+answer_content
						+"</answer_content><answer_time>"+answer_time_str+"</answer_time></answer>";
		}
		result += "</answers>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("user/{user_id}")
	@Produces("text/plain")
	public Response getAnswersByUserId(@PathParam("user_id")String user_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Answers as a where a.user_id = :user_id order by a.answer_id desc";
		List<Answers> list = se.createQuery(sql).setParameter("user_id", user_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><answers></answers>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><answers>";
		int length = list.size();
		for(int i= 0; i<length; ++i){
			int answer_id = list.get(i).getAnswer_id();
			String answer_content = list.get(i).getAnswer_content();
			int question_id = list.get(i).getQuestion_id();
			Date answer_time = list.get(i).getAnswer_time();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String answer_time_str = sdf.format(answer_time);
			result += "<answer><answer_id>"+answer_id+"</answer_id><user_id>"
						+user_id+"</user_id><question_id>"+question_id+"</question_id><answer_content>"+answer_content
						+"</answer_content><answer_time>"+answer_time_str+"</answer_time></answer>";
		}
		result += "</answers>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@POST
	@Produces("text/plain")
	public Response createAnswers(@Context HttpServletRequest request) throws ParseException, IOException, DocumentException{
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
		
		String answer_content = root.elementText("answer_content");
		String answer_time_str = root.elementText("answer_time");
		String user_id = root.elementText("user_id");
		int question_id = Integer.parseInt(root.elementText("question_id"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date answer_time = sdf.parse(answer_time_str);
		
		Users user = (Users)se.get(Users.class, user_id);
		if(user.getState()==0)
			return Response.status(403).entity("you are banned").type(MediaType.TEXT_PLAIN).build();
		
		Answers answer = new Answers();
		answer.setAnswer_content(answer_content);
		answer.setAnswer_time(answer_time);
		answer.setQuestion_id(question_id);
		answer.setUser_id(user_id);
		
		se.save(answer);
		int answer_id = answer.getAnswer_id();
		
		Questions question = (Questions)se.get(Questions.class, answer.getQuestion_id());
		int question_answer_num = question.getQuestion_answer_num();
		question.setQuestion_answer_num(question_answer_num+1);
		se.update(question);
				
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><answer><answer_id>"+answer_id+"</answer_id><user_id>"
						+user_id+"</user_id><question_id>"+question_id+"</question_id><answer_content>"+answer_content
						+"</answer_content><answer_time>"+answer_time_str+"</answer_time></answer>";
		
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@DELETE
	@Path("{answer_id}")
	@Produces("text/plain")
	public Response deleteAnswers(@PathParam("answer_id")int answer_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Answers answer = (Answers) se.get(Answers.class, answer_id);
		if(answer==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		int question_id = answer.getQuestion_id();
		se.delete(answer);
		
		Questions question = (Questions)se.get(Questions.class, question_id);
		int question_answer_num = question.getQuestion_answer_num();
		question.setQuestion_answer_num(question_answer_num-1);
		se.update(question);
		
		tran.commit();
		se.close();
		sf.close();
		
		return Response.status(204).entity("delete successfully").type(MediaType.TEXT_PLAIN).build();
	}
	
	@PUT
	@Produces("text/plain")
	@Path("{answer_id}")
	public Response updateAnswer(@PathParam("answer_id")int answer_id, @Context HttpServletRequest request) throws Exception{
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
		
		String answer_content = root.elementText("answer_content");
		String answer_time_str = root.elementText("answer_time");
		String user_id = root.elementText("user_id");
		int question_id = Integer.parseInt(root.elementText("question_id"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date answer_time = sdf.parse(answer_time_str);
		
		Answers answer = (Answers) se.get(Answers.class, answer_id);
		if(answer==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		answer.setAnswer_content(answer_content);
		answer.setAnswer_time(answer_time);
		answer.setQuestion_id(question_id);
		answer.setUser_id(user_id);
		
		se.update(answer);
				
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><answer><answer_id>"+answer_id+"</answer_id><user_id>"
						+user_id+"</user_id><question_id>"+question_id+"</question_id><answer_content>"+answer_content
						+"</answer_content><answer_time>"+answer_time_str+"</answer_time></answer>";
		
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
}
