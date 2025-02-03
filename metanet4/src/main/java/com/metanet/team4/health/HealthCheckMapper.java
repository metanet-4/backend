package com.metanet.team4.health;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HealthCheckMapper {
    
	@Select("SELECT COUNT(*) FROM employees")
    int getEmployeeCount();
}
