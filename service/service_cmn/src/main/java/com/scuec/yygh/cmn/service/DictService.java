package com.scuec.yygh.cmn.service;

import com.scuec.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DictService extends IService<Dict> {
    //根据id查询子数据
    List<Dict> findChildData(Long id);

    //导出数据字典接口
    void exportData(HttpServletResponse response);

    //导入数据字典
    void importDictData(MultipartFile file);

    //根据dictCode 和 value查询
    String getDictName(String dictCode, String value);

    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
