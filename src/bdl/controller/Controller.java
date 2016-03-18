package bdl.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bdl.build.CodeGenerator;
import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.build.javafx.scene.control.GMenuBar;
import bdl.build.javafx.scene.control.GRadioButton;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettings;
import bdl.model.ComponentSettingsStore;
import bdl.model.ListenerProperty;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryListener;
import bdl.model.history.HistoryManager;
import bdl.model.history.update.HistoryItemDescription;
import bdl.model.history.update.HistoryUpdate;
import bdl.model.selection.SelectionListener;
import bdl.model.selection.SelectionManager;
import bdl.view.View;
import bdl.view.left.ComponentMenuItem;
import bdl.view.left.RasterPane;
import bdl.view.left.hierarchy.HierarchyTreeItem;
import bdl.view.right.PropertyEditPane;
import bdl.view.right.history.HistoryPanelItem;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.ProjectNotOpenException;
import di.blueJLink.Bezeichnertester;
import di.blueJLink.BlueJExporter;
import di.blueJLink.BlueJInterface;
import di.errorlog.Fehlerausgabe;
import di.inout.ExportJAVA;
import di.inout.LanguageChooser;
import di.mover.MoverSet;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Controller {

	private View view;
	private ComponentSettingsStore componentSettingsStore;
	private ViewListeners viewListeners;
	private static ArrayList<String> fieldNames = new ArrayList<>();

	private HistoryManager historyManager;
	private SelectionManager selectionManager;
	private BlueJInterface blueJInterface2;
	private boolean isOpeningFile = false;
	private MoverSet anfasser = new MoverSet();
	private Fehlerausgabe errorlog;

	public View getView() {
		return view;
	}

	public static ArrayList<String> getFieldNames() {
		return fieldNames;
	}

	public Controller(View view, ComponentSettingsStore componentSettingsStore, BlueJInterface blueJInterface2) {
		errorlog = new Fehlerausgabe();
		this.view = view;
		this.componentSettingsStore = componentSettingsStore;
		this.blueJInterface2 = blueJInterface2;
		historyManager = new HistoryManager();
		selectionManager = new SelectionManager();
		viewListeners = new ViewListeners(historyManager, selectionManager);
		setupLeftPanel();
		setupMiddlePanel();
		setupRightPanel();
		setupTopPanel();
		toggleHistory();
		view.middleTabPane.viewPane.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				selectionManager.updateSelected(view.middleTabPane.viewPane);
				// selectionManager.clearSelection();

				mouseEvent.consume();
			}
		});

	}

	BProject[] aktuelleBlueJProjekte;
	MenuItem[] bjpitemFXMLExport;
	MenuItem[] bjpitemJAVAExport;
	MenuItem[] bjpitemFXMLImport;

	private MenuItem[] generateMenuItemsForOpenBluejProjects(final Menu oben) {
		MenuItem[] bjpitem = null;
		oben.getItems().clear();

		if (blueJInterface2 != null) {
			aktuelleBlueJProjekte = blueJInterface2.getBlueJProjekte();
			if (aktuelleBlueJProjekte == null || aktuelleBlueJProjekte.length == 0) {
				return null;
			}
			MenuItem erstes = new MenuItem(LabelGrabber.getLabel("menu.bluej.openprojects"));
			erstes.setDisable(true);
			oben.getItems().add(erstes);
			bjpitem = new MenuItem[aktuelleBlueJProjekte.length];
			for (int i = 0; i < aktuelleBlueJProjekte.length; i++) {
				try {
					bjpitem[i] = new MenuItem(aktuelleBlueJProjekte[i].getName());
				} catch (ProjectNotOpenException e) {
					e.printStackTrace();
				}
				bjpitem[i].setOnAction(e -> handleBlueJOpenProjects(e));
				oben.getItems().add(bjpitem[i]);
			}
		}
		return bjpitem;
	}

	private void handleBlueJOpenProjects(ActionEvent e) {
		MenuItem mi = (MenuItem) e.getSource();
		if (blueJInterface2 != null) {
			BProject aktuellesProjekt = null;
			for (BProject bp : aktuelleBlueJProjekte) {
				try {
					if (bp.getName().equals(mi.getText())) {
						aktuellesProjekt = bp;
						break;
					}
				} catch (ProjectNotOpenException e1) {
					e1.printStackTrace();
				}
			}
			// JOptionPane.showMessageDialog(null,"Export"+abp);

			if (aktuellesProjekt != null) {
				for (MenuItem item : bjpitemJAVAExport) {
					if (mi == item) {
						exportJavaToBlueJ(aktuellesProjekt);
						break;
					}
				}
				for (MenuItem item : bjpitemFXMLImport) {
					if (mi == item) {
						importFXMLFromBlueJ(aktuellesProjekt);
						break;
					}
				}
				for (MenuItem item : bjpitemFXMLExport) {
					if (mi == item) {
						exportFXMLToBlueJ(aktuellesProjekt);
						break;
					}
				}
			}
		}
	}

	private void exportFXMLToBlueJ(BProject aktuellesProjekt) {
		if (aktuellesProjekt == null) {
			return;
		}
		try {
			saveFXMLFile(aktuellesProjekt.getDir());
		} catch (ProjectNotOpenException e) {
			e.printStackTrace();
		}
	}

	private void importFXMLFromBlueJ(BProject aktuellesProjekt) {
		if (aktuellesProjekt == null) {
			return;
		}
		FileChooser fileChooser = new FileChooser();
		try {
			fileChooser.setInitialDirectory(aktuellesProjekt.getDir());
		} catch (ProjectNotOpenException e) {
			e.printStackTrace();
		}
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
		fileChooser.getExtensionFilters().add(filter);
		File file = fileChooser.showOpenDialog(view.getStage());
		openFile(file);

	}

	/**
	 *
	 */
	public void exportJavaToBlueJ(BProject aktuellesBlueJProjekt) {

		if (blueJInterface2 == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle(LabelGrabber.getLabel("bluejlink.dialog.infoheader"));
			alert.setHeaderText(null);
			alert.setContentText(LabelGrabber.getLabel("bluejlink.error.attach"));
			alert.showAndWait();
			return;

		}
		BlueJ bluej = blueJInterface2.getBlueJ();
		// BProject aktuellesBlueJProjekt = blueJInterface.getBlueJProjekt();
		if (bluej == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText(LabelGrabber.getLabel("bluejlink.error.attach"));
			alert.showAndWait();

			return;
		}
		if (aktuellesBlueJProjekt == null) {
			try {
				aktuellesBlueJProjekt = bluej.getCurrentPackage().getProject();
			} catch (ProjectNotOpenException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(LabelGrabber.getLabel("bluejlink.dialog.infoheader"));
				alert.setHeaderText(null);
				alert.setContentText(LabelGrabber.getLabel("bluejlink.error.missingprojekt"));
				alert.showAndWait();
				return;
			}
		}

		if (bluej != null) {
			try {
				bluej.openProject(aktuellesBlueJProjekt.getDir());
			} catch (ProjectNotOpenException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(LabelGrabber.getLabel("bluejlink.dialog.infoheader"));
				alert.setHeaderText(null);
				alert.setContentText(LabelGrabber.getLabel("bluejlink.error.missingprojekt"));
				alert.showAndWait();
				return;
			}
			boolean inputOK = false;

			while (!inputOK) {

				TextInputDialog dialog = new TextInputDialog(LabelGrabber.getLabel("bluejlink.dialog.fieldname"));
				dialog.setTitle(LabelGrabber.getLabel("bluejlink.dialog.inputfieldnameheader"));
				dialog.setContentText(LabelGrabber.getLabel("bluejlink.dialog.inputfieldname") + ":");

				Optional<String> result = dialog.showAndWait();
				if (!result.isPresent()) {
					return;
				}

				if (!Bezeichnertester.variablenBezeichnerOK(result.get())) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle(LabelGrabber.getLabel("bluejlink.dialog.infoheader"));
					alert.setHeaderText(null);
					alert.setContentText(result.get() + ":" + LabelGrabber.getLabel("bluejlink.dialog.wrongfieldname"));
					alert.showAndWait();
				} else {
					inputOK = true;
					new BlueJExporter(aktuellesBlueJProjekt, result.get(), generateJavaCode(result.get(), false));
				}
			}

		}

	}

	private void setupTopPanel() {
		final Stage stage = view.getStage();

		view.topPanel.mntmNeuesProjekt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				blueJInterface2.erzeugeProjekt();
			}
		});
		view.topPanel.mntmProjektffnen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				blueJInterface2.oeffneProjekt();
			}
		});
		view.topPanel.menuBluej.addEventHandler(Event.ANY, e -> {
			if (e.getEventType().getName().equals("MENU_ON_SHOWN")) {
				bjpitemFXMLExport = generateMenuItemsForOpenBluejProjects(view.topPanel.mnModellInProjekt);
				bjpitemJAVAExport = generateMenuItemsForOpenBluejProjects(view.topPanel.mnClassInProjekt);
				bjpitemFXMLImport = generateMenuItemsForOpenBluejProjects(view.topPanel.mnModellAusProjekt);
			}
		});
		// File > Load File
		view.topPanel.mItmLoadFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
				fileChooser.getExtensionFilters().add(filter);
				File file = fileChooser.showOpenDialog(view.getStage());
				// Platform.runLater(()->{openFile(file);});
				openFile(file);
			}
		});
		// File > Save FXML File
		view.topPanel.mItmSaveFXMLFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				saveFile();
			}
		});
		// File > Save JAVA File
		view.topPanel.mItmSaveJAVAFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				saveJAVAFile();
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
		// View > Errorlog
		view.topPanel.mItmErrorlog.setOnAction(e -> showErrorlog());

		// Add HistoryListener for the Undo/Redo menu items in the Edit menu
		historyManager.addHistoryListener(new HistoryListener() {
			@Override
			public void historyUpdated(final HistoryUpdate historyUpdate) {
				// Undo MenuItem
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

				// Redo MenuItem
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

		// Edit Menu > Delete button functionality
		selectionManager.addSelectionListener(getDeleteSelectionListener());

		view.topPanel.mItmLanguage.setOnAction(e -> LanguageChooser.getLanguageByDialog());
		// View > Show Hierarchy
		view.topPanel.mItmHierarchy.setOnAction(new EventHandler<ActionEvent>() {
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
		view.topPanel.mItmHistory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				toggleHistory();
			}
		});

		view.topPanel.mItmAbout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				Stage stage = new Stage();
				GridPane pane = new GridPane();
				Label label = new Label(LabelGrabber.getLabel("about.text"));
				label.setMaxWidth(300);
				label.setWrapText(true);
				label.setFont(new Font(18));
				label.setTextAlignment(TextAlignment.CENTER);
				ImageView imageview = new ImageView(
						new Image(getClass().getResourceAsStream("/bdl/icons/BlueJ_Orange_64.png")));
				pane.add(imageview, 1, 1);
				pane.add(label, 1, 2);
				GridPane.setHalignment(imageview, HPos.CENTER);
				stage.setScene(new Scene(pane));
				stage.show();
			}
		});

	}

	private void showErrorlog() {
		if (errorlog != null) {
			errorlog.show();
		}
	}

	private void setupLeftPanel() {
		view.leftPanel.leftList
				.setCellFactory(new Callback<ListView<ComponentMenuItem>, ListCell<ComponentMenuItem>>() {
					@Override
					public ListCell<ComponentMenuItem> call(ListView<ComponentMenuItem> list) {
						return new LeftListCellFactory(view);
					}
				});

		for (ComponentSettings componentSettings : componentSettingsStore.getComponents()) {
			String type = componentSettings.getType();
			// System.out.println(type);
			ImageView icon = new ImageView(
					new Image(getClass().getResourceAsStream("/bdl/icons/" + componentSettings.getIcon())));
			view.leftPanel.leftList.getItems().add(new ComponentMenuItem(type, icon, componentSettings));
		}

		view.leftPanel.leftList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					ComponentSettings componentSettings = view.leftPanel.leftList.getSelectionModel().getSelectedItem()
							.getComponentSettings();
					if (componentSettings != null) {
						GObject newThing = null;

						historyManager.pause();
						try {
							Class panelPropertyClass = Class.forName("bdl.build." + componentSettings.getPackageName()
									+ ".G" + componentSettings.getType());
							Constructor constructor = panelPropertyClass.getConstructor();
							newThing = (GObject) constructor.newInstance();
						} catch (Exception e) {
							e.printStackTrace();
						}

						addGObject(newThing, componentSettings, view, viewListeners, null, -1, -1,
								view.middleTabPane.viewPane);
						historyManager.unpause();
					}
					view.leftPanel.leftList.getSelectionModel().select(-1);
				}
			}
		});

		view.leftPanel.hierarchyPane.treeRoot = new TreeItem<>(
				new HierarchyTreeItem(view.middleTabPane.viewPane, view, selectionManager, historyManager));
		view.leftPanel.hierarchyPane.treeView.setRoot(view.leftPanel.hierarchyPane.treeRoot);
		view.leftPanel.hierarchyPane.treeRoot.setExpanded(true);
		view.leftPanel.hierarchyPane.treeView.setShowRoot(true);

		view.leftPanel.hierarchyPane.treeView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				TreeItem<HierarchyTreeItem> item = view.leftPanel.hierarchyPane.treeView.getSelectionModel()
						.getSelectedItem();
				if (item != null) {
					selectionManager.updateSelected(item.getValue().getGObject());
					selectionManager.updateSelected(item.getValue().getGObject());

				}

			}
		});

		// Add listener to node list to update hierarchy pane
		addPaneListChangeListener(view.middleTabPane.viewPane, view.leftPanel.hierarchyPane.treeRoot);

		// Add selection handlers for Hierarchy Pane
		selectionManager.addSelectionListener(getHierachyPanelSelectionistener());

	}

	private void setupMiddlePanel() {
		for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
			ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
			try {
				if (componentSettings.getType().equals(view.middleTabPane.viewPane.getNodeClassName())) {
					historyManager.pause();

					PropertyEditPane propertyEditPane = new PropertyEditPane(view.middleTabPane.viewPane,
							componentSettings, fieldNames, view.middleTabPane.viewPane, null, historyManager);

					view.middleTabPane.viewPane
							.setPEP(new PropertyEditPane(view.middleTabPane.viewPane, componentSettings));
					historyManager.unpause();

					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		selectionManager.addSelectionListener(getMiddlePanelSelectionistener());

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
					view.middleTabPane.codePane.setText(generateJavaCode("", false));
				}
			}
		});
		view.middleTabPane.previewTab.setOnSelectionChanged(e -> generatePreviewInMemory());

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
						Class panelPropertyClass = Class.forName(
								"bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
						Constructor constructor = panelPropertyClass.getConstructor();
						newThing = (GObject) constructor.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					int xpos = RasterPane.getRasterPosX(t.getX());
					int ypos = RasterPane.getRasterPosY(t.getY());

					addGObject(newThing, componentSettings, view, viewListeners, null, xpos, ypos,
							view.middleTabPane.viewPane);
					selectionManager.updateSelected(newThing);
					historyManager.unpause();
				}
				view.leftPanel.leftList.getSelectionModel().select(-1);
			}
		});
	}

	/**
	 * based on
	 * http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.
	 * html by Rekha Kumari (June 2011)
	 */
	private void generatePreviewInMemory() {

		if (view.middleTabPane.previewTab.isSelected()) {
			String className = view.middleTabPane.viewPane.getClassName();
			String javacode = generateJavaCode("", true);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText(LabelGrabber.getLabel("controller.nopreview") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview2") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview3") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview4"));
				alert.showAndWait();
				throw new RuntimeException("Jar could not be created as Java version requires javac.\n"
						+ "May solve the problem: Install JDK and copy tools.jar in JDK/lib/ into _all_ existing JRE/lib/");
			}
			final JavaFileManager fileManager;
			URI uri = null;
			uri = URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension);
			if (uri != null) {
				final SimpleJavaFileObject file = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
					@Override
					public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
						return javacode;
					}
				};
				Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
				fileManager = new ForwardingJavaFileManager<JavaFileManager>(
						compiler.getStandardFileManager(null, null, null)) {

					final private Map<String, ByteArrayOutputStream> byteStreams = new HashMap<>();

					@Override
					public ClassLoader getClassLoader(final Location location) {

						return new SecureClassLoader() {

							@Override
							protected Class<?> findClass(final String className) throws ClassNotFoundException {

								final ByteArrayOutputStream bos = byteStreams.get(className);
								if (bos == null) {
									return null;
								}
								final byte[] b = bos.toByteArray();
								return super.defineClass(className, b, 0, b.length);
							}
						};
					}

					@Override
					public JavaFileObject getJavaFileForOutput(final Location location, final String className,
							final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {

						return new SimpleJavaFileObject(
								URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {

							@Override
							public OutputStream openOutputStream() throws IOException {

								ByteArrayOutputStream bos = byteStreams.get(className);

								if (bos == null) {
									bos = new ByteArrayOutputStream();
									byteStreams.put(className, bos);
								}
								return bos;
							}
						};
					}
				};

				final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null,
						compilationUnits);

				boolean success = task.call();

				if (success) {
					final ClassLoader classLoader = fileManager.getClassLoader(null);
					try {
						final Class<?> guiClass = classLoader.loadClass(className);
						Method main = guiClass.getMethod("start", Stage.class);
						main.invoke(guiClass.newInstance(), new Stage());
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			view.middleTabPane.getSelectionModel().select(0);
		}
	}

	private void generatePreview() {
		if (view.middleTabPane.previewTab.isSelected()) {

			// Write .java file
			// Make temporary space in BlueJ user dir for compilation.
			File fileJava;
			File fileClass;

			fileJava = new File(view.middleTabPane.viewPane.getClassName() + ".java");
			fileClass = new File(view.middleTabPane.viewPane.getClassName() + ".class");

			try {
				BufferedOutputStream cssOutput = new BufferedOutputStream(new FileOutputStream(fileJava));
				cssOutput.write(generateJavaCode("", true).getBytes());
				cssOutput.flush();
				cssOutput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Compile class
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText(LabelGrabber.getLabel("controller.nopreview") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview2") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview3") + "\n"
						+ LabelGrabber.getLabel("controller.nopreview4"));
				alert.showAndWait();
				throw new RuntimeException("Jar could not be created as Java version requires javac.\n"
						+ "May solve the problem: Install JDK and copy tools.jar in JDK/lib/ into _all_ existing JRE/lib/");
			}
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
					.getJavaFileObjectsFromFiles(Arrays.asList(fileJava));

			compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

			try {
				fileManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Load & run class
			try {
				Class guiClass;

				URL[] urls = new URL[] { new File(".").toURI().toURL() };
				URLClassLoader ucl = new URLClassLoader(urls);
				guiClass = Class.forName(view.middleTabPane.viewPane.getClassName(), true, ucl);

				Method main = guiClass.getMethod("start", Stage.class);
				Object obj = guiClass.newInstance();
				main.invoke(obj, new Stage());

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Delete created files
			boolean fJd = fileJava.delete();
			boolean fCd = fileClass.delete();
			// Alert alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("Files deleted?"+fJd+" "+fCd);
			// alert.showAndWait();
			view.middleTabPane.getSelectionModel().select(0);
		}
	}

	private SelectionListener getDeleteSelectionListener() {
		return new SelectionListener() {
			@Override
			public void updateSelected(final GObject gObject) {
				view.topPanel.mItmDelete.setDisable(false);
				view.topPanel.mItmDelete.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						final Pane contentpane;
						if (((Node) gObject).getParent() instanceof Pane) {
							contentpane = (Pane) ((Node) gObject).getParent();
						} else {
							return;
						}
						contentpane.getChildren().remove(gObject);
						// view.middleTabPane.viewPane.getChildren().remove(gObject);
						selectionManager.clearSelection();
						historyManager.addHistory(new HistoryItem() {
							@Override
							public void restore() {
								contentpane.getChildren().remove(gObject);
								// view.middleTabPane.viewPane.getChildren().remove(gObject);
								selectionManager.clearSelection();
							}

							@Override
							public void revert() {
								contentpane.getChildren().add((Node) gObject);
								// view.middleTabPane.viewPane.getChildren().add((Node)
								// gObject);
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
						// don't remove mover!
						final List<Node> list = new ArrayList<>();
						for (Node n : view.middleTabPane.viewPane.getChildren()) {
							if (n instanceof GObject) {
								list.add(n);
							}
						}

						for (Node n : list) {
							view.middleTabPane.viewPane.getChildren().remove(n);
						}

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
				// view.topPanel.mItmClearAll.setDisable(true);
			}
		};
	}

	private SelectionListener getHierachyPanelSelectionistener() {
		return new SelectionListener() {
			@Override
			public void updateSelected(GObject gObject) {
				update(gObject.getFieldName(), view.leftPanel.hierarchyPane.treeRoot);
			}

			private void update(String fieldName, TreeItem<HierarchyTreeItem> treeRoot) {
				if (treeRoot.getValue().getGObject().getFieldName().equals(fieldName)) {
					view.leftPanel.hierarchyPane.treeView.getSelectionModel().select(treeRoot);
				} else {

					for (TreeItem<HierarchyTreeItem> ti : treeRoot.getChildren()) {
						GObject gObject = ti.getValue().getGObject();
						if (gObject.getFieldName().equals(fieldName)) {
							view.leftPanel.hierarchyPane.treeView.getSelectionModel().select(ti);

						} else if (gObject instanceof Pane) {
							update(fieldName, ti);
						}

					}
				}
			}

			@Override
			public void clearSelection() {
				// System.out.println("ClearSelection");
				view.leftPanel.hierarchyPane.treeView.getSelectionModel().select(-1);

			}
		};
	}

	private SelectionListener getMiddlePanelSelectionistener() {
		return new SelectionListener() {
			@Override
			public void updateSelected(GObject gObject) {
				Node node = (Node) gObject;
				Rectangle outline = view.middleTabPane.outline;
				if (gObject instanceof GUIObject) {
					outline.setVisible(false);
					if (anfasser != null) {
						anfasser.setVisible(false);
					}
				} else {
					outline.setVisible(true);

					if (anfasser != null) {

						anfasser.setNode((Node) gObject, (Pane) node.getParent());

					}
					double nodeX = 0;
					double nodeY = 0;
					Node node2 = node;
					while (!(node2 instanceof GUIObject)) {
						nodeX += node2.getLayoutX();
						nodeY += node2.getLayoutY();
						node2 = node2.getParent();
					}

					// double nodeX = node.getParent().getLayoutX() +
					// node.getLayoutX();
					// double nodeY = node.getParent().getLayoutY() +
					// node.getLayoutY();

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
			}

			@Override
			public void clearSelection() {
				view.middleTabPane.outline.setVisible(false);
				if (anfasser != null) {
					// anfasser.removeFromParent();
					anfasser.setVisible(false);
				}
				// if (anfasser!=null){anfasser.setzeSichtbar(false);}
			}
		};
	}

	private SelectionListener getRightPanelSelectionistener() {
		return new SelectionListener() {
			@Override
			public void updateSelected(final GObject gObject) {
				// Following may be obsolete now:
				// Yes, it really does make no sense to put this in a
				// Platform.runLater,
				// and removing and readding the splitpane makes no sense, but
				// it fixes
				// the panel not showing properties when loaded from BlueJ...
				/*
				 * Platform.runLater(new Runnable() {
				 *
				 * @Override public void run() {
				 * view.rightPanel.propertyScroll.setContent(gObject.getPEP());
				 * view.rightPanel.rightSplitPaneTop.getChildren().clear();
				 * view.rightPanel.rightSplitPaneTop.getChildren().add(view.
				 * rightPanel.propertyScroll); } });
				 */

				view.rightPanel.propertyScroll.setContent(gObject.getPEP());
			}

			@Override
			public void clearSelection() {
				view.rightPanel.propertyScroll.setContent(new PropertyEditPane());
			}
		};
	}

	private void setupRightPanel() {
		// Add selection handlers for the Property Edit Pane
		selectionManager.addSelectionListener(getRightPanelSelectionistener());

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
					HistoryPanelItem historyItemDescription = view.rightPanel.historyPanel.getSelectionModel()
							.getSelectedItem();
					if (historyItemDescription != null) {
						historyManager.updateTo(historyItemDescription.getIndex());
					}
				}
			}
		});

	}

	private void openFileAsXML(File file,HashMap<String,String> rbTn) {
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(file);
			document.normalize();
			Element root = document.getDocumentElement();
			readXML(root,rbTn);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	private void readXML(Element e, HashMap<String,String> rbTn) {
		String nodename = e.getNodeName();
		System.out.println(e.getNodeName());
		if (e != null) {
			if (nodename.equals("RadioButton")) {
				String rbname = e.getAttribute("fx:id");
//				System.out.println(rbname);
				String toggleName = e.getAttribute("toggleGroup");
//				System.out.println(toggleName);
				if (rbname!=null && !rbname.isEmpty()){
					if (toggleName!=null && !toggleName.isEmpty()){
						rbTn.put(rbname, toggleName);
					}
				}
			}

			NodeList childs = e.getChildNodes();
			for (int k = 0; k < childs.getLength(); k++) {
				if (childs.item(k) instanceof Element) {
					readXML((Element) childs.item(k),rbTn);
				}
			}
		}
	}

	/**
	 * Open the specified FXML file.
	 *
	 * @param file
	 *            the File referencing the FXML file.
	 */
	public void openFile(File file) {
		HashMap<String,String> radiobuttonsToggleGroupNames = new HashMap<>();
		isOpeningFile = true;
		Parent parent = null;

		if (file != null) {
			openFileAsXML(file,radiobuttonsToggleGroupNames);
			view.middleTabPane.viewPane.getChildren().clear();
			selectionManager.clearSelection();
			// selectionManager.setEnabled(false);
			historyManager.clearHistory();
			fieldNames.clear();
			fieldNames.add("root");

			try {
				parent = FXMLLoader.load(file.toURI().toURL());
				// ausgabe(parent);
				GUIHelper.setBounds(view.middleTabPane.viewPane, view.middleTabPane.viewPaneDecorator,
						parent.prefWidth(0), parent.prefHeight(0));
				String className = parent.getId();
				if (className != null && !className.isEmpty()) {
					view.middleTabPane.viewPane.setClassName(className);
				}
				for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
					ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
					try {
						if (componentSettings.getType().equals(parent.getClass().getSimpleName())) {
							historyManager.pause();
							view.middleTabPane.viewPane
									.setPEP(new PropertyEditPane(view.middleTabPane.viewPane, componentSettings));
							historyManager.unpause();
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				for (Node node : parent.getChildrenUnmodifiable()) {

					for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
						ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
						try {
							if (componentSettings.getType().equals(node.getClass().getSimpleName())) {
								historyManager.pause();

								Class componentClass = Class.forName("bdl.build." + componentSettings.getPackageName()
										+ ".G" + componentSettings.getType());
								Constructor constructor = componentClass.getConstructor();
								GObject newThing = (GObject) constructor.newInstance();
								// System.out.println("NewThing "
								// +newThing.getClass());
								newThing.setFieldName(node.getId());

								addGObjectRec(newThing, componentSettings, view, viewListeners, node,
										view.middleTabPane.viewPane,radiobuttonsToggleGroupNames);
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

		// Platform.runLater(() -> {
		// visitTree(view.middleTabPane.viewPane);
		// });// otherwise to early for mover (seems now to be unnecessary)
		isOpeningFile = false;
		// ausgabe(parent);
	}

	private void visitTree(Pane parent) {
		if (parent == null) {
			return;
		}

		for (Node node : parent.getChildren()) {
			if (node instanceof GObject) {
				node.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
					@Override
					public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {

						if (selectionManager.getCurrentlySelected() == node) {
							selectionManager.updateSelected((GObject) node);
						} else {
							if (node instanceof Pane) {
								((Pane) node).setPrefWidth(t1.getWidth());
								((Pane) node).setPrefHeight(t1.getWidth());
							}
						}
					}
				});
				if (node instanceof Pane) {
					visitTree((Pane) node);
				}
			}
		}

	}

	// for debugging only
	private void ausgabe(Parent parent) {
		if (parent == null) {
			return;
		}
		System.out.println(parent);
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodeausgabe(node, "  ");
		}

	}

	private void nodeausgabe(Node node, String string) {
		System.out.println(string + node);
		if (node instanceof Pane) {
			for (Node cnode : ((Pane) node).getChildren()) {
				nodeausgabe(cnode, string + "  ");
			}
		}
	}

	/**
	 * Reset the workspace.
	 */
	public void newFile() {
		isOpeningFile = true;
		view.middleTabPane.viewPane.getChildren().clear();
		selectionManager.clearSelection();
		historyManager.clearHistory();
		isOpeningFile = false;
	}

	/**
	 * Reset the workspace and set the new GUI's name to className.
	 *
	 * @param className
	 *            the desired name of the new GUI
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

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
		fileChooser.getExtensionFilters().add(filter);
		String vorgabe = view.middleTabPane.viewPane.getClassName() + ".fxml";
		fileChooser.setInitialFileName(vorgabe);

		file = fileChooser.showSaveDialog(view.getStage());

		if (file != null) {
			if (!file.getName().toLowerCase().endsWith(".fxml")) {
				file = new File(file.getAbsoluteFile() + ".fxml");
			}
			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(CodeGenerator.generateFXMLCode(view.middleTabPane.viewPane, this));
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Save file to FXML.
	 */
	private void saveFXMLFile(File dir) {
		if (isOpeningFile) {
			return;
		}
		File file;

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(dir);
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("FXML files (*.fxml)", "*.fxml");
		fileChooser.getExtensionFilters().add(filter);
		String vorgabe = view.middleTabPane.viewPane.getClassName() + ".fxml";
		fileChooser.setInitialFileName(vorgabe);
		file = fileChooser.showSaveDialog(view.getStage());

		if (file != null) {
			if (!file.getName().toLowerCase().endsWith(".fxml")) {
				file = new File(file.getAbsoluteFile() + ".fxml");
			}

			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(CodeGenerator.generateFXMLCode(view.middleTabPane.viewPane, this));
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Toggle the history panel's visibility.
	 */
	public void toggleHistory() {
		if (view.topPanel.mItmHistory.isSelected()) {
			if (!view.rightPanel.getItems().contains(view.rightPanel.historyPanel)) {
				view.rightPanel.getItems().add(view.rightPanel.historyPanel);
				view.rightPanel.setDividerPosition(0, 0.6);
			}
		} else {
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

	/** Generates the full Java code */
	public String generateJavaCode(String className, boolean forPreview) {
	return	CodeGenerator.generateJavaCode(className,view.leftPanel.leftList,view.middleTabPane.viewPane,  forPreview);
//		HashMap<String, String> imports = new HashMap<>();

//		for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
//			ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
//			imports.put(componentSettings.getType(), componentSettings.getPackageName());
//			if (componentSettings.getListenerProperties() != null
//					&& componentSettings.getListenerProperties().size() > 0) {
//				for (ListenerProperty lh : componentSettings.getListenerProperties()) {
//					if (!imports.containsKey(lh.getListenerEvent())) {
//						imports.put(lh.getListenerEvent(), lh.getPackageName());
//					}
//				}
//			}
//		}
//		if (className != null && !className.isEmpty()) {
//			view.middleTabPane.viewPane.setClassName(className);
//		}
//		return CodeGenerator.generateJavaCode(view.middleTabPane.viewPane, imports, forPreview);
	}

	public HashMap<String, String> getFXMLImports() {
		HashMap<String, String> allImports = new HashMap<>();
		for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
			ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
			allImports.put(componentSettings.getType(), componentSettings.getPackageName());
		}
		return allImports;
	}

	// x and y are initial layout positions. To be used only with drag and drop.
	private void addGObject(final GObject newThing, ComponentSettings componentSettings, final View view,
			final ViewListeners viewListeners, Node settingsNode, int x, int y, final Pane destination) {

		// Sets the default settings on the gObject and creates the property
		// edit pane
		final PropertyEditPane propertyEditPane = new PropertyEditPane(newThing, componentSettings, fieldNames,
				view.middleTabPane.viewPane, settingsNode, historyManager);

		newThing.setPEP(propertyEditPane);

		if (newThing instanceof AnchorPane) {
			dealWithPane((Pane) newThing);
		}

		final Node newNode = (Node) newThing;
		setNewNodeListeners(viewListeners, newNode);

		setNewNodeDeleteButton(destination, newNode);

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
		setNewNodeToHistory(newThing, destination, newNode);

	}

	private void setNewNodeDeleteButton(final Pane destination, final Node newNode) {
		final ContextMenu nodePopUp = new ContextMenu();
		final MenuItem deletebutton = new MenuItem(LabelGrabber.getLabel("delete.node.text"));
		nodePopUp.getItems().add(deletebutton);
		newNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (t.getButton().equals(MouseButton.SECONDARY)) {
					nodePopUp.show(newNode, Side.RIGHT, 0, 0);
					deletebutton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent t) {
							// if (newNode instanceof GMenuBar){
							// ((GMenuBar)newNode).clearTree();
							// }
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
	}

	// x and y are initial layout positions. To be used only with drag and drop.
	private void addGObjectRec(GObject newThing, ComponentSettings componentSettings, View view,
			ViewListeners viewListeners, Node settingsNode, Pane destination,HashMap<String,String> rbTn) {

		// Sets the default settings on the gObject and creates the property
		// edit pane
		PropertyEditPane propertyEditPane = new PropertyEditPane(newThing, componentSettings, fieldNames,
				view.middleTabPane.viewPane, settingsNode, historyManager);

		newThing.setPEP(propertyEditPane);

		// if (componentSettings.getLayoutType().equals("anchorpane")) {
		// dealWithPane((Pane) newThing);
		// }
		if (newThing instanceof AnchorPane) {
			dealWithPane((Pane) newThing);
		}
		if (newThing instanceof GRadioButton) {
			String tgn = rbTn.get(newThing.getFieldName());
			if (tgn!=null && !tgn.isEmpty()){
				if (tgn.startsWith("$")){
					tgn=tgn.substring(1);
					((GRadioButton)newThing).setToggleGroupName(tgn);
				}
			}

		}
		Node newNode = (Node) newThing;
		destination.getChildren().add(newNode);

		setNewNodeListeners(viewListeners, newNode);
		setNewNodeDeleteButton(destination, newNode);

		setNewNodeToHistory(newThing, destination, newNode);

		if (settingsNode instanceof Pane) {

			Pane newPane = (Pane) settingsNode;
			for (Node cnode : newPane.getChildren()) {
				for (ComponentMenuItem componentMenuItem : view.leftPanel.leftList.getItems()) {
					ComponentSettings componentSettings2 = componentMenuItem.getComponentSettings();
					try {
						// System.out.println(cnode+ "
						// "+componentSettings2.getType()+"
						// "+cnode.getClass().getSimpleName());
						if (componentSettings2.getType().equals(cnode.getClass().getSimpleName())) {
							Class componentClass = Class.forName("bdl.build." + componentSettings2.getPackageName()
									+ ".G" + componentSettings2.getType());
							Constructor constructor = componentClass.getConstructor();
							GObject verynewThing = (GObject) constructor.newInstance();
							verynewThing.setFieldName(cnode.getId());

							addGObjectRec(verynewThing, componentSettings2, view, viewListeners, cnode,
									(Pane) newThing,rbTn);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (settingsNode instanceof MenuBar) {
			((GMenuBar) newThing).getMenuBuilder().buildTreeFromNode(settingsNode);
		}

	}

	private void setNewNodeToHistory(GObject newThing, Pane destination, Node newNode) {
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

	private void setNewNodeListeners(ViewListeners viewListeners, Node newNode) {
		newNode.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {

				if (selectionManager.getCurrentlySelected() == newNode) {
					selectionManager.updateSelected((GObject) newNode);
				} else {
					if (newNode instanceof Pane) {
						((Pane) newNode).setPrefWidth(t1.getWidth());
						((Pane) newNode).setPrefHeight(t1.getHeight());
					}
				}
			}
		});
		newNode.layoutXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (selectionManager.getCurrentlySelected() == newNode) {
					selectionManager.updateSelected((GObject) newNode);
				}
			}
		});

		newNode.layoutYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (selectionManager.getCurrentlySelected() == newNode) {
					selectionManager.updateSelected((GObject) newNode);
				}
			}
		});

		if (newNode instanceof Pane) {

			newNode.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {
					selectionManager.updateSelected((GObject) newNode);
					viewListeners.onMousePressed(newNode, mouseEvent);
					mouseEvent.consume();// Stops the mouseEvent falling through
											// to
											// the viewPane which would clear
											// selection
				}
			});
			newNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					viewListeners.onMouseReleased(newNode, mouseEvent);
					mouseEvent.consume();
				}
			});
			newNode.setOnMouseDragged(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {
					// System.out.println("MouseDragged");
					viewListeners.onMouseDragged(newNode, mouseEvent);
					selectionManager.updateSelected((GObject) newNode);
					mouseEvent.consume();
				}
			});

		} else {

			newNode.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {
					selectionManager.updateSelected((GObject) newNode);
					viewListeners.onMousePressed(newNode, mouseEvent);
					mouseEvent.consume();// Stops the mouseEvent falling through
											// to
											// the viewPane which would clear
											// selection
				}
			});
			newNode.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					viewListeners.onMouseReleased(newNode, mouseEvent);
					mouseEvent.consume();
				}
			});
			newNode.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {
					// System.out.println("MouseDragged");
					viewListeners.onMouseDragged(newNode, mouseEvent);
					selectionManager.updateSelected((GObject) newNode);
					mouseEvent.consume();
				}
			});
		}
	}

	private void dealWithPane(final Pane newThing) {
		if (!(newThing instanceof AnchorPane)) {
			return;
		}
		// System.out.println("dealWithPane");
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
						Class panelPropertyClass = Class.forName(
								"bdl.build." + componentSettings.getPackageName() + ".G" + componentSettings.getType());
						Constructor constructor = panelPropertyClass.getConstructor();
						newnewThing = (GObject) constructor.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					final GObject newGObj = (GObject) newnewThing;
					int xpos = RasterPane.getRasterPosX(t.getX());
					int ypos = RasterPane.getRasterPosY(t.getY());
					addGObject(newGObj, componentSettings, view, viewListeners, null, xpos, ypos, newThing);
					// newThing.getChildren().add((Node) newGObj);
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
							return newThing.getClass().getSuperclass().getSimpleName() + " > "
									+ newGObj.getClass().getSuperclass().getSimpleName() + " added!";
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
			if (curNode instanceof GObject || curNode instanceof GUIObject) {
				TreeItem ti = new TreeItem<>(
						new HierarchyTreeItem((GObject) curNode, view, selectionManager, historyManager));
				root.getChildren().add(ti);
				if (curNode instanceof Pane) {
					addPaneChildrenToHierarchy((Pane) curNode, ti);// Recurse to
																	// continue
																	// adding
																	// all
																	// children,
																	// grandkids
																	// etc
																	// to
																	// hierarchy
																	// pane
					addPaneListChangeListener((Pane) curNode, ti);// Add
																	// listener
																	// to
																	// any panes
				}
				ti.setExpanded(true);
			}
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

	private void saveJAVAFile() {
		ExportJAVA.saveJavaFile(view, this);

	}
}