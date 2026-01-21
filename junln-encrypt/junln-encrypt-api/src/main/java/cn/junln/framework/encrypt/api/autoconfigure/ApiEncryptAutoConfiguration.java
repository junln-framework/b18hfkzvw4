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

package cn.junln.framework.encrypt.api.autoconfigure;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import cn.junln.framework.core.constant.OrderedConstants;
import cn.junln.framework.core.constant.PropertiesConstants;
import cn.junln.framework.core.constant.StringConstants;
import cn.junln.framework.encrypt.api.filter.ApiEncryptFilter;

/**
 * API 加密自动配置
 *
 * @author lishuyan
 * @author Charles7c
 * @since 2.14.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiEncryptProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ENCRYPT_API, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class ApiEncryptAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApiEncryptAutoConfiguration.class);

    /**
     * API 加密过滤器
     */
    @Bean
    public FilterRegistrationBean<ApiEncryptFilter> apiEncryptFilter(ApiEncryptProperties properties) {
        FilterRegistrationBean<ApiEncryptFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiEncryptFilter(properties));
        registrationBean.setOrder(OrderedConstants.Filter.API_ENCRYPT_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Junln Framework] - Auto Configuration 'Encrypt-API' completed initialization.");
    }
}
