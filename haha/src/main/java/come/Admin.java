package come;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin")
public class Admin extends HttpServlet {
static Logger logger = LoggerFactory.getLogger(Admin.class);

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    logger.info("get amdin");
    resp.setCharacterEncoding("utf8");
    resp.setHeader("content-type", "text/html;charset=UTF-8");
    try {
        resp.getWriter().print(DB.getUsers());
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    logger.info("post admin");
    String openid = req.getParameter("id");
    String key = req.getParameter("key");
    String value = req.getParameter("value");
    logger.info("post params " + openid + " " + key + " " + value);
    resp.setCharacterEncoding("utf8");
    resp.setHeader("content-type", "text/html;charset=UTF-8");
    if (key.equals("nick") || key.equals("valid")) {
        try {
            DB.write(openid, key, value);
            String msg = DB.getTable();
            logger.info("after update db, " + msg);
            Chat.broadCast(msg);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
}
