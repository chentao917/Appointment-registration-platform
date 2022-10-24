package com.scuec.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        //读取文件的路径
        String fileName = "E:\\BaiduNetdiskDownload\\excel\\01.xlsx";


        //调用读取方法
        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
