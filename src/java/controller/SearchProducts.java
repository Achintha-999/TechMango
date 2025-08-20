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
import hibernate.ProductVariant;
import hibernate.Quality;
import hibernate.Ram;
import hibernate.Storage;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    private static final int MAX_RESULT = 6;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject resposeObject = new JsonObject();
        resposeObject.addProperty("status", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Category category = null;
        boolean isVariantCategory = false;

        if (requestJsonObject.has("categoryName")) {
            String categoryValue = requestJsonObject.get("categoryName").getAsString();

            Criteria catCriteria = s.createCriteria(Category.class);
            catCriteria.add(Restrictions.eq("name", categoryValue));
            category = (Category) catCriteria.uniqueResult();

            if (category != null) {
                int categoryId = category.getId();
                isVariantCategory = (categoryId == 1 || categoryId == 2);
            }
        }

        List<Integer> productIds = null;

        if (isVariantCategory) {
            Criteria variantCriteria = s.createCriteria(ProductVariant.class);

            if (requestJsonObject.has("storageValue")) {
                String storageValue = requestJsonObject.get("storageValue").getAsString();
                Storage storage = (Storage) s.createCriteria(Storage.class)
                        .add(Restrictions.eq("name", storageValue))
                        .uniqueResult();
                if (storage != null) {
                    variantCriteria.add(Restrictions.eq("storage", storage));
                }
            }

            if (requestJsonObject.has("ramValue")) {
                String ramValue = requestJsonObject.get("ramValue").getAsString();
                Ram ram = (Ram) s.createCriteria(Ram.class)
                        .add(Restrictions.eq("name", ramValue))
                        .uniqueResult();
                if (ram != null) {
                    variantCriteria.add(Restrictions.eq("ram", ram));
                }
            }

            if (requestJsonObject.has("colorName")) {
                String colorValue = requestJsonObject.get("colorName").getAsString();
                Color color = (Color) s.createCriteria(Color.class)
                        .add(Restrictions.eq("name", colorValue))
                        .uniqueResult();
                if (color != null) {
                    variantCriteria.add(Restrictions.eq("color", color));
                }
            }

            if (requestJsonObject.has("processorValue")) {
                String processorValue = requestJsonObject.get("processorValue").getAsString();
                Processor processor = (Processor) s.createCriteria(Processor.class)
                        .add(Restrictions.eq("name", processorValue))
                        .uniqueResult();
                if (processor != null) {
                    variantCriteria.add(Restrictions.eq("processor", processor));
                }
            }

            if (requestJsonObject.has("graphicValue")) {
                String graphicValue = requestJsonObject.get("graphicValue").getAsString();
                Graphics graphics = (Graphics) s.createCriteria(Graphics.class)
                        .add(Restrictions.eq("name", graphicValue))
                        .uniqueResult();
                if (graphics != null) {
                    variantCriteria.add(Restrictions.eq("graphics", graphics));
                }
            }

            List<ProductVariant> variants = variantCriteria.list();
            productIds = variants.stream()
                    .map(v -> v.getProductl().getId())
                    .distinct()
                    .collect(Collectors.toList());

            if (productIds.isEmpty()) {
                s.close();
                resposeObject.add("productList", gson.toJsonTree(Collections.emptyList()));
                resposeObject.addProperty("allProductCount", 0);
                resposeObject.addProperty("status", true);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(resposeObject));
                return;
            }
        }

        Criteria productCriteria = s.createCriteria(Product.class);

        if (category != null) {
            productCriteria.add(Restrictions.eq("category", category));
        }

        if (productIds != null) {
            productCriteria.add(Restrictions.in("id", productIds));
        }

        if (requestJsonObject.has("brandName")) {
            String brandName = requestJsonObject.get("brandName").getAsString();
            Brand brand = (Brand) s.createCriteria(Brand.class)
                    .add(Restrictions.eq("name", brandName))
                    .uniqueResult();

            if (brand != null) {
                List<Model> modelList = s.createCriteria(Model.class)
                        .add(Restrictions.eq("brand", brand))
                        .list();
                if (!modelList.isEmpty()) {
                    productCriteria.add(Restrictions.in("model", modelList));
                }
            }
        }

        if (requestJsonObject.has("conditionName")) {
            String qualityValue = requestJsonObject.get("conditionName").getAsString();
            Quality quality = (Quality) s.createCriteria(Quality.class)
                    .add(Restrictions.eq("name", qualityValue))
                    .uniqueResult();
            if (quality != null) {
                productCriteria.add(Restrictions.eq("quality", quality));
            }
        }

        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();
            productCriteria.add(Restrictions.between("price", priceStart, priceEnd));
        }

        if (requestJsonObject.has("sortValue")) {
            String sortValue = requestJsonObject.get("sortValue").getAsString();
            switch (sortValue) {
                case "Sort by Latest":
                    productCriteria.addOrder(Order.desc("id"));
                    break;
                case "Sort by Oldest":
                    productCriteria.addOrder(Order.asc("id"));
                    break;
                case "Sort by Name":
                    productCriteria.addOrder(Order.asc("title"));
                    break;
                case "Sort by Price":
                    productCriteria.addOrder(Order.asc("price"));
                    break;
            }
        }

        // Get full result count before pagination
        resposeObject.addProperty("allProductCount", productCriteria.list().size());

        // Pagination logic
        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            productCriteria.setFirstResult(firstResult);
        }
        productCriteria.setMaxResults(MAX_RESULT);

        List<Product> filteredProducts = productCriteria.list();
        s.close();

        resposeObject.add("productList", gson.toJsonTree(filteredProducts));
        resposeObject.addProperty("status", true);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(resposeObject));
    }

}
