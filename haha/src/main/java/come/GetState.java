package come;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/state")
public class GetState extends HttpServlet {
Logger logger = LoggerFactory.getLogger(this.getClass());

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setCharacterEncoding("utf8");
    resp.setHeader("content-type","text/html;charset=UTF-8");
    try {
        String ans = DB.getTable();
        resp.getWriter().print(ans);
        logger.debug(ans);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
