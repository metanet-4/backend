package com.metanet.team4.health;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface IHealthCheckRepository {
    
    int getEmployeeCount();
}
