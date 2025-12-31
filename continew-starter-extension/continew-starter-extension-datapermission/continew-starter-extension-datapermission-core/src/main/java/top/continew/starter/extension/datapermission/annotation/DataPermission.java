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

package top.continew.starter.extension.datapermission.annotation;

import top.continew.starter.extension.datapermission.enums.PermissionJudgeType;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * @author Charles7c
 * @since 1.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 表别名
     */
    String tableAlias() default "";

    /**
     * ID
     */
    String id() default "id";

    /**
     * 部门 ID
     */
    String deptId() default "dept_id";

    /**
     * 用户 ID
     */
    String userId() default "create_user";

    /**
     * 角色 ID（角色和部门关联表）
     */
    String roleId() default "role_id";

    /**
     * 部门表别名
     */
    String deptTableAlias() default "sys_dept";

    /**
     * 角色和部门关联表别名
     */
    String roleDeptTableAlias() default "sys_role_dept";

    /**
     * 关联的表 ,如sys_user_dept
     */
    String relationTableField() default "sys_user_dept";

    /**
     * 关联表 ID 如 sys_user_dept.user_id
     */
    String relationField() default "user_id";

    /**
     * 关联表的 DeptId
     */
    String relationDeptIdField() default "dept_id";

    /**
     * 其他字段名（当judgeType=OTHER_RELATION时使用）
     * EXISTS (SELECT 1 FROM sys_user_dept sud WHERE sud.user_id = t1.id AND sud.dept_id IN (''))
     */
    String mainIdField() default "id";

    /**
     * 自定义SQL（当scopeType=CUSTOM时使用）
     */
    String customSql() default "";

    /**
     * 权限判断方式
     */
    PermissionJudgeType judgeType() default PermissionJudgeType.DEF_DIRECT;
}
