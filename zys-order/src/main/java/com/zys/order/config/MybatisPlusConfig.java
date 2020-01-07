package com.zys.order.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
  * @title: MyBatis-Plus 配置
  * @author SunXingbo
  * @date 2019/3/11 0011 13:39
  */
@Configuration
@MapperScan(basePackages={
		"com.zys.order.dao","com.zys.order.mapper"
})
public class MybatisPlusConfig {

	/**
	 * sql性能分析插件，输出sql语句及所需时间
	 * @return
	 */
	@Bean
	public PerformanceInterceptor performanceInterceptor() {
		PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
		performanceInterceptor.setMaxTime(5000);
		performanceInterceptor.setFormat(true);
		return performanceInterceptor;
	}

	/**
	 * 分页插件
	 * @return
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		paginationInterceptor.setDialectType("mysql");

		return paginationInterceptor;
	}
}
