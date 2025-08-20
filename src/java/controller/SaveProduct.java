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
import hibernate.Status;
import hibernate.Storage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    private static final int ACTIVE_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please sign in!");
            sendResponse(response, responseObject);
            return;
        }

        String brandId = request.getParameter("brandId");
        String modelId = request.getParameter("modelId");
        String categoryId = request.getParameter("categoryId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String storageId = request.getParameter("storageId");
        String ramId = request.getParameter("ramId");
        String processorID = request.getParameter("processorID");
        String graphicID = request.getParameter("graphicID");
        String colorId = request.getParameter("colorId");
        String conditionId = request.getParameter("conditionId");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");

        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");

        if (title == null || title.trim().isEmpty()) {
            responseObject.addProperty("message", "Product title can not be empty");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isInteger(brandId) || Integer.parseInt(brandId) == 0) {
            responseObject.addProperty("message", "Please select a Brand");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isInteger(modelId) || Integer.parseInt(modelId) == 0) {
            responseObject.addProperty("message", "Please select a Model");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isInteger(categoryId) || Integer.parseInt(categoryId) == 0) {
            responseObject.addProperty("message", "Please select a Category");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isInteger(conditionId) || Integer.parseInt(conditionId) == 0) {
            responseObject.addProperty("message", "Please select a Condition");
            sendResponse(response, responseObject);
            return;
        }
        if (description == null || description.trim().isEmpty()) {
            responseObject.addProperty("message", "Product description can not be empty");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isDouble(price) || Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0");
            sendResponse(response, responseObject);
            return;
        }
        if (!Util.isInteger(qty) || Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0");
            sendResponse(response, responseObject);
            return;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {

            Brand brand = (Brand) s.get(Brand.class, Integer.valueOf(brandId));
            if (brand == null) {
                responseObject.addProperty("message", "Please select a valid Brand Name!");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            Model model = (Model) s.get(Model.class, Integer.valueOf(modelId));
            if (model == null) {
                responseObject.addProperty("message", "Please select a valid Model Name!");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            if (model.getBrand().getId() != brand.getId()) {
                responseObject.addProperty("message", "Selected Model does not belong to the chosen Brand!");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            Category category = (Category) s.get(Category.class, Integer.valueOf(categoryId));
            if (category == null) {
                responseObject.addProperty("message", "Please select a valid Category!");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            Quality quality = (Quality) s.get(Quality.class, Integer.valueOf(conditionId));
            if (quality == null) {
                responseObject.addProperty("message", "Please select a valid Quality!");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            boolean needsVariant = category.getValue().equalsIgnoreCase("Laptop")
                    || category.getValue().equalsIgnoreCase("Desktop");

            Storage storage = null;
            Ram ram = null;
            Color color = null;
            Processor processor = null;
            Graphics graphics = null;

            if (needsVariant) {
                // Validate and check each variant entity individually

                if (!Util.isInteger(storageId) || Integer.parseInt(storageId) == 0) {
                    responseObject.addProperty("message", "Please select a valid Storage!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
                storage = (Storage) s.get(Storage.class, Integer.valueOf(storageId));
                if (storage == null) {
                    responseObject.addProperty("message", "Selected Storage is not valid!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }

                if (!Util.isInteger(ramId) || Integer.parseInt(ramId) == 0) {
                    responseObject.addProperty("message", "Please select a valid RAM!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
                ram = (Ram) s.get(Ram.class, Integer.valueOf(ramId));
                if (ram == null) {
                    responseObject.addProperty("message", "Selected RAM is not valid!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }

                if (!Util.isInteger(processorID) || Integer.parseInt(processorID) == 0) {
                    responseObject.addProperty("message", "Please select a valid Processor!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
                processor = (Processor) s.get(Processor.class, Integer.valueOf(processorID));
                if (processor == null) {
                    responseObject.addProperty("message", "Selected Processor is not valid!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }

                if (!Util.isInteger(graphicID) || Integer.parseInt(graphicID) == 0) {
                    responseObject.addProperty("message", "Please select a valid Graphics!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
                graphics = (Graphics) s.get(Graphics.class, Integer.valueOf(graphicID));
                if (graphics == null) {
                    responseObject.addProperty("message", "Selected Graphics is not valid!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }

                if (!Util.isInteger(colorId) || Integer.parseInt(colorId) == 0) {
                    responseObject.addProperty("message", "Please select a valid Color!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
                color = (Color) s.get(Color.class, Integer.valueOf(colorId));
                if (color == null) {
                    responseObject.addProperty("message", "Selected Color is not valid!");
                    sendResponse(response, responseObject);
                    s.close();
                    return;
                }
            }

            if (part1 == null || part2 == null || part3 == null) {
                responseObject.addProperty("message", "All three product images are required");
                sendResponse(response, responseObject);
                s.close();
                return;
            }
            if (part1.getSubmittedFileName() == null || part1.getSubmittedFileName().isEmpty()
                    || part2.getSubmittedFileName() == null || part2.getSubmittedFileName().isEmpty()
                    || part3.getSubmittedFileName() == null || part3.getSubmittedFileName().isEmpty()) {
                responseObject.addProperty("message", "All three product images are required");
                sendResponse(response, responseObject);
                s.close();
                return;
            }

            s.beginTransaction();

            Product p = new Product();
            p.setModel(model);
            p.setTitle(title);
            p.setDescription(description);
            p.setCategory(category);
            p.setQuality(quality);
            p.setPrice(Double.parseDouble(price));
            p.setQty(Integer.parseInt(qty));
            p.setCreated_at(new Date());
            Status status = (Status) s.get(Status.class, SaveProduct.ACTIVE_ID);
            p.setStatus(status);
            s.save(p);

            if (needsVariant) {
                ProductVariant pv = new ProductVariant();
                pv.setProductl(p);
                pv.setStorage(storage);
                pv.setColor(color);
                pv.setProcessor(processor);
                pv.setRam(ram);
                pv.setGraphics(graphics);

                s.save(pv);
            }

            s.getTransaction().commit();
            int id = p.getId();
            s.close();

            // Save images 
            String appPath = getServletContext().getRealPath("");
            String newPath = appPath.replace("build" + File.separator + "web",
                    "web" + File.separator + "product-images");
            File productFolder = new File(newPath, String.valueOf(id));
            productFolder.mkdirs();

            Files.copy(part1.getInputStream(), new File(productFolder, "image1.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.copy(part2.getInputStream(), new File(productFolder, "image2.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.copy(part3.getInputStream(), new File(productFolder, "image3.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            responseObject.addProperty("status", true);

        } catch (Exception e) {
            if (s.getTransaction() != null && s.getTransaction().isActive()) {
                s.getTransaction().rollback();
            }
            e.printStackTrace();
            responseObject.addProperty("message", "Server error: " + e.getMessage());
        } finally {
            if (s.isOpen()) {
                s.close();
            }
        }

        sendResponse(response, responseObject);
    }

    private void sendResponse(HttpServletResponse response, JsonObject responseObject) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
