package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 用户自定义的类静态方法操作
 *
 * @author qhlhl2010@gmail.com
 */
public class OperatorSelfDefineClassFunction extends OperatorBase implements CanClone {
    private final String functionName;
    private final String[] parameterTypes;
    private final Class<?>[] parameterClasses;
    private final Class<?> operatorClass;
    private Object operatorInstance;
    private final Method method;
    private boolean isReturnVoid;
    private final boolean maybeDynamicParams;

    public OperatorSelfDefineClassFunction(String operatorName, Class<?> aOperatorClass, String aFunctionName,
        Class<?>[] aParameterClassTypes, String[] aParameterDesc, String[] aParameterAnnotation, String errorInfo)
        throws Exception {
        this.name = operatorName;
        this.errorInfo = errorInfo;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.parameterTypes = new String[aParameterClassTypes.length];
        this.operateDataDesc = aParameterDesc;
        this.operateDataAnnotation = aParameterAnnotation;
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        operatorClass = aOperatorClass;
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineClassFunction(String operatorName, String aClassName, String aFunctionName,
        Class<?>[] aParameterClassTypes, String[] aParameterDesc, String[] aParameterAnnotation, String errorInfo)
        throws Exception {
        this.name = operatorName;
        this.errorInfo = errorInfo;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.parameterTypes = new String[aParameterClassTypes.length];
        this.operateDataDesc = aParameterDesc;
        this.operateDataAnnotation = aParameterAnnotation;
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        operatorClass = ExpressUtil.getJavaClass(aClassName);
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineClassFunction(String operatorName, String aClassName, String aFunctionName,
        String[] aParameterTypes, String[] aParameterDesc, String[] aParameterAnnotation, String errorInfo)
        throws Exception {
        this.name = operatorName;
        this.errorInfo = errorInfo;
        this.functionName = aFunctionName;
        this.parameterTypes = aParameterTypes;
        this.operateDataDesc = aParameterDesc;
        this.operateDataAnnotation = aParameterAnnotation;
        this.parameterClasses = new Class[this.parameterTypes.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
        }
        operatorClass = ExpressUtil.getJavaClass(aClassName);
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    @Override
    public OperatorBase cloneMe(String opName, String errorInfo) throws Exception {
        return new OperatorSelfDefineClassFunction(opName, this.operatorClass.getName(), this.functionName,
            this.parameterClasses, this.operateDataDesc, this.operateDataAnnotation, errorInfo);
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Object[] parameters = DynamicParamsUtil.transferDynamicParams(parent, list, parameterClasses,
            this.maybeDynamicParams);
        Object obj;
        if (Modifier.isStatic(this.method.getModifiers())) {
            obj = this.method.invoke(null, ExpressUtil.transferArray(parameters, parameterClasses));
        } else {
            if (operatorInstance == null) {
                operatorInstance = operatorClass.newInstance();
            }
            obj = this.method.invoke(operatorInstance, ExpressUtil.transferArray(parameters, parameterClasses));
        }

        if (obj != null) {
            return OperateDataCacheManager.fetchOperateData(obj, obj.getClass());
        }
        if (this.isReturnVoid) {
            return OperateDataCacheManager.fetchOperateDataAttr("null", void.class);
        } else {
            return OperateDataCacheManager.fetchOperateDataAttr("null", null);
        }
    }
}
