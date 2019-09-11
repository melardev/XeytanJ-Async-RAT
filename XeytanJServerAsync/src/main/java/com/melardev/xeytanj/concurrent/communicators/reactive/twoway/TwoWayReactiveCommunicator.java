package com.melardev.xeytanj.concurrent.communicators.reactive.twoway;

import com.melardev.xeytanj.concurrent.communicators.TwoWayCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;

public interface TwoWayReactiveCommunicator<T> extends TwoWayCommunicator<T> {

    @Override
    IReactiveCommunicator<T> getLeftSide();

    @Override
    IReactiveCommunicator<T> getRightSide();

    void setLeftSide(IReactiveCommunicator<T> leftCommunicator);
    void setRightSide(IReactiveCommunicator<T> rightCommunicator);

    default void dispatchToSecond(T event) {
        getRightSide().dispatchEvent(event);
    }

    default void dispatchToLeft(T event) {
        getLeftSide().dispatchEvent(event);
    }

}
