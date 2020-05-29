package cn.lancedai.weye.server.config;

import cn.lancedai.weye.common.exception.UserNotLoginException;
import cn.lancedai.weye.common.tool.StoreTool;
import cn.lancedai.weye.server.enumerate.Property;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<HttpFilter> filterRegistrationBean() {
        FilterRegistrationBean<HttpFilter> bean = new FilterRegistrationBean<>();
        bean.setDispatcherTypes(DispatcherType.REQUEST);
        bean.setFilter(new TokenFilter());
        bean.addInitParameter("exclusions", "/login");
        bean.addUrlPatterns("/*");
        return bean;
    }


    static class TokenFilter extends HttpFilter {
        private Set<String> excludesPattern;
        public static final String PARAM_NAME_EXCLUSIONS = "exclusions";

        @Override
        public void init(javax.servlet.FilterConfig config) {
            String param = config.getInitParameter(PARAM_NAME_EXCLUSIONS);
            if (param != null && param.trim().length() != 0) {
                this.excludesPattern = new HashSet<>(Arrays.asList(param.split("\\s*,\\s*")));
            }
        }

        @Override
        protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
            if (excludesPattern.contains(req.getRequestURI())) {
                chain.doFilter(req, res);
            } else {
                // token 是放在Header中的
                Optional<String> token = Optional.ofNullable((String) req.getHeader("token"));
                Optional<String> storeToken = StoreTool.get(Property.TOKEN.name());
                if (!storeToken.isPresent() || !storeToken.equals(token)) {
                    res.sendError(HttpStatus.UNAUTHORIZED.value(), new UserNotLoginException().getMessage());
                } else {
                    chain.doFilter(req, res);
                }
            }
        }
    }
}