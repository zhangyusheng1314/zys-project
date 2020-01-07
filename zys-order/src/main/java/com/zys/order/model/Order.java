package com.zys.order.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
@Data
@TableName("t_order")
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

	@TableId("order_id")
	private String orderId;
	@TableField("order_type")
	private String orderType;
	@TableField("city_id")
	private String cityId;
	@TableField("platform_id")
	private String platformId;
	@TableField("user_id")
	private String userId;
	@TableField("supplier_id")
	private String supplierId;
	@TableField("goods_id")
	private String goodsId;
	@TableField("order_status")
	private String orderStatus;
	@TableField("create_by")
	private String createBy;
	@TableField("create_time")
	private Date createTime;
	@TableField("update_by")
	private String updateBy;
	@TableField("update_time")
	private Date updateTime;
	@Override
	protected Serializable pkVal() {
		return this.orderId;
	}

}
