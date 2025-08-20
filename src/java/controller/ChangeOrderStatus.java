package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Product;
import hibernate.Status;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "ChangeOrderStatus", urlPatterns = {"/ChangeOrderStatus"})
public class ChangeOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderId = request.getParameter("orderId"); // orders.id
        System.out.println(orderId);

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            @SuppressWarnings("unchecked")
            List<OrderItems> orderItemsList = (List<OrderItems>) session.createQuery(
                    "FROM OrderItems oi WHERE oi.orders.id = :orderId")
                    .setParameter("orderId", Integer.valueOf(orderId))
                    .list();

            if (orderItemsList != null && !orderItemsList.isEmpty()) {
                for (OrderItems item : orderItemsList) {
                    if (item.getOrderStatus() != null) {
                        int currentStatusId = item.getOrderStatus().getId();
                        int newStatusId = currentStatusId;

                        if (currentStatusId == 1) {
                            newStatusId = 2;
                        } else if (currentStatusId == 2) {
                            newStatusId = 3;
                        } else if (currentStatusId == 3) {
                            newStatusId = 4;
                        } else if (currentStatusId == 4) {
                            newStatusId = 5;
                        } else if (currentStatusId == 5) {
                            newStatusId = 5;
                        }

                        if (newStatusId != currentStatusId) {
                            OrderStatus newStatus = (OrderStatus) session.get(OrderStatus.class, newStatusId);
                            item.setOrderStatus(newStatus);
                            session.update(item);
                            responseObject.addProperty("newStatus", newStatus.getValue());
                        }
                    }
                }

                transaction.commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Order item statuses updated successfully!");
            } else {
                responseObject.addProperty("message", "No order items found for the given order ID.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            responseObject.addProperty("message", "Error: " + e.getMessage());
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
