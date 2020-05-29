package cn.lancedai.weye.server.util;


import cn.lancedai.weye.common.model.record.*;
import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.exception.UnKnownSourceTypeException;

import java.util.Arrays;

public class CommonUtils {
    public static Class<? extends BaseRecord> getRecordClass(String sourceType) throws UnKnownSourceTypeException {
        switch (sourceType.toUpperCase()) {
            case "SERVER":
                return ServerCollectorRecord.class;
            case "WEB":
                return HttpRequestCollectorRecord.class;
            case "COMMAND":
                return CustomCommandCollectorRecord.class;
            case "COMPUTE":
                return ComputeRecord.class;
            default:
                throw new UnKnownSourceTypeException(sourceType);
        }
    }


    public static void throwSQLThrowable(Boolean res, Class<?> clazz, String method, Object... args) throws SQLExecuteErrorException {
        if (!res) {
            throw new SQLExecuteErrorException(
                    String.format("%s: %s(%s)", clazz.getName(), method, argsToString(args))
            );
        }
    }

    private static String argsToString(Object... args) {
        return Arrays.toString(args);
    }
}
