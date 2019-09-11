package com.melardev.xeytanj.concurrent.communicators.reactive.twoway.worker;

import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.QueueReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.TwoWayReactiveCommunicator;
import com.melardev.xeytanj.concurrent.messaging.IGuiEvent;
import com.melardev.xeytanj.concurrent.messaging.MainGuiEvent;
import com.melardev.xeytanj.services.logger.ILogger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerAndWorkerCommunicator<T> implements TwoWayReactiveCommunicator<T> {

    private IReactiveCommunicator<T> leftSideCommunicator;
    private IReactiveCommunicator<T> rightSideCommunicator;

    private ExecutorService threadPoolLeftEventProducers;
    private ExecutorService threadPoolDispatchersToLeft;
    private ExecutorService threadPoolRightEventProducers;
    private ExecutorService threadPoolDispatchersToRight;
    private Thread leftThread;
    private Thread rightThread;
    protected ILogger logger;

    @Override
    public IReactiveCommunicator<T> getLeftSide() {
        return leftSideCommunicator;
    }

    @Override
    public IReactiveCommunicator<T> getRightSide() {
        return rightSideCommunicator;
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public Thread getLeftThread() {
        return leftThread;
    }

    @Override
    public void setRightThread(Thread rightThread) {
        this.rightThread = rightThread;
    }

    @Override
    public Thread getRightThread() {
        return null;
    }

    @Override
    public void setLeftThread(Thread thread) {
        this.leftThread = thread;
    }

    @Override
    public void setLeftSide(IReactiveCommunicator<T> leftCommunicator) {
        this.leftSideCommunicator = leftCommunicator;
    }

    @Override
    public void setRightSide(IReactiveCommunicator<T> rightCommunicator) {
        this.rightSideCommunicator = rightCommunicator;
    }

    public void queueToRightSideAsync(Runnable runnable) {
        getRightSide().queueAsync(runnable);
    }

    public void queueToLeftSideAsync(Runnable runnable) {
        getLeftSide().queueAsync(runnable);
    }

    public void queueToRightSideSync(Runnable runnable) {
        threadPoolRightEventProducers.submit(() -> {
            getRightSide().queueSync(runnable);
        });
    }


    public void queueToRightSideAsync(T event) {
        getRightSide().queueAsync(event);
    }

    /**
     * Submit an event to the Right Communicator Queue in a synchronous
     * fashion
     *
     * @param event
     */
    public void queueToLeftSideSync(T event) {
        threadPoolLeftEventProducers.submit(() -> {
            getLeftSide().queueSync(event);
        });
    }


    void queueToLeftSideAsync(T event) {
        getLeftSide().queueAsync(event);
    }


    void queueToRightSideSync(T event) {
        getRightSide().queueSync(event);
    }

    void dispatchToLeftSide(T event) {
        getLeftSide().dispatchEvent(event);
    }

    void dispatchToRightSide(T event) {
        getRightSide().dispatchEvent(event);
    }

    public static void main(String[] args) {

        WorkerAndWorkerCommunicator<Object> rxCommunicator = new WorkerAndWorkerCommunicator<>();
        QueueReactiveCommunicator<Object> leftCommunicator = new QueueReactiveCommunicator<>();
        leftCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<Object>() {
            @Override
            public void onEvent(Object event) {
                System.out.println("Received in Left" + Thread.currentThread().getName());
                System.out.println(event);
            }
        });
        rxCommunicator.setLeftSide(leftCommunicator);

        QueueReactiveCommunicator<Object> rightCommunicator = new QueueReactiveCommunicator<>();
        rightCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<Object>() {
            @Override
            public void onEvent(Object event) {
                System.out.println("Received In Right" + Thread.currentThread().getName());
                System.out.println(event);
            }
        });

        rxCommunicator.setRightSide(rightCommunicator);
        rxCommunicator.start();

        rxCommunicator.queueToLeftSideAsync(new MainGuiEvent("Targeting Left from " + Thread.currentThread().getName()));
        rxCommunicator.queueToRightSideAsync(new MainGuiEvent("Targeting Right from " + Thread.currentThread().getName()));

        String line = "";
        Scanner scanner = new Scanner(System.in);

        while (!line.equals("exit")) {
            line = scanner.nextLine();
            System.out.println(line);
            IGuiEvent event = new MainGuiEvent("Ralala");
            rxCommunicator.queueToLeftSideAsync(event);
        }
    }

    public void start() {
        threadPoolLeftEventProducers = Executors.newCachedThreadPool();
        threadPoolDispatchersToLeft = Executors.newCachedThreadPool();

        threadPoolRightEventProducers = Executors.newCachedThreadPool();
        threadPoolDispatchersToRight = Executors.newCachedThreadPool();

        // A pool of threads will be waiting for new events to be dispatched to the left side
        for (int i = 0; i < 5; i++) {
            threadPoolDispatchersToLeft.execute(() -> {
                while(true) {
                    try {
                        T event = getLeftSide().getEventSync();
                        dispatchToLeftSide(event);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // A pool of threads will be waiting for new events to be dispatched to the right side
        for (int i = 0; i < 5; i++) {
            threadPoolDispatchersToRight.execute(() -> {
                while(true) {
                    try {
                        T event = getRightSide().getEventSync();
                        dispatchToRightSide(event);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public ExecutorService getThreadPoolLeftEventProducers() {
        return threadPoolLeftEventProducers;
    }

    public void setThreadPoolLeftEventProducers(ExecutorService threadPoolLeftEventProducers) {
        this.threadPoolLeftEventProducers = threadPoolLeftEventProducers;
    }

    public ExecutorService getThreadPoolDispatchersToLeft() {
        return threadPoolDispatchersToLeft;
    }

    public void setThreadPoolDispatchersToLeft(ExecutorService threadPoolDispatchersToLeft) {
        this.threadPoolDispatchersToLeft = threadPoolDispatchersToLeft;
    }

    public ExecutorService getThreadPoolRightEventProducers() {
        return threadPoolRightEventProducers;
    }

    public void setThreadPoolRightEventProducers(ExecutorService threadPoolRightEventProducers) {
        this.threadPoolRightEventProducers = threadPoolRightEventProducers;
    }

    public ExecutorService getThreadPoolDispatchersToRight() {
        return threadPoolDispatchersToRight;
    }

    public void setThreadPoolDispatchersToRight(ExecutorService threadPoolDispatchersToRight) {
        this.threadPoolDispatchersToRight = threadPoolDispatchersToRight;
    }
}
