package com.the9grounds.aeadditions.gui;

import java.util.List;

public interface IToolTipProvider {

	List<String> getToolTip(int mouseX, int mouseY);

	boolean isMouseOver(int mouseX, int mouseY);
}
