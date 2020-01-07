package com.zys.store.model;

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
@TableName("store")
public class Store extends Model<Store> {

    private static final long serialVersionUID = 1L;

    @TableId("store_id")
	private String storeId;
	@TableField("goods_id")
	private String goodsId;
	@TableField("supplier_id")
	private String supplierId;
	@TableField("goods_name")
	private String goodsName;
	@TableField("store_count")
	private Integer storeCount;
	private Integer version;
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
		return this.storeId;
	}

}
