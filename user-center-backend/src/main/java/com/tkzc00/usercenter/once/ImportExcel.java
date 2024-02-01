package com.tkzc00.usercenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 一次性代码，用于导入Excel数据
 *
 * @author tkzc00
 */
public class ImportExcel {
    public static void main(String[] args) {
        String filename = "C:\\Users\\tkzc00\\Desktop\\星球表.xlsx";
        readByListener(filename);
        synchronizedRead(filename);
    }

    /**
     * 通过监听器读取Excel文件
     *
     * @param fileName 文件名
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new DataListener())
                .sheet().doRead();
    }

    /**
     * 同步读取Excel文件
     *
     * @param fileName 文件名
     */
    public static void synchronizedRead(String fileName) {
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class)
                .sheet().doReadSync();
        for (XingQiuTableUserInfo xingQiuTableUserInfo : list) {
            System.out.println(xingQiuTableUserInfo);
        }
    }
}
