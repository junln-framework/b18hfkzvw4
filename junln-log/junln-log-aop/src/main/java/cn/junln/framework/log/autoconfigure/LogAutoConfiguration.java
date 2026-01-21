/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.junln.framework.log.autoconfigure;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.junln.framework.core.constant.OrderedConstants;
import cn.junln.framework.core.constant.StringConstants;
import cn.junln.framework.log.annotation.ConditionalOnEnabledLog;
import cn.junln.framework.log.aspect.AccessLogAspect;
import cn.junln.framework.log.aspect.LogAspect;
import cn.junln.framework.log.dao.LogDao;
import cn.junln.framework.log.dao.impl.DefaultLogDaoImpl;
import cn.junln.framework.log.filter.LogFilter;
import cn.junln.framework.log.handler.AopLogHandler;
import cn.junln.framework.log.handler.LogHandler;
import cn.junln.framework.log.model.LogProperties;

/**
 * 日志自动配置
 *
 * @author Charles7c
 * @author echo
 * @since 1.1.0
 */
@Configuration
@ConditionalOnEnabledLog
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LogAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LogAutoConfiguration.class);
    private final LogProperties logProperties;
    private final LogHandler logHandler;

    public LogAutoConfiguration(LogProperties logProperties, LogHandler logHandler) {
        this.logProperties = logProperties;
        this.logHandler = logHandler;
    }

    /**
     * 日志过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(logProperties));
        registrationBean.setOrder(OrderedConstants.Filter.LOG_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 日志切面
     *
     * @param logDao 日志持久层接口
     * @return {@link LogAspect }
     */
    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect(LogDao logDao) {
        return new LogAspect(logProperties, logHandler, logDao);
    }

    /**
     * 访问日志切面
     *
     * @return {@link LogAspect }
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessLogAspect accessLogAspect() {
        return new AccessLogAspect(logProperties, logHandler);
    }

    /**
     * 日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new AopLogHandler();
    }

    /**
     * 日志持久层接口
     */
    @Bean
    @ConditionalOnMissingBean
    public LogDao logDao() {
        return new DefaultLogDaoImpl();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Junln Framework] - Auto Configuration 'Log-AOP' completed initialization.");
    }
}
