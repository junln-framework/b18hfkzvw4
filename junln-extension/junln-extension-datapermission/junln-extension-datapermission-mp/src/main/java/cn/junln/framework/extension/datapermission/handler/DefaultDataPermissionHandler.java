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

package cn.junln.framework.extension.datapermission.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.junln.framework.core.constant.StringConstants;
import cn.junln.framework.data.enums.DatabaseType;
import cn.junln.framework.data.util.MetaUtils;
import cn.junln.framework.extension.datapermission.annotation.DataPermission;
import cn.junln.framework.extension.datapermission.constant.DataPermissionConstants;
import cn.junln.framework.extension.datapermission.enums.DataScope;
import cn.junln.framework.extension.datapermission.exception.DataPermissionException;
import cn.junln.framework.extension.datapermission.model.DeptData;
import cn.junln.framework.extension.datapermission.model.RoleData;
import cn.junln.framework.extension.datapermission.model.UserData;
import cn.junln.framework.extension.datapermission.provider.DataPermissionUserDataProvider;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认数据权限处理器
 *
 * @author <a href="https://gitee.com/baomidou/mybatis-plus/issues/I37I90">DataPermissionInterceptor 如何使用？</a>
 * @author Charles7c
 * @since 1.1.0
 */
public class DefaultDataPermissionHandler implements DataPermissionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataPermissionHandler.class);
    private final DataPermissionUserDataProvider dataPermissionUserDataProvider;
    /**
     * Mapper类中所有方法数据权限注解缓存
     */
    private final Map<String, Map<String, DataPermission>> annotationCache = new ConcurrentHashMap<>();

    public DefaultDataPermissionHandler(DataPermissionUserDataProvider dataPermissionUserDataProvider) {
        this.dataPermissionUserDataProvider = dataPermissionUserDataProvider;
    }

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            DataPermission dataPermission = findDataPermissionAnnotation(mappedStatementId);
            if (dataPermission != null && dataPermissionUserDataProvider.isFilter()) {
                return buildDataScopeFilter(dataPermission, where);
            }
        } catch (Exception e) {
            log.error("Data permission handler build data scope filter occurred an error: {}.", e.getMessage(), e);
        }
        return where;
    }

    /**
     * 查找数据权限注解
     *
     * @param mappedStatementId Mapper 方法 ID
     * @return 数据权限注解
     */
    private DataPermission findDataPermissionAnnotation(String mappedStatementId) {
        try {
            int lastDotIndex = mappedStatementId.lastIndexOf(StringConstants.DOT);
            if (lastDotIndex == -1) {
                return null;
            }

            String className = mappedStatementId.substring(0, lastDotIndex);
            String methodName = mappedStatementId.substring(lastDotIndex + 1);

            // 先根据类名从缓存获取，如果methodAnnotations不为空，则说明该类中的所有方法都已缓存， 只是值为null。
            Map<String, DataPermission> methodAnnotations = annotationCache.get(className);
            if (methodAnnotations != null) {
                // methodName 可能是 ** 或者 **_COUNT
                return methodAnnotations.getOrDefault(methodName, methodAnnotations
                    .get(methodName + DataPermissionConstants.COUNT_METHOD_SUFFIX));
            }

            // 缓存未命中，执行反射操作
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();

            // 创建新的缓存映射
            Map<String, DataPermission> newMethodAnnotations = new ConcurrentHashMap<>();

            // 缓存所有带@DataPermission注解的方法
            for (Method method : methods) {
                String name = method.getName();
                DataPermission annotation = method.getAnnotation(DataPermission.class);
                if (annotation != null) {
                    newMethodAnnotations.put(name, annotation);
                }
            }
            // 存入缓存
            annotationCache.put(className, newMethodAnnotations);

            return newMethodAnnotations.get(methodName);
        } catch (ClassNotFoundException e) {
            throw DataPermissionException.methodNotFound(mappedStatementId);
        }
    }

    /**
     * 构建数据范围过滤条件
     *
     * @param dataPermission 数据权限
     * @param where          当前查询条件
     * @return 构建后查询条件
     */
    private Expression buildDataScopeFilter(DataPermission dataPermission, Expression where) {
        UserData userData = dataPermissionUserDataProvider.getUserData();
        if (userData == null || !userData.isValid()) {
            throw DataPermissionException.invalidUserData("User data is null or invalid");
        }

        Expression expression = null;
        Set<RoleData> roles = userData.getRoles();

        List<Long> deptIds = new ArrayList<>(userData.getDepartments()
            .stream()
            .map(DeptData::getDeptId)
            .distinct()  // 去重
            .toList());
        if (!userData.getChildrenDepartments().isEmpty()) {
            List<Long> tempIds = userData.getDepartments()
                .stream()
                .map(DeptData::getDeptId)
                .distinct()  // 去重
                .toList();
            deptIds.addAll(tempIds);
        }

        for (RoleData roleData : roles) {
            DataScope dataScope = roleData.getDataScope();
            // 如果包含全部权限,就不加SQL条件
            if (DataScope.ALL.equals(dataScope)) {
                return where;
            }

            expression = switch (dataScope) {
                case DEPT_AND_CHILD ->
                    buildDeptAndChildExpressionByJudgeType(dataPermission, userData, expression, deptIds);
                case DEPT -> buildDeptExpressionByJudgeType(dataPermission, userData, expression, deptIds);
                case SELF -> buildSelfExpression(dataPermission, userData, expression);
                case CUSTOM -> buildCustomExpression(dataPermission, roleData, expression);
                default -> throw DataPermissionException.unsupportedDataScope(dataScope.toString());
            };
        }

        return where != null ? new AndExpression(where, new ParenthesedExpressionList<>(expression)) : expression;
    }

    /**
     * 构建本部门及以下数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id in (select id from sys_dept where id = xxx or
     * find_in_set(xxx, ancestors));
     * </p>
     *
     * @param dataPermission 数据权限
     * @param userData       用户数据
     * @param expression     处理前的表达式
     * @return 处理完后的表达式
     */
    private Expression buildDeptAndChildExpression(DataPermission dataPermission,
                                                   UserData userData,
                                                   Expression expression) {
        ParenthesedSelect subSelect = new ParenthesedSelect();
        PlainSelect select = new PlainSelect();
        select.setSelectItems(Collections.singletonList(new SelectItem<>(new Column(dataPermission.id()))));
        select.setFromItem(new Table(dataPermission.deptTableAlias()));

        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(dataPermission.id()));
        equalsTo.setRightExpression(new LongValue(userData.getDeptId()));

        DatabaseType databaseType = MetaUtils.getDatabaseType(SpringUtil.getBean(DataSource.class));
        Expression inSetExpression;
        if (DatabaseType.MYSQL.getDatabase().equalsIgnoreCase(databaseType.getDatabase())) {
            Function findInSetFunction = new Function();
            findInSetFunction.setName("find_in_set");
            findInSetFunction.setParameters(new ExpressionList<>(new LongValue(userData
                .getDeptId()), new Column(DataPermissionConstants.ANCESTORS_COLUMN)));
            inSetExpression = findInSetFunction;
        } else if (DatabaseType.POSTGRE_SQL.getDatabase().equalsIgnoreCase(databaseType.getDatabase())) {
            // 构建 concat 函数
            Function concatFunction = new Function("concat");
            concatFunction
                .setParameters(new ExpressionList<>(new Column(DataPermissionConstants.ANCESTORS_COLUMN), new StringValue(",")));

            // 创建 LIKE 函数
            LikeExpression likeExpression = new LikeExpression();
            likeExpression.setLeftExpression(concatFunction);
            likeExpression.setRightExpression(new StringValue("%," + userData.getDeptId() + ",%"));
            inSetExpression = likeExpression;
        } else {
            throw DataPermissionException.unsupportedDatabase(databaseType.getDatabase());
        }

        select.setWhere(new OrExpression(equalsTo, inSetExpression));
        subSelect.setSelect(select);
        // 构建父查询
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(this.buildColumn(dataPermission.tableAlias(), dataPermission.deptId()));
        inExpression.setRightExpression(subSelect);
        return expression != null ? new OrExpression(expression, inExpression) : inExpression;
    }

    /**
     * 构建EXISTS 子查询表达式
     *
     * @param dataPermission 数据权限
     * @param userData       角色上下文
     * @param deptIds        部门 ID列表
     * @return EXISTS 表达式
     */
    public Expression buildDeptAndChildRelationExistsExpression(DataPermission dataPermission,
                                                                UserData userData,
                                                                List<Long> deptIds) {

        String relationTable = dataPermission.relationTableField();
        String relationField = dataPermission.relationField();
        String relationDeptIdField = dataPermission.relationDeptIdField();
        String mainTableAlias = dataPermission.tableAlias();
        String mainUserIdField = dataPermission.id();

        // 1. 构建EXISTS表达式
        ExistsExpression existsExpression = new ExistsExpression();

        // 2. 构建子查询
        PlainSelect subSelect = new PlainSelect();

        // 2.1 设置SELECT子句
        subSelect.setSelectItems(Collections.singletonList(new SelectItem<>(new LongValue(1))));

        // 2.2 设置FROM子句
        subSelect.setFromItem(new Table(relationTable));

        // 2.3 构建WHERE条件
        List<Expression> conditions = new ArrayList<>();

        // 条件1：关联表.user_id = 主表.id
        EqualsTo userCondition = new EqualsTo(this.buildColumn(relationTable, relationField), this
            .buildColumn(mainTableAlias, mainUserIdField));
        conditions.add(userCondition);

        // 条件2：关联表.dept_id IN (...)
        if (deptIds != null && !deptIds.isEmpty()) {
            Expression deptCondition = buildInCondition(this.buildColumn(relationTable, relationDeptIdField), //关联表.dept_id
                deptIds);
            conditions.add(deptCondition);
        } else {
            // 如果deptIds为空，返回1=0（false条件）
            return buildFalseCondition();
        }

        // 2.4 合并WHERE条件
        Expression whereExpression = conditions.get(0);
        for (int i = 1; i < conditions.size(); i++) {
            whereExpression = new AndExpression(whereExpression, conditions.get(i));
        }
        subSelect.setWhere(whereExpression);
        // 3. 将子查询包装到EXISTS中
        ParenthesedSelect parenthesedSelect = new ParenthesedSelect();
        parenthesedSelect.setSelect(subSelect);
        existsExpression.setRightExpression(parenthesedSelect);

        return existsExpression;
    }

    /**
     * 构建本部门及以下数据权限表达式
     *
     * @param dataPermission 数据权限
     * @param userData       用户数据
     * @param expression     处理前的表达式
     * @param deptIds        参数 id
     */
    private Expression buildDeptAndChildExpressionByJudgeType(DataPermission dataPermission,
                                                              UserData userData,
                                                              Expression expression,
                                                              List<Long> deptIds) {
        return switch (dataPermission.judgeType()) {
            case DEF_DIRECT -> buildDeptAndChildExpression(dataPermission, userData, expression);
            case RELATION_DIRECT -> {
                yield buildDeptAndChildRelationExistsExpression(dataPermission, userData, deptIds);
            }
            case USER_DIRECT ->
                // USER_DIRECT不适用于部门权限，返回false
                buildFalseCondition();
            default -> buildFalseCondition();
        };
    }

    /**
     * 构建本部门数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id = xxx;
     * </p>
     *
     * @param dataPermission 数据权限
     * @param userData       用户数据
     * @param expression     处理前的表达式
     * @return 处理完后的表达式
     */
    private Expression buildDeptExpression(DataPermission dataPermission, UserData userData, Expression expression) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.buildColumn(dataPermission.tableAlias(), dataPermission.deptId()));
        equalsTo.setRightExpression(new LongValue(userData.getDeptId()));
        return expression != null ? new OrExpression(expression, equalsTo) : equalsTo;
    }

    /**
     * 构建本部门及以下数据权限表达式
     *
     * @param dataPermission 数据权限
     * @param userData       用户数据
     * @param expression     处理前的表达式
     * @param deptIds        参数 id
     */
    private Expression buildDeptExpressionByJudgeType(DataPermission dataPermission,
                                                      UserData userData,
                                                      Expression expression,
                                                      List<Long> deptIds) {
        return switch (dataPermission.judgeType()) {
            case DEF_DIRECT -> buildDeptExpression(dataPermission, userData, expression);
            case RELATION_DIRECT -> buildDeptAndChildRelationExistsExpression(dataPermission, userData, deptIds);
            case USER_DIRECT ->
                // USER_DIRECT不适用于部门权限，返回false
                buildFalseCondition();
            default -> buildFalseCondition();
        };
    }

    /**
     * 构建仅本人数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.create_user = xxx;
     * </p>
     *
     * @param dataPermission 数据权限
     * @param userData       用户数据
     * @param expression     处理前的表达式
     * @return 处理完后的表达式
     */
    private Expression buildSelfExpression(DataPermission dataPermission, UserData userData, Expression expression) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.buildColumn(dataPermission.tableAlias(), dataPermission.userId()));
        equalsTo.setRightExpression(new LongValue(userData.getUserId()));
        return expression != null ? new OrExpression(expression, equalsTo) : equalsTo;
    }

    /**
     * 构建自定义数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id in (select dept_id from sys_role_dept where
     * role_id = xxx);
     * </p>
     *
     * @param dataPermission 数据权限
     * @param roleData       角色上下文
     * @param expression     处理前的表达式
     * @return 处理完后的表达式
     */
    private Expression buildCustomExpression(DataPermission dataPermission, RoleData roleData, Expression expression) {
        ParenthesedSelect subSelect = new ParenthesedSelect();
        PlainSelect select = new PlainSelect();
        select.setSelectItems(Collections.singletonList(new SelectItem<>(new Column(dataPermission.deptId()))));
        select.setFromItem(new Table(dataPermission.roleDeptTableAlias()));
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(dataPermission.roleId()));
        equalsTo.setRightExpression(new LongValue(roleData.getRoleId()));
        select.setWhere(equalsTo);
        subSelect.setSelect(select);
        // 构建父查询
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(this.buildColumn(dataPermission.tableAlias(), dataPermission.deptId()));
        inExpression.setRightExpression(subSelect);
        return expression != null ? new OrExpression(expression, inExpression) : inExpression;
    }

    /**
     * 构建 Column
     *
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @return 带表别名字段
     */
    private Column buildColumn(String tableAlias, String columnName) {
        if (StringUtils.isNotEmpty(tableAlias)) {
            return new Column("%s.%s".formatted(tableAlias, columnName));
        }
        return new Column(columnName);
    }

    /*    public static String expressionToString(Expression expression) {
        return expression.toString();
    }*/

    /**
     * 构建 IN条件
     */
    private Expression buildInCondition(Column column, List<Long> values) {
        if (values == null || values.isEmpty()) {
            return buildFalseCondition();
        }
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(column);
        ExpressionList<Expression> valueList = new ExpressionList<>();
        for (Long value : values) {
            valueList.addExpressions(new LongValue(value));
        }
        ParenthesedExpressionList<Expression> parenthesesList = new ParenthesedExpressionList<>(valueList);
        inExpression.setRightExpression(parenthesesList);
        return inExpression;
    }

    private Expression buildFalseCondition() {
        return new EqualsTo(new LongValue(1), new LongValue(0));
    }

    /*    private String renderExpression(Expression expression) {
        if (expression == null) {
            return "";
        }
    
        StringBuilder buffer = new StringBuilder();
        // JSqlParser 5.x: 通过构造函数传递 StringBuilder
        ExpressionDeParser deParser = new ExpressionDeParser(buffer);
        expression.accept(deParser);
    
        return buffer.toString();
    }*/
}
