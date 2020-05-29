package cn.lancedai.weye.server.util;

import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class StartTask implements CommandLineRunner {


    private final ServerConfig serverConfig;

    public StartTask(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * 启动任务
     *
     * @param args 启动参数
     */
    @Override
    public void run(String... args) {
        // 遍历Config
//        StringBuffer sb = new StringBuffer("");
        StringBuffer sb = new StringBuffer();
        showField(ServerConfig.class, serverConfig, 0, sb);
        log.debug("config => \n{}",sb.toString());
    }

    /**
     * 展示obj值
     *
     * @param tClass  类
     * @param obj     对象
     * @param tabNums 前置tab个数， 用于显示层级结构
     */
    private void showField(Class<?> tClass, Object obj, int tabNums, StringBuffer sb) {
        try {
            assert obj != null;
            assert tClass.isInstance(obj);
            val fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                // 判断是否基本类型'
                val fieldType = field.getType();
                if (isBasicType(fieldType)) {
                    sb.append(String.format("%s\t%s -- %s\n", StringTool.repeatChar('\t', tabNums), field.getName(), field.get(obj)));
                } else if (isArrayType(fieldType)) {
                    sb.append(String.format("%s\t%s -- %s\n", StringTool.repeatChar('\t', tabNums), field.getName(), Arrays.toString(new Object[]{field.get(obj)})));
                } else {
                    showField(fieldType, field.get(obj), tabNums + 1, sb);
                }
            }
        } catch (IllegalAccessException | AssertionError e) {
            e.printStackTrace();
        }
    }

    private boolean isArrayType(Class<?> fieldType) {
        return fieldType.isArray();
    }


    private final static List<Class<?>> BASIC_CLASS = Arrays.asList(new Class<?>[]{
            Short.class,
            short.class,
            Integer.class,
            int.class,
            Long.class,
            long.class,
            Float.class,
            float.class,
            Double.class,
            double.class,
            Character.class,
            char.class,
            String.class,
            Boolean.class,
            boolean.class,
            Byte.class,
            byte.class
    });

    private boolean isBasicType(Class<?> fieldClass) {
        return BASIC_CLASS.contains(fieldClass);
    }
}
