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
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ForgotPassword", urlPatterns = {"/ForgotPassword"})
public class ForgotPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String email = signIn.get("email").getAsString();

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email can not be empty!");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;

            try {
                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", email));

                List<User> userList = c.list();

                if (userList.isEmpty()) {
                    responseObject.addProperty("message", "Invalid email!");
                } else {
                    User u = userList.get(0);
                    final String verificationCode = Util.generateCode();
                    u.setVerification(verificationCode);

                    tx = s.beginTransaction();
                    s.update(u);
                    tx.commit();

                    new Thread(() -> {
                        Mail.sendMail(email, "TechMango - Reset Your Password using this Verification Code :", "<h1>" + verificationCode + "</h1>");
                    }).start();

                    HttpSession ses = request.getSession();
                    ses.setAttribute("user", u);
                    ses.setAttribute("email", email);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Please check your email for the verification code");
                }
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                responseObject.addProperty("message", "Server error occurred: " + e.getMessage());
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
