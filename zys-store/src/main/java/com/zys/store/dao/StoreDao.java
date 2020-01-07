package com.zys.store.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zys.store.model.Store;

import java.util.Date;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
public interface StoreDao extends BaseMapper<Store> {
    Integer selectVersion(String supplierId, String goodsId);

    Integer selectStoreCount(String supplierId, String goodsId);

    Integer updateVersionAndCount(Integer version, String updateBy,
                                  Date updateTime, String supplierId, String goodsId);
}