package com.melardev.xeytanj.gui.mediator.swing;

import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.IUiListener;

import java.util.UUID;

class WindowInfoStructure {
    private IGui frame;
    private Integer id;
    private UUID idBelongsTo;

    public WindowInfoStructure(Integer id, IGui window, IUiListener listener) {
        this.id = id;
        this.frame = window;
    }

    public Integer getId() {
        return id;
    }

    public UUID getIdBelongsTo() {
        return idBelongsTo;
    }

    public void setIdBelongsTo(UUID idBelongsTo) {
        this.idBelongsTo = idBelongsTo;
    }

    public IGui getFrame() {
        return frame;
    }
}
