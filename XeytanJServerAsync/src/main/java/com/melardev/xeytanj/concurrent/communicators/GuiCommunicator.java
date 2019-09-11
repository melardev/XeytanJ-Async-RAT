package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.concurrent.messaging.IGuiEvent;
import com.melardev.xeytanj.concurrent.messaging.MainGuiEvent;

import javax.swing.*;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiCommunicator {

    private ExecutorService threadPoolEventProducer;
    private ArrayBlockingQueue<IGuiEvent> queue;
    private ExecutorService threadPoolEventDispatcher;

    private CommunicatorListener backgroundListener;
    private CommunicatorListener guiListener;

    public CommunicatorListener getBackgroundListener() {
        return backgroundListener;
    }

    public void setBackgroundListener(CommunicatorListener backgroundListener) {
        this.backgroundListener = backgroundListener;
    }

    public CommunicatorListener getGuiListener() {
        return guiListener;
    }

    public void setGuiListener(CommunicatorListener guiListener) {
        this.guiListener = guiListener;
    }

    public interface CommunicatorListener {
        void onEvent(IGuiEvent event);
    }

    public GuiCommunicator() {
        this.queue = new ArrayBlockingQueue<>(30);
        threadPoolEventProducer = Executors.newCachedThreadPool();
        threadPoolEventDispatcher = Executors.newFixedThreadPool(5);
    }

    public void dispatchToGui(IGuiEvent event) {
        SwingUtilities.invokeLater(() -> {
            backgroundListener.onEvent(event);
        });
    }

    public void start() {
        for (int i = 0; i < 5; i++) {
            threadPoolEventDispatcher.submit(() -> {
                try {
                    IGuiEvent event = queue.take();
                    guiListener.onEvent(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
    }

    public void dispatchToBackground(IGuiEvent event) {
        threadPoolEventProducer.submit(() -> {
            try {
                System.out.println("DispatchTobackground");
                queue.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        CommunicatorListener listener2 = new CommunicatorListener() {

            @Override
            public void onEvent(IGuiEvent event) {
                System.out.println("Received " + event + " from " + Thread.currentThread().getName());
            }
        };
        GuiCommunicator communicator = new GuiCommunicator();
        communicator.backgroundListener = listener2;

        String line = "";
        Scanner scanner = new Scanner(System.in);
        do {
            line = scanner.nextLine();
            communicator.dispatchToBackground(new MainGuiEvent("I was created in " + Thread.currentThread().getName() + " " + line));
            String finalLine = line;
            new Thread(() -> {
                communicator.dispatchToGui(new MainGuiEvent("Dispatched from " + Thread.currentThread().getName() + " " + finalLine));
            });
        } while (!line.equalsIgnoreCase("exit"));
    }
}
