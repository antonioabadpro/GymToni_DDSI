/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Monitor;
import Modelo.Socio;
import Utilidad.GestionTablaActividades;
import Utilidad.GestionTablaInscripcion;
import Vista.VistaActividad;
import Vista.VistaIncio;
import Vista.VistaMensaje;
import Vista.VistaMonitor;
import Vista.VistaPrincipal;
import Vista.VistaSocio;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import Utilidad.GestionTablaMonitores;
import Utilidad.GestionTablaSocios;
import Vista.VistaActividadCRUD;
import Vista.VistaInscripcion;
import Vista.VistaInscripcionCRUD;
import Vista.VistaMonitorCRUD;
import Vista.VistaSocioCRUD;
import javax.swing.JFrame;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class ControladorPrincipal implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    // Variables para Iniciar Sesion
    private SessionFactory sessionFactory;
    
    // Variables para realizar Consultas
    private Transaction tr;
    private Session sesion;
    
    // Variables de los Controladores
    private ControladorMonitor cMonitor;
    private ControladorSocio cSocio;
    private ControladorActividad cActividad;
    private ControladorInscripcion cInscripcion;
    
    // Variables de las Vistas
    private VistaPrincipal vPrincipal; // JFrame
    private VistaIncio vInicio; // JPanel
    private VistaSocio vSocio; // JPanel
    private VistaMonitor vMonitor; // JPanel
    private VistaActividad vActividad; // JPanel
    private VistaMensaje vMensaje; // JOptionPane
    private VistaInscripcion vInscripcion; // JPanel
    
    // Variables de las Vistas CRUD
    private VistaMonitorCRUD vCRUD_monitor;
    private VistaSocioCRUD vCRUD_socio;
    private VistaActividadCRUD vCRUD_actividad;
    private VistaInscripcionCRUD vCRUD_inscripcion;
    
    // Variables de las Tablas (JTable)
    private static GestionTablaMonitores tablaMonitor;
    private static GestionTablaSocios tablaSocio;
    private static GestionTablaActividades tablaActividad;
    private static GestionTablaInscripcion tablaInscripcion;
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************
    
    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaPrincipal' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        this.vPrincipal.jMenuItem_inicio.addActionListener(this);
        this.vPrincipal.jMenuItem_monitores.addActionListener(this);
        this.vPrincipal.jMenuItem_socios.addActionListener(this);
        this.vPrincipal.jMenuItem_actividades.addActionListener(this);
        this.vPrincipal.jMenuItem_salir.addActionListener(this);
        this.vPrincipal.jMenuItem_inscripcionSocio.addActionListener(this);
    }
    
    // ********************************* METODOS PUBLICOS *********************************
    
    /**
     * Constructor de 'ControladorPrincipal'
     * @param sF Sesion abierta desde el 'actionPerormed' de la Clase 'ControladorConexion'
     */
    public ControladorPrincipal(SessionFactory sF)
    {
        this.sessionFactory=sF;
        
        // Inicializamos las Vistas
        this.vPrincipal=new VistaPrincipal();
        this.vPrincipal.setExtendedState(JFrame.MAXIMIZED_BOTH); // Establecemos el Frame en Pantalla Completa
        this.vMensaje=new VistaMensaje();
        this.vInicio=new VistaIncio();
        this.vSocio=new VistaSocio();
        this.vMonitor=new VistaMonitor();
        this.vActividad=new VistaActividad();
        this.vInscripcion=new VistaInscripcion();
        
        // Inicializamos las Vistas CRUD
        this.vCRUD_monitor=new VistaMonitorCRUD();
        this.vCRUD_socio=new VistaSocioCRUD();
        this.vCRUD_actividad=new VistaActividadCRUD();
        this.vCRUD_inscripcion=new VistaInscripcionCRUD();
        
        // Inicializamos los Controladores
        this.cMonitor=new ControladorMonitor(this.sessionFactory, this.vMonitor, this.vCRUD_monitor);
        this.cSocio=new ControladorSocio(this.sessionFactory, this.vSocio, this.vCRUD_socio);
        this.cActividad=new ControladorActividad(this.sessionFactory, this.vActividad, this.vCRUD_actividad, this.cMonitor);
        this.cInscripcion=new ControladorInscripcion(this.sessionFactory, this.vInscripcion, this.vCRUD_inscripcion, this.cSocio, this.cActividad);
        
        // Inicializamos las Tablas
        ControladorPrincipal.tablaMonitor=new GestionTablaMonitores();
        ControladorPrincipal.tablaSocio=new GestionTablaSocios();
        ControladorPrincipal.tablaActividad=new GestionTablaActividades();
        ControladorPrincipal.tablaInscripcion=new GestionTablaInscripcion();
        
        addListeners(); // Añadimos los eventos de los botones de la 'VistaPrincipal'
        
        // Ponemos el contenedor Frame de la Vista Principal con el estilo 'CardLayout' para sobreponer los distintos Frames encima de este
        this.vPrincipal.getContentPane().setLayout(new CardLayout()); 
        // Añadimos los Paneles de las distintas vistas al Frame de la 'VistaPrincipal'
        this.vPrincipal.add(this.vInicio);
        this.vPrincipal.add(this.vMonitor);
        this.vPrincipal.add(this.vSocio);
        this.vPrincipal.add(this.vActividad);
        this.vPrincipal.add(this.vInscripcion);
        
        this.vPrincipal.setLocationRelativeTo(null); // Situamos la Vista Principal en el centro de la pantalla
        this.vPrincipal.setTitle("Menú de Inicio -> Antonio Abad Hernández Gálvez"); // Escribimos el titulo de la ventana del Frame Principal tras realizar una Conexion correcta
        this.vPrincipal.setVisible(true); // Hacemos visible el Frame Principal
    }

    
    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaPrincipal'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "INICIO": // Boton 'jMenuItem_inicio' de 'VistaPrincipal'
            {
                this.vPrincipal.setTitle("Menú de Inicio -> Antonio Abad Hernández Gálvez");
                mostrarPanel(this.vInicio);
            }; break;
            
            case "MONITOR": // Boton 'jMenuItem_monitores' de 'VistaPrincipal'
            {
                this.vPrincipal.setTitle("Gestión de Monitores -> Antonio Abad Hernández Gálvez");
                mostrarPanel(this.vMonitor);
                GestionTablaMonitores.inicializarTablaMonitores(this.vMonitor);
                GestionTablaMonitores.dibujarTablaMonitores(this.vMonitor);
                this.sesion=this.sessionFactory.openSession();
                this.tr=this.sesion.beginTransaction();
                try
                {
                    ArrayList<Monitor> v_monitores=this.cMonitor.pideMonitores();
                    GestionTablaMonitores.borrarTablaMonitores();
                    GestionTablaMonitores.escribirTablaMonitores(v_monitores);
                    this.tr.commit();
                }
                catch(Exception ex)
                {
                    this.tr.rollback();
                    this.vMensaje.mensaje(null, "ControladorPrincipal/MONITOR: Error en actionPerformed() -> " + ex.getMessage(), "Consulta 'listarTodos() de pideMonitores()", "ERROR");
                }
                finally
                {
                    if(this.sesion!=null && this.sesion.isOpen())
                    {
                        this.sesion.close();
                    }
                }
            }; break;
            
            case "SOCIO": // Boton 'jMenuItem_socios' de 'VistaPrincipal'
            {
                this.vPrincipal.setTitle("Gestión de Socios -> Antonio Abad Hernández Gálvez");
                mostrarPanel(this.vSocio);
                GestionTablaSocios.inicializarTablaSocios(this.vSocio);
                GestionTablaSocios.dibujarTablaSocios(this.vSocio);
                this.sesion=this.sessionFactory.openSession();
                this.tr=this.sesion.beginTransaction();
                try
                {
                    ArrayList<Socio> v_socios=this.cSocio.pideSocios();
                    GestionTablaSocios.borrarTablaSocios();
                    GestionTablaSocios.escribirTablaSocios(v_socios);
                    this.tr.commit();
                }
                catch(Exception ex)
                {
                    this.tr.rollback();
                    this.vMensaje.mensaje(null, "ControladorPrincipal/SOCIO: Error en actionPerformed() -> " + ex.getMessage(), "Consulta 'listarTodos() de pideSocios()", "ERROR");
                }
                finally
                {
                    if(this.sesion!=null && this.sesion.isOpen())
                    {
                        this.sesion.close();
                    }
                }
            }; break;
            
            case "ACTIVIDAD": // Boton 'jMenuItem_actividades' de 'VistaPrincipal'
            {
                this.vPrincipal.setTitle("Gestión de Actividades -> Antonio Abad Hernández Gálvez");
                mostrarPanel(this.vActividad);
                GestionTablaActividades.inicializarTablaActividades(this.vActividad);
                GestionTablaActividades.dibujarTablaActividades(this.vActividad);
                this.sesion=this.sessionFactory.openSession();
                this.tr=this.sesion.beginTransaction();
                try
                {
                    ArrayList<Object[]> v_actividades=this.cActividad.pideActividades();
                    GestionTablaActividades.borrarTablaActividades();
                    GestionTablaActividades.escribirTablaActividades(v_actividades);
                    this.tr.commit();
                }
                catch(Exception ex)
                {
                    this.tr.rollback();
                    this.vMensaje.mensaje(null, "ControladorPrincipal/ACTIVIDAD: Error en actionPerformed() -> " + ex.getMessage(), "Consulta 'listarTodos() de pideActividades()", "ERROR");
                }
                finally
                {
                    if(this.sesion!=null && this.sesion.isOpen())
                    {
                        this.sesion.close();
                    }
                }
            }; break;
            
            case "SALIR": // Boton 'jMenuItem_salir' de 'VistaPrincipal'
            {
                System.exit(0);
            }
            
            case "GestionInscripcion": // Boton 'jMenuItem_inscripcionSocio' de 'VistaPrincipal'
            {
                this.vPrincipal.setTitle("Gestión de Inscripciones -> Antonio Abad Hernández Gálvez");
                mostrarPanel(this.vInscripcion);
                GestionTablaInscripcion.inicializarTablaInscripcion(this.vInscripcion);
                GestionTablaInscripcion.dibujarTablaInscripcion(this.vInscripcion);
                this.sesion=this.sessionFactory.openSession();
                this.tr=this.sesion.beginTransaction();
                try
                {
                    ArrayList<Socio> v_socios=this.cSocio.pideSocios();
                    GestionTablaInscripcion.borrarTablaInscripcion();
                    GestionTablaInscripcion.escribirTablaInscripcion(v_socios);
                    this.tr.commit();
                }
                catch(Exception ex)
                {
                    this.tr.rollback();
                    this.vMensaje.mensaje(null, "ControladorPrincipal/GestionInscripcion: Error en actionPerformed() -> " + ex.getMessage(), "Consulta 'listarTodos() de pideSocios()", "ERROR");
                }
                finally
                {
                    if(this.sesion!=null && this.sesion.isOpen())
                    {
                        this.sesion.close();
                    }
                }
            }; break;
        }
    }
    
    /**
     * Muestra o hace visible un JPanel en concreto en la Pantalla
     * @param nomPanel Es el objeto de tipo JPanel que queremos mostrar, es decir, la Vista que queremos mostrar (VistaInicio, VistaMonitor, VistaSocio, VistaActividad, VistaInscripcion)
     */
    public void mostrarPanel(JPanel nomPanel)
    {
        this.vInicio.setVisible(false);
        this.vMonitor.setVisible(false);
        this.vSocio.setVisible(false);
        this.vActividad.setVisible(false);
        this.vInscripcion.setVisible(false);
        nomPanel.setVisible(true);
    }
};
