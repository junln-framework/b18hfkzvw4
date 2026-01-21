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

package cn.junln.framework.extension.datapermission.enums;

public enum PermissionJudgeType {
    /**
     * 直接字段判断权限：如通过默认的dept_id字段直接判断
     * 适用于：业务表直接有默认dept_id字段
     */
    DEF_DIRECT("直接字段判断权限"),

    /**
     * 其他字段关联权限：如通过user_id关联sys_user_dept表判断
     * 适用于：指定业务表某字段，需要通过关联表判断权限
     */
    RELATION_DIRECT("其他字段关联权限"),

    /**
     * 用户直接权限：通过user_id字段直接判断
     * 适用于：仅本人数据权限的场景
     */
    USER_DIRECT("用户直接权限");

    private final String description;

    PermissionJudgeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
