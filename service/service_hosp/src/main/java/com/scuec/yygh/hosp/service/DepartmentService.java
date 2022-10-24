package com.scuec.yygh.hosp.service;

import com.scuec.yygh.model.hosp.Department;
import com.scuec.yygh.vo.hosp.DepartmentQueryVo;
import com.scuec.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室的接口
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    //删除科室接口
    void remove(String hoscode, String depcode);

    //根据医院编号,查询医院所有科室的列表
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据科室编号,和医院编号,查询科室名称
    String getDepName(String hoscode, String depcode);

    //根据科室编号,和医院编号,查询科室名称
    Department getDepartment(String hoscode, String depcode);
}
