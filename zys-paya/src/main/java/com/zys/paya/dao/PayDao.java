package com.zys.paya.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zys.paya.model.CustomerAccount;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
public interface PayDao extends BaseMapper<CustomerAccount> {
    Integer updateAccount(String accountId, BigDecimal newMoney, Integer version, Date updateTime);
}