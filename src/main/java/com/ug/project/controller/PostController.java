/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ug.project.controller;

import com.ug.project.service.Navigation;
import javafx.event.ActionEvent;

/**
 *
 * @author nan2p
 */
public class PostController {

    public void onCreatePost(ActionEvent actionEvent) {
        Navigation.openNewScreen("/com/ug/project/ui/Login.fxml", "Nuevo Test");
    }

    public void onRefresh(ActionEvent actionEvent) {
    }

    public void onBack(ActionEvent actionEvent) {
    }
}
