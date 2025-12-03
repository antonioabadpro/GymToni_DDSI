/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Config.HibernateUtil;
import Vista.VistaConexion;
import Vista.VistaMensaje;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.hibernate.SessionFactory;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class ControladorConexion implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    
    public static String user; // Nombre de Usuario que cargamos en la Clase 'HibernateUtil'
    public static String pass; // Contraseña que cargamos en la Clase 'HibernateUtil'
    private SessionFactory sessionFactory=null;
    private VistaMensaje vMensaje=null;
    private VistaConexion vConexion=null;
    private ControladorPrincipal cPrincipal=null;
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************
    
    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaConexion' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        this.vConexion.jButton_entrar.addActionListener(this);
        this.vConexion.jButton_cancelar.addActionListener(this);
    }
    
    /**
     * Metodo privado auxiliar para realizar la conexion al servidor de la BD con Hibernate para poder iniciar Sesion en el SGBD y poder realizar consultas
     * @return Devuelve el objeto 'SessionFactory' inciado en la clase 'HibernateUtil' para utilizarlo durante toda la ejecucion de nuestra Aplicacion
     */
    private SessionFactory conectarBD()
    {
        this.sessionFactory = HibernateUtil.buildSessionFactory();
        
        if(this.sessionFactory==null)
        {
            this.vMensaje.mensaje(null, "Error al introducir las credenciales", "Error de Inicio de Sesion", "ERROR");
        }
        else
        {
            this.vMensaje.mensaje(null, "Conexion Correcta con Hibernate", "Conexion con Exito", "INFO");
        }
        
        return this.sessionFactory;
    }
    
    // ********************************* METODOS PUBLICOS *********************************
    
    /**
     * Constructor de 'ControladorConexion'
     */
    public ControladorConexion()
    {
        this.vMensaje=new VistaMensaje();
        this.vConexion=new VistaConexion();
        this.vConexion.setLocationRelativeTo(null); // Situamos la Ventana de Inicio de Sesion en el centro de la pantalla
        this.vConexion.setVisible(true);
        
        addListeners(); // Añadimos los eventos de los botones de 'VistaConexion'
    }
    
    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaConexion'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "Entrar": // Si seleccionamos el Boton 'Entrar' del Frame 'VistaConexion' o presionamos la tecla 'Enter'
            {
                ControladorConexion.user=this.vConexion.jTextField_usuario.getText();
                ControladorConexion.pass=new String(this.vConexion.jTextField_password.getPassword());
                this.sessionFactory=conectarBD();
                this.vConexion.dispose();
                if(this.sessionFactory!=null) // Si hemos introducido las credenciales correctamente, inciamos sesion
                {
                    this.cPrincipal=new ControladorPrincipal(this.sessionFactory);
                }
                else // Si NO hemos introducido las credenciales correctamente, volvemos a mostrar el Frame 'vConexion' para volver a introducir los datos
                {
                    this.vConexion.setLocationRelativeTo(null); // Situamos la Ventana de Inicio de Sesion en el centro de la pantalla
                    this.vConexion.setVisible(true);
                }
            }; break;
            
            case "Cancelar": // Si seleccionamos el Boton 'Cancelar' del Frame 'VistaConexion'
            {
                this.vMensaje.mensaje(null, "Salida correcta de la Aplicacion", "Operacion Cancelada", "CANCELAR");
                this.vConexion.dispose();
                System.exit(0);
            }; break;
        }
    }
    
    
    
}
