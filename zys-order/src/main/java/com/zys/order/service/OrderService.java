package com.zys.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zys.order.model.Order;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
public interface OrderService extends IService<Order> {
	boolean createOrder(String cityId,String platformId,String userId,
                     String supplierId,String goodsId);

	void sendOrderlyMessagePkg(String userId, String orderId);
}
