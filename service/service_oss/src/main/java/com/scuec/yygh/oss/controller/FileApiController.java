package com.scuec.yygh.oss.controller;

import com.scuec.yygh.common.result.Result;
import com.scuec.yygh.oss.service.FileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss/file")
//@CrossOrigin
public class FileApiController {
    //上传文件到阿里云oss
    @Autowired
    private FileService fileService;

    @ApiOperation(value ="文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
