package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.errors.IllegalThreadAccessException;
import com.melardev.xeytanj.services.IService;

public interface TwoWayCommunicator<T> extends IService {

    enum Side {
        LEFT, RIGHT
    }

    ICommunicator<T> getLeftSide();

    ICommunicator<T> getRightSide();

    Thread getLeftThread();

    default T getFromLeftSide() throws InterruptedException {
        ensureValidAccessThread(Side.LEFT);
        return getLeftSide().getEvent();
    }

    default T getFromRightSide() throws InterruptedException {
        ensureValidAccessThread(Side.LEFT);
        return getRightSide().getEvent();
    }

    default boolean hasPendingEventsLeftSide() {
        ensureValidAccessThread(Side.LEFT);
        return hasPendingEventsLeftSide();
    }

    default boolean hasPendingEventsRightSide() {
        ensureValidAccessThread(Side.RIGHT);
        return hasPendingEventsRightSide();
    }

    default void ensureValidAccessThread(Side side) {
        if (side == Side.LEFT) {
            if (getLeftSide().isThreadSafe())
                return;
            if (getLeftThread() == null)
                setLeftThread(Thread.currentThread());
            else {
                if (getLeftThread() != Thread.currentThread())
                    throw new IllegalThreadAccessException();
            }
        } else {
            if (getRightSide().isThreadSafe())
                return;

            if (getRightThread() == null)
                setRightThread(Thread.currentThread());
            else {
                if (getRightThread() != Thread.currentThread())
                    throw new IllegalThreadAccessException();
            }
        }
    }

    void setRightThread(Thread currentThread);

    Thread getRightThread();

    void setLeftThread(Thread thread);
}
