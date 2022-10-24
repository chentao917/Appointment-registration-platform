package com.scuec.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {


        List<UserData> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UserData userData = new UserData();
            userData.setUserId(i);
            userData.setUserName("lucy"+i);
            list.add(userData);
        }
        //设置excel 文件路径和 文件名称
        String fileName = "E:\\BaiduNetdiskDownload\\excel\\01.xlsx";


        //调用方法实现写操作
        EasyExcel.write(fileName,UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
