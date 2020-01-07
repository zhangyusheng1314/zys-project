package com.zys.paya.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zys.paya.model.CustomerAccount;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
public interface PayService extends IService<CustomerAccount> {
	String payment(String userId,String orderId,String accountId,Double money,String platformAccount);
	Integer updateAccount(String accountId, BigDecimal newMoney, Integer version, Date updateTime);
}
