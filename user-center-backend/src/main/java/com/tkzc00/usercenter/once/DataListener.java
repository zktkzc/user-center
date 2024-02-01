package com.tkzc00.usercenter.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

public class DataListener implements ReadListener<XingQiuTableUserInfo> {
    @Override
    public void invoke(XingQiuTableUserInfo data, AnalysisContext context) {
        System.out.println("读取到数据：" + data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("读取完毕");
    }
}
