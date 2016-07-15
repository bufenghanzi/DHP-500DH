package com.mingseal.RxBus;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Administrator on 2016/6/12.
 * <p/>
 * Description:
 */
public class RxBus {
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    private final Map<String, Object> tags = new HashMap<>();

    private static RxBus rxbus;

    public static RxBus getInstance()
    {
        if(rxbus == null)
        {
            synchronized (RxBus.class) {
                if(rxbus == null) {
                    rxbus = new RxBus();
                }
            }
        }
        return rxbus;
    }

    /**
     * 发送事件消息
     * @param tag 用于区分事件
     * @param object 事件的参数
     */
    public void post(String tag, Object object)
    {
        if(!tags.containsKey(tag))
        {
            tags.put(tag, object);
            _bus.onNext(object);
        }
    }

    /**
     * 主线程中执行
     * @param tag
     * @param rxBusResult
     */
    public void toObserverableOnMainThread(final String tag, final RxBusResult rxBusResult) {

        _bus.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (tags.containsKey(tag)) {
                            rxBusResult.onRxBusResult(o);
                            tags.remove(tag);
                        }
                    }
                });
    }

    /**
     * 子线程中执行
     * @param tag
     * @param rxBusResult
     */
    public void toObserverableChildThread(final String tag, final RxBusResult rxBusResult) {

        _bus.observeOn(Schedulers.io()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (tags.containsKey(tag)) {
                    rxBusResult.onRxBusResult(o);
                    tags.remove(tag);
                }
            }
        });
    }

    /**
     * 移除tag
     * @param tag
     */
    public void removeObserverable(String tag)
    {
        if(tags.containsKey(tag))
        {
            tags.remove(tag);
        }
    }

    /**
     * 退出应用时，清空资源
     */
    public void release()
    {
        tags.clear();
        rxbus = null;
    }
}
