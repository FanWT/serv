package jaxrs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Downloads;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

@Path("/downloads")
public class DownloadsResource {
	@GET
	@Produces("text/palin")
	@Path("course_id/{course_id}")
	public Response listDownloads(@PathParam("course_id")String course_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String sql = "from Downloads as a where a.course_id = :course_id";
		Query query = se.createQuery(sql);
		List<Downloads> list = query.setParameter("course_id", course_id).list();
		if(list.isEmpty())
			return Response.status(200).entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><downloads></downloads>")
					.type(MediaType.TEXT_PLAIN).build();
		
		tran.commit();
		se.close();
		sf.close();
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><downloads>";
		for(int i=0; i<list.size();++i){
			result += "<download><download_id>"+list.get(i).getDownload_id()+"</download_id><user_id>"
					+list.get(i).getUser_id()+"</user_id><course_id>"+list.get(i).getCourse_id()+"</course_id><download_time>"
					+list.get(i).getDownload_time()+"</download_time><download_name>"+list.get(i).getDownload_name()
					+"</download_name><download_size>"+list.get(i).getDownload_size()+ "</download_size><download_type>"
					+list.get(i).getDownload_type()+"</download_type><download_url>"
					+"http://115.28.41.28:8080/download/"+list.get(i).getDownload_id()+"."+list.get(i).getDownload_type()
					+"</download_url><download_num>"+list.get(i).getDownload_num()+"</download_num></download>";
		}
		result +="</downloads>";
		
		return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@POST
	@Produces("text/plain")
	public Response createDownloads(@Context HttpServletRequest request) throws Exception{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		String download_name = request.getHeader("name");
		download_name = URLDecoder.decode(download_name, "utf-8");
		String user_id = request.getHeader("user");
		String course_id = request.getHeader("course");
		String download_size = request.getHeader("size");
	    String download_type = request.getHeader("type");
	    String date_str = request.getHeader("date");
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date date = sdf.parse(date_str);
		
		Downloads download = new Downloads();
		download.setCourse_id(course_id);
		download.setDownload_time(date);
		download.setDownload_name(download_name);
		download.setDownload_num(0);
		download.setDownload_size(Integer.parseInt(download_size));
		download.setDownload_url("1");
		download.setUser_id(user_id);
		download.setDownload_type(download_type);
		
		se.save(download);
		int download_id = download.getDownload_id();
		
		tran.commit();
		se.close();
		sf.close();
		
		InputStream in = request.getInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[10240];
	    int n = 0;
	    while (-1 != (n = in.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    
	    byte[] file_content = output.toByteArray();
	    System.out.println(download_name);
		File file = new File("/home/apache-tomcat-7.0.70/webapps/download/"+download_id+"."+download_type);
		FileOutputStream fstream = new FileOutputStream(file);
		BufferedOutputStream stream = new BufferedOutputStream(fstream);
		stream.write(file_content);
		stream.close();
				
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><download><download_id>"+download_id+"</download_id><user_id>"
				+user_id+"</user_id><course_id>"+course_id+"</course_id><download_time>"+date+"</download_time><download_name>"
				+download_name+"</download_name><download_size>"+download_size+ "</download_size><download_type>"+download_type
				+"</download_type><download_url>"+"http://115.28.41.28:8080/download/"+download_id+"."+download_type
				+"</download_url><download_num>"+0+"</download_num></download>";
		
		return Response.status(201).entity(result).type(MediaType.TEXT_PLAIN).build();
	}
	
	@PUT
	@Produces("text/plain")
	@Path("{download_id}")
	public Response updateDownloads(@PathParam("download_id")int download_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Downloads download = (Downloads)se.get(Downloads.class, download_id);
		if(download==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		int num = download.getDownload_num();
		download.setDownload_num(num+1);
		
		se.update(download);
		
		tran.commit();		
		se.close();
		sf.close();
		
		return Response.status(200).entity("success").type(MediaType.TEXT_PLAIN).build();
	}
	
	@DELETE
	@Produces("text/plain")
	@Path("{download_id}")
	public Response deleteDownload(@PathParam("download_id")int download_id){
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session se = sf.openSession();
		Transaction tran = se.beginTransaction();
		
		Downloads download = (Downloads)se.get(Downloads.class, download_id);		
		if(download==null)
			return Response.status(404).entity("error").type(MediaType.TEXT_PLAIN).build();
		
		se.delete(download);
		tran.commit();		
		se.close();
		sf.close();
		
		return Response.status(204).entity("success").type(MediaType.TEXT_PLAIN).build();
	}
}
