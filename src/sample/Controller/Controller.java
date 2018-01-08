
/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 *
 * Autor maciejziolkowski,
 */

package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jssc.SerialPort.MASK_RXCHAR;

public class Controller {

    public Controller() {
        detectPort();
    }

    SerialPort devicePort = null;
    public ObservableList<String> portList;
    Label labelValue;

    @FXML
    private ComboBox comboBoxPorts = new ComboBox();

    @FXML
    private TextArea resultsArea = new TextArea();

    @FXML
    private Button diodeOn;

    @FXML
    private Button diodeOff;

    @FXML
    void setDiodeOff(ActionEvent event) {

        try {
            devicePort.writeString("OFF");
            resultsArea.setText("Wyłączono DIODE");
            System.out.println("Wyłączono DIODE");

        } catch (SerialPortException ex) {
            System.out.println(ex);
            resultsArea.setText(String.valueOf(ex));
        }
    }

    @FXML
    void setDiodeOn(ActionEvent event) {

        try {
            devicePort.writeString("ON");
            resultsArea.setText("Włączono DIODE");
            System.out.println("Włączono DIODE");

        } catch (SerialPortException ex) {
            System.out.println(ex);
            resultsArea.setText(String.valueOf(ex));
        }
    }

    @FXML
    public void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }

    @FXML
    void setPorts(ActionEvent event) {
        Object newValue = comboBoxPorts.getValue();
        System.out.println(newValue);
        resultsArea.setText((String) newValue);
        disconnectArduino();
        connectArduino((String) newValue);

    }

    @FXML
    private void initialize() {
        comboBoxPorts.setValue("Port");
        comboBoxPorts.setItems(portList);

        resultsArea.setText("WITAMY W NASZYM PROGRAMIE!!! :)\n\n");
        resultsArea.setWrapText(true);
    }


    @FXML
    public boolean connectArduino(String port) {

        System.out.println("Podłączono Arduino poprawnie");
        resultsArea.setText("Podłączono Arduino poprawnie\n");

        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
            devicePort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(Controller.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }

        return success;
    }

    @FXML
    public void disconnectArduino() {

        System.out.println("disconnectAdruino()");
        if (devicePort != null) {
            try {
                devicePort.removeEventListener();

                if (devicePort.isOpened()) {
                    devicePort.closePort();
                }

            } catch (SerialPortException ex) {
                Logger.getLogger(Controller.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }


}
