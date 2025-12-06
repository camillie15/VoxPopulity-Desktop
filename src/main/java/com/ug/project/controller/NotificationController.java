package com.ug.project.controller;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Notification;
import com.ug.project.service.NotificationService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationController {

    @FXML
    private ListView<String> notificationList;

    @FXML
    private Label emptyLabel;

    private final NotificationService service = new NotificationService();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {

        Integer userId = SessionManager.getCurrentUser().getId();

        List<Notification> list = service.list(userId);

        notificationList.getItems().clear();

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

            notificationList.getItems().add(prefix + text);
        }
    }

    @FXML
    public void onMarkAllRead() {
        Integer userId = SessionManager.getCurrentUser().getId();
        service.markAllRead(userId);
        loadNotifications();
    }
}
