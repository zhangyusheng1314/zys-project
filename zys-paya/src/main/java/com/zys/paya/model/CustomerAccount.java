package com.zys.paya.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("customer_account")
public class CustomerAccount extends Model<CustomerAccount> {

    private static final long serialVersionUID = 1L;

    @TableId("account_id")
	private String accountId;
	@TableField("account_no")
	private String accountNo;
	@TableField("date_time")
	private Date dateTime;
	@TableField("current_balance")
	private BigDecimal currentBalance;
	private Integer version;
	@TableField("create_time")
	private Date createTime;
	@TableField("update_time")
	private Date updateTime;

	@Override
	protected Serializable pkVal() {
		return this.accountId;
	}

}
