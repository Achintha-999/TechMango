package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadAllOrders", urlPatterns = {"/LoadAllOrders"})
public class LoadAllOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria orderCriteria = s.createCriteria(hibernate.Orders.class);
            List<hibernate.Orders> ordersList = orderCriteria.list();

            JsonArray ordersJsonArray = new JsonArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (hibernate.Orders order : ordersList) {
                Criteria itemCriteria = s.createCriteria(hibernate.OrderItems.class);
                itemCriteria.add(Restrictions.eq("orders", order));
                List<hibernate.OrderItems> items = itemCriteria.list();

                double totalPrice = 0.0;
                Set<String> statusSet = new HashSet<>();

                JsonArray productTitles = new JsonArray();
                JsonArray productId = new JsonArray();
                JsonArray category = new JsonArray();

                if (!items.isEmpty()) {
                    for (hibernate.OrderItems item : items) {
                        totalPrice += item.getQty() * item.getProduct().getPrice();
                        productTitles.add(item.getProduct().getTitle());
                        productId.add(item.getProduct().getId());
                        category.add(item.getProduct().getCategory().getValue());
                        if (item.getOrderStatus() != null) {
                            statusSet.add(item.getOrderStatus().getValue());
                        }
                    }

                }

                JsonArray statusArray = new JsonArray();
                for (String status : statusSet) {
                    statusArray.add(status);
                }

                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("id", order.getId());

                orderJson.addProperty("date", sdf.format(order.getCreatedAt()));

                orderJson.addProperty("first_name", order.getUser().getFirst_name());
                orderJson.addProperty("last_name", order.getUser().getLast_name());
                orderJson.add("product_titles", productTitles);
                orderJson.add("product_id", productId);
                orderJson.add("category", category);
                orderJson.addProperty("price", totalPrice);

                orderJson.add("status", statusArray);

                ordersJsonArray.add(orderJson);
            }

            responseObject.addProperty("status", true);
            responseObject.add("orders", ordersJsonArray);

        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
