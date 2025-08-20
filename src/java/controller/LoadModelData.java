package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "LoadModelData", urlPatterns = {"/LoadModelData"})
public class LoadModelData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Model.class);
        List<Model> modelList = c1.list();

        JsonArray modelsArray = new JsonArray();
        for (Model model : modelList) {
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("id", model.getId());
            modelJson.addProperty("model_name", model.getName());

            if (model.getBrand() != null) {
                modelJson.addProperty("brand_name", model.getBrand().getName());
            } else {
                modelJson.addProperty("brand_name", "");
            }

            modelsArray.add(modelJson);
        }

        responseObject.add("models", modelsArray);

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();

    }

}
