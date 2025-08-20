package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProductDTO;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

@WebServlet(name = "LoadAllProducts", urlPatterns = {"/LoadAllProducts"})
public class LoadAllProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c2 = s.createCriteria(Product.class, "product")
                .createAlias("product.model", "model") 
                .createAlias("model.brand", "brand")
                .setFetchMode("model", FetchMode.JOIN)
                .setFetchMode("brand", FetchMode.JOIN)
                .setFetchMode("status", FetchMode.JOIN)
                .addOrder(Order.asc("product.id"))
                .setFirstResult(0)
                .setMaxResults(8);

        List<Product> productList = c2.list();
        List<ProductDTO> dtoList = productList.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        responseObject.add("productList", gson.toJsonTree(dtoList));

        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
