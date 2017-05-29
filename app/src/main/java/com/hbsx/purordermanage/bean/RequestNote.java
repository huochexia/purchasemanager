package com.hbsx.purordermanage.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 报货单：用于采购员每日的报货，只有商品名称和申请数量。因为报货员有多人（热菜、冷菜、面点及库管）
 * 每个人每日可形成多个报货单（正数为加，负数为减），当日报货单经审核后形成一个总表，
 * 报货单有三种状态：0，新报货单；1，已审核汇总；2，总报货单。
 * Created by liuyong on 2017/1/8 0008.
 */

public class RequestNote extends BmobObject implements Cloneable {
    private static final long serialVersionUID = 1L;
    private String person;
    //报货单编号(编号规则：报货人姓名年月日时分）
    private String orderNumber;
    //商品类别
    private String category;
    //商品名称
    private String commodityName;
    //商品单位
    private String unit;
    //订量
    private Float purchaseNum;
    //    报货单状态(0:新报货单，1：已汇总，2：汇总单
    private Integer orderState;

    /*
       Getter 和Setter
     */

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public Float getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Float purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Object clone(){
        RequestNote o = null;
        try {
            o = (RequestNote) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
