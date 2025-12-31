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

package top.continew.starter.data.datapermission;

import java.util.Set;

/**
 * 当前用户信息
 *
 * @author hellokaton
 * @since 2.0.2
 */
public class DataPermissionCurrentUser {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 角色列表
     */
    private Set<CurrentUserRole> roles;

    /**
     * 部门 ID
     */
    private String deptId;

    /**
     * 如果用户属于多部门情况
     */
    private Set<String> deptIds;  // 改为集合

    /**
     * 当前用户角色信息
     */
    public static class CurrentUserRole {

        /**
         * 角色 ID
         */
        private String roleId;

        /**
         * 数据权限
         */
        private DataScope dataScope;

        public CurrentUserRole() {
        }

        public CurrentUserRole(String roleId, DataScope dataScope) {
            this.roleId = roleId;
            this.dataScope = dataScope;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public DataScope getDataScope() {
            return dataScope;
        }

        public void setDataScope(DataScope dataScope) {
            this.dataScope = dataScope;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<CurrentUserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<CurrentUserRole> roles) {
        this.roles = roles;
    }

    public String getDeptId() {
        if (deptId != null) {
            return deptId;
        }
        if (deptIds != null && !deptIds.isEmpty()) {
            return deptIds.iterator().next();  // 返回第一个部门
        }
        return null;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    // 获取所有部门
    public Set<String> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(Set<String> deptIds) {
        this.deptIds = deptIds;
    }

}
