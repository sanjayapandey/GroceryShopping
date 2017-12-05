package edu.und.sanjaya.database.groceryshopping;

import java.util.List;

/**
 * Created by sanjaya on 12/1/17.
 */

public class Coupon {
    private int id;
    private double discount;
    private List<String> productNames;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", discount=" + discount +
                ", productNames=" + productNames +
                '}';
    }
}
