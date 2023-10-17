package com.example.farming_partner.Market_Price;

import java.util.Date;

public class Pricepost {
    private String priceId;
    private String crop;
    private String price;
    private String validityDate; // Add validity date field

    public Pricepost() {
    }

    public Pricepost(String priceId, String crop, String price, String validityDate) {
        this.priceId = priceId;
        this.crop = crop;
        this.price = price;
        this.validityDate = validityDate;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }
}
