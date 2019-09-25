package bdl.model.selection;

import java.util.ArrayList;
import java.util.List;

import bdl.build.GObject;
import bdl.build.javafx.scene.control.GMenuBar;

public class SelectionManager {
	private boolean enabled=true;
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled){
		this.enabled=enabled;
	}

    private List<SelectionListener> selectionListeners;
    private GObject currentlySelected = null;

    public SelectionManager() {
        selectionListeners = new ArrayList<>();
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        selectionListeners.add(selectionListener);
    }

    public void updateSelected(GObject gObject) {
    	if (!enabled){return;}
    	
        for (SelectionListener selectionListener : selectionListeners) {
            selectionListener.updateSelected(gObject);
        }
        currentlySelected = gObject;
    	
        
    }

    public void clearSelection() {   
    	if (!enabled){return;}
        for (SelectionListener selectionListener : selectionListeners) {
            selectionListener.clearSelection();
        }
        currentlySelected = null;
    }

    /**
     * Gets the currently selected GObject or null if no GObject is selected
     * @return The GObject that is currently selected or null if no GObject selected
     */
    public GObject getCurrentlySelected() {
        return currentlySelected;
    }

}
