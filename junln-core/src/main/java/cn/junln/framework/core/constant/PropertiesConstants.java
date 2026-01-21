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

package cn.junln.framework.core.constant;

/**
 * 配置属性相关常量
 *
 * @author Charles7c
 * @since 1.1.1
 */
public class PropertiesConstants {

    /**
     * Junln Framework
     */
    public static final String JUNLN = "junln";

    /**
     * 启用配置
     */
    public static final String ENABLED = "enabled";

    /**
     * Web 配置
     */
    public static final String WEB = JUNLN + StringConstants.DOT + "web";

    /**
     * Web-跨域配置
     */
    public static final String WEB_CORS = WEB + StringConstants.DOT + "cors";

    /**
     * Web-响应配置
     */
    public static final String WEB_RESPONSE = WEB + StringConstants.DOT + "response";

    /**
     * 认证-JustAuth 配置
     */
    public static final String AUTH_JUSTAUTH = JUNLN + StringConstants.DOT + "justauth";

    /**
     * 加密配置
     */
    public static final String ENCRYPT = JUNLN + StringConstants.DOT + "encrypt";

    /**
     * 加密-密码编码器
     */
    public static final String ENCRYPT_PASSWORD_ENCODER = ENCRYPT + StringConstants.DOT + "password-encoder";

    /**
     * 加密-字段加密
     */
    public static final String ENCRYPT_FIELD = ENCRYPT + StringConstants.DOT + "field";

    /**
     * 加密-API 加密
     */
    public static final String ENCRYPT_API = ENCRYPT + StringConstants.DOT + "api";

    /**
     * 安全配置
     */
    public static final String SECURITY = JUNLN + StringConstants.DOT + "security";

    /**
     * 安全-XSS 配置
     */
    public static final String SECURITY_XSS = SECURITY + StringConstants.DOT + "xss";

    /**
     * 安全-敏感词配置
     */
    public static final String SECURITY_SENSITIVE_WORDS = SECURITY + StringConstants.DOT + "sensitive-words";

    /**
     * 限流配置
     */
    public static final String RATE_LIMITER = JUNLN + StringConstants.DOT + "rate-limiter";

    /**
     * 幂等配置
     */
    public static final String IDEMPOTENT = JUNLN + StringConstants.DOT + "idempotent";

    /**
     * 链路追踪配置
     */
    public static final String TRACE = JUNLN + StringConstants.DOT + "trace";

    /**
     * 验证码配置
     */
    public static final String CAPTCHA = JUNLN + StringConstants.DOT + "captcha";

    /**
     * 图形验证码配置
     */
    public static final String CAPTCHA_GRAPHIC = CAPTCHA + StringConstants.DOT + "graphic";

    /**
     * 行为验证码配置
     */
    public static final String CAPTCHA_BEHAVIOR = CAPTCHA + StringConstants.DOT + "behavior";

    /**
     * 消息配置
     */
    public static final String MESSAGING = JUNLN + StringConstants.DOT + "messaging";

    /**
     * WebSocket 配置
     */
    public static final String MESSAGING_WEBSOCKET = MESSAGING + StringConstants.DOT + "websocket";

    /**
     * MQTT 配置
     */
    public static final String MESSAGING_MQTT = MESSAGING + StringConstants.DOT + "mqtt";

    /**
     * 日志配置
     */
    public static final String LOG = JUNLN + StringConstants.DOT + "log";

    /**
     * 存储配置
     */
    public static final String STORAGE = JUNLN + StringConstants.DOT + "storage";

    /**
     * License 配置
     */
    public static final String LICENSE = JUNLN + StringConstants.DOT + "license";

    /**
     * License 生成器配置
     */
    public static final String LICENSE_GENERATOR = LICENSE + StringConstants.DOT + "generator";

    /**
     * License 校验器配置
     */
    public static final String LICENSE_VERIFIER = LICENSE + StringConstants.DOT + "verifier";

    /**
     * CRUD 配置
     */
    public static final String CRUD = JUNLN + StringConstants.DOT + "crud";

    /**
     * 数据权限配置
     */
    public static final String DATA_PERMISSION = JUNLN + StringConstants.DOT + "data-permission";

    /**
     * 租户配置
     */
    public static final String TENANT = JUNLN + StringConstants.DOT + "tenant";

    private PropertiesConstants() {
    }
}
