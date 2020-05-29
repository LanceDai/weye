package cn.lancedai.weye.server.config;

import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.server.enumerate.Property;
import lombok.Data;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Email;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConfigurationProperties(prefix = "weye.server")
@Data
public class ServerConfig {
    private String username;
    private String password;
    private String expirationTime;
    private int processCenterPort;
    private String version;
    private FlinkServerConfig flinkServerConfig;

    // kafkaServers
    @Value("${spring.kafka.bootstrap-servers}")
    public String kafkaBootstrapServers;

    // MYSQL URL
    @Value("${spring.datasource.url}")
    public String dbUrl;
    @Value("${spring.datasource.username}")
    public String dbUsername;
    @Value("${spring.datasource.password}")
    public String dbPassword;



    @Bean
    public ConcurrentHashMap<Property, String> serverProperties() {
        val map = new ConcurrentHashMap<Property, String>();
        map.put(Property.DIGEST, StringTool.encode(this.username + "$" + this.password));
        return map;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
