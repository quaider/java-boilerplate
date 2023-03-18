package cn.kankancloud.jbp.mbp;

import cn.kankancloud.jbp.mbp.persistence.BasePo;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import cn.kankancloud.jbp.core.RequestScopeDisposableTracker;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

/**
 * 用于临时关闭多租户插件和数据范围插件的hint
 * 更推荐的内置方式：在mapper方法中加上如下注解：
 *
 * <pre>@InterceptorIgnore(tenantLine = "true")</pre>
 * <example>
 * TenantIgnoreHint.hint(HintType.TENANT, Tenant.class, PermissionDefinition.class, PermissionGrant.class);
 * List<Tenant> tenants = tenantService.list();
 * TenantHint.releaseAllHint();
 * </example>
 */
public class InterceptorIgnoreHint {

    private InterceptorIgnoreHint() {
    }

    private static final ThreadLocal<Map<HintType, Set<Class<? extends BasePo>>>> hintEntityClassHolder = new InheritableThreadLocal<>();

    static {
        RequestScopeDisposableTracker.track(InterceptorIgnoreHint::releaseAllHint);
    }

    @SafeVarargs
    public static void hint(HintType hintType, Class<? extends BasePo>... hintEntityClasses) {
        Map<HintType, Set<Class<? extends BasePo>>> map = hintEntityClassHolder.get();
        if (map == null) {
            map = new EnumMap<>(HintType.class);
            hintEntityClassHolder.set(map);
        }

        map.putIfAbsent(hintType, new HashSet<>());

        Set<Class<? extends BasePo>> classes = map.get(hintType);
        classes.addAll(Arrays.asList(hintEntityClasses));
    }

    public static void releaseHint(HintType hintType, Class<? extends BasePo> hintEntityClass) {
        Map<HintType, Set<Class<? extends BasePo>>> map = hintEntityClassHolder.get();
        if (map == null || !map.containsKey(hintType)) {
            return;
        }

        Set<Class<? extends BasePo>> classes = map.get(hintType);
        if (ObjectUtils.isEmpty(classes)) {
            return;
        }

        classes.remove(hintEntityClass);
    }

    public static void releaseAllHint(HintType hintType) {
        Map<HintType, Set<Class<? extends BasePo>>> map = hintEntityClassHolder.get();
        if (map == null || !map.containsKey(hintType)) {
            return;
        }

        map.remove(hintType);
    }

    public static void releaseAllHint() {
        hintEntityClassHolder.remove();
    }

    public static boolean useHint(HintType hintType, String tableName) {
        Map<HintType, Set<Class<? extends BasePo>>> map = hintEntityClassHolder.get();
        if (map == null || ObjectUtils.isEmpty(map)) {
            return false;
        }

        Set<Class<? extends BasePo>> classes = map.get(hintType);
        if (ObjectUtils.isEmpty(classes)) {
            return false;
        }

        return classes.stream().anyMatch(hintEntityClass -> SqlHelper.table(hintEntityClass).getTableName().equals(tableName));
    }
}
