package cn.kankancloud.jbp.core;

import cn.kankancloud.jbp.core.abstraction.IDisposable;

import java.util.ArrayList;
import java.util.List;

/**
 * 跟踪需要释放的请求级别上下文
 * 注：由filter在请求级别自动释放，定时任务或者后台线程请务必手动释放
 */
public class RequestScopeDisposableTracker {

    private RequestScopeDisposableTracker() {
    }

    private static final List<IDisposable> disposableList = new ArrayList<>();

    public static void track(IDisposable disposable) {

        disposableList.add(disposable);
    }

    public static void dispose() {
        disposableList.forEach(IDisposable::dispose);
    }

}
