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
import bdl.model.history.HistoryManager;
import bdl.model.history.update.HistoryItemDescription;
import bdl.model.selection.SelectionListener;
import bdl.model.selection.SelectionManager;
import bdl.view.LogWindow;
import bdl.view.View;
import bdl.view.left.ComponentMenuItem;
import bdl.view.left.hierarchy.HierarchyTreeItem;
import bdl.view.right.PropertyEditPane;
import bdl.view.right.history.HistoryPanelItem;
import bluej.extensions.BClass;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureClassLoader;
import java.util.*;

public class Controller {

    private View view;
    private ComponentSettingsStore componentSettingsStore;
    private ViewListeners viewListeners;
    private ArrayList<String> fieldNames;
    private HistoryManager historyManager;
    private SelectionManager selectionManager;
    private Interface blueJInterface;
    private boolean isOpeningFile = false;
    private LogWindow logWindow;
    private List<String> oldCode;
    private List<String> moddedCode;
    private Patch<String> modsOnOldCode;

    public Controller(View view, ComponentSettingsStore componentSettingsStore, Interface blueJInterface) {
        this.view = view;
        this.componentSettingsStore = componentSettingsStore;
        this.blueJInterface = blueJInterface;
        fieldNames = new ArrayList<>();
        historyManager = new HistoryManager();
        selectionManager = new SelectionManager();
        viewListeners = new ViewListeners(historyManager, selectionManager);


        logWindow = new LogWindow(LabelGrabber.getLabel("logwindow.title"));
        System.setErr(new PrintStream(logWindow.getOutputErrStream()));
        System.setOut(new PrintStream(logWindow.getOutputStream()));

        System.out.println("Controller initializing");

        setupLeftPanel();
        setupMiddlePanel();
        setupRightPanel();
        setupTopPanel();
        setupAutoSave();

        BClass target = blueJInterface.getTarget();
        try {
            File classFile = target.getClassFile();
            String fileName = classFile.getName();
            Path fxmlpath = classFile.toPath().resolveSibling(fileName.substring(0, fileName.lastIndexOf(".")) + ".fxml");
            openFile(fxmlpath.toFile());
        } catch (ProjectNotOpenException | PackageNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Controller initialized");
    }

    private void setupTopPanel() {
        final Stage stage = view.getStage();
        // File > Load File
        view.topPanel.mItmLoadFile.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            if (blueJInterface != null) {
                fileChooser.setInitialDirectory(blueJInterface.getWorkingDirectory());
            }
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
            fileChooser.getExtensionFilters().add(filter);

            File file = fileChooser.showOpenDialog(view.getStage());

            openFile(file);
        });
        // File > Save File
        view.topPanel.mItmSaveFile.setOnAction(actionEvent -> saveFile());
        // File > Close
        view.topPanel.mItmClose.setOnAction(actionEvent -> stage.close());
        // File > Make Full Screen
        view.topPanel.mItmFullScreen.setOnAction(actionEvent -> {
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
                view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.enable.text"));
            } else {
                stage.setFullScreen(true);
                view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.disable.text"));
            }
        });

        //Add HistoryListener for the Undo/Redo menu items in the Edit menu
        historyManager.addHistoryListener(historyUpdate -> {
            //Undo MenuItem
            if (historyUpdate.canUndo()) {
                view.topPanel.mItmUndo.setDisable(false);
                view.topPanel.mItmUndo.setOnAction(actionEvent -> historyManager.updateTo(historyUpdate.getCurrentIndex() - 1));
            } else {
                view.topPanel.mItmUndo.setDisable(true);
            }

            //Redo MenuItem
            if (historyUpdate.canRedo()) {
                view.topPanel.mItmRedo.setDisable(false);
                view.topPanel.mItmRedo.setOnAction(actionEvent -> historyManager.updateTo(historyUpdate.getCurrentIndex() + 1));
            } else {
                view.topPanel.mItmRedo.setDisable(true);
            }
        });

        //Edit Menu > Delete button functionality
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(final GObject gObject) {
                view.topPanel.mItmDelete.setDisable(false);
                view.topPanel.mItmDelete.setOnAction(actionEvent -> {
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
                });
                view.topPanel.mItmClearAll.setDisable(false);
                view.topPanel.mItmClearAll.setOnAction(actionEvent -> {
                    final List<Node> list = new ArrayList<>(view.middleTabPane.viewPane.getChildren());
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
                t -> {
                    if (view.topPanel.mItmHierarchy.isSelected()) {
                        view.leftPanel.getItems().add(view.leftPanel.hierarchyTitledPane);
                        view.leftPanel.setDividerPosition(0, 0.6);
                    } else {
                        view.leftPanel.getItems().remove(view.leftPanel.hierarchyTitledPane);
                    }
                });
        // View > Show History
        view.topPanel.mItmHistory.setOnAction(
                t -> toggleHistory());
        // View > Show Log
        view.topPanel.mItemShowLog.setOnAction(t -> logWindow.show());

        view.topPanel.mItmAbout.setOnAction(
                new EventHandler<>() {
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
        view.leftPanel.leftList.setCellFactory(list -> new LeftListCellFactory(view));


        for (ComponentSettings componentSettings : componentSettingsStore.getComponents()) {
            String type = componentSettings.getType();
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/" + componentSettings.getIcon())));
            view.leftPanel.leftList.getItems().add(new ComponentMenuItem(type, icon, componentSettings));
        }

        view.leftPanel.leftList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                ComponentSettings componentSettings = view.leftPanel.leftList.getSelectionModel().getSelectedItem().getComponentSettings();
                if (componentSettings != null) {
                    GObject newThing = createNewGObject(componentSettings);
                    addGObject(newThing, componentSettings, view, viewListeners, null, -1, -1, view.middleTabPane.viewPane);
                    historyManager.unpause();
                }
                view.leftPanel.leftList.getSelectionModel().select(-1);
            }
        });

        view.leftPanel.hierarchyPane.treeRoot = new TreeItem<>(new HierarchyTreeItem(view.middleTabPane.viewPane, view, selectionManager, historyManager));
        view.leftPanel.hierarchyPane.treeView.setRoot(view.leftPanel.hierarchyPane.treeRoot);
        view.leftPanel.hierarchyPane.treeRoot.setExpanded(true);
        view.leftPanel.hierarchyPane.treeView.setShowRoot(true);

        view.leftPanel.hierarchyPane.treeView.setOnMousePressed(mouseEvent -> {
            TreeItem<HierarchyTreeItem> item = view.leftPanel.hierarchyPane.treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                selectionManager.updateSelected(item.getValue().getGObject());
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

    private GObject createNewGObject(ComponentSettings componentSettings) {
        GObject newThing = null;

        historyManager.pause();
        try {
            Class<?> panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
            Constructor<?> constructor = panelPropertyClass.getConstructor();
            newThing = (GObject) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newThing;
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

        view.middleTabPane.viewPane.setOnMousePressed(mouseEvent -> {
            view.middleTabPane.viewPane.requestFocus();
            selectionManager.clearSelection();
            mouseEvent.consume();
        });

        view.middleTabPane.codeTab.setOnSelectionChanged(event -> {
            if (view.middleTabPane.codeTab.isSelected()) {
                selectionManager.clearSelection();
                view.middleTabPane.codePane.setText(generateJavaCode());
            }
        });
        view.middleTabPane.previewTab.setOnSelectionChanged(event -> {
            //previewCompile();
            try {
                generateInMemoryPreview();
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        });

        view.middleTabPane.viewPane.setOnDragOver(t -> t.acceptTransferModes(TransferMode.ANY));

        view.middleTabPane.viewPane.setOnDragDropped(t -> {
            ComponentMenuItem cmj = view.leftPanel.leftList.getSelectionModel().getSelectedItem();
            ComponentSettings componentSettings = cmj.getComponentSettings();
            if (componentSettings != null) {
                GObject newThing = createNewGObject(componentSettings);

                addGObject(newThing, componentSettings, view, viewListeners, null, (int) t.getX(), (int) t.getY(), view.middleTabPane.viewPane);
                historyManager.unpause();
            }
            view.leftPanel.leftList.getSelectionModel().select(-1);
        });
    }

    private void generateInMemoryPreview() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (view.middleTabPane.previewTab.isSelected()) {
            String cname = view.middleTabPane.viewPane.getClassName();
            String code = generateJavaCode();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Keinen Compiler gefunden!");
                alert.showAndWait();
                throw new RuntimeException("No Compiler found");
            }
            URI uri = URI.create("string:////" + cname.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);

            SimpleJavaFileObject fileObject = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return code;
                }
            };
            Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(fileObject);
            JavaFileManager fileManager = new ForwardingJavaFileManager<JavaFileManager>(compiler.getStandardFileManager(null, Locale.getDefault(), StandardCharsets.UTF_8)) {
                HashMap<String, ByteArrayOutputStream> byteStreams = new HashMap<>();

                @Override
                public ClassLoader getClassLoader(Location location) {
                    return new SecureClassLoader() {
                        @Override
                        protected Class<?> findClass(String name) {
                            ByteArrayOutputStream outputStream = byteStreams.get(name);
                            if (outputStream == null)
                                return null;
                            byte[] b = outputStream.toByteArray();
                            return super.defineClass(name, b, 0, b.length);
                        }
                    };
                }

                @Override
                public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
                    return new SimpleJavaFileObject(URI.create("string:////" + className.replace('.', '/') + kind.extension), kind) {
                        @Override
                        public OutputStream openOutputStream() {
                            ByteArrayOutputStream outputStream = byteStreams.get(className);
                            if (outputStream == null) {
                                outputStream = new ByteArrayOutputStream();
                                byteStreams.put(className, outputStream);
                            }
                            return outputStream;
                        }
                    };
                }
            };

            JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
            if (compilationTask.call()) {
                ClassLoader classLoader = fileManager.getClassLoader(null);
                Class<?> guiClass = classLoader.loadClass(cname);
                Method main = guiClass.getMethod("start", Stage.class);
                Constructor<?> constructor = guiClass.getConstructor((Class<?>) null);
                main.invoke(constructor.newInstance(), new Stage());
            }
        }
        view.middleTabPane.getSelectionModel().select(0);
    }

    private void setupRightPanel() {

        // Add selection handlers for the Property Edit Pane
        selectionManager.addSelectionListener(new SelectionListener() {
            @Override
            public void updateSelected(final GObject gObject) {
                // Yes, it really does make no sense to put this in a Platform.runLater,
                // and removing and readding the splitpane makes no sense, but it fixes
                // the panel not showing properties when loaded from BlueJ...
                Platform.runLater(() -> {
                    view.rightPanel.propertyScroll.setContent(gObject.getPEP());
                    view.rightPanel.rightSplitPaneTop.getChildren().clear();
                    view.rightPanel.rightSplitPaneTop.getChildren().add(view.rightPanel.propertyScroll);
                });

            }

            @Override
            public void clearSelection() {
                view.rightPanel.propertyScroll.setContent(new PropertyEditPane());
            }
        });

        // Add history handlers for the History Pane
        historyManager.addHistoryListener(historyUpdate -> {
            ObservableList<HistoryPanelItem> panelItems = view.rightPanel.historyPanel.getItems();
            panelItems.clear();

            for (HistoryItemDescription item : historyUpdate.getHistory()) {
                panelItems.add(new HistoryPanelItem(item));
            }

            view.rightPanel.historyPanel.getSelectionModel().select(historyUpdate.getCurrentIndex());
            view.rightPanel.historyPanel.scrollTo(historyUpdate.getCurrentIndex());
        });

        view.rightPanel.historyPanel.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                HistoryPanelItem historyItemDescription = view.rightPanel.historyPanel.getSelectionModel().getSelectedItem();
                if (historyItemDescription != null) {
                    historyManager.updateTo(historyItemDescription.getIndex());
                }
            }
        });

    }

    /**
     * Adds a HistoryListener and saves whenever a change is made.
     * Only effective when running with BlueJ.
     */
    private void setupAutoSave() {
        if (blueJInterface != null) {
            historyManager.addHistoryListener(historyUpdate -> saveFile());
        }
    }

    /**
     * Open the specified FXML file.
     *
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
                                Class<?> componentClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                                Constructor<?> constructor = componentClass.getConstructor();
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

    /**
     * Reset the workspace.
     */
    public void newFile() {
        isOpeningFile = true;
        view.middleTabPane.viewPane.getChildren().clear();
        selectionManager.clearSelection();
        historyManager.clearHistory();
        oldCode = Arrays.asList(generateJavaCode().split("\\R"));
        try {
            moddedCode = Files.readAllLines(blueJInterface.getOpenGUIFile().toPath());
            modsOnOldCode = DiffUtils.diff(oldCode, moddedCode);
        } catch (IOException | DiffException e) {
            e.printStackTrace();
        }
        isOpeningFile = false;
    }

    /**
     * Reset the workspace and set the new GUI's name to className.
     *
     * @param className the desired name of the new GUI
     */
    public void newFile(String className) {
        newFile();
        view.middleTabPane.viewPane.setClassName(className);
        view.middleTabPane.viewPane.setGUITitle(className);
    }

    /**
     * Save file to FXML. If running with BlueJ, output Java code to file.
     */
    private void saveFile() {
        if (isOpeningFile) {
            return;
        }
        File file;
        if (blueJInterface == null) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
            fileChooser.getExtensionFilters().add(filter);

            file = fileChooser.showSaveDialog(view.getStage());
        } else {
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
        if ((blueJInterface != null) && (blueJInterface.isEditingGUI())) {
            // Write java code to GUI java file.
            try {
                FileWriter fileWriter = new FileWriter(blueJInterface.getOpenGUIFile());
                fileWriter.write(patchOutput(generateJavaCode());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Mark the file as dirty in BlueJ
            blueJInterface.markAsDirty();
        }
    }


    private String patchOutput(String newCode) {
        List<String> newCodeList = Arrays.asList(newCode.split("\\R"));
        List<String> patchedText = new LinkedList<>();
        try {
            patchedText = DiffUtils.patch(newCodeList, modsOnOldCode);
        } catch (PatchFailedException e) {
            System.out.println("Too much changes detected, Trying Variant B");
            e.printStackTrace();
            try {
                Patch<String> rawcodediff = DiffUtils.diff(oldCode, newCodeList);
                patchedText = DiffUtils.patch(moddedCode, rawcodediff);
            } catch (DiffException | PatchFailedException ex) {
                System.out.println("Variant B Failed");
                ex.printStackTrace();
            }
        }
        return String.join(System.lineSeparator(), patchedText);
    }

    /**
     * Toggle the history panel's visibility.
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

    /**
     * Set the name of the GUI class. (BlueJ interface functionality.)
     */
    public void setClassName(String className) {
        view.middleTabPane.viewPane.setClassName(className);
    }

    /**
     * Make the stage visible. (BlueJ interface functionality.)
     */
    public void showStage() {
        view.getStage().show();
        view.getStage().toFront();
    }

    /**
     * Make the stage invisible. (BlueJ interface functionality.)
     */
    public void hideStage() {
        view.getStage().hide();
    }

    /**
     * Generates the full Java code.
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

        newNode.layoutBoundsProperty().addListener((ov, t, t1) -> selectionManager.updateSelected((GObject) newNode));

        newNode.layoutXProperty().addListener((ov, t, t1) -> selectionManager.updateSelected((GObject) newNode));

        newNode.layoutYProperty().addListener((ov, t, t1) -> selectionManager.updateSelected((GObject) newNode));

        newNode.setOnMousePressed(mouseEvent -> {
            selectionManager.updateSelected((GObject) newNode);
            viewListeners.onMousePressed(newNode, mouseEvent);
            mouseEvent.consume();//Stops the mouseEvent falling through to the viewPane which would clear selection
        });
        newNode.setOnMouseReleased(mouseEvent -> viewListeners.onMouseReleased(newNode, mouseEvent));
        newNode.setOnMouseDragged(mouseEvent -> {
            viewListeners.onMouseDragged(newNode, mouseEvent);
            selectionManager.updateSelected((GObject) newNode);
        });


        final ContextMenu nodePopUp = new ContextMenu();
        final MenuItem deletebutton = new MenuItem(LabelGrabber.getLabel("delete.node.text"));
        nodePopUp.getItems().add(deletebutton);
        newNode.addEventFilter(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton().equals(MouseButton.SECONDARY)) {
                nodePopUp.show(newNode, Side.RIGHT, 0, 0);
                deletebutton.setOnAction(t12 -> {
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
                });
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

        newThing.setOnDragOver(t -> {
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
        });

        newThing.setOnDragExited(t -> view.middleTabPane.highlight.setVisible(false));

        newThing.setOnDragDropped(t -> {
            t.consume();
            ComponentMenuItem cmj = view.leftPanel.leftList.getSelectionModel().getSelectedItem();
            ComponentSettings componentSettings = cmj.getComponentSettings();
            if (componentSettings != null) {
                GObject newnewThing = null;

                historyManager.pause();
                try {
                    Class<?> panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
                    Constructor<?> constructor = panelPropertyClass.getConstructor();
                    newnewThing = (GObject) constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final GObject newGObj = newnewThing;

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
                        assert newGObj != null;
                        return newThing.getClass().getSuperclass().getSimpleName() + " > " + newGObj.getClass().getSuperclass().getSimpleName() + " added!";
                    }
                });
            }
            view.leftPanel.leftList.getSelectionModel().select(-1);
        });
    }

    private void addPaneListChangeListener(final Pane ap, final TreeItem<HierarchyTreeItem> ti) {
        ap.getChildren().addListener((ListChangeListener<Node>) change -> {
            ti.getChildren().clear();
            addPaneChildrenToHierarchy(ap, ti);
        });
    }

    private void addPaneChildrenToHierarchy(Pane pane, TreeItem<HierarchyTreeItem> root) {
        ObservableList<Node> nodes = pane.getChildren();
        // Add backwards so that they appear in the correct order
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node curNode = nodes.get(i);
            TreeItem<HierarchyTreeItem> ti = new TreeItem<>(new HierarchyTreeItem((GObject) curNode, view, selectionManager, historyManager));
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
            this.setOnMouseMoved(t -> cp.setValue(Cursor.MOVE));

            this.setOnDragDetected(t -> {
                if (view.leftPanel.leftList.getSelectionModel().getSelectedItem() != null) {
                    Dragboard db = view.leftPanel.leftList.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("");
                    db.setContent(cc);
                    t.consume();
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
            }
        }
    }
}