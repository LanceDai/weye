package cn.lancedai.weye.server.test;

import cn.lancedai.weye.common.model.record.ServerCollectorRecord;
import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.enumerate.Property;
import cn.lancedai.weye.server.service.ComputeRuleService;
import cn.lancedai.weye.server.util.FlinkUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WeyeApplicationTest {
    @Autowired
    private ConcurrentHashMap<Property, String> serverProperties;
    @Autowired
    private ServerConfig serverConfig;

    @Test
    @Ignore
    public void stringUtil() {
        String str = "[source]";
        System.out.println(StringTool.trimStartEndChars(str, "\\[", "\\]"));

        String str2 = "***内容1*内容2**";
        String o = StringTool.trimStartEndChars(str2, "\\*", "\\*");
        System.out.println("结果：" + o);

    }

    @Test
    @Ignore
    public void encodeTest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        val digest = StringTool.encode(URLEncoder.encode("admin$admin", "UTF-8"));
        val mess = MessageDigest.getInstance("MD5");
        val res = new String(mess.digest(URLEncoder.encode("admin$admin","UTF-8").getBytes()));
        System.out.println("res = " + res);
        System.out.println("digest = " + digest);
//        System.out.println("digest = " + digest);
//        val res = serverProperties.get(Property.DIGEST);
//        System.out.println("res = " + res);
    }

    @Autowired
    KafkaProperties properties;

    @Test
    @Ignore
    public void kafkaConfigTest() {
        val producerProps1 = properties.getProducer().buildProperties();
        // display
        System.out.println(1);
        producerProps1.forEach((key, value) -> System.out.println("key - " + key + " <--> value - " + value));
        val producerProps2 = properties.getProperties();
        System.out.println(2);
        producerProps2.forEach((key, value) -> System.out.println("key - " + key + " <--> value - " + value));
    }

    @Test
    @Ignore
    public void getMavenProjectVersionTest() {

        System.out.println("serverConfig = " + serverConfig.getVersion());
        assertEquals("0.0.1", serverConfig.getVersion());
    }

    @Autowired
    FlinkUtils flinkUtils;
    @Autowired
    ComputeRuleService service;

    @Test
    @Ignore
    public void flinkJobAddTest() {
        val rule = service.getById(1);
        flinkUtils.submitComputeJob(ServerCollectorRecord.class, rule);
    }
}
