package jaxrs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Courses;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;


@Path("courses")
public class CoursesResource {
	@GET
	@Path("keyword/{keyword}")
	@Produces("text/plain")
	public Response getCoursesByKeyword(@PathParam("keyword")String keyword) throws UnsupportedEncodingException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String keyword2 = URLDecoder.decode(keyword, "utf-8");
		String sql = "from Courses as a where a.course_id like :keyword1 or a.course_name like :keyword2";
		Query query = se.createQuery(sql);
		query.setParameter("keyword1", "%"+keyword2+"%");
		query.setParameter("keyword2", "%"+keyword2+"%");
		List<Courses> list = query.list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><courses></courses>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><courses>";
		int length = list.size();
		for(int i = 0; i<length; ++i){
			String course_id = list.get(i).getCourse_id();
			String course_dept = list.get(i).getCourse_dept();
			Double course_credit = list.get(i).getCourse_credit();
			Double course_rate = list.get(i).getCourse_rate();
			int course_comment_num = list.get(i).getCourse_comment_num();
			String course_name = list.get(i).getCourse_name();
			result += "<course><course_id>"+course_id+"</course_id><course_name>"+course_name+"</course_name><course_dept>"
						+course_dept+"</course_dept><course_credit>"+course_credit+"</course_credit><course_rate>"+course_rate
						+"</course_rate><course_comment_num>"+course_comment_num+"</course_comment_num></course>";
		}
		result += "</courses>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("course_id/{course_id}")
	@Produces("text/plain")
	public Response getCoursesByCourseId(@PathParam("course_id")String course_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Courses course = (Courses)se.get(Courses.class, course_id);
		if(course==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		String course_dept = course.getCourse_dept();
		Double course_credit = course.getCourse_credit();
		Double course_rate = course.getCourse_rate();
		int course_comment_num = course.getCourse_comment_num();
		String course_name = course.getCourse_name();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"<course><course_id>"+course_id+"</course_id><course_name>"
						+course_name+"</course_name><course_dept>"+course_dept+"</course_dept><course_credit>"
						+course_credit+"</course_credit><course_rate>"+course_rate
						+"</course_rate><course_comment_num>"+course_comment_num+"</course_comment_num></course>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Produces("text/plain")
	public Response getCourses(@Context HttpServletRequest request){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Courses";
		Query query = se.createQuery(sql);
		String start = request.getHeader("start");
		query.setFirstResult((Integer.parseInt(start))*10); 
		query.setMaxResults(10);

		List<Courses> list = query.list();
		if(list.isEmpty())
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();	
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><courses>";
		int length = list.size();
		for(int i = 0; i<length; ++i){
			String course_id = list.get(i).getCourse_id();
			String course_dept = list.get(i).getCourse_dept();
			Double course_credit = list.get(i).getCourse_credit();
			Double course_rate = list.get(i).getCourse_rate();
			int course_comment_num = list.get(i).getCourse_comment_num();
			String course_name = list.get(i).getCourse_name();
			result += "<course><course_id>"+course_id+"</course_id><course_name>"+course_name+"</course_name><course_dept>"
						+course_dept+"</course_dept><course_credit>"+course_credit+"</course_credit><course_rate>"+course_rate
						+"</course_rate><course_comment_num>"+course_comment_num+"</course_comment_num></course>";
		}
		result += "</courses>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	
	/*@GET
	@Path("insert_courses")
	@Produces("text/plain")
	public Response aa() throws IOException{
		File fin = new File("C:\\Users\\Administrator\\Desktop\\lessons.sql");
		FileInputStream fis = new FileInputStream(fin);			
		InputStreamReader isr = new InputStreamReader(fis, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		 
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
				   
		String line = null;
		while ((line = br.readLine()) != null) {
			Transaction tran = se.beginTransaction();
			String a = line.substring(78, 83);
			List<Courses> list = se.createQuery("from Courses as a where a.course_id = ?").setParameter(0, a).list();
			if(list.isEmpty())
				se.createSQLQuery(line).executeUpdate();
			tran.commit();
		}
		 
		br.close();		
		se.close();
		sf.close();
				
		return Response.status(200).entity("haha").build();
	}*/
}
