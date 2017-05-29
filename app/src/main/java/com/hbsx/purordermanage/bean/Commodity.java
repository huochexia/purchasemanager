package com.hbsx.purordermanage.bean;

import cn.bmob.v3.BmobObject;

/**
 * 商品：存储商品的属性，如类别、名称、数量、计量单位、单价、小计金额等。
 * 与商品分类是一对一关系
 * Created by Administrator on 2017/1/7 0007.
 */

public class Commodity extends BmobObject {
    private static final long serialVersionUID = 1L;
    //类别
    private CommodityCategory commCategory;
    //商品名称
    private String commName;
     //计量单位
    private UnitOfMeasurement unit;
    //商品数量，不写入商品数据库中
    private Float purchaseNum=0.0f;

    //选择状态标志,不写入商品数据库中
    private boolean isSelected ;

    //无参数构造方法

    public Commodity(){

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public CommodityCategory getCommCategory() {
        return commCategory;
    }

    public void setCommCategory(CommodityCategory commCategory) {
        this.commCategory = commCategory;
    }

    public String getCommName() {
        return commName;
    }

    public void setCommName(String commName) {
        this.commName = commName;
    }



    public Float getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Float purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public UnitOfMeasurement getUnit() {
        return unit;
    }

    public void setUnit(UnitOfMeasurement unit) {
        this.unit = unit;
    }


}
