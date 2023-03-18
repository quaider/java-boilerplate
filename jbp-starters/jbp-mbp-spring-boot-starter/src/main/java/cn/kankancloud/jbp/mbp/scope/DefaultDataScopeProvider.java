package cn.kankancloud.jbp.mbp.scope;

import cn.kankancloud.jbp.core.RequestScopeDisposableTracker;
import cn.kankancloud.jbp.core.util.tuple.KeyValue;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * 需要查找绑定关系，为了处理简单，这种绑定关系由网关透传到后端服务，因此，这里简单地按照优先级从如下上下文获取
 * <ol>
 *     <li>从线程上下文取</li>
 * </ol>
 */
public class DefaultDataScopeProvider implements DataScopeProvider, InitializingBean {

    private static final ThreadLocal<KeyValue<DataScope, List<String>>> scopeValues = new InheritableThreadLocal<>();

    @Override
    public KeyValue<DataScope, List<String>> getScopeValues() {
        return scopeValues.get();
    }

    @Override
    public void setDataScope(DataScope scope, List<String> values) {
        scopeValues.set(KeyValue.with(scope, values));
    }

    /**
     * 确保请求结束后调用(aop)
     */
    @Override
    public void dispose() {
        scopeValues.remove();
    }

    @Override
    public void afterPropertiesSet() {
        RequestScopeDisposableTracker.track(this);
    }
}
