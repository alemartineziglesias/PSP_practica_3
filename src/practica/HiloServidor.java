package practica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread
{
	DataInputStream fentrada;
	Socket socket;
	String nombre;
	boolean fin = false;

	public HiloServidor(Socket socket, String nombre)
	{
		this.socket = socket;
		this.nombre = nombre;
		try
		{
			fentrada = new DataInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}

	public void run()
	{
		//Envía el contenido del textArea al método EnviarMensajes
		Servidor.mensaje.setText("Número de conexiones actuales: " + Servidor.ACTUALES);
		String texto = Servidor.textarea.getText();
		EnviarMensajes(texto);
		while(!fin)
		{
			String cadena = "";
			try
			{
				cadena = fentrada.readUTF();
				if(cadena.trim().equals("*"))
				{
					Servidor.ACTUALES--;
					Servidor.mensaje.setText("Número de conexiones actuales: " + Servidor.ACTUALES);
					fin = true;
					Servidor.tabla.remove(socket);
					socket.close();
				}
				else if(cadena.trim().equals("CERRAR_SERVIDOR"))
				{
					Servidor.mensaje2.setText("Número correcto adivinado, cerrando el servidor...");
					try 
					{
						for (Socket sock : Servidor.tabla) 
						{
							sock.close();
						}
						Servidor.servidor.close();
						System.exit(0);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					Servidor.textarea.append(cadena + "\n");
					texto = Servidor.textarea.getText();
					EnviarMensajes(texto);
				}
			}
			catch (EOFException eof)
			{
				fin = true;
				synchronized (Servidor.nombres) 
				{
				    Servidor.nombres.remove(nombre);
				}
				Servidor.ACTUALES--;
				Servidor.mensaje.setText("Número de conexiones actuales: " + Servidor.ACTUALES);
				Servidor.tabla.remove(socket);
				try
				{
					socket.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			catch (Exception ex)
			{
				fin = true;
				synchronized (Servidor.nombres) 
				{
				    Servidor.nombres.remove(nombre);
				}
				Servidor.tabla.remove(socket);
				try 
				{
					socket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void EnviarMensajes(String texto)
	{
		for (Socket s : Servidor.tabla)
		{
			try
			{
				DataOutputStream fsalida = new DataOutputStream(s.getOutputStream());
				fsalida.writeUTF(texto);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}