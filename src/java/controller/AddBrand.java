package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddBrand", urlPatterns = {"/AddBrand"})
public class AddBrand extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject brand = gson.fromJson(request.getReader(), JsonObject.class);

        String brandName = brand.get("brandName").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (brandName == null || brandName.trim().isEmpty()) {
            responseObject.addProperty("message", "Brand Name cannot be empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(Brand.class);
            criteria.add(Restrictions.eq("name", brandName));

            if (!criteria.list().isEmpty()) {
                responseObject.addProperty("message", "This Brand name already exists!");
            } else {
                s.beginTransaction();

                Brand b = new Brand();
                b.setName(brandName.trim());

                s.save(b);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Brand has been added successfully!");
            }

        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("appliaction/json");
        response.getWriter().write(responseText);

    }
}
