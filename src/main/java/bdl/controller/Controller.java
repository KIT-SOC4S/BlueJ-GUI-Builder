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

package bdl.controller;

import bdl.Interface;
import bdl.build.CodeGenerator;
import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettings;
import bdl.model.ComponentSettingsStore;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryListener;
import bdl.model.history.HistoryManager;
import bdl.model.history.update.HistoryItemDescription;
import bdl.model.history.update.HistoryUpdate;
import bdl.model.selection.SelectionListener;
import bdl.model.selection.SelectionManager;
import bdl.view.View;
import bdl.view.left.ComponentMenuItem;
import bdl.view.left.hierarchy.HierarchyTreeItem;
import bdl.view.right.PropertyEditPane;
import bdl.view.right.history.HistoryPanelItem;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Controller {

    private View view;
    private ComponentSettingsStore componentSettingsStore;
    private ViewListeners viewListeners;
    private ArrayList<String> fieldNames;
    private HistoryManager historyManager;
    private SelectionManager selectionManager;
    private Interface blueJInterface;
    private boolean isOpeningFile = false;

    public Controller(View view, ComponentSettingsStore componentSettingsStore, Interface blueJInterface) {
        this.view = view;
        this.componentSettingsStore = componentSettingsStore;
        this.blueJInterface = blueJInterface;
        fieldNames = new ArrayList<>();
        historyManager = new HistoryManager();
        selectionManager = new SelectionManager();
        viewListeners = new ViewListeners(historyManager, selectionManager);

        setupLeftPanel();
        setupMiddlePanel();
        setupRightPanel();
        setupTopPanel();
        setupAutoSave();
    }

    private void setupTopPanel() {
        final Stage stage = view.getStage();
        // File > Load File
        view.topPanel.mItmLoadFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                if (blueJInterface != null) { fileChooser.setInitialDirectory(blueJInterface.getWorkingDirectory()); }
                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
                fileChooser.getExtensionFilters().add(filter);

                File file = fileChooser.showOpenDialog(view.getStage());

                openFile(file);
            }
        });
        // File > Save File
        view.topPanel.mItmSaveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                saveFile();
            }
        });
        // File > Close
        view.topPanel.mItmClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        // File > Make Full Screen
        view.topPanel.mItmFullScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.enable.text"));
                } else {
                    stage.setFullScreen(true);
                    view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.disable.text"));
                }
            }
        });

        //Add HistoryListener for the Undo/Redo menu items in the Edit menu
        historyManager.addHistoryListener(new HistoryListener() {
            @Override
            public void historyUpdated(final HistoryUpdate historyUpdate) {
                //Undo MenuItem
                if (historyUpdate.canUndo()) {
                    view.topPanel.mItmUndo.setDisable(false);
                    view.topPanel.mItmUndo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            historyManager.updateTo(historyUpdate.getCurrentIndex() - 1);
                        }
                    });
                } else {
                    view.topPanel.mItmUndo.setDisable(true);
                }

                //Redo MenuItem
                if (historyUpdate.canRedo()) {
                    view.topPanel.mItmRedo.setDisable(false);
                    view.topPanel.mItmRedo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            historyManager.updateTo(historyUpdate.getCurrentIndex() + 1);
                        }
                    });
                } else {
                    view.topPanel.mItmRedo.setDisable(true);
                }
            }
        });

        //Edit Menu > Delete button functionality
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(final GObject gObject) {
                view.topPanel.mItmDelete.setDisable(false);
                view.topPanel.mItmDelete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        view.middleTabPane.viewPane.getChildren().remove(gObject);
                        selectionManager.clearSelection();
                        historyManager.addHistory(new HistoryItem() {
                            @Override
                            public void restore() {
                                view.middleTabPane.viewPane.getChildren().remove(gObject);
                                selectionManager.clearSelection();
                            }

                            @Override
                            public void revert() {
                                view.middleTabPane.viewPane.getChildren().add((Node) gObject);
                                selectionManager.updateSelected(gObject);
                            }

                            @Override
                            public String getAppearance() {
                                return gObject.getFieldName() + " deleted";
                            }
                        });
                    }
                });
                view.topPanel.mItmClearAll.setDisable(false);
                view.topPanel.mItmClearAll.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        final List<Node> list = new ArrayList<>();
                        list.addAll(view.middleTabPane.viewPane.getChildren());
                        view.middleTabPane.viewPane.getChildren().clear();
                        selectionManager.clearSelection();

                        historyManager.addHistory(new HistoryItem() {
                            @Override
                            public void restore() {
                                for (Node n : list) {
                                    view.middleTabPane.viewPane.getChildren().remove(n);
                                }
                                selectionManager.clearSelection();
                            }

                            @Override
                            public void revert() {
                                for (Node n : list) {
                                    view.middleTabPane.viewPane.getChildren().add(n);
                                    selectionManager.updateSelected((GObject) n);
                                }
                            }

                            @Override
                            public String getAppearance() {
                                return ("Clear All");
                            }
                        });
                    }
                });
            }

            @Override
            public void clearSelection() {
                view.topPanel.mItmDelete.setDisable(true);
                view.topPanel.mItmClearAll.setDisable(true);
            }
        });

        // View > Show Hierarchy
        view.topPanel.mItmHierarchy.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (view.topPanel.mItmHierarchy.isSelected()) {
                    view.leftPanel.getItems().add(view.leftPanel.hierarchyTitledPane);
                    view.leftPanel.setDividerPosition(0, 0.6);
                } else {
                    view.leftPanel.getItems().remove(view.leftPanel.hierarchyTitledPane);
                }
            }
        });
        // View > Show History
        view.topPanel.mItmHistory.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                toggleHistory();
            }
        });
        
        view.topPanel.mItmAbout.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Stage stage = new Stage();
                GridPane pane = new GridPane();
                Label label = new Label(LabelGrabber.getLabel("about.text"));
                label.setMaxWidth(300);
                label.setWrapText(true);
                label.setFont(new Font(18));
                label.setTextAlignment(TextAlignment.CENTER);
                ImageView imageview = new ImageView(new Image(getClass().getResourceAsStream("/bdl/icons/BlueJ_Orange_64.png")));
                pane.add(imageview, 1, 1);
                pane.add(label, 1, 2);
                GridPane.setHalignment(imageview, HPos.CENTER);
                stage.setScene(new Scene(pane));
                stage.show();
            }
        });
        
    }
    
    private void setupLeftPanel() {
        view.leftPanel.leftList.setCellFactory(new Callback<ListView<ComponentMenuItem>, ListCell<ComponentMenuItem>>() {
            @Override
            public ListCell<ComponentMenuItem> call(ListView<ComponentMenuItem> list) {
                return new LeftListCellFactory(view);
            }
        });


        for (ComponentSettings componentSettings : componentSettingsStore.getComponents()) {
            String type = componentSettings.getType();
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/" + componentSettings.getIcon())));
            view.leftPanel.leftList.getItems().add(new ComponentMenuItem(type, icon, componentSettings));
        }

        view.leftPanel.leftList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    ComponentSettings componentSettings = view.leftPanel.leftList.getSelectionModel().getSelectedItem().getComponentSettings();
                    if (componentSettings != null) {
                        GObject newThing = null;

                        historyManager.pause();
                        try {
                            Class panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                            Constructor constructor = panelPropertyClass.getConstructor();
                            newThing = (GObject) constructor.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        addGObject(newThing, componentSettings, view, viewListeners, null, -1, -1, view.middleTabPane.viewPane);
                        historyManager.unpause();
                    }
                    view.leftPanel.leftList.getSelectionModel().select(-1);
                }
            }
        });

        view.leftPanel.hierarchyPane.treeRoot = new TreeItem<>(new HierarchyTreeItem(view.middleTabPane.viewPane, view, selectionManager, historyManager));
        view.leftPanel.hierarchyPane.treeView.setRoot(view.leftPanel.hierarchyPane.treeRoot);
        view.leftPanel.hierarchyPane.treeRoot.setExpanded(true);
        view.leftPanel.hierarchyPane.treeView.setShowRoot(true);

        view.leftPanel.hierarchyPane.treeView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TreeItem<HierarchyTreeItem> item = view.leftPanel.hierarchyPane.treeView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    selectionManager.updateSelected(item.getValue().getGObject());
                }
            }
        });

        //Add listener to node list to update hierarchy pane
        addPaneListChangeListener(view.middleTabPane.viewPane, view.leftPanel.hierarchyPane.treeRoot);


        //Add selection handlers for Hierarchy Pane
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(GObject gObject) {
                update(gObject.getFieldName(), view.leftPanel.hierarchyPane.treeRoot);
            }

            private void update(String fieldName, TreeItem<HierarchyTreeItem> treeRoot) {
                for (TreeItem<HierarchyTreeItem> ti : treeRoot.getChildren()) {
                    GObject gObject = ti.getValue().getGObject();
                    if (gObject.getFieldName().equals(fieldName)) {
                        view.leftPanel.hierarchyPane.treeView.getSelectionModel().select(ti);
                    } else if (gObject instanceof Pane) {
                        update(fieldName, ti);
                    }

                }
            }

            @Override
            public void clearSelection() {
                view.leftPanel.hierarchyPane.treeView.getSelectionModel().select(-1);
            }
        });
    }

    private void setupMiddlePanel() {

        // Add selection handlers for the outline
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(GObject gObject) {
                if (gObject instanceof GUIObject) {
                    return;
                }
                Node node = (Node) gObject;
                Rectangle outline = view.middleTabPane.outline;
                outline.setVisible(true);

                double nodeX = 0;
                double nodeY = 0;
                Node node2 = node;
                while (!(node2 instanceof GUIObject)) {
                    nodeX += node2.getLayoutX();
                    nodeY += node2.getLayoutY();
                    node2 = node2.getParent();
                }

                //double nodeX = node.getParent().getLayoutX() + node.getLayoutX();
                //double nodeY = node.getParent().getLayoutY() + node.getLayoutY();
                Bounds bounds = node.getLayoutBounds();
                double nodeW = bounds.getWidth();
                double nodeH = bounds.getHeight();
                if (node instanceof Circle) {
                    outline.setLayoutX(nodeX - 4 - (nodeW / 2));
                    outline.setLayoutY(nodeY - 4 - (nodeH / 2));
                } else if (node instanceof Rectangle) {
                    Rectangle r = (Rectangle) node;
                    outline.setLayoutX(nodeX - 4 - (r.getStrokeWidth() / 2));
                    outline.setLayoutY(nodeY - 4 - (r.getStrokeWidth() / 2));
                } else {
                    outline.setLayoutX(nodeX - 4);
                    outline.setLayoutY(nodeY - 4);
                }
                outline.setWidth(nodeW + 8);
                outline.setHeight(nodeH + 8);
            }

            @Override
            public void clearSelection() {
                view.middleTabPane.outline.setVisible(false);
            }
        });

        view.middleTabPane.viewPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                view.middleTabPane.viewPane.requestFocus();
                selectionManager.clearSelection();
                mouseEvent.consume();
            }
        });

        view.middleTabPane.codeTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (view.middleTabPane.codeTab.isSelected()) {
                    selectionManager.clearSelection();
                    view.middleTabPane.codePane.setText(generateJavaCode());
                }
            }
        });
        view.middleTabPane.previewTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (view.middleTabPane.previewTab.isSelected()) {

                    //Write .java file
                    // Make temporary space in BlueJ user dir for compilation.
                    File fileJava;
                    File fileClass;
                    if (blueJInterface != null) {
                        File tempDir = new File(blueJInterface.getUserPrefDir(), "guibuilder");
                        if (tempDir.isDirectory() == false) { tempDir.mkdirs(); }
                        fileJava = new File(tempDir, blueJInterface.getOpenGUIName() + ".java");
                        fileClass = new File(tempDir, blueJInterface.getOpenGUIName() + ".class");
                    } else {
                        fileJava = new File(view.middleTabPane.viewPane.getClassName() + ".java");
                        fileClass = new File(view.middleTabPane.viewPane.getClassName() + ".class");
                    }
                    try {
                        BufferedOutputStream cssOutput = new BufferedOutputStream(new FileOutputStream(fileJava));
                        cssOutput.write(generateJavaCode().getBytes());
                        cssOutput.flush();
                        cssOutput.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Compile class
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    if (compiler == null) {
                        throw new RuntimeException("Jar could not be created as Java version requires javac.");
                    }
                    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

                    Iterable<? extends JavaFileObject> compilationUnits1 =
                            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(fileJava));

                    // Compiler options
//                    List<String> optionsList = new ArrayList<String>();
//                    File fileJfxrt = new File(System.getProperty("java.home"), "lib\\jfxrt.jar");
//                    optionsList.add("-classpath "+fileJfxrt.getAbsolutePath()+":.");
                    
                    compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

                    try {
                        fileManager.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Load & run class
                    try {
                        Class guiClass;
                        if (blueJInterface == null) {
                            URL[] urls = new URL[]{new File(".").toURI().toURL()};
                            URLClassLoader ucl = new URLClassLoader(urls);
                            guiClass = Class.forName(view.middleTabPane.viewPane.getClassName(), false, ucl);
                        } else {
                            URL[] urls = new URL[]{new File(blueJInterface.getUserPrefDir(), "guibuilder").toURI().toURL()};
                            URLClassLoader ucl = new URLClassLoader(urls);
                            guiClass = Class.forName(blueJInterface.getOpenGUIName(), false, ucl);
                        }
                        Method main = guiClass.getMethod("start", Stage.class);
                        Object obj = guiClass.newInstance();
                        main.invoke(obj, new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Delete created files
                    fileJava.delete();
                    fileClass.delete();

                    view.middleTabPane.getSelectionModel().select(0);
                }
            }
        });

        view.middleTabPane.viewPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                t.acceptTransferModes(TransferMode.ANY);
            }
        });

        view.middleTabPane.viewPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                ComponentMenuItem cmj = view.leftPanel.leftList.getSelectionModel().getSelectedItem();
                ComponentSettings componentSettings = cmj.getComponentSettings();
                if (componentSettings != null) {
                    GObject newThing = null;

                    historyManager.pause();
                    try {
                        Class panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                        Constructor constructor = panelPropertyClass.getConstructor();
                        newThing = (GObject) constructor.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    addGObject(newThing, componentSettings, view, viewListeners, null, (int) t.getX(), (int) t.getY(), view.middleTabPane.viewPane);
                    historyManager.unpause();
                }
                view.leftPanel.leftList.getSelectionModel().select(-1);
            }
        });
    }

    private void setupRightPanel() {

        // Add selection handlers for the Property Edit Pane
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(final GObject gObject) {
                // Yes, it really does make no sense to put this in a Platform.runLater,
                // and removing and readding the splitpane makes no sense, but it fixes
                // the panel not showing properties when loaded from BlueJ...
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.rightPanel.propertyScroll.setContent(gObject.getPEP());
                        view.rightPanel.rightSplitPaneTop.getChildren().clear();
                        view.rightPanel.rightSplitPaneTop.getChildren().add(view.rightPanel.propertyScroll);
                    }
                });

            }

            @Override
            public void clearSelection() {
                view.rightPanel.propertyScroll.setContent(new PropertyEditPane());
            }
        });

        // Add history handlers for the History Pane
        historyManager.addHistoryListener(new HistoryListener() {
            @Override
            public void historyUpdated(HistoryUpdate historyUpdate) {
                ObservableList<HistoryPanelItem> panelItems = view.rightPanel.historyPanel.getItems();
                panelItems.clear();

                for (HistoryItemDescription item : historyUpdate.getHistory()) {
                    panelItems.add(new HistoryPanelItem(item));
                }

                view.rightPanel.historyPanel.getSelectionModel().select(historyUpdate.getCurrentIndex());
                view.rightPanel.historyPanel.scrollTo(historyUpdate.getCurrentIndex());
            }
        });

        view.rightPanel.historyPanel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    HistoryPanelItem historyItemDescription = view.rightPanel.historyPanel.getSelectionModel().getSelectedItem();
                    if (historyItemDescription != null) {
                        historyManager.updateTo(historyItemDescription.getIndex());
                    }
                }
            }
        });

    }
    
    /** Adds a HistoryListener and saves whenever a change is made.
     * Only effective when running with BlueJ.
     */
    private void setupAutoSave() {
        if (blueJInterface != null) {
            historyManager.addHistoryListener(new HistoryListener() {
                @Override
                public void historyUpdated(HistoryUpdate historyUpdate) {
                    saveFile();
                }
            });
        }
    }
    
    /** Open the specified FXML file.
     * @param file the File referencing the FXML file.
     */
    public void openFile(File file) {
        isOpeningFile = true;
        if (file != null) {
            view.middleTabPane.viewPane.getChildren().clear();
            selectionManager.clearSelection();
            historyManager.clearHistory();

            try {
                Parent parent = FXMLLoader.load(file.toURI().toURL());

                GUIHelper.setBounds(view.middleTabPane.viewPane, view.middleTabPane.viewPaneDecorator, parent.prefWidth(0), parent.prefHeight(0));
                String className = parent.getId();
                if (className != null && !className.isEmpty()) {
                    view.middleTabPane.viewPane.setClassName(className);
                }

                for (Node node : parent.getChildrenUnmodifiable()) {

                    for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
                        ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
                        try {
                            if (componentSettings.getType().equals(node.getClass().getSimpleName())) {
                                historyManager.pause();
                                Class componentClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                                Constructor constructor = componentClass.getConstructor();
                                GObject newThing = (GObject) constructor.newInstance();
                                newThing.setFieldName(node.getId());

                                addGObject(newThing, componentSettings, view, viewListeners, node, -1, -1, view.middleTabPane.viewPane);
                                historyManager.unpause();
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isOpeningFile = false;
    }
    
    /** Reset the workspace.
     */
    public void newFile() {
        isOpeningFile = true;
        view.middleTabPane.viewPane.getChildren().clear();
        selectionManager.clearSelection();
        historyManager.clearHistory();
        isOpeningFile = false;
    }
    /** Reset the workspace and set the new GUI's name to className.
     * @param className the desired name of the new GUI
     */
    public void newFile(String className) {
        newFile();
        view.middleTabPane.viewPane.setClassName(className);
        view.middleTabPane.viewPane.setGUITitle(className);
    }
    
    /** Save file to FXML. If running with BlueJ, output Java code to file.
     */
    private void saveFile() {
        if (isOpeningFile) { return; }
        File file;
        if (blueJInterface == null) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
            fileChooser.getExtensionFilters().add(filter);

            file = fileChooser.showSaveDialog(view.getStage());
        }
        else {
            file = new File(blueJInterface.getWorkingDirectory(), blueJInterface.getOpenGUIName() + ".fxml");
        }

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".fxml")) {
                file = new File(file.getAbsoluteFile() + ".fxml");
            }

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(CodeGenerator.generateFXMLCode(view.middleTabPane.viewPane, null));//We don't need the imports, for the minute...
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Update BlueJ Java code
        if ( (blueJInterface != null) && (blueJInterface.isEditingGUI()) ) {
            // Write java code to GUI java file.
            try {
                FileWriter fileWriter = new FileWriter(blueJInterface.getOpenGUIFile());
                fileWriter.write(generateJavaCode());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Mark the file as dirty in BlueJ
            blueJInterface.markAsDirty();
        }
    }
    
    /** Toggle the history panel's visibility.
     */
    public void toggleHistory() {
        if (!view.rightPanel.getItems().contains(view.rightPanel.historyPanel)) {
            view.topPanel.mItmHistory.setSelected(true);
            view.rightPanel.getItems().add(view.rightPanel.historyPanel);
            view.rightPanel.setDividerPosition(0, 0.6);
        } else {
            view.topPanel.mItmHistory.setSelected(false);
            view.rightPanel.getItems().remove(view.rightPanel.historyPanel);
        }
    }
    
    /** Set the name of the GUI class. (BlueJ interface functionality.)
     */
    public void setClassName(String className) {
        view.middleTabPane.viewPane.setClassName(className);
    }
    /** Make the stage visible. (BlueJ interface functionality.)
     */
    public void showStage() {
        view.getStage().show();
        view.getStage().toFront();
    }
    /** Make the stage invisible. (BlueJ interface functionality.)
     */
    public void hideStage() {
        view.getStage().hide();
    }
    
    /** Generates the full Java code.
     */
    private String generateJavaCode() {
        HashMap<String, String> imports = new HashMap<>();
        for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
            ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
            imports.put(componentSettings.getType(), componentSettings.getPackageName());
        }
        return CodeGenerator.generateJavaCode(view.middleTabPane.viewPane, imports, blueJInterface);
    }
    
    //x and y are initial layout positions. To be used only with drag and drop.
    private void addGObject(final GObject newThing, ComponentSettings componentSettings, final View view, final ViewListeners viewListeners, Node settingsNode, int x, int y, final Pane destination) {

        //Sets the default settings on the gObject and creates the property edit pane
        final PropertyEditPane propertyEditPane = new PropertyEditPane(newThing, componentSettings, fieldNames, view.middleTabPane.viewPane, settingsNode, historyManager);

        newThing.setPEP(propertyEditPane);

        if (componentSettings.getLayoutType().equals("anchorpane")) {
            dealWithPane((Pane) newThing);
        }

        final Node newNode = (Node) newThing;

        newNode.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                selectionManager.updateSelected((GObject) newNode);
            }
        });

        newNode.layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                selectionManager.updateSelected((GObject) newNode);
            }
        });

        newNode.layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                selectionManager.updateSelected((GObject) newNode);
            }
        });

        newNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectionManager.updateSelected((GObject) newNode);
                viewListeners.onMousePressed(newNode, mouseEvent);
                mouseEvent.consume();//Stops the mouseEvent falling through to the viewPane which would clear selection
            }
        });
        newNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                viewListeners.onMouseReleased(newNode, mouseEvent);
            }
        });
        newNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                viewListeners.onMouseDragged(newNode, mouseEvent);
                selectionManager.updateSelected((GObject) newNode);
            }
        });


        final ContextMenu nodePopUp = new ContextMenu();
        final MenuItem deletebutton = new MenuItem(LabelGrabber.getLabel("delete.node.text"));
        nodePopUp.getItems().add(deletebutton);
        newNode.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getButton().equals(MouseButton.SECONDARY)) {
                    nodePopUp.show(newNode, Side.RIGHT, 0, 0);
                    deletebutton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            destination.getChildren().remove(newNode);
                            selectionManager.clearSelection();
                            historyManager.addHistory(new HistoryItem() {
                                @Override
                                public void revert() {
                                    destination.getChildren().add(newNode);
                                    selectionManager.updateSelected((GObject) newNode);
                                }

                                @Override
                                public void restore() {
                                    destination.getChildren().remove(newNode);
                                    selectionManager.clearSelection();
                                }

                                @Override
                                public String getAppearance() {
                                    return ((GObject) newNode).getFieldName() + " deleted!";
                                }
                            });
                        }
                    });
                }
            }
        });

        destination.getChildren().add(newNode);

        if (settingsNode == null) {
            if (newNode instanceof Circle) {
                newNode.setLayoutX((newNode.getLayoutBounds().getWidth() / 2) + 4);
                newNode.setLayoutY((newNode.getLayoutBounds().getWidth() / 2) + 4);
            } else {
                newNode.setLayoutX(newNode.getLayoutX() + 4);
                newNode.setLayoutY(newNode.getLayoutY() + 4);
            }
        }

        if (x > 0 && y > 0) {
            newNode.setLayoutX(x);
            newNode.setLayoutY(y);
        }

        // Finally, let the history manager know this new thing has happened.
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void restore() {
                destination.getChildren().add(newNode);
                selectionManager.updateSelected(newThing);
            }

            @Override
            public void revert() {
                destination.getChildren().remove(newThing);
                selectionManager.clearSelection();
            }

            @Override
            public String getAppearance() {
                return newThing.getClass().getSuperclass().getSimpleName() + " added!";
            }
        });
    }

    private void dealWithPane(final Pane newThing) {

        newThing.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                t.acceptTransferModes(TransferMode.ANY);
                Rectangle highlight = view.middleTabPane.highlight;
                highlight.setVisible(true);
                double nodeX = 0;
                double nodeY = 0;
                Node newThing2 = newThing;
                while (!(newThing2 instanceof GUIObject)) {
                    nodeX += newThing2.getLayoutX();
                    nodeY += newThing2.getLayoutY();
                    newThing2 = newThing2.getParent();
                }
                Bounds bounds = newThing.getLayoutBounds();
                double nodeW = bounds.getWidth();
                double nodeH = bounds.getHeight();
                highlight.setLayoutX(nodeX - 4);
                highlight.setLayoutY(nodeY - 4);
                highlight.setWidth(nodeW + 8);
                highlight.setHeight(nodeH + 8);
            }
        });

        newThing.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                view.middleTabPane.highlight.setVisible(false);
            }
        });

        newThing.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                t.consume();
                ComponentMenuItem cmj = view.leftPanel.leftList.getSelectionModel().getSelectedItem();
                ComponentSettings componentSettings = cmj.getComponentSettings();
                if (componentSettings != null) {
                    GObject newnewThing = null;

                    historyManager.pause();
                    try {
                        Class panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                        Constructor constructor = panelPropertyClass.getConstructor();
                        newnewThing = (GObject) constructor.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final GObject newGObj = (GObject) newnewThing;

                    addGObject(newGObj, componentSettings, view, viewListeners, null, (int) t.getX(), (int) t.getY(), newThing);
                    //newThing.getChildren().add((Node) newGObj);
                    historyManager.unpause();
                    historyManager.addHistory(new HistoryItem() {
                        @Override
                        public void restore() {
                            newThing.getChildren().add((Node) newGObj);
                            selectionManager.updateSelected(newGObj);
                        }

                        @Override
                        public void revert() {
                            newThing.getChildren().remove(newGObj);
                            selectionManager.clearSelection();
                        }

                        @Override
                        public String getAppearance() {
                            return newThing.getClass().getSuperclass().getSimpleName() + " > " + newGObj.getClass().getSuperclass().getSimpleName() + " added!";
                        }
                    });
                }
                view.leftPanel.leftList.getSelectionModel().select(-1);
            }
        });
    }

    private void addPaneListChangeListener(final Pane ap, final TreeItem ti) {
        ap.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> change) {
                TreeItem<HierarchyTreeItem> root = ti;
                root.getChildren().clear();

                addPaneChildrenToHierarchy(ap, root);
            }
        });
    }

    private void addPaneChildrenToHierarchy(Pane pane, TreeItem root) {
        ObservableList<Node> nodes = pane.getChildren();
        // Add backwards so that they appear in the correct order
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node curNode = nodes.get(i);
            TreeItem ti = new TreeItem<>(new HierarchyTreeItem((GObject) curNode, view, selectionManager, historyManager));
            root.getChildren().add(ti);
            if (curNode instanceof Pane) {
                addPaneChildrenToHierarchy((Pane) curNode, ti);//Recurse to continue adding all children, grandkids etc to hierarchy pane
                addPaneListChangeListener((Pane) curNode, ti);// Add listener to any panes
            }
            ti.setExpanded(true);
        }
    }

    private static class LeftListCellFactory extends ListCell<ComponentMenuItem> {

        public LeftListCellFactory(final View view) {
            super();
            final ObjectProperty<Cursor> cp = this.cursorProperty();
            this.setOnMouseMoved(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    cp.setValue(Cursor.MOVE);
                }
            });

            this.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if (view.leftPanel.leftList.getSelectionModel().getSelectedItem() != null) {
                        Dragboard db = view.leftPanel.leftList.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent cc = new ClipboardContent();
                        cc.putString("");
                        db.setContent(cc);
                        t.consume();
                    }
                }
            });
        }

        @Override
        public void updateItem(ComponentMenuItem cmi, boolean empty) {
            super.updateItem(cmi, empty);
            if (!empty && cmi != null) {
                setText(cmi.getText());
                setGraphic(cmi.getGraphic());
            } else {
                setText(null);
                setGraphic(null);
                return;
            }
        }
    }
}