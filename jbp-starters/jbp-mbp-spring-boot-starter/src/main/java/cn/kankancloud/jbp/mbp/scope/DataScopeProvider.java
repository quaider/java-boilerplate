package cn.kankancloud.jbp.mbp.scope;

import cn.kankancloud.jbp.core.abstraction.IDisposable;
import cn.kankancloud.jbp.core.util.tuple.KeyValue;

import java.util.List;

public interface DataScopeProvider extends IDisposable {
    KeyValue<DataScope, List<String>> getScopeValues();

    void setDataScope(DataScope scope, List<String> values);

    default void allDataScope() {
        setDataScope(DataScope.ALL, null);
    }
}
