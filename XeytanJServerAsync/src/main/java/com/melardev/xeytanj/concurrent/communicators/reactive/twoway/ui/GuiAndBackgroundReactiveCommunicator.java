package com.melardev.xeytanj.concurrent.communicators.reactive.twoway.ui;

import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.TwoWayReactiveCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.services.logger.ILogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GuiAndBackgroundReactiveCommunicator implements TwoWayReactiveCommunicator<AppEvent> {

    private Thread guiThread;
    private Thread backgroundThread;

    private IReactiveCommunicator<AppEvent> guiCommunicator;
    private IReactiveCommunicator<AppEvent> backgroundCommunicator;
    private ExecutorService threadPoolDispatchersToGui;
    private ExecutorService threadPoolGuiProducers;
    private ExecutorService threadPoolBackProducers;
    private ExecutorService threadPoolDispatchersToBack;
    private ILogger logger;


    public void setGuiCommunicator(IReactiveCommunicator<AppEvent> guiCommunicator) {
        setLeftSide(guiCommunicator);
    }

    @Override
    public void setLeftSide(IReactiveCommunicator<AppEvent> leftCommunicator) {
        this.guiCommunicator = leftCommunicator;
    }

    @Override
    public void setRightSide(IReactiveCommunicator<AppEvent> rightCommunicator) {
        this.backgroundCommunicator = rightCommunicator;
    }

    public void setBackgroundCommunicator(IReactiveCommunicator<AppEvent> backgroundCommunicator) {
        setRightSide(backgroundCommunicator);
    }

    @Override
    public IReactiveCommunicator<AppEvent> getLeftSide() {
        return guiCommunicator;
    }

    @Override
    public IReactiveCommunicator<AppEvent> getRightSide() {
        return backgroundCommunicator;
    }

    IReactiveCommunicator<AppEvent> getGuiCommunicator() {
        return getLeftSide();
    }

    IReactiveCommunicator<AppEvent> getBackgroundCommunicator() {
        return getRightSide();
    }

    // This depends on the framework used, it is not the same for Swing than for Java Fx, this is why
    // It has to be implemented by concrete classes and not this abstract class
    public abstract void queueToGuiAsync(Runnable runnable);

    public abstract void dispatchToGuiSync(Runnable runnable);

    public abstract void queueToGuiAsync(AppEvent event);

    public abstract void dispatchToGuiSync(AppEvent event);

    public void queueToBackgroundAsync(AppEvent event) {
        getRightSide().queueAsync(event);
    }

    public void queueToBackgroundSync(AppEvent event) {
        getRightSide().queueSync(event);
    }

    @Override
    public Thread getLeftThread() {
        return guiThread;
    }

    @Override
    public boolean hasPendingEventsLeftSide() {
        return false;
    }

    @Override
    public boolean hasPendingEventsRightSide() {
        return false;
    }

    @Override
    public void setRightThread(Thread backgroundThread) {
        this.backgroundThread = backgroundThread;
    }

    @Override
    public Thread getRightThread() {
        return backgroundThread;
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void setLeftThread(Thread thread) {
        this.guiThread = thread;
    }

    public void start() {
        threadPoolGuiProducers = Executors.newFixedThreadPool(5);
        // threadPoolDispatchersToGui = Executors.newFixedThreadPool(5);

        threadPoolBackProducers = Executors.newFixedThreadPool(5);
        threadPoolDispatchersToBack = Executors.newFixedThreadPool(5);

        /* Not used, Gui Frameworks provide a mean to queue events using their API
        // So use that instead of this
        for (int i = 0; i < 5; i++) {
            threadPoolDispatchersToGui.execute(() -> {
                try {
                    IGuiEvent event = getGuiCommunicator().getEventSync();
                    getGuiCommunicator().dispatchEvent(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        */

        for (int i = 0; i < 5; i++) {
            threadPoolDispatchersToBack.execute(() -> {
                while (true) {
                    try {
                        AppEvent event = getBackgroundCommunicator().getEventSync();
                        logger.trace("Received IGuiEvent, Dispatching to App; Current Thread " + Thread.currentThread().getName());
                        getBackgroundCommunicator().dispatchEvent(event);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
