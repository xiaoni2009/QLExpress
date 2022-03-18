package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class ImportTest {
    @Test
    public void testImport() throws Exception {
        String express = ""
            + "import java.math.*;"
            + "import com.ql.util.express.test.BeanExample;"
            + "abc = new BeanExample(\"张三\").unionName(\"李四\") ;"
            + "return new BigInteger(\"1000\");";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("import 实现错误", "1000", r.toString());
        System.out.println(r);
        System.out.println(context);

        context.put("a", "张三");
        //相当于变量a和"张三"的值比较
        System.out.println("test String" + runner.execute("a == \"张三\"", context, null, false, true));
        System.out.println("test String" + runner.execute("a == '张三'", context, null, false, true));
        //相当于两个变量a和张三比较，返回false
        System.out.println("test String" + runner.execute("a == 张三", context, null, false, true));
        //相当于两个变量a比较，所以返回true
        System.out.println("test String" + runner.execute("a == a", context, null, false, true));
    }
}
