package com.zys.paya.controller;

import com.zys.paya.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
@RestController
public class PayController {

    private @Resource
    PayService payService;

    @RequestMapping("/pay")
    public String pay(@RequestParam("userId")String userId,
                      @RequestParam("orderId")String orderId,
                      @RequestParam("accountId")String accountId,
                      @RequestParam("money")Double money,
                      @RequestParam("platformAccount")String platformAccount) throws Exception {
        return payService.payment(userId, orderId, accountId, money,platformAccount);
    }
}
