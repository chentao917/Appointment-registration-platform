package com.scuec.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("package com.scuec.yygh.order.mapper")
public class OrderConfig {
}
