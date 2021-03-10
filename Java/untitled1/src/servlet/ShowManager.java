package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

@WebServlet(name = "ShowManager")
public class ShowManager extends HttpServlet {

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

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("gb2312");
        response.setContentType("text/html;charset=gb2312");
        Statement st ;
        Connection con ;
        ResultSet res = null;
        String sql ;
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");
        String stuff_name = request.getParameter("stuff_name");
        String owner = request.getParameter("owner");
        String previous = request.getParameter("previous");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con =  DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/depstore?serverTimezone=UTC",
                    "root", "ti163799");
            st = con.createStatement();
            if (type.equals("f")) {   //新建文件夹时，安卓传来的type值是"f"
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sql = "insert into thing values('f','" + stuff_name + "','_','" + previous + "','" + owner + "','" + formatter.format(new Date()) + "');";
                st.executeUpdate(sql);
                out.println("{\"status\":\"success\"}");

            } else {      //查询时，安卓传来的type值是"_"（短下划线）,如果上传图片，由FileDB处理
                sql = "select * from thing where owner='" + owner + "' and previous='" + previous + "';";
                res = st.executeQuery(sql);
                    StringBuilder output = new StringBuilder("[");
                    while (res.next()) {
                        output.append("{\"stuff_name\":\"").append(res.getString(2)).append("\",\"type\":\"").append(res.getString(1)).append("\",\"url\":\"").append(res.getString(3)).append("\"},");
                    }
                    output = new StringBuilder(output.substring(0, output.length() - 1));
                    output.append("]");
                    out.println(output);
                }
            res.close();
            st.close();
            con.close();
        } catch (Exception e) {
            out.print(e.getMessage());
        }
        out.close();
    }
}