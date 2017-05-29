package com.hbsx.purordermanage.bean;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 用户：
 * Created by liuyong on 2017/1/7 0007.
 */

public class User extends BmobUser {
    private static final long serialVersionUID = 1L;
    //用户单位名称
    private String company;
    //用户角色
    private Roles role;

    //商品所属类别，如果用户是供货商，这里反映该用户经营范围
    //用户与商品类别应该是多对多的关系
//    private List<CommodityCategory>  category;

    /*
      Getter 和 Setter
     */
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }


}
