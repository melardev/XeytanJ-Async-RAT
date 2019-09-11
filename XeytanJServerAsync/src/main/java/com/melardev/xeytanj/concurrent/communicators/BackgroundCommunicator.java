package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.concurrent.messaging.events.NetEvent;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundCommunicator {

    private ArrayBlockingQueue<NetEvent> queueForApp;
    private ArrayBlockingQueue<NetEvent> queueForNet;

    private ExecutorService threadPoolAppEventProducer;
    private ExecutorService threadPoolAppEventDispatcher;

    private ExecutorService threadPoolNetEventProducer;
    private ExecutorService threadPoolNetEventDispatcher;

    private BackgroundCommunicator.CommunicatorListener appListener;
    private BackgroundCommunicator.CommunicatorListener netListener;


    public CommunicatorListener getAppListener() {
        return appListener;
    }

    public void setAppListener(CommunicatorListener appListener) {
        this.appListener = appListener;
    }

    public BackgroundCommunicator.CommunicatorListener getBackgroundListener() {
        return appListener;
    }

    public void setBackgroundListener(BackgroundCommunicator.CommunicatorListener appListener) {
        this.appListener = appListener;
    }

    public BackgroundCommunicator.CommunicatorListener getNetListener() {
        return netListener;
    }

    public void setNetListener(BackgroundCommunicator.CommunicatorListener netListener) {
        this.netListener = netListener;
    }

    public interface CommunicatorListener {
        void onEvent(NetEvent event);
    }

    public BackgroundCommunicator() {
        this.queueForApp = new ArrayBlockingQueue<>(30);
        this.queueForNet = new ArrayBlockingQueue<>(30);
        threadPoolAppEventProducer = Executors.newCachedThreadPool();
        threadPoolAppEventDispatcher = Executors.newFixedThreadPool(5);

        threadPoolNetEventProducer = Executors.newCachedThreadPool();
        threadPoolNetEventDispatcher = Executors.newFixedThreadPool(5);

    }

    public void dispatchToApp(NetEvent event) {
        threadPoolAppEventProducer.submit(() -> {
            try {
                System.out.println("DispatchTobackground");
                queueForApp.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void dispatchToNet(NetEvent event) {
        threadPoolNetEventProducer.submit(() -> {
            try {
                System.out.println("Dispatching to Net");
                queueForNet.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() {
        for (int i = 0; i < 5; i++) {
            threadPoolAppEventDispatcher.submit(() -> {
                try {
                    NetEvent event = queueForApp.take();
                    appListener.onEvent(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


            threadPoolNetEventDispatcher.submit(() -> {
                try {
                    NetEvent event = queueForNet.take();
                    netListener.onEvent(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public static void main(String[] args) {
        BackgroundCommunicator.CommunicatorListener netListener = new BackgroundCommunicator.CommunicatorListener() {

            @Override
            public void onEvent(NetEvent event) {
                System.out.println("I am netListener, I received " + event);
            }
        };

        BackgroundCommunicator.CommunicatorListener appListener = new BackgroundCommunicator.CommunicatorListener() {

            @Override
            public void onEvent(NetEvent event) {
                System.out.println("I am appListener, I received " + event.toString());
            }
        };

        BackgroundCommunicator communicator = new BackgroundCommunicator();
        communicator.setNetListener(netListener);
        communicator.setAppListener(appListener);
        communicator.start();

        String line = "";
        Scanner scanner = new Scanner(System.in);
        do {
            line = scanner.nextLine();
            communicator.dispatchToNet(new NetEvent("I was created in " + Thread.currentThread().getName() + " " + line));
            String finalLine = line;
            new Thread(() -> {
                //communicator.dispatchToApp(new NetEvent("Dispatched from " + Thread.currentThread().getName() + " " + finalLine));
            });
        } while (!line.equalsIgnoreCase("exit"));
    }
}
