package com.hbsx.purordermanage.bean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于保存购物车中的商品对应的单位，对应本地数据库Unit表。其中属性objectId对应Bmob云数据库中UnitOfMeasurement中
 * 的objectId,unitName对应Bmob云中数据库表UnitOfMeasurement中的unitName.
 * Created by Administrator on 2017/2/8 0008.
 */

public class Unit extends DataSupport {
    private String objectId;
    private String unitName;
    private List<ShoppingCart> commodity = new ArrayList<>();

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<ShoppingCart> getCommodity() {
        return commodity;
    }

    public void setCommodity(List<ShoppingCart> commodity) {
        this.commodity = commodity;
    }
}
