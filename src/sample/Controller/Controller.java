package sample.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import jssc.SerialPort;
import jssc.SerialPortEvent;
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

    }

    @FXML
    void setDiodeOn(ActionEvent event) {

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
        disconnectSTM32();
        connectSTM32((String) newValue);

    }

    @FXML
    private void initialize() {
        comboBoxPorts.setValue("Port");
        comboBoxPorts.setItems(portList);

        resultsArea.setText("WITAMY W NASZYM PROGRAMIE!!! :)\n\n");
        resultsArea.setWrapText(true);
    }


    @FXML
    public boolean connectSTM32(String port) {

        System.out.println("connect STM32");
        resultsArea.setText("connect STM32\n");

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
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if (serialPortEvent.isRXCHAR()) {
                    try {
                        String st = serialPort.readString(serialPortEvent
                                .getEventValue());
                        System.out.println(st);
                        resultsArea.setText(st);

                        //Update label in ui thread
                        Platform.runLater(() -> {
                            labelValue.setText(st);
                            resultsArea.setText(st);
                        });

                    } catch (SerialPortException ex) {
                        Logger.getLogger(Controller.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }

                }
            });

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
    public void disconnectSTM32() {

        System.out.println("disconnectSTM32()");
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
