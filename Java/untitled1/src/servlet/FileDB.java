package servlet;

import com.jspsmart.upload.File;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "FileDB")
public class FileDB extends HttpServlet {
    /**
     * 取得数据库的连接
     * @return 一个数据库的连接
     */
    public static Connection getConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/depstore?serverTimezone=UTC",
                    "root", "ti163799");
            //该类就在 mysql-connector-java-5.0.8-bin.jar中,如果忘记了第一个步骤的导包，就会抛出ClassNotFoundException
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
        request.setCharacterEncoding("gb2312");
        res.setContentType("text/html;charset=gb2312");
        SmartUpload smartUpload=new SmartUpload();
        ServletConfig config=this.getServletConfig();
        smartUpload.initialize(config,request,res);
        PrintWriter out=res.getWriter();
        Statement st ;  Connection con ;    String sql ;
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=formatter.format(date);
        try {
            smartUpload.upload();
            Request req=smartUpload.getRequest();
            String previous=req.getParameter("previous");
            String owner=req.getParameter("owner");
            File smartFile=smartUpload.getFiles().getFile(0);
            smartFile.saveAs("/pictures/"+smartFile.getFileName(),
                    SmartUpload.SAVE_VIRTUAL);

            String url= "http://192.168.1.4:8080/untitled1_war/pictures/" + smartFile.getFileName();
            Class.forName("com.mysql.cj.jdbc.Driver");
            con  =  DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/depstore?serverTimezone=UTC",
                    "root", "ti163799");
            st = con.createStatement();
            sql = "insert into thing values('i','" + smartFile.getFileName() + "','" + url + "','" + previous + "','" + owner + "','" + time + "');";
            st.executeUpdate(sql);
        }catch (Exception e){
            out.println(e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
