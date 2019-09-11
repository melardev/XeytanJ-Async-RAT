package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.concurrent.messaging.IGuiEvent;
import com.melardev.xeytanj.concurrent.messaging.MainGuiEvent;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class SwingAndBackgroundCommunicator extends GuiAndBackgroundCommunicator {


    @Override
    void queueToGuiAsync(IGuiEvent event) {
        /*throw new NotSupportedException("You can not submit an event to a non-Reactive UI Communicator." +
                "The main thread will never ask for messages, use the reactive extension instead");
        */
        // Only for testing, do not use this method, it just does not make sense, the AWT Thread is looping through
        // its own queue, not ours
        SwingUtilities.invokeLater(() -> {
            System.out.println("Run in " + Thread.currentThread().getName() + " " + event);
        });
    }

    @Override
    void queueToGuiAsync(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    @Override
    void queueToGuiSync(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    IGuiEvent getFromGui() throws InterruptedException {
        return getFromLeftSide();
    }

    IGuiEvent getFromBackground() throws InterruptedException {
        //throw new NotSupportedException("Swing components should never ask for events actively to pass events to gui call queueToGui(A)Sync()");
        return getFromRightSide();
    }


    public static void main(String[] args) {
        SwingAndBackgroundCommunicator sw = new SwingAndBackgroundCommunicator();
        sw.setBackgroundCommunicator(new GuiEventQueueCommunicator());
        sw.setGuiCommunicator(new GuiEventQueueCommunicator());
        sw.queuToBackground(new MainGuiEvent("lalala"));

        Runnable runnableShowThread = new Runnable() {
            @Override
            public void run() {
                System.out.printf("this is run in the %s thread %n", Thread.currentThread().getName());
            }
        };
        sw.queueToGuiSync(runnableShowThread);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sw.queueToGuiSync(runnableShowThread);
            }
        }, "Thread-1");

        t.start();

        sw.queueToGuiAsync(new MainGuiEvent(""));
        sw.queueToGuiAsync(new MainGuiEvent(""));
        sw.queuToBackground(new MainGuiEvent() {
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IGuiEvent event = sw.getFromGui();
                    if (event != null) {
                        System.out.println("We retrieved some event from gui, we are in " + Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-2");


        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IGuiEvent event = sw.getFromGui();
                    if (event != null)
                        System.out.println("We retrieved some event from gui, we are in " + Thread.currentThread().getName());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-3");
        t3.start();


        Thread t4 = new Thread(() -> {
            try {
                IGuiEvent aa = sw.getFromBackground();
                System.out.println("Got From Background " + aa);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread-4");
        t4.start();

        try {
            t.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // TODO: onRunnable(Runnable r) would be nice as well

    }
}
