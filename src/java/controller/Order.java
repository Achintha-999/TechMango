package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Order", urlPatterns = {"/Order"})
public class Order extends HttpServlet {

 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user != null) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            try {
                // Get all orders for the user
                Criteria orderCriteria = session.createCriteria(hibernate.Orders.class);
            
                List<hibernate.Orders> ordersList = orderCriteria.list();

                if (ordersList.isEmpty()) {
                    responseObject.addProperty("message", "No order history found.");
                } else {
                   
                    JsonObject orderMap = new JsonObject();

                    for (hibernate.Orders order : ordersList) {
                        Criteria itemCriteria = session.createCriteria(hibernate.OrderItems.class);
                        itemCriteria.add(Restrictions.eq("orders", order));
                        List<hibernate.OrderItems> items = itemCriteria.list();

          
                        for (hibernate.OrderItems item : items) {
                            JsonObject itemJson = new JsonObject();
                            itemJson.addProperty("product_id", item.getProduct().getId());
                            itemJson.addProperty("product_title", item.getProduct().getTitle());
                            itemJson.addProperty("order_id", order.getId());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            itemJson.addProperty("date", sdf.format(order.getCreatedAt()));

                            itemJson.addProperty("qty", item.getQty());
                            itemJson.addProperty("price", item.getProduct().getPrice());

                            double total = item.getQty() * item.getProduct().getPrice();
                            itemJson.addProperty("total", total);

                            itemJson.addProperty("status", item.getOrderStatus().getValue());

                         
                            if (!orderMap.has(String.valueOf(order.getId()))) {
                                orderMap.add(String.valueOf(order.getId()), gson.toJsonTree(new java.util.ArrayList<>()));
                            }
                            orderMap.getAsJsonArray(String.valueOf(order.getId())).add(itemJson);
                        }
                    }

                    responseObject.addProperty("status", true);
                    responseObject.add("orders", orderMap);
                    responseObject.addProperty("message", "Order history loaded successfully.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                responseObject.addProperty("message", "Server error occurred.");
            } finally {
                session.close();
            }
        } else {
            responseObject.addProperty("message", "Please login to view order history.");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
