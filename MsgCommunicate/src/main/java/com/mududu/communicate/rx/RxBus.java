package com.mududu.communicate.rx;

import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by tiger on 2016/10/20.
 */

public class RxBus {
    private static volatile RxBus mInstance;
    private ConcurrentHashMap<Object, Subject> subjectMapper = new ConcurrentHashMap<>();
    public RxBus() {
    }
    /**
    * 单例模式RxBus
    * @return
            */
    public static RxBus getInstance() {
        RxBus rxBus2 = mInstance;
        if (mInstance == null) {
            synchronized (RxBus.class) {
                rxBus2 = mInstance;
                if (mInstance == null) {
                    rxBus2 = new RxBus();
                    mInstance = rxBus2;
                }
            }
        }
        return rxBus2;
    }
    /**
     * 发送消息
     * @param object
     */
    public void post(Object object) {
      post(mInstance.hashCode()+"",object);
    }

    /**
     * 接收消息
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return toObservable(mInstance.hashCode()+"",eventType);
    }
    /**
     * 提供了一个新的事件,根据code进行分发
     * @param tag 事件code
     * @param o
     */
    public void post(String tag, Object o){
        String key=getKey(tag,o.getClass());
        Subject subject = subjectMapper.get(key);
        if(subject == null) {
            return ;
        }
        subject.onNext(o);
    }


     /**
      * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
      * @param eventType 事件类型
      * @param <T>
      * @return
     */
    public <T> Observable<T> toObservable(String tag, final Class<T> eventType) {
        String key=getKey(tag,eventType);
        Subject subject = subjectMapper.get(key);
        if (null == subject) {
            subject = new SerializedSubject<>(PublishSubject.create());
            subjectMapper.put(key, subject) ;
        }
        return subject.ofType(eventType);
    }
    private String getKey(String tag,Class c){
        String name=c.getName();
        return tag+"@"+name;
    }
}