package bdl.build;

public enum GType {
    Button("javafx.scene.control.Button"),
    Menu("javafx.scene.control.Menu"),
    MenuBar("javafx.scene.control.MenuBar"),
    MenuItem("javafx.scene.control.MenuItem"),
    ToolBar("javafx.scene.control.ToolBar"),
    Image("javafx.scene.image.Image"),
    ImageView("javafx.scene.image.ImageView"),
    AnchorPane("javafx.scene.layout.AnchorPane"),
    CheckBox("javafx.scene.control.CheckBox"),
    ComboBox("javafx.scene.control.ComboBox"),
    Label("javafx.scene.control.Label"),
    ListView("javafx.scene.control.ListView"),
    TextArea("javafx.scene.control.TextArea"),
    TextField("javafx.scene.control.TextField");

    private String importStatement;

    GType (String importStatement) {
        this.importStatement = "import " + importStatement + ";";
    }

    public String getImportStatement() {
        return importStatement;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
