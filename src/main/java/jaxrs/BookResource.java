package jaxrs;

import model.Book;
import model.DatabaseConnection;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

@Path("books")
public class BookResource {
    //参考AnswersResource::createAnswers
    @POST
    @Produces("text/plain")
    public Response doCreate(@Context HttpServletRequest request) throws IOException, SQLException {
        System.out.print("FWT\n\n\n\n\n\n");
        InputStream in = request.getInputStream();
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String input = s.hasNext() ? s.next() : "";
        s.close();

        System.out.print(input);
        System.out.print("FWT\n\n\n\n\n\n");
        JSONObject jsonObject = JSONObject.fromObject(input);
        Book book = (Book)JSONObject.toBean(jsonObject, Book.class);
        DatabaseConnection connection = new DatabaseConnection();
        String sql = "INSERT INTO books(iid, aid,name,status,note)VALUES(?,?,?,?,?)";

        PreparedStatement pstmt = connection.getConn().prepareStatement(sql);
        int index =2;
        //pstmt.setString(1, "0");
        pstmt.setString(1,"1");
        pstmt.setString(index, "admin");
        pstmt.setString(index+1, book.getName());
        pstmt.setInt(index+2, book.getStatus());
        pstmt.setString(index + 3, book.getNote());
        String result = null;
        if (pstmt.executeUpdate() > 0)
            result = "true";
        else
            result = "false";


        return Response.status(200).entity(result).type(MediaType.TEXT_PLAIN).build();
    }
}
