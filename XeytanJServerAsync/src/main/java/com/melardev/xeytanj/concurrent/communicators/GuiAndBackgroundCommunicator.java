package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.concurrent.messaging.IGuiEvent;

/**
 * Left is by convention Gui, Right is other side
 */
public abstract class GuiAndBackgroundCommunicator implements TwoWayCommunicator<IGuiEvent> {

    private Thread guiThread;
    private Thread backgroundThread;

    private ICommunicator<IGuiEvent> guiCommunicator;
    private ICommunicator<IGuiEvent> backgroundCommunicator;

    @Override
    public ICommunicator<IGuiEvent> getLeftSide() {
        return guiCommunicator;
    }

    @Override
    public ICommunicator<IGuiEvent> getRightSide() {
        return backgroundCommunicator;
    }

    ICommunicator<IGuiEvent> getGuiCommunicator() {
        return getLeftSide();
    }

    ICommunicator<IGuiEvent> getBackgroundCommunicator() {
        return getRightSide();
    }

    abstract void queueToGuiAsync(IGuiEvent event);

    // This dependes on the framework used, it is not the same for Swing than for Java Fx, this is why
    // It has to be implemented by concrete classes and not this abstract class
    abstract void queueToGuiAsync(Runnable runnable);

    abstract void queueToGuiSync(Runnable runnable);


    void queuToBackground(IGuiEvent event) {
        getRightSide().queueAsync(event);
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

    public void setGuiCommunicator(ICommunicator<IGuiEvent> guiCommunicator) {
        this.guiCommunicator = guiCommunicator;
    }

    public void setBackgroundCommunicator(ICommunicator<IGuiEvent> backgroundCommunicator) {
        this.backgroundCommunicator = backgroundCommunicator;
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
    public void setLeftThread(Thread thread) {
        this.guiThread = thread;
    }

}
