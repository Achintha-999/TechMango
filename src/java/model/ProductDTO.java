package model;

import hibernate.Product;

public class ProductDTO {

    private int id;
    private String title;
    private String brand;
    private String status;

    private double price;
    private int qty;

    public ProductDTO(Product p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.price = p.getPrice();
        this.qty = p.getQty();
        this.brand = p.getModel() != null && p.getModel().getBrand() != null
                ? p.getModel().getBrand().getName()
                : "";
        this.status = p.getStatus() != null ? p.getStatus().getValue() : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
