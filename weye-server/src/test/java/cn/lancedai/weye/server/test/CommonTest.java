package cn.lancedai.weye.server.test;

import cn.lancedai.weye.common.tool.NetWorkTool;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 简单测试， 不依赖于SpringBoot 环境
 */
@RunWith(JUnit4.class)
public class CommonTest {
    @Test
    public void testWhetherIsSubClass() {
        System.out.println(Long.class.isAssignableFrom(Number.class));
        System.out.println(Double.class.isAssignableFrom(Number.class));
        System.out.println(Integer.class.isAssignableFrom(Number.class));
        System.out.println(Number.class.isAssignableFrom(Long.class));
        System.out.println(Number.class.isAssignableFrom(Double.class));
        System.out.println(Number.class.isAssignableFrom(Integer.class));
        System.out.println(Number.class.isAssignableFrom(Long.class));
    }

    @Test
    public void getHostTest() {
        System.out.println("getLocalHostName() = " + getLocalHostName());
        System.out.println("getAllLocalHostIP() = " + Arrays.toString(getAllLocalHostIP()));
    }

    public String getLocalHostName() {
        String hostName;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (Exception ex) {
            hostName = "";
        }
        return hostName;
    }

    public String[] getAllLocalHostIP() {
        String[] ret = null;
        try {
            String hostName = getLocalHostName();
            if (hostName.length() > 0) {
                InetAddress[] addrs = InetAddress.getAllByName(hostName);
                if (addrs.length > 0) {
                    ret = new String[addrs.length];
                    for (int i = 0; i < addrs.length; i++) {
                        ret[i] = addrs[i].getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ret = null;
        }
        return ret;
    }
}
