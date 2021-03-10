package servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(name = "Database")
public class Database extends HttpServlet {

    /**
     * 取得数据库的连接
     *
     * @return 一个数据库的连接
     */
    public static Connection getConnection() {
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


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("gb2312");
        response.setContentType("text/html;charset=gb2312");
        PrintWriter out = response.getWriter();
        String aim = request.getParameter("aim");
        Statement st;
        Connection con;
        ResultSet res;
        String sql;
        Account account = new Account(request.getParameter("name"), request.getParameter("password"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con  =  DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/depstore?serverTimezone=UTC",
                    "root", "ti163799");
            st = con.createStatement();
            sql = "select * from student;";
            res = st.executeQuery(sql);
            String status = "0";//0表示用户名和密码都错
            while (res.next()) {
                if (res.getString(1).equals(account.username)) {
                    status = "1";  //1 表示用户名错误，查无此人
                    if (res.getString(2).equals(account.pwd)) {
                        status = "2";   //2表示用户名，密码都正确，准许登录
                    }
                    break;
                }
            }
            if (aim.equals("login")) {
                if (status.equals("2"))
                    out.println("{\"status\":\"login_success\"}");    //返回JSON:{"status":"login_success"}
                else if (status.equals("1"))
                    out.println("{\"status\":\"password_mismatch\"}");  //返回JSON:{"status":"password_mismatch"}
                else
                    out.println("{\"status\":\"name_not_found\"}");                    //返回JSON:{"status":"name_not_found"}
            } else if (aim.equals("createAccount")) {
                if (status.equals("0")) {
                    sql = "insert into student values('" + account.username + "','" + account.pwd + "');";
                    st.executeUpdate(sql);
                    out.println("{\"status\":\"create_success\"}");                                          //返回JSON:{"status":"create_success"}
                } else {
                    out.println("{\"status\":\"name_duplicated\"}");                                          //返回JSON:{"status":"name_duplicated"}
                }
            } else {
                out.println("{\"status\":\"unknown_error\"}");                                          //返回JSON:{"status":"name_duplicated"}
            }
            out.close();
            res.close();
            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println(e.getMessage());
        }
    }
}
