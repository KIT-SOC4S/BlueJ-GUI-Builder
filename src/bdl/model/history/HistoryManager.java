/*
 * This file is part of JavaFX-GUI-Builder.
 *
 * Copyright (C) 2014  Leon Atherton, Ben Goodwin, David Hodgson
 *
 * JavaFX-GUI-Builder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * JavaFX-GUI-Builder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaFX-GUI-Builder.  If not, see <http://www.gnu.org/licenses/>.
 */

package bdl.model.history;

import bdl.model.history.update.HistoryItemDescription;
import bdl.model.history.update.HistoryUpdate;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private int currentIndex;
    private ArrayList<HistoryItem> chain;
    private List<HistoryListener> historyListeners;
    private boolean pauseHistory = false;

    private HistoryItem startState = new HistoryItem() {
        @Override public void restore() { /*As the first item, this one can't revert*/ }

        @Override public void revert() { /*As the first item, this one can't revert*/ }

        @Override public String getAppearance() { return "Start"; }
    };

    public HistoryManager() {
        currentIndex = 0;
        chain = new ArrayList<>();
        chain.add(0, startState);
        historyListeners = new ArrayList<>();
    }

    public void addHistoryListener(HistoryListener historyListener) {
        historyListeners.add(historyListener);
    }

    public void addHistory(HistoryItem newHistoryItem) {
        currentIndex++;
        for (int i = chain.size() - 1; i >= currentIndex; i--) {
            chain.remove(i);
        }
        chain.add(currentIndex, newHistoryItem);
        notifyOfUpdate();
    }

    public void clearHistory() {
        chain.clear();
        currentIndex = 0;
        chain.add(0, startState);
        notifyOfUpdate();
    }

    public void updateTo(int index) {
        if (index >= 0 && index < chain.size()) {
            if (currentIndex > index) {
                //Go back
                for (int i = currentIndex; i > index; i--) {
                    chain.get(i).revert();
                }
            } else if (currentIndex < index) {
                //Go forwards
                for (int i = currentIndex + 1; i <= index; i++) {
                    chain.get(i).restore();
                }
            }
            currentIndex = index;
            notifyOfUpdate();
        }
    }

    private void notifyOfUpdate() {
        if (historyListeners.size() > 0) {
            ArrayList<HistoryItemDescription> historyDescriptions = new ArrayList<>();
            int i = 0;
            for (HistoryItem historyItem : chain) {
                historyDescriptions.add(new HistoryItemDescription(i, historyItem.getAppearance()));
                i++;
            }
            HistoryUpdate historyUpdate = new HistoryUpdate(currentIndex, historyDescriptions.toArray(new HistoryItemDescription[historyDescriptions.size()]));
            for (HistoryListener historyListener : historyListeners) {
                historyListener.historyUpdated(historyUpdate);
            }
        }
    }

    /**
     * Important - HistoryManager can still add history when paused.
     * It is up to the specific case to decide if it should not add history when paused.
     */
    public boolean isPaused() {
        return pauseHistory;
    }

    public void pause() {
        pauseHistory = true;
    }

    public void unpause() {
        pauseHistory = false;
    }


}
