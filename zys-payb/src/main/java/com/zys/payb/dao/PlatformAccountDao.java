package com.zys.payb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zys.payb.model.PlatformAccount;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
public interface PlatformAccountDao extends BaseMapper<PlatformAccount> {

    PlatformAccount selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(PlatformAccount record);
}