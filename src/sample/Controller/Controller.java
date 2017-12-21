package sample.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jssc.SerialPort.MASK_RXCHAR;

public class Controller {

    @FXML
    private ComboBox<String> comboBoxPorts;

    @FXML
    private TextArea resultsArea;

    SerialPort devicePort = null;
    ObservableList<String> portList;
    Label labelValue;

    private void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
//            resultsArea.setText(name);
            portList.add(name);

        }
    }

    public void init() {

        labelValue = new Label();

        detectPort();
        comboBoxPorts = new ComboBox(portList);
        comboBoxPorts.valueProperty()
                .addListener(new ChangeListener<String>() {


                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {

                        System.out.println(newValue);
                        disconnectSTM32();
                        connectSTM32(newValue);
                    }

                });

    }

    public boolean connectSTM32(String port) {

        System.out.println("connect STM32");

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

                        //Update label in ui thread
                        Platform.runLater(() -> {
                            labelValue.setText(st);
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
