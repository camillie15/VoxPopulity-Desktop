package com.ug.project.controller;

import com.ug.project.model.Community;
import com.ug.project.service.CommunityService;
import com.ug.project.ui.LoginApp;
import com.ug.project.service.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CommunityController {

    @FXML private ListView<Community> lvCommunities;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtFilter;

    private final CommunityService service = new CommunityService();
    private final ObservableList<Community> items = FXCollections.observableArrayList();
    private FilteredList<Community> filtered;

    @FXML
    public void initialize() {
        items.setAll(service.listAll());
        // Si no hay comunidades, crear una de ejemplo para pruebas
        if (items.isEmpty()) {
            Community sample = service.create("General", "Comunidad por defecto");
            items.add(sample);
        }

        // Configurar filtrado por nombre
        filtered = new FilteredList<>(items, c -> true);
        SortedList<Community> sorted = new SortedList<>(filtered);
        // opcional: mantener orden alfabético (por name) si quieres que SortedList ordene
        // sorted.setComparator((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        lvCommunities.setItems(sorted);

        // Listener para filtrar en tiempo real por nombre
        if (txtFilter != null) {
            txtFilter.textProperty().addListener((obs, oldV, newV) -> {
                String q = newV == null ? "" : newV.trim().toLowerCase();
                filtered.setPredicate(c -> {
                    if (q.isEmpty()) return true;
                    return c.getName() != null && c.getName().toLowerCase().contains(q);
                });
            });
        }

        // Cell factory para mostrar cada comunidad como tarjeta (card)
        lvCommunities.setCellFactory(list -> new ListCell<>() {
            private final VBox container = new VBox();
            private final Label lblTitle = new Label();
            private final Label lblDesc = new Label();
            private final Label lblMeta = new Label();
            private final Button btnJoin = new Button();
            private final Button btnDetails = new Button("Detalles");
            private final HBox actions = new HBox(8, btnJoin, btnDetails);

            {
                lblTitle.getStyleClass().add("community-title");
                lblDesc.setWrapText(true);
                lblMeta.getStyleClass().add("community-meta");
                actions.getStyleClass().add("community-actions");
                container.getStyleClass().add("community-card");
                container.getChildren().addAll(lblTitle, lblDesc, lblMeta, actions);
                VBox.setVgrow(lblDesc, Priority.ALWAYS);

                btnJoin.setOnAction(evt -> {
                    Community c = getItem();
                    if (c == null) return;
                    com.ug.project.model.User current = com.ug.project.infrastructure.SessionManager.getCurrentUser();
                    if (current == null) {
                        new Alert(Alert.AlertType.WARNING, "Debes iniciar sesión para unirte.", ButtonType.OK).showAndWait();
                        return;
                    }
                    boolean member = c.getMembers().stream().anyMatch(u -> u.getId() != null && u.getId().equals(current.getId()));
                    if (member) {
                        service.leaveCommunity(c, current);
                        c.getMembers().removeIf(u -> u.getId() != null && u.getId().equals(current.getId()));
                    } else {
                        service.joinCommunity(c, current);
                        c.getMembers().add(current);
                    }
                    updateCard(c);
                });

                btnDetails.setOnAction(evt -> {
                    Community c = getItem();
                    if (c == null) return;
                    // Navegar en SPA y pasar comunidad al controlador de detalles
                    LoginApp.switchToWithInit("/com/ug/project/ui/CommunityDetails.fxml", 480, 260,
                            "Detalles - " + c.getName(), (com.ug.project.controller.CommunityDetailsController ctrl) -> {
                                ctrl.setCommunity(service.findById(c.getId()));
                            });
                });
            }

            private void updateCard(Community c) {
                lblTitle.setText(c.getName());
                lblDesc.setText(c.getDescription() == null ? "" : c.getDescription());
                lblMeta.setText("Miembros: " + c.getMembersCount());
                com.ug.project.model.User current = com.ug.project.infrastructure.SessionManager.getCurrentUser();
                if (current == null) {
                    btnJoin.setText("Unirse");
                    btnJoin.setDisable(true);
                } else {
                    boolean member = c.getMembers().stream().anyMatch(u -> u.getId() != null && u.getId().equals(current.getId()));
                    btnJoin.setText(member ? "Salir" : "Unirse");
                    btnJoin.setDisable(false);
                }
            }

            @Override
            protected void updateItem(Community item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    updateCard(item);
                    setGraphic(container);
                }
            }
        });

        // Mantener compatibilidad con el botón externo (si se desea)
        lvCommunities.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updateSelection(newV);
        });
        updateSelection(lvCommunities.getSelectionModel().getSelectedItem());
    }

    private void updateSelection(Community c) {
        // dejamos el manejo visual a las tarjetas; aquí solo podemos manejar lógica si es necesario.
        // Por ahora no hacemos nada para evitar NullPointerException al cambiar a la vista de tarjetas.
    }

    @FXML
    private void onCreate(ActionEvent e) {
        String name = txtName.getText();
        String desc = txtDescription.getText();
        if (name == null || name.isBlank()) {
            alert(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return;
        }
        Community c = service.create(name.trim(), desc == null ? "" : desc.trim());
        items.add(c);
        txtName.clear(); txtDescription.clear();
    }

    @FXML
    private void onJoinLeave(ActionEvent e) {
        Community sel = lvCommunities.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        com.ug.project.model.User current = com.ug.project.infrastructure.SessionManager.getCurrentUser();
        if (current == null) {
            alert(Alert.AlertType.WARNING, "Debes iniciar sesión para unirte.");
            return;
        }
        boolean member = sel.getMembers().stream().anyMatch(u -> u.getId() != null && u.getId().equals(current.getId()));
        if (member) {
            service.leaveCommunity(sel, current);
            sel.getMembers().removeIf(u -> u.getId() != null && u.getId().equals(current.getId()));
        } else {
            service.joinCommunity(sel, current);
            sel.getMembers().add(current);
        }
        // actualizar la vista forzando refresh del ListView
        lvCommunities.refresh();
    }

    @FXML
    private void onDetails(ActionEvent e) {
        Community sel = lvCommunities.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.WARNING, "Selecciona una comunidad.");
            return;
        }
        // Navegar en la misma ventana (SPA) y pasar la comunidad al controlador
        com.ug.project.ui.LoginApp.switchToWithInit("/com/ug/project/ui/CommunityDetails.fxml", 480, 260,
                "Detalles - " + sel.getName(), (com.ug.project.controller.CommunityDetailsController ctrl) -> {
                    ctrl.setCommunity(service.findById(sel.getId()));
                });
    }

    @FXML
    private void onDelete(ActionEvent e) {
        Community sel = lvCommunities.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.WARNING, "Selecciona una comunidad para eliminar.");
            return;
        }
        service.delete(sel);
        items.remove(sel);
    }

    @FXML
    private void onBack(ActionEvent e) {
        // Volver al Dashboard (muro principal) de forma robusta
        LoginApp.switchTo("/com/ug/project/ui/Dashboard.fxml", 1000, 600, "Muro de Publicaciones");
    }

    @FXML
    private void onEdit(ActionEvent e) {
        Community sel = lvCommunities.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert(Alert.AlertType.WARNING, "Selecciona una comunidad para editar.");
            return;
        }

        // Crear diálogo de edición
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar comunidad");
        dialog.setHeaderText("Editar nombre y descripción");

        // Campos
        TextField nameField = new TextField(sel.getName());
        TextField descField = new TextField(sel.getDescription() == null ? "" : sel.getDescription());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Validación simple: no permitir OK si nombre vacío
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            if (nameField.getText() == null || nameField.getText().isBlank()) {
                ev.consume();
                alert(Alert.AlertType.WARNING, "El nombre no puede estar vacío.");
            }
        });

        dialog.setResultConverter(btn -> btn);
        var result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sel.setName(nameField.getText().trim());
                sel.setDescription(descField.getText() == null ? "" : descField.getText().trim());
                Community updated = service.update(sel);
                // Reemplazar en la lista local para que ObservableList notifique cambios
                int idx = items.indexOf(sel);
                if (idx >= 0) items.set(idx, updated);
                else lvCommunities.refresh();
            } catch (RuntimeException ex) {
                alert(Alert.AlertType.ERROR, "Error al actualizar la comunidad: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void onClearFilter(ActionEvent e) {
        if (txtFilter != null) {
            txtFilter.clear();
        }
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
