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

import model.Ddls;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

@Path("/ddls")
public class DdlsResource {
	@GET
	@Path("user_id/{user_id}")
	@Produces("text/plain")
	public Response getDdlsByUserId(@Context HttpServletRequest request){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String user_id = request.getHeader("user_id");
		String sql = "from Ddls as a where a.user_id = :user_id";
		List<Ddls> list = se.createQuery(sql).setParameter("user_id", Integer.parseInt(user_id)).list();
		if(list.isEmpty())
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ddls>";
		int length = list.size();
		for(int i = 0; i<length; ++i){
			int ddl_id = list.get(i).getDdl_id();
			Date ddl_time = list.get(i).getDdl_time();
			String content = list.get(i).getDdl_content();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			String ddl_time_str = sdf.format(ddl_time);
			result += "<ddl><ddl_id>"+ddl_id+"</ddl_id><ddl_time>"+ddl_time_str
						+"</ddl_time><user_id>"+user_id+"</user_id><content>"+content+"</content></ddl>";
		}
		result += "</ddls>";
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@POST
	@Produces("text/plain")
	public Response createDdls(@Context HttpServletRequest request) throws IOException, DocumentException, ParseException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		InputStream in = request.getInputStream();
		Scanner sc = new Scanner(in, "utf-8");
		String input = sc.useDelimiter("\\A").next();

		Document dom = DocumentHelper.parseText(input);
		Element root=dom.getRootElement();
		
		String content = root.elementText("content");
		String ddl_time_str = root.elementText("ddl_time");
		String user_id = root.elementText("user_id");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date ddl_time = sdf.parse(ddl_time_str);
		
		Ddls ddl = new Ddls();
		ddl.setDdl_content(content);
		ddl.setDdl_time(ddl_time);
		ddl.setUser_id(user_id);
		se.save(ddl);
		
		int ddl_id = ddl.getDdl_id();
		
		tran.commit();
		se.close();
		sf.close();
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ddl><ddl_id>"+ddl_id+"</ddl_id><ddl_time>"+ddl_time_str
						+"</ddl_time><user_id>"+user_id+"</user_id><content>"+content+"</content></ddl>";
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@DELETE
	@Path("{ddl_id}")
	@Produces("text/plain")
	public Response deleteDdls(@Context HttpServletRequest request){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String ddl_id = request.getHeader("ddl_id");
		
		Ddls ddl = (Ddls)se.get(Ddls.class, Integer.parseInt(ddl_id));
		if(ddl==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		se.delete(ddl);
		tran.commit();
		se.close();
		sf.close();
		
		return Response.status(204).entity("delete successfully").type(MediaType.TEXT_PLAIN).build();
	}
	
}
