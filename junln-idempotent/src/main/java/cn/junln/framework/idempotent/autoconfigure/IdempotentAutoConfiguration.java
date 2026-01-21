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

package cn.junln.framework.idempotent.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import cn.junln.framework.cache.redisson.autoconfigure.RedissonAutoConfiguration;
import cn.junln.framework.core.constant.PropertiesConstants;
import cn.junln.framework.idempotent.aop.IdempotentAspect;
import cn.junln.framework.idempotent.generator.DefaultIdempotentNameGenerator;
import cn.junln.framework.idempotent.generator.IdempotentNameGenerator;

/**
 * 幂等自动配置
 *
 * @author loach
 * @author Charles7c
 * @since 2.10.0
 */
@AutoConfiguration(after = RedissonAutoConfiguration.class)
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.IDEMPOTENT, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class IdempotentAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(IdempotentAutoConfiguration.class);

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect(IdempotentProperties properties,
                                             IdempotentNameGenerator idempotentNameGenerator) {
        return new IdempotentAspect(properties, idempotentNameGenerator);
    }

    /**
     * 幂等名称生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentNameGenerator idempotentNameGenerator() {
        return new DefaultIdempotentNameGenerator();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Junln Framework] - Auto Configuration 'Idempotent' completed initialization.");
    }
}