package com.ug.project.controller;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Notification;
import com.ug.project.service.NotificationService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonBar;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationController {

    @FXML
    private ListView<String> notificationList;

    @FXML
    private Label emptyLabel;

    private final NotificationService service = new NotificationService();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Mapa para saber qué String corresponde a qué ID
    private final Map<String, Integer> notificationIdMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {

        Integer userId = SessionManager.getCurrentUser().getId();
        List<Notification> list = service.list(userId);

        notificationList.getItems().clear();
        notificationIdMap.clear();

        if (list.isEmpty()) {
            emptyLabel.setVisible(true);
            return;
        }

        emptyLabel.setVisible(false);

        for (Notification n : list) {

            String formattedDate =
                    n.getCreatedAt() == null
                            ? "Fecha desconocida"
                            : n.getCreatedAt().format(FORMATTER);

            String text = String.format("[%s] %s", formattedDate, n.getMessage());
            String prefix = n.isRead() ? "✓ " : "● ";

            String displayText = prefix + text;

            // Agregar al ListView
            notificationList.getItems().add(displayText);

            // Guardar relación texto → ID
            notificationIdMap.put(displayText, n.getId());
        }
    }

    @FXML
    public void onMarkAllRead() {
        Integer userId = SessionManager.getCurrentUser().getId();
        service.markAllRead(userId);
        loadNotifications();
    }

    @FXML
    public void onDelete() {

        String selected = notificationList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No se seleccionó ninguna notificación");
            return;
        }

        // 🟦 Mostrar ventana de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿Seguro que deseas eliminar esta notificación?");
        alert.setContentText(selected);

        // Botones personalizados
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonSi, botonNo);

        // Mostrar y esperar respuesta
        ButtonType resultado = alert.showAndWait().orElse(botonNo);

        if (resultado != botonSi) {
            return;
        }

        // 🟢 Si confirmó, borrar notificación
        Integer id = notificationIdMap.get(selected);
        if (id != null) {
            service.delete(id);
        }

        loadNotifications();
    }

}
