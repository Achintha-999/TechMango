package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.HibernateUtil;
import hibernate.Model;
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

@WebServlet(name = "AddModel", urlPatterns = {"/AddModel"})
public class AddModel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Gson gson = new Gson();
        JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);

        String modelName = jsonRequest.get("modelName").getAsString();
        String brandId = jsonRequest.get("brandId").getAsString();

        if (modelName == null || modelName.trim().isEmpty()) {
            responseObject.addProperty("message", "Model Name cannot be empty!");
        } else if (!model.Util.isInteger(brandId) || Integer.parseInt(brandId) == 0) {
            responseObject.addProperty("message", "Please select a valid Brand!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            try {

                Brand brand = (Brand) s.get(Brand.class, Integer.valueOf(brandId));

                if (brand == null) {
                    responseObject.addProperty("message", "Selected Brand does not exist!");
                } else {

                    Criteria c1 = s.createCriteria(Model.class);
                    c1.add(Restrictions.eq("name", modelName));
                    c1.add(Restrictions.eq("brand", brand));

                    if (!c1.list().isEmpty()) {
                        responseObject.addProperty("message", "This Model name already exists for the selected Brand!");
                    } else {

                        s.beginTransaction();

                        Model m = new Model();
                        m.setName(modelName);
                        m.setBrand(brand);

                        s.save(m);
                        s.getTransaction().commit();

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Model has been added successfully!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                responseObject.addProperty("message", "An error occurred while adding the model.");
            } finally {
                s.close();
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
