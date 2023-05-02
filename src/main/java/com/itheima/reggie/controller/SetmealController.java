package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.service.impl.SetmealServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<SetmealDto> save(@RequestBody SetmealDto setmealDto) {
        SetmealDto newSetmealDto = setmealService.saveWithDish(setmealDto);
        return R.success(newSetmealDto);
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<SetmealDto> setmealDtoRecords;
        setmealDtoRecords = setmealRecords.stream().map(item -> {
            SetmealDto s = new SetmealDto();
            BeanUtils.copyProperties(item, s);
            Category category = categoryService.getById(item.getCategoryId());
            String categoryName = category.getName();
            s.setCategoryName(categoryName);
            return s;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoRecords);
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteWithDish(ids);
        return R.success("菜品删除成功");
    }

    @PostMapping("/status/{type}")
    public R<String> update(@RequestParam Long ids,
                            @PathVariable int type) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, type == 0 ? 0 : 1);
        setmealService.update(updateWrapper);
        return R.success("套餐已经停售");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,Setmeal::getId,id);
        Setmeal setmeal = setmealService.getById(id);

        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(id != null,SetmealDish::getSetmealId,id);
        List<SetmealDish> dishList = setmealDishService.list(dishLambdaQueryWrapper);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(dishList);
        return R.success(setmealDto);
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(status != null,Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        List<SetmealDto> setmealDtoList;
        setmealDtoList = setmealList.stream().map(item -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Category category = categoryService.getById(categoryId);
            dto.setCategoryName(category.getName());
            LambdaQueryWrapper<SetmealDish> dishQuerywapper = new LambdaQueryWrapper<>();
            dishQuerywapper.eq(SetmealDish::getSetmealId, item.getId());
            List<SetmealDish> list = setmealDishService.list(dishQuerywapper);
            dto.setSetmealDishes(list);
            return dto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }

    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){
        Category category = categoryService.getById(setmealDto.getCategoryId());
        setmealService.updateById(setmealDto);
        setmealDto.setCategoryName(category.getName());
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.updateBatchById(setmealDishes);
        return R.success("修改菜品成功");
    }


}
