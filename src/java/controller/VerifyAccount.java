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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = request.getSession(false); // do not create new if missing
        if (ses == null || ses.getAttribute("email") == null) {
            responseObject.addProperty("message", "Email not found in session!");
            sendJson(response, gson, responseObject);
            return;
        }

        String email = ses.getAttribute("email").toString();
        JsonObject verificationData = gson.fromJson(request.getReader(), JsonObject.class);
        String verificationCode = verificationData.get("verificationCode").getAsString();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(User.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("verification", verificationCode));

        List<User> users = c1.list();

        if (users.isEmpty()) {
            responseObject.addProperty("message", "Invalid verification code!");
        } else {
            User user = users.get(0);

            Transaction tx = s.beginTransaction();
            user.setVerification("Verified");
            s.update(user);
            tx.commit();

            ses.setAttribute("user", user);
            ses.setAttribute("verified", true);

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Verification successful!");
        }

        s.close();
        sendJson(response, gson, responseObject);
    }

    private void sendJson(HttpServletResponse response, Gson gson, JsonObject obj) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(obj));
    }
}
