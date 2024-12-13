package org.sevengod.javabe.handler.checkinsChain;

import org.sevengod.javabe.entity.context.CheckInContext;

/**
 * 签到处理器抽象类
 */
public abstract class AbstractCheckInHandler {
    protected AbstractCheckInHandler next;

    public void setNext(AbstractCheckInHandler next) {
        this.next = next;
    }

    /**
     * 处理签到逻辑
     */
    public void handle(CheckInContext context) {
        if (!context.isSuccess()) {
            return;
        }
        
        doHandle(context);
        
        if (next != null && context.isSuccess()) {
            next.handle(context);
        }
    }

    /**
     * 具体的处理逻辑
     */
    protected abstract void doHandle(CheckInContext context);
}
