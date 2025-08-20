package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (newPassword.isEmpty() || !Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "The password must contain at least uppercase, lowercase, number, special character and be minimum eight characters long!");
        } else if (confirmPassword.isEmpty() || !Util.isPasswordValid(confirmPassword)) {
            responseObject.addProperty("message", "The password must contain at least uppercase, lowercase, number, special character and be minimum eight characters long!");
        } else if (!confirmPassword.equals(newPassword)) {
            responseObject.addProperty("message", "Confirmed password does not match the entered new password!");
        } else {
            HttpSession ses = request.getSession(false);
            if (ses != null && ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tx = null;
                try {
                    tx = s.beginTransaction();

                    Criteria c = s.createCriteria(User.class);
                    c.add(Restrictions.eq("email", u.getEmail()));
                    List<User> users = c.list();

                    if (!users.isEmpty()) {
                        User u1 = users.get(0);

                        u1.setPassword(confirmPassword);

                        s.update(u1);

                        ses.setAttribute("user", u1);

                        tx.commit();

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Password updated successfully!");
                    } else {
                        responseObject.addProperty("message", "User not found.");
                    }
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    responseObject.addProperty("message", "Error updating password: " + e.getMessage());
                } finally {
                    s.close();
                }
            } else {
                responseObject.addProperty("message", "User session not found or expired.");
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
