package bdl.view.right;

import bdl.view.right.history.HistoryPanel;
import bdl.view.right.history.HistoryPanelItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class RightPanel extends SplitPane {

    public HistoryPanel<HistoryPanelItem> historyPanel;
    public ScrollPane propertyScroll;
    public AnchorPane rightSplitPaneTop;

    public RightPanel() {
        rightSplitPaneTop = new AnchorPane();

        //Begin right properties panel
        propertyScroll = new ScrollPane();
        propertyScroll.setContent(new PropertyEditPane());
        AnchorPane.setTopAnchor(propertyScroll, 0.0);
        AnchorPane.setBottomAnchor(propertyScroll, 0.0);
        AnchorPane.setLeftAnchor(propertyScroll, 0.0);
        AnchorPane.setRightAnchor(propertyScroll, 0.0);
        rightSplitPaneTop.getChildren().add(propertyScroll);
        //End right properties panel

        historyPanel = new HistoryPanel<>();

        getItems().addAll(rightSplitPaneTop, historyPanel);
    }
}
