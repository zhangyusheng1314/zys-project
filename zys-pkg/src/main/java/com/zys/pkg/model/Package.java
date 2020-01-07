package com.zys.pkg.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Package extends Model<Package> {

    private static final long serialVersionUID = 1L;

    @TableId("package_id")
	private String packageId;
	@TableField("order_id")
	private String orderId;
	@TableField("supplier_id")
	private String supplierId;
	@TableField("address_id")
	private String addressId;
	private String remark;
	@TableField("package_status")
	private String packageStatus;
	@TableField("create_time")
	private Date createTime;
	@TableField("update_time")
	private Date updateTime;

	@Override
	protected Serializable pkVal() {
		return this.packageId;
	}

}
