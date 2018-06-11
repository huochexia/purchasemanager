package com.hbsx.purordermanage.bean;

import org.litepal.crud.DataSupport;

/**
 * 该类是购物车中商品，对应本地数据库存储中的ShoppingCart表，其中属性objectId 对应Bmob云数据库
 * Commodity表中objectId。
 * Created by Administrator on 2017/2/8 0008.
 */

public class ShoppingCart extends DataSupport {
    private String objectId;
    private String category;
    private String commodityName;
    private Unit unit;
    private Float purchaseNum;
    private Float purchasePrice;

    public Float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }



    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Float getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Float purchaseNum) {
        this.purchaseNum = purchaseNum;
    }
}
