//Para compilar: 
//  Linux: javac -cp .:jSerialComm-1.3.11.jar LeerSerial.java
//  Windows: javac -cp .;jSerialComm-1.3.11.jar LeerSerial.java
//Para ejecutar: 
//  Linux: java -cp .:jSerialComm-1.3.11.jar LeerSerial
//  Windows: java -cp .;jSerialComm-1.3.11.jar LeerSerial
//Nota: jSerialComm-1.3.11.jar y LeerSerial.java deben estar en el mismo directorio


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.util.Scanner;

import com.fazecast.jSerialComm.*;
public class LeerSerial extends JFrame implements ActionListener{
    
    //private String PUERTO = "COM6"; //"/dev/ttyUSB0"
    private int BAUDRATE = 9600;

    private JPanel panel;
    private JLabel lblMedida;
    private JTextField txtMedida;
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnNivel1;
    private JButton btnNivel2;
    private JButton btnNivel3;
    private JButton btnNivel4;

    private Monitor monitor;
    private Thread t;
    private SerialPort comPort;

    boolean corriendo = false;

    public LeerSerial(){

        configurarPuerto();


        this.setTitle("Leer Serial");
        panel = new JPanel();
        panel.setLayout(null);
		
		lblMedida = new JLabel("Medida:");
		txtMedida = new JTextField("Valor...");
		btnIniciar = new JButton("Iniciar");
		btnDetener = new JButton("Detener");
		btnIniciar.setBackground(Color.decode("#FF7171")); 
		btnDetener.setBackground(Color.decode("#FF7171"));

        btnNivel1 = new JButton();
        btnNivel2 = new JButton();
        btnNivel3 = new JButton();
        btnNivel4 = new JButton();
        btnNivel1.setBackground(Color.decode("#e6e6ff"));
        btnNivel2.setBackground(Color.decode("#e6e6ff"));
        btnNivel3.setBackground(Color.decode("#e6e6ff"));
        btnNivel4.setBackground(Color.decode("#e6e6ff"));
		
		panel.add(lblMedida);
		panel.add(txtMedida);
		panel.add(btnIniciar);
		panel.add(btnDetener);

        panel.add(btnNivel1);
        panel.add(btnNivel2);
        panel.add(btnNivel3);
        panel.add(btnNivel4);




		lblMedida.setBounds(150, 65, 75, 30);
		txtMedida.setBounds(230, 65, 100,30);
		btnIniciar.setBounds(375, 25, 100, 50);
		btnDetener.setBounds(375, 80, 100, 50);
                            /*x    y   w    h*/   
        btnNivel1.setBounds(30, 250, 40, 50);
        btnNivel2.setBounds(30, 200, 40, 50);
        btnNivel3.setBounds(30, 150, 40, 50);
        btnNivel4.setBounds(30, 100, 40, 50);
        
        this.add(panel);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.setSize(500,350);
        this.setVisible(true);
		
		btnIniciar.addActionListener(this);
		btnDetener.addActionListener(this);

		
    }

    
    public void actionPerformed(ActionEvent event){
        if(event.getSource() == btnIniciar){
        	iniciar();
        }
        	
        else if(event.getSource() == btnDetener){
        	terminar();
        }
    }

    public void configurarPuerto(){

        /*-----------------------*/
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Seleccionar puerto:");
        int i = 1;
        for(SerialPort port : ports)
                System.out.println(i++ + ": " + port.getSystemPortName());
        Scanner s = new Scanner(System.in);
        int chosenPort = s.nextInt();

        comPort = ports[chosenPort - 1];
        if(comPort.openPort())
                System.out.println("Puerto abierto con éxito.");
        else {
                System.out.println("No es posible abrir el puerto.");
                return;
        }

        /*------------------------*/

        //comPort = SerialPort.getCommPort(PUERTO);
        comPort.setBaudRate(BAUDRATE);
        //comPort.openPort();
    }
    
    public void iniciar(){

		System.out.println("Iniciando...");
		corriendo = true;
		
		monitor = new Monitor(comPort, txtMedida, corriendo, btnNivel1, btnNivel2, btnNivel3, btnNivel4);
        t = new Thread(monitor);
        t.start();
        btnIniciar.setBackground(Color.decode("#64FF69"));
    }


    public void terminar(){
    	System.out.println("Cerrando...");
    	t.interrupt();
    	//t.stop();
    	//comPort.closePort();
    	monitor.detener();
    	btnIniciar.setBackground(Color.decode("#FF7171"));
    	txtMedida.setText("Valor...");  	
    }
    
    
    public static void main(String[] args){
        LeerSerial miCliente = new LeerSerial();
    }

}


class Monitor implements Runnable{
    JTextField txtMedida;
    SerialPort comPort;
    boolean corriendo;
    byte[] arr;
    JButton btnNivel1;
    JButton btnNivel2;
    JButton btnNivel3;
    JButton btnNivel4;

    public Monitor(SerialPort comPort, JTextField txtMedida, boolean corriendo, JButton btnNivel1, JButton btnNivel2, JButton btnNivel3, JButton btnNivel4){
        this.txtMedida = txtMedida;
        this.comPort = comPort;
        this.corriendo = corriendo;
        this.btnNivel1 = btnNivel1;
        this.btnNivel2 = btnNivel2;
        this.btnNivel3 = btnNivel3;
        this.btnNivel4 = btnNivel4;

        arr = new byte[1];
		arr[0] = 114;

        //System.out.println("Constructor Monitor. Caracter:"+arr[0]);
    }


    public void detener(){
    	this.corriendo = false;
    }


    @Override
    public void run(){
    	comPort.writeBytes(arr,1);
		try {
		   while (this.corriendo)
		   {
		      while (comPort.bytesAvailable() == 0)
		      	Thread.sleep(50);

		      byte[] readBuffer = new byte[comPort.bytesAvailable()];
		      int numRead = comPort.readBytes(readBuffer, readBuffer.length);
		      //System.out.println("Leidos " + numRead + " bytes.");
		      String v = new String(readBuffer, "ASCII");
		      //System.out.println(v);
		      txtMedida.setText(v);
              int v_int = Integer.parseInt(v.trim());

              if(v_int<256)
              {
                btnNivel1.setBackground(Color.decode("#0000ff"));
                btnNivel2.setBackground(Color.decode("#e6e6ff"));
                btnNivel3.setBackground(Color.decode("#e6e6ff"));
                btnNivel4.setBackground(Color.decode("#e6e6ff"));
              }
              else if(v_int<513)
              {
                btnNivel1.setBackground(Color.decode("#0000ff"));
                btnNivel2.setBackground(Color.decode("#0000ff"));
                btnNivel3.setBackground(Color.decode("#e6e6ff"));
                btnNivel4.setBackground(Color.decode("#e6e6ff"));
              }

              else if(v_int<769)
              {
                btnNivel1.setBackground(Color.decode("#0000ff"));
                btnNivel2.setBackground(Color.decode("#0000ff"));
                btnNivel3.setBackground(Color.decode("#0000ff"));
                btnNivel4.setBackground(Color.decode("#e6e6ff"));
              }

              else
              {
                btnNivel1.setBackground(Color.decode("#0000ff"));
                btnNivel2.setBackground(Color.decode("#0000ff"));
                btnNivel3.setBackground(Color.decode("#0000ff"));
                btnNivel4.setBackground(Color.decode("#0000ff"));
              }

              //System.out.println("Valor entero: "+v_int);
		      comPort.writeBytes(arr,1);
		   }
		} catch (Exception e) { e.printStackTrace(); }
        
    }

}
