package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    Integer countByMap(Map map);

    Setmeal getById(Long setmealId);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<Employee> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除菜品表中的菜品数据
     * @param id
     */
    void deleteById(Long id);

    void update(Setmeal setmeal);

    List<Setmeal> list(Setmeal setmeal);


    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}