package com.scuec.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.scuec.yygh.hosp.repository.DepartmentRepository;
import com.scuec.yygh.hosp.service.DepartmentService;
import com.scuec.yygh.model.hosp.Department;
import com.scuec.yygh.vo.hosp.DepartmentQueryVo;
import com.scuec.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转换为department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString,Department.class);

        //根据 医院编号和科室编号查询
        Department departmentExist = departmentRepository
                .getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        if (departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

    //查询科室
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {

        //创建Pageable对象,设置当前页和么也记录数
        Pageable pageable = PageRequest.of(page-1,limit);

        //创建Example对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> all = departmentRepository.findAll(example, pageable);

        return all;
    }

    //删除科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        //先根据医院编号 和 科室编号查询
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            //调用方法删除
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建List集合,用于最终数据的封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号,查询医院的所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);

        Example<Department> example = Example.of(departmentQuery);

        //所有的科室信息
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号(bigCode)进行分组,获取每个大科室里面下级子科室
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //遍历map集合 departmentMap
        for (Map.Entry<String,List<Department>> entry:departmentMap.entrySet()){
            //大科室编号
            String bigCode = entry.getKey();
            //大科室编号 对应的全部数据(即下级科室)
            List<Department> departmentList1 = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigCode);
            departmentVo1.setDepname(departmentList1.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department:departmentList1){
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合中
                children.add(departmentVo2);
            }

            //把小科室的list集合放入大科室的children里面
            departmentVo1.setChildren(children);

            //放到最终的result中
            result.add(departmentVo1);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            return department.getDepname();
        }

        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
