package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Arg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    SetmealDto saveWithDish(SetmealDto setmealDto);
    void deleteWithDish(List<Long> ids);
}
