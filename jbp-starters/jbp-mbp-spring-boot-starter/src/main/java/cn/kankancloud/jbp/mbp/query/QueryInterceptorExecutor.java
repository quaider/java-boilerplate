package cn.kankancloud.jbp.mbp.query;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@SuppressWarnings({"rawtypes"})
public class QueryInterceptorExecutor {

    private QueryInterceptorExecutor() {
    }

    /**
     * 执行查询拦截器
     */
    static void exec(QueryInterceptor[] interceptors, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        if (ObjectUtils.isEmpty(interceptors)) {
            return;
        }

        for (QueryInterceptor interceptor : interceptors) {
            interceptor.intercept(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        }
    }
}
