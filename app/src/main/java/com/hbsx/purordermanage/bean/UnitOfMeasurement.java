package com.hbsx.purordermanage.bean;

import cn.bmob.v3.BmobObject;

/**
 * 计量单位：斤、件、盒、块、袋等
 * Created by Administrator on 2017/1/7 0007.
 */

public class UnitOfMeasurement extends BmobObject {
    private static final long serialVersionUID = 1L;
    private String unitName;
    public UnitOfMeasurement(){}
    public UnitOfMeasurement( String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
