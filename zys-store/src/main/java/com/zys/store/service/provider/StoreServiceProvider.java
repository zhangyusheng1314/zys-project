package com.zys.store.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.zys.store.dao.StoreDao;
import com.zys.store.service.api.StoreServiceApi;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
/**
 * 暴露的接口的实现类
 */
@Component
@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class StoreServiceProvider implements StoreServiceApi {

    private @Resource
    StoreDao storeDao;
    @Override
    public Integer selectVersion(String supplierId, String goodsId) {
        return storeDao.selectVersion(supplierId,goodsId);
    }

    @Override
    public Integer selectStoreCount(String supplierId, String goodsId) {
        return storeDao.selectStoreCount(supplierId,goodsId);
    }

    @Override
    public Integer updateVersionAndCount(Integer version, String updateBy, Date updateTime, String supplierId, String goodsId) {
        return storeDao.updateVersionAndCount(version,updateBy,updateTime,supplierId,goodsId);
    }


}
