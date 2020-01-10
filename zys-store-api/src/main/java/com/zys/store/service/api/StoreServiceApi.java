package com.zys.store.service.api;

import java.util.Date;

/**
 * 需要暴露的接口
 */
public interface StoreServiceApi {

   Integer selectVersion(String supplierId, String goodsId);

   Integer selectStoreCount(String supplierId, String goodsId);

   Integer updateVersionAndCount(Integer version, String updateBy,
                                 Date updateTime,String supplierId, String goodsId);
}
