package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Category;
import hibernate.Color;
import hibernate.Graphics;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Processor;
import hibernate.Product;
import hibernate.Quality;
import hibernate.Ram;

import hibernate.Storage;
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
import org.hibernate.criterion.Order;

@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //search-brands
        Criteria c1 = s.createCriteria(Brand.class);
        List<Brand> brandList = c1.list();

        //get-models
        Criteria c2 = s.createCriteria(Model.class);
        List<Model> modelList = c2.list();
        //get-models-end

        //get-colors
        Criteria c3 = s.createCriteria(Color.class);
        List<Color> colorList = c3.list();
        //get-colors-end

        //get-storage
        Criteria c4 = s.createCriteria(Storage.class);
        List<Storage> storageList = c4.list();
        //get-storage-end

        //get-quality
        Criteria c5 = s.createCriteria(Quality.class);
        List<Quality> qualityList = c5.list();
        //get-quality-end

        //load-product-data
        Criteria c6 = s.createCriteria(Product.class);
        c6.addOrder(Order.desc("id"));

        //get-categoryList
        Criteria c10 = s.createCriteria(Category.class);
        List<Category> categoryList = c10.list();
        //get-categoryList-end

        //get-ram
        Criteria c7 = s.createCriteria(Ram.class);
        List<Ram> ramList = c7.list();
        //get-ram-end

        //get-processorList
        Criteria c8 = s.createCriteria(Processor.class);
        List<Processor> processorList = c8.list();
        //get-processorList-end

        //get-processorList
        Criteria c9 = s.createCriteria(Graphics.class);
        List<Graphics> graphicList = c9.list();
        //get-processorList-end

        responseObject.addProperty("allProductCount", c6.list().size());

        c6.setFirstResult(0);
        c6.setMaxResults(6);

        List<Product> productList = c6.list();

        //load-product-data-end
        responseObject.add("brandList", gson.toJsonTree(brandList));
        responseObject.add("modelList", gson.toJsonTree(modelList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("storageList", gson.toJsonTree(storageList));
        responseObject.add("qualityList", gson.toJsonTree(qualityList));
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("ramList", gson.toJsonTree(ramList));
        responseObject.add("processorList", gson.toJsonTree(processorList));
        responseObject.add("graphicList", gson.toJsonTree(graphicList));

        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
    //    System.out.println(gson.toJson(responseObject));

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();
    }

}
