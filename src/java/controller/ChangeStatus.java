package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


@WebServlet(name = "ChangeStatus", urlPatterns = {"/ChangeStatus"})
public class ChangeStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String prId = request.getParameter("prId");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(prId)) {
            responseObject.addProperty("message", "Invalid product Id!");

        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tr = null;

            try {
                tr = s.beginTransaction();

                Product product = (Product) s.get(Product.class, Integer.valueOf(prId));
                if (product == null) {
                    responseObject.addProperty("message", "Product not found!");
                } else {
        
                    int currentStatusId = product.getStatus().getId();
                    int newStatusId = (currentStatusId == 1) ? 2 : 1;

                    Status newStatus = (Status) s.get(Status.class, newStatusId);
                    product.setStatus(newStatus);

                    s.update(product);
                    tr.commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product status updated successfully!");
                    responseObject.addProperty("newStatus", newStatus.getValue());
                }
            } catch (Exception e) {
                if (tr != null) {
                    tr.rollback();
                }
                responseObject.addProperty("message", "Error: " + e.getMessage());
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
