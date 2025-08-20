package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminSignIn", urlPatterns = {"/AdminSignIn"})
public class AdminSignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String email = signIn.get("email").getAsString();
        String password = signIn.get("password").getAsString();

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email can not be empty!");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email!");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password can not be empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Admin.class);

            Criterion crt1 = Restrictions.eq("email", email);
            Criterion crt2 = Restrictions.eq("password", password);

            c.add(crt1);
            c.add(crt2);

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid credentials!");
            } else {

                Admin a = (Admin) c.list().get(0);
                final String verificationCode = Util.generateCode();
                a.setVerification(verificationCode);

                s.save(a);
                s.beginTransaction().commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "TechMango - Admin Verification", "<h1>" + verificationCode + "</h1>");
                    }
                }).start();

                HttpSession ses = request.getSession();
                ses.setAttribute("admin", a);
                ses.setAttribute("email", email);

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Please check your email for the verfication code");

            }
            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("admin") != null) {

            responseObject.addProperty("message", "1");
        } else {
            responseObject.addProperty("message", "2");
        }

        Gson gson = new Gson();
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
