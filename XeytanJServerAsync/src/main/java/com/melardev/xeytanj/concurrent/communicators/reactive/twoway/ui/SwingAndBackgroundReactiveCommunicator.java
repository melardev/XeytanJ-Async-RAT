package com.melardev.xeytanj.concurrent.communicators.reactive.twoway.ui;

import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

@Component
@Qualifier("swing")
public class SwingAndBackgroundReactiveCommunicator extends GuiAndBackgroundReactiveCommunicator {

    @Override
    public void queueToGuiAsync(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    @Override
    public void dispatchToGuiSync(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queueToGuiAsync(AppEvent event) {
        // getGuiCommunicator().queueAsync(event);
        EventQueue.invokeLater(() -> {
            getGuiCommunicator().dispatchEvent(event);
        });
    }

    @Override
    public void dispatchToGuiSync(AppEvent event) {
        try {
            EventQueue.invokeAndWait(() -> {
                getGuiCommunicator().dispatchEvent(event);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getName() {
        return "swing";
    }


    public static void main(String[] args) {
/*
        SwingAndBackgroundReactiveCommunicator rxCommunicator = new SwingAndBackgroundReactiveCommunicator();
        QueueReactiveCommunicator<IGuiEvent> guiCommunicator = new QueueReactiveCommunicator<>();
        guiCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<IGuiEvent>() {
            @Override
            public void onEvent(IGuiEvent event) {
                System.out.println("Supposed to run in Main Thread Received in " + Thread.currentThread().getName() + " " + event);
            }
        });

        rxCommunicator.setGuiCommunicator(guiCommunicator);

        QueueReactiveCommunicator backgroundCommunicator = new QueueReactiveCommunicator();
        backgroundCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<IGuiEvent>() {
            @Override
            public void onEvent(IGuiEvent event) {
                System.out.println("Supposed to run in Background Thread Received " + Thread.currentThread().getName() + " " + event);
            }
        });

        rxCommunicator.setBackgroundCommunicator(backgroundCommunicator);
        rxCommunicator.start();

        rxCommunicator.queueToGuiAsync(() -> System.out.println("this is Created in Main Thread,It is run in " + Thread.currentThread().getName()));
        rxCommunicator.queueToGuiAsync(new MainGuiEvent("Huuuuu"));
        rxCommunicator.queueToBackgroundAsync(new MainGuiEvent("Cool"));
        String currentThreadName = Thread.currentThread().getName();
        new Thread(() -> {
            System.out.println("This is run in " + Thread.currentThread().getName() + " submitted from " + currentThreadName);
        }, "background-1").start();

        String line = "";
        Scanner scanner = new Scanner(System.in);

        while (!line.equals("exit")) {
            line = scanner.nextLine();
            System.out.println(line);
            IGuiEvent event = new MainGuiEvent("Ralala") {
            };

            rxCommunicator.queueToBackgroundAsync(event);
            new Thread(() -> {
                System.out.println(event.toString() + " Dispatched from" + Thread.currentThread().getName());
                rxCommunicator.queueToGuiAsync(event);
            }).start();
        }
        */

    }
}
