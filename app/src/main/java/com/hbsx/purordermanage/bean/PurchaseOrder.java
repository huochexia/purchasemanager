package com.hbsx.purordermanage.bean;

import cn.bmob.v3.BmobObject;

/**
 * 采购订单：
 * Created by liuyong on 2017/1/7 0007.
 */

public class PurchaseOrder extends BmobObject {
    private static final long serialVersionUID = 1L;
    //下单人
    private String orderUserName;
    //订单日期
    private String orderDate;
    //商品分类
    private String category;
    //以商品名称、商品单位及数量组成对应数据列表。不用商品对象做为记录的原因，是因为商品可能会被删除
    //或修改，所以不能用商品做为关联关系。
    //商品名称
    private String commodityName;
    //商品单位
    private String commodityUnit;
    //商品数量
    private Float purchaseNum;
    //商品单价
    private Float price = 0.0f;
    //商品实际数量
    private Float actualNum = 0.0f;
    //再次确定数量
    private Float actualAgain = 0.0f;
    //商品金额
    private Float sum = 0.0f;

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    //供货商
    private User provider;
    //供货商名称
    private String providername;
    //订单状态 （0：初始订单，1：已分配，2：已验货，3：已确认，4：已录入）
    private Integer orderState;
    //录入状态(0:尚未录入，1：已经录入）
    private Integer inputState;
    /*
       Getter和Setter
     */

    public Float getActualAgain() {
        return actualAgain;
    }

    public void setActualAgain(Float actualAgain) {
        this.actualAgain = actualAgain;
    }

    public String getProvidername() {
        return providername;
    }

    public void setProvidername(String providername) {
        this.providername = providername;
    }

    public String getOrderUserName() {
        return orderUserName;
    }

    public void setOrderUserName(String orderUserName) {
        this.orderUserName = orderUserName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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

    public String getCommodityUnit() {
        return commodityUnit;
    }

    public void setCommodityUnit(String commodityUnit) {
        this.commodityUnit = commodityUnit;
    }

    public Float getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Float purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getActualNum() {
        return actualNum;
    }

    public void setActualNum(Float actualNum) {
        this.actualNum = actualNum;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(User provider) {
        this.provider = provider;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public Integer getInputState() {
        return inputState;
    }

    public void setInputState(Integer inputState) {
        this.inputState = inputState;
    }

}
