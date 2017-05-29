package com.hbsx.purordermanage.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 角色表，指定用户的角色，对应不同的权限。角色不用使用的功能不同。一个用户可以有多种角色，
 * 一个角色可以属于多个用户(关联表）
 * 1、管理角色--基本设置，如商品类别，商品物料，计量单位等的维护；负责其他人员的权限分配
 * 2、报货角色--确定采购品种及数量
 * 3、订货角色--按商品种类整理，形成订单并发送给供货商
 * 4、供货角色--录入商品单价，确认订单
 * 5、验货角色--确定最终收到数量，形成验货单
 * 6、录入角色--将验货单录入库存管理软件系统
 * Created by liuyong 2017/1/7 0007.
 */

public class Roles extends BmobObject {
    private static final long serialVersionUID = 1L;
    //角色名称
    private  String roleName;


    /*
          Getter 和 Setter
         */

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }




}
