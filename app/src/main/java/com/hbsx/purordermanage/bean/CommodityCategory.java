package com.hbsx.purordermanage.bean;

import android.widget.ImageView;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 商品分类：存储各种商品所属的类别，如蔬菜、水果、猪肉、牛羊肉、活鱼、冻货、副食品、粮油、
 *           厨杂等。与商品是一对多的关系，一种商品对应一个类别，一个类别包含多种商品；
 *           与供货商是多对多关系（关联表），一家供货商可提供多种类型商品，一个商品类型可以由
 *           多家供货商提供。
 * Created by liuyong on 2017/1/7 0007.
 */

public class CommodityCategory extends BmobObject {
    private static final long serialVersionUID = 1L;
    //商品类别名称
    private String categoryName;
    private Integer imageId;
    public  CommodityCategory(){

    }
    public CommodityCategory(String name,Integer imageId){
        this.categoryName = name;
        this.imageId = imageId;
    }
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
