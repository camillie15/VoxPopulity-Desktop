package com.ug.project.ui;


import com.ug.project.service.Navigation;
import javafx.event.ActionEvent;


public class PostHomeUI {

    public void onCreatePost(ActionEvent actionEvent) {
        Navigation.openNewScreen("/com/ug/project/ui/Login.fxml", "Nuevo Test");
    }

    public void onRefresh(ActionEvent actionEvent) {
    }

    public void onBack(ActionEvent actionEvent) {
    }

}
