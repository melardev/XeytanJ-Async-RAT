package com.melardev.xeytanj.concurrent.communicators.reactive.twoway.worker;

import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.concurrent.messaging.events.RunnableEvent;

public class AppAndNetCommunicator extends WorkerAndWorkerCommunicator<AppEvent> {

    public void queueToNetAsync(Runnable runnable) {
        logger.trace("Queueing, Runnable to Net; Current Thread " + Thread.currentThread().getName());
        RunnableEvent event = new RunnableEvent(runnable);
        // queueToRightSideAsync(event);
    }

    public void queueToNetSync(Runnable runnable) {
        queueToRightSideSync(runnable);
    }

    public void queueToNetAsync(AppEvent event) {
        logger.trace("Queueing, NetEvent to Net; Current Thread " + Thread.currentThread().getName());
        queueToRightSideAsync(event);
    }


    public void queueToAppSync(AppEvent event) {
        queueToLeftSideSync(event);
    }


    public void queueToAppAsync(AppEvent event) {
        queueToLeftSideAsync(event);
    }

    public void queueToAppAsync(Runnable runnable) {
        RunnableEvent event = new RunnableEvent(runnable);
        // queueToLeftSideAsync(event);
    }


    @Override
    void dispatchToLeftSide(AppEvent event) {
        logger.trace("Dispatching, NetEvent to App; Current Thread " + Thread.currentThread().getName());
        super.dispatchToLeftSide(event);
    }

    @Override
    void dispatchToRightSide(AppEvent event) {
        logger.trace("Dispatching, NetEvent to Net; Current Thread " + Thread.currentThread().getName());
        super.dispatchToRightSide(event);
    }

    void queueToNetSync(AppEvent event) {
        queueToRightSideSync(event);
    }


    public static void main(String[] args) {
/*
        AppAndNetCommunicator rxCommunicator = new AppAndNetCommunicator();
        QueueReactiveCommunicator<NetEvent> appCommunicator = new QueueReactiveCommunicator<NetEvent>();
        appCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<NetEvent>() {
            @Override
            public void onEvent(NetEvent event) {
                if (event.getClass() == RunnableEvent.class)
                    ((Runnable) event.getObject()).run();
                System.out.println("Received in AppCommunicator" + Thread.currentThread().getName());
                System.out.println(event);
            }
        });

        rxCommunicator.setAppSide(appCommunicator);

        QueueReactiveCommunicator<NetEvent> back = new QueueReactiveCommunicator<NetEvent>();
        back.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<NetEvent>() {
            @Override
            public void onEvent(NetEvent event) {
                if (event.getClass() == RunnableEvent.class)
                    ((Runnable) event.getObject()).run();
                System.out.println("Received In NetCommunicator" + Thread.currentThread().getName());
                System.out.println(event);
            }
        });

        rxCommunicator.setRightSide(back);
        rxCommunicator.start();

        rxCommunicator.queueToNetAsync(() -> System.out.println("[Runnable] Targeting Net from " + Thread.currentThread().getName()));
        rxCommunicator.queueToAppAsync(() -> System.out.println("[Runnable] Targeting App from " + Thread.currentThread().getName()));

        rxCommunicator.queueToLeftSideAsync(new NetEvent("Targeting App from " + Thread.currentThread().getName()));
        rxCommunicator.queueToRightSideAsync(new NetEvent("Targeting Net from " + Thread.currentThread().getName()));

        String line = "";
        Scanner scanner = new Scanner(System.in);

        while (!line.equals("exit")) {
            line = scanner.nextLine();
            System.out.println(line);
            NetEvent event = new NetEvent("Ralala");
            rxCommunicator.queueToLeftSideAsync(event);
        }

        */
    }

    public void setAppSide(IReactiveCommunicator<AppEvent> appCommunicator) {
        setLeftSide(appCommunicator);
    }

    public void setNetSide(IReactiveCommunicator<AppEvent> appCommunicator) {
        setRightSide(appCommunicator);
    }

}
