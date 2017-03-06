package com.mududu.communicate.ipc.message;

import com.alibaba.fastjson.JSON;

/**
 * Created by tiger on 2016/12/19.
 */

public class RecongnizerMessage {
    public static final int RECONGNIZER_FINISH = 0 ;
    public static final int RECONGNIZER_RESULT = 1 ;
    private SemanticItem item = null;
    private int type ;

    public RecongnizerMessage() {

    }

    public RecongnizerMessage(Object item, int type) {
        if(item != null)
            this.item = JSON.parseObject(JSON.toJSONString(item),SemanticItem.class) ;
        this.type = type ;
    }

    public SemanticItem getItem() {
        return item;
    }

    public int getType() {
        return type;
    }

    public void setItem(Object item) {
        if(item != null)
            this.item = JSON.parseObject(JSON.toJSONString(item),SemanticItem.class) ;
    }

    public void setItem(SemanticItem item) {
        this.item = item;
    }

    public void setType(int type) {
        this.type = type;
    }
}
