package practica;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Servidor extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    static ServerSocket servidor;
    static final int PUERTO = 44444;
    static int CONEXIONES = 0;
    static int ACTUALES = 0;
    static int numero = (int) (Math.random() * 100) + 1;
    static JTextField mensaje = new JTextField("");
    static JTextField mensaje2 = new JTextField("");
    private JScrollPane scrollpane1;
    static JTextArea textarea;
    JButton salir = new JButton("Salir");
    static List<Socket> tabla = new ArrayList<Socket>();

    public Servidor()
    {
        super(" VENTANA DEL SERVIDOR DE CHAT ");
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        mensaje.setEditable(false);
        mensaje2.setBounds(10, 348, 400, 30);
        add(mensaje2);
        mensaje2.setEditable(false);
        textarea = new JTextArea();
        scrollpane1 = new JScrollPane(textarea);
        scrollpane1.setBounds(10, 50, 400, 300);
        add(scrollpane1);
        salir.setBounds(420, 10, 100, 30);
        add(salir);
        textarea.setEditable(false);
        salir.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) throws Exception
    {
    	try
    	{
    		servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado...");
            Servidor pantalla = new Servidor();
            pantalla.setBounds(0, 0, 540, 450);
            pantalla.setVisible(true);
            mensaje.setText("Número de conexiones actuales: " + 0);
            while (!servidor.isClosed())
            {
                Socket socket;
                try
                {
                    socket = servidor.accept();
                }
                catch (SocketException sex)
                {
                    break;
                }
                tabla.add(socket);
                CONEXIONES++;
                ACTUALES++;
                mensaje.setText("Número de conexiones actuales: " + ACTUALES);
                HiloServidor hilo = new HiloServidor(socket);
                hilo.start();
                for (Socket s : tabla) 
                {
                    try 
                    {
                        DataInputStream entrada = new DataInputStream(s.getInputStream());
                        if (entrada.available() > 0) 
                        {
                            String mensaje = entrada.readUTF();
                            if (mensaje.equals("CERRAR_SERVIDOR")) 
                            {
                                mensaje2.setText("¡Número correcto adivinado! Cerrando el servidor...");

                                // Cerrar todos los sockets
                                for (Socket sock : tabla) 
                                {
                                    sock.close();
                                }

                                // Cerrar el ServerSocket
                                servidor.close();
                                System.exit(0);
                            }
                        }
                    } 
                    catch (IOException ex) 
                    {
                        ex.printStackTrace();
                    }
                }
            }

            if (servidor.isClosed())
            {
            	System.out.println("Servidor finalizado...");
            }
    	}
    	catch (BindException be)
    	{
    		JOptionPane.showMessageDialog(null, "El servidor ya está activo", "Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == salir)
        {
            try
            {
                servidor.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }
}