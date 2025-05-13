package org.example.bonussystem.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.bonussystem.model.Employee;
import org.example.bonussystem.service.EmployeeService;

public class ButtonCellFactory implements Callback<TableColumn<Employee, Long>, TableCell<Employee, Long>> {

    private final EmployeeService employeeService;
    private final AdminController adminController;

    public ButtonCellFactory(EmployeeService employeeService, AdminController adminController) {
        this.employeeService = employeeService;
        this.adminController = adminController;
    }
    public ButtonCellFactory() {
        this.employeeService = null;
        this.adminController = null;
    }

    @Override
    public TableCell<Employee, Long> call(TableColumn<Employee, Long> param) {
        return new TableCell<>() {
            private final Button approveButton = new Button("Одобрить");
            private final Button rejectButton = new Button("Отклонить");
            private final HBox hBox = new HBox(approveButton, rejectButton);

            {
                hBox.setSpacing(10);
                approveButton.getStyleClass().add("custom-button");
                rejectButton.getStyleClass().add("custom-button");

                approveButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    if (employee != null && employeeService != null) {
                        employeeService.approveEmployee(employee.getId());
                        getTableView().getItems().remove(employee);
                        adminController.loadAllData();
                    }
                });

                rejectButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    if (employee != null && employeeService != null) {
                        employeeService.rejectEmployee(employee.getId());
                        getTableView().getItems().remove(employee);
                        adminController.loadAllData();
                    }
                });
            }

            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hBox);
                }
            }
        };
    }
}