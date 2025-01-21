package org.lpc.map;

import org.lpc.BaseInputHandler;

public interface IMapInput {
    void handleClick(BaseInputHandler handler, int button, int x, int y);
    void handleDrag(BaseInputHandler handler, int x, int y);
    void handleKey(BaseInputHandler handler, int keycode);
    void handleScroll(BaseInputHandler handler, float amountX, float amountY);
}
