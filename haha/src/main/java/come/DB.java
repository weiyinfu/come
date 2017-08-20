package come;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DB {

static DruidDataSource dataSource = new DruidDataSource();
static Logger logger = LoggerFactory.getLogger(DB.class);

static {
    try {
        Properties p = new Properties();
        p.load(DB.class.getResourceAsStream("/db.properties"));
        dataSource.configFromPropety(p);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


static void insert(String nick, String openid) throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    PreparedStatement select = conn.prepareStatement("select*from user where id=? or nick=?");
    select.setString(1, openid);
    select.setString(2, nick);
    ResultSet res = select.executeQuery();
    if (res.next() == false) {
        System.out.println("insert");
        PreparedStatement insert_ = conn.prepareStatement("insert into user set id=? , nick=? ");
        insert_.setString(1, openid);
        insert_.setString(2, nick);
        insert_.execute();
    }
    conn.close();
}

static List<JSONObject> tojson(ResultSet res) throws SQLException {
    List<JSONObject> ans = new ArrayList<>();
    int cols = res.getMetaData().getColumnCount();
    while (res.next()) {
        JSONObject json = new JSONObject();
        for (int i = 1; i <= cols; i++) {
            json.put(res.getMetaData().getColumnName(i), res.getString(i));
        }
        ans.add(json);
    }
    return ans;
}

static void reverseCome(String openid, int day) throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    Statement statement = conn.createStatement();
    String dayColumn = "day" + day;
    statement.executeUpdate(String.format("update user set %s=1-%s where id='%s'", dayColumn, dayColumn, openid));
    conn.close();
}

static boolean valid(String openid) throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    PreparedStatement statement = conn.prepareStatement("select*from user where id=? and valid=true");
    statement.setString(1, openid);
    ResultSet res = statement.executeQuery();
    boolean ans = res.next();
    conn.close();
    return ans;
}

static void write(String openid, String attr, String value) throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    String sql = String.format("update user set %s='%s' where id='%s'", attr, value, openid);
    Statement statement = conn.createStatement();
    System.out.println(sql);
    statement.execute(sql);
    conn.close();
}

static String getTable() throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    Statement statement = conn.createStatement();
    ResultSet res = statement.executeQuery("select nick,day1,day2,day3,day4,day5 from user where valid=true");
    List<JSONObject> ans = tojson(res);
    conn.close();
    ans = ans.stream().sorted(Comparator.comparing(o -> o.getString("nick"))).collect(Collectors.toList());
    for (JSONObject i : ans) {
        JSONArray days = new JSONArray(5);
        for (int j = 1; j <= 5; j++) {
            String k = "day" + j;
            days.add(i.getIntValue(k));
            i.remove(k);
        }
        i.put("days", days);
    }
    return JSON.toJSONString(ans);
}

public static String getUsers() throws SQLException {
    DruidPooledConnection conn = dataSource.getConnection();
    Statement statement = conn.createStatement();
    ResultSet res = statement.executeQuery("select id ,nick,valid from user");
    JSONArray ans = new JSONArray();
    while (res.next()) {
        JSONObject user = new JSONObject();
        user.put("id", res.getString("id"));
        user.put("nick", res.getString("nick"));
        user.put("valid", res.getInt("valid"));
        ans.add(user);
    }
    conn.close();
    return JSON.toJSONString(ans);
}

public static void main(String[] args) throws SQLException {
    Arrays.stream("吕正义 杨芳芳 姜璐 彭琴 魏印福 王沙沙".split(" ")).sorted().forEach(System.out::println);
}

}
