package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "Users", urlPatterns = {"/Users"})
public class Users extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria criteria = s.createCriteria(User.class);
        criteria.addOrder(org.hibernate.criterion.Order.asc("id"));
        List<User> userList = criteria.list();
        
        responseObject.addProperty("status", true);
        responseObject.add("users", gson.toJsonTree(userList));
        
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
