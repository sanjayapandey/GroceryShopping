package edu.und.sanjaya.database.groceryshopping;

/**
 * Created by sanjaya on 12/1/17.
 */

public class CouponProduct {
    private int id;
    private int couponId;
    private String productName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "CouponProduct{" +
                "id=" + id +
                ", couponId=" + couponId +
                ", productName=" + productName +
                '}';
    }
}
