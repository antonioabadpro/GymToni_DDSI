/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Socio;
import Modelo.SocioDAO;
import Utilidad.GestionTablaInscripcion;
import Vista.VistaInscripcion;
import Vista.VistaInscripcionCRUD;
import Vista.VistaMensaje;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @version 04/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class ControladorInscripcion implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private SessionFactory sessionFactory=null;
    private Session sesion=null;
    private Transaction tr=null;
    private VistaInscripcion vInscripcion=null;
    private VistaInscripcionCRUD vCRUD_inscripcion=null;
    private ControladorSocio cSocio=null;
    private SocioDAO socioDAO=null;
    private ActividadDAO actividadDAO=null;
    private ControladorActividad cActividad=null;
    private char operacion; // Inscribir ('I'), Eliminar ('E') para saber que operacion debemos realizar cuando presionemos el boton 'Aceptar'
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************
    
    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaInscripcion' y 'VistaInscripcionCRUD' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        this.vInscripcion.jButton_altaActividad.addActionListener(this);
        this.vInscripcion.jButton_bajaActividad.addActionListener(this);
        this.vCRUD_inscripcion.jComboBox_actividad.addActionListener(this);
        this.vCRUD_inscripcion.jButton_aceptarI.addActionListener(this);
        this.vCRUD_inscripcion.jButton_cancelarI.addActionListener(this);
        this.vCRUD_inscripcion.jTextField_nombreSocio.addActionListener(this);
    }
    
    /**
     * Metodo privado auxiliar privado para actualizar el valor del 'jComboBox' para seleccionar una Actividad en 'VistaInscripcionCRUD'
     * @param nombreSocio Es el 'nombre' del Socio que queremos inscribir o eliminar de una Actividad en la BD
     */
    private void actualizarActividades(String nombreSocio)
    {
        int fila_seleccionada;
        int opcion_confirmacion;
        int columna_numeroSocio=0;
        int columna_nomSocio=1;
        Socio s=null;
        Object valor_nomActividad;
        String nomActividad;
        ArrayList<Object[]> v_actividades=new ArrayList<>(); // Contiene todas las Actividades que hay en la BD
        ArrayList<Actividad> v_actividadesSocio=new ArrayList<>(); // Contiene las Actividades en las que el Socio esta inscrito
        ArrayList<Actividad> v_actividadesDisponibles=new ArrayList<>(); // Contiene las Actividades en las que el Socio NO esta inscrito
        
        // Obtenemos el Socio que queremos inscribir o eliminar de una Actividad de la BD
        try
        {
            s=this.cSocio.obtenerSocioPorNombre(nombreSocio);
        }
        catch (Exception ex)
        {
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'obtenerNSocioPorNombre()' -> " + ex.getMessage() + "\n");
            Logger.getLogger(ControladorInscripcion.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Obtenemos todas las Actividades de la BD
        try
        {
            v_actividades=this.cActividad.pideActividades();
        }
        catch (Exception ex)
        {
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'pideActividades()' de 'actualizarActividades()' -> " + ex.getMessage() + "\n");
            Logger.getLogger(ControladorInscripcion.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Obtenemos las Actividades en las que esta inscrito el Socio que hemos seleccionado
        try
        {
            v_actividadesSocio=this.cSocio.obtenerActividadesSocio(s.getNumeroSocio());
        }
        catch (Exception ex)
        {
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'obtenerActividadesSocio()' de 'actualizarActividades()' -> " + ex.getMessage() + "\n");
            Logger.getLogger(ControladorInscripcion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Obtenemos todas las Actividades en las que NO esta inscrito el Socio que hemos seleccionado
        try
        {
            v_actividadesDisponibles=this.cSocio.obtenerNOActividadesSocio(s.getNumeroSocio());
        }
        catch (Exception ex)
        {
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'obtenerNOActividadesSocio()' de 'actualizarActividades()' -> " + ex.getMessage() + "\n");
            Logger.getLogger(ControladorInscripcion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Vaciamos los campos del 'JDialog'
        this.vCRUD_inscripcion.jTextField_codigo.setText("");
        this.vCRUD_inscripcion.jTextField_nombreSocio.setText("");
        this.vCRUD_inscripcion.jComboBox_actividad.removeAllItems();
        
        // Rellenamos los campos del 'JDialog'
        this.vCRUD_inscripcion.jTextField_codigo.setText(s.getNumeroSocio());
        this.vCRUD_inscripcion.jTextField_nombreSocio.setText(nombreSocio);
        if(this.operacion=='I') // Si queremos Dar de Alta a un Socio en una Actividad
        {
            if(v_actividadesSocio!=null && v_actividades.size()==v_actividadesSocio.size()) // Si el Socio esta inscrito en todas las Actividades, mostramos un mensaje de error
            {
                this.vMensaje.mensaje(this.vInscripcion, "El Socio (" + nombreSocio + ") esta inscrito en todas las Actividades", "ERROR al Dar de Alta", "ERROR");
            }
            else // Si el Socio NO esta inscrito en todas las Actividades, Escribimos las Actividades en las que NO esta inscrito el Socio en el 'jComboBox'
            {
                for(Actividad a: v_actividadesDisponibles)
                {
                    String nom_actividad=a.getNombre();
                    this.vCRUD_inscripcion.jComboBox_actividad.addItem(nom_actividad);
                }
            }
        }
        if(this.operacion=='E') // Si queremos Dar de Baja a un Socio de una Actividad
        {
            if(v_actividadesSocio==null) // Si el Socio NO esta inscrito en ninguna Actividad mostramos un mensaje de error
            {
                this.vMensaje.mensaje(this.vInscripcion, "El Socio (" + nombreSocio + ") NO esta inscrito en ninguna Actividad", "ERROR al Dar de Baja", "ERROR");
            }
            else // Si el Socio esta inscrito en alguna Actividad añadimos dichas Actividades al 'jComboBox'
            {
                // Escribimos las Actividades en las que esta inscrito el Socio en el 'jComboBox'
                for(Actividad a: v_actividadesSocio)
                {
                    String nom_actividad=a.getNombre();
                    this.vCRUD_inscripcion.jComboBox_actividad.addItem(nom_actividad);
                }
            }
        }
    }
    
    /**
     * Metodo privado auxiliar para actualizar/refrescar los datos de la Tabla de las Inscripciones de la BD tras inscribir un Socio en una Actividad de la BD
     */
    private void refrescarTablaInscripcion()
    {
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
            this.vMensaje.mensaje(null, "ControladorInscripcion: Error en 'refrescarTablaInscripcion()'" + ex.getMessage(), "Consulta 'listarTodos()' de 'pideSocios()'", "ERROR");
        }
        finally
        {
            if(this.sesion!=null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
    }
    
    
    // ********************************* METODOS PUBLICOS *********************************
    
    /**
     * Consutructor de 'ControladorInscripcion'
     * @param sesionF Sesion abierta desde el 'actionPerormed' de la Clase 'ControladorConexion'
     * @param vInscripcion Objeto de tipo 'VistaInscripcion' para poder acceder a los botones de 'VistaInscripcion'
     * @param vCRUD_inscripcion Objeto de tipo 'VistaInscripcionCRUD' para poder acceder a los botones de 'VistaInscripcionCRUD'
     * @param cSocio Objeto de tipo 'ControladorSocio' para poder realizar una Consulta sobre un Socio
     * @param cActividad Objeto de tipo 'ControladorActividad' para poder realizar una Consulta sobre una Actividad
     */
    public ControladorInscripcion(SessionFactory sesionF, VistaInscripcion vInscripcion, VistaInscripcionCRUD vCRUD_inscripcion, ControladorSocio cSocio, ControladorActividad cActividad)
    {
        this.sessionFactory=sesionF;
        this.sesion=this.sessionFactory.openSession();
        this.vMensaje=new VistaMensaje();
        this.vInscripcion=vInscripcion;
        this.cSocio=cSocio;
        this.socioDAO=new SocioDAO();
        this.actividadDAO=new ActividadDAO();
        this.cActividad=cActividad;
        this.operacion=' ';
        // Creamos el JDialog de 'VistaInscripcionCRUD'
        this.vCRUD_inscripcion=vCRUD_inscripcion;
        this.vCRUD_inscripcion.setLocationRelativeTo(null);
        this.vCRUD_inscripcion.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.vCRUD_inscripcion.setResizable(false);
        
        addListeners(); // Añadimos los eventos de los botones de 'VistaInscripcion' y 'VistaInscripcionCRUD'
    }

    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaInscripcion' y 'VistaInscripcionCRUD'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "AltaActividad": // Boton 'jButton_altaActividad' de 'VistaInscripcion'
            {
                int fila_seleccionada;
                int columna_numeroSocio=0;
                int columna_nomSocio=1;
                String numeroSocio, nombreSocio;
                Socio s;
                
                this.operacion='I';
                
                fila_seleccionada=this.vInscripcion.jTable_inscripcionSocios.getSelectedRow();
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Inscripción Socio", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    numeroSocio=this.vInscripcion.jTable_inscripcionSocios.getValueAt(fila_seleccionada, columna_numeroSocio).toString();
                    nombreSocio=this.vInscripcion.jTable_inscripcionSocios.getValueAt(fila_seleccionada, columna_nomSocio).toString();
                    
                    // Inicializamos los campos del 'jComboBox' con las Actividades en las que NO esta inscrito el Socio
                    actualizarActividades(nombreSocio);
                    this.vCRUD_inscripcion.setTitle("Dar de Alta a un Socio en una Actividad");
                    if(this.vCRUD_inscripcion.jComboBox_actividad.getItemCount()>0)
                    {
                        this.vCRUD_inscripcion.setVisible(true);
                    }
                }
            }; break;
            
            case "BajaActividad": // Boton 'jButton_bajaActividad' de 'VistaInscripcion'
            {
                int fila_seleccionada;
                int columna_numeroSocio=0;
                int columna_nomSocio=1;
                String numeroSocio, nombreSocio;
                Socio s;
                
                this.operacion='E';
                
                fila_seleccionada=this.vInscripcion.jTable_inscripcionSocios.getSelectedRow();
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Inscripción Socio", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    numeroSocio=this.vInscripcion.jTable_inscripcionSocios.getValueAt(fila_seleccionada, columna_numeroSocio).toString();
                    nombreSocio=this.vInscripcion.jTable_inscripcionSocios.getValueAt(fila_seleccionada, columna_nomSocio).toString();
                    
                    // Inicializamos los campos del 'jComboBox' con las Actividades en las que esta inscrito el Socio
                    actualizarActividades(nombreSocio);
                    this.vCRUD_inscripcion.setTitle("Dar de Baja a un Socio de una Actividad");
                    if(this.vCRUD_inscripcion.jComboBox_actividad.getItemCount()>0)
                    {
                        this.vCRUD_inscripcion.setVisible(true);
                    }
                }
            }; break;
            
            case "AceptarI": // Boton 'jButton_aceptarI' de 'VistaInscripcionCRUD'
            {
                int opcion_confirmacion=-1;
                String nombreSocio;
                Socio s=null;
                Object valor_nomActividad;
                String nomActividad;

                // Obtenemos el valor de los campos de la Nueva Actividad que queremos asignarle al Socio seleccionado
                valor_nomActividad=this.vCRUD_inscripcion.jComboBox_actividad.getSelectedItem();
                if(valor_nomActividad==null || valor_nomActividad.equals("")) // Si NO hemos seleccionado ninguna Actividad
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna Actividad", "Advertencia en Dar de Alta a un Socio", "AVISO");
                }
                else // Si hemos seleccionado una Actividad
                {
                    nomActividad=valor_nomActividad.toString();
                    nombreSocio=this.vCRUD_inscripcion.jTextField_nombreSocio.getText();
                    
                    try
                    {
                        s=this.cSocio.obtenerSocioPorNombre(nombreSocio);
                    }
                    catch (Exception ex)
                    {
                        this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'obtenerSocioPorNombre()' -> " + ex.getMessage() + "\n");
                        Logger.getLogger(ControladorInscripcion.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Mostramos una ventana para confirmar si queremos inscribir o eliminar al Socio de la Actividad
                    if(this.operacion=='I')
                    {
                        opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vInscripcion, "¿Estás seguro que deseas Dar de Alta el Socio seleccionado (" + nombreSocio + ") en la Actividad (" + nomActividad + ")?", "Confirmacion Inscribir Socio en una Actividad");
                    }
                    if(this.operacion=='E')
                    {
                        opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vInscripcion, "¿Estás seguro que deseas Dar de Baja el Socio seleccionado (" + nombreSocio + ") de la Actividad (" + nomActividad + ")?", "Confirmacion Eliminar Socio de una Actividad");
                    }
                    switch(opcion_confirmacion)
                    {
                        case 0: // Pulsamos el boton 'YES'
                        {
                            if(this.operacion=='I') // Inscribimos el Socio en la Actividad seleccionada en el 'jComboBox'
                            {
                                altaSocioActividad(s.getNumeroSocio(), nomActividad);
                                this.vCRUD_inscripcion.setVisible(false); // Ocultamos el JDialog
                            }
                            if(this.operacion=='E') // Eliminamos el Socio de la Actividad seleccionada en el 'jComboBox'
                            {
                                bajaSocioActividad(s.getNumeroSocio(), nomActividad);
                                this.vCRUD_inscripcion.setVisible(false); // Ocultamos el JDialog
                            }

                            // Mostramos la Tabla de los Inscriptores actualizada correctamente
                            refrescarTablaInscripcion();
                        }; break;

                        case 1: // Pulsamos el boton 'NO'
                        {
                            this.vMensaje.mensaje(null, "Inscripcion de un Socio CANCELADA", "Operacion Cancelada", "CANCELAR");
                        }; break;

                        case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                        {

                        }; break;
                    }
                }
            }; break;
            
            case "CancelarI": // Boton 'jButton_cancelarI' de 'VistaInscripcionCRUD'
            {
                if(this.operacion=='I')
                {
                    this.vMensaje.mensaje(null, "Operación de Incripción de un Socio en una Actividad CANCELADA", "Operacion Cancelada", "CANCELAR");
                }
                if(this.operacion=='E')
                {
                    this.vMensaje.mensaje(null, "Operación de Borrado de un Socio de una Actividad CANCELADA", "Operacion Cancelada", "CANCELAR");
                }
                this.vCRUD_inscripcion.setVisible(false); // Ocultamos el 'JDialog'
            }; break;
        }
    }
    
    /**
     * Permite insertar/inscribir a un Socio de una Actividad de la BD
     * @param numeroSocio Es el 'numeroSocio' del Socio que queremos inscribir en una Actividad de la BD
     * @param nomActividad Es el 'nombre' de la Actividad en la que queremos inscribir al Socio de la BD.
     */
    public void altaSocioActividad(String numeroSocio, String nomActividad)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            // Insertamos el Socio en la Actividad seleccionada de la BD
            Socio s = this.sesion.get(Socio.class, numeroSocio); // Obtenemos el Socio con el 'numeroSocio' introducido por parametro
            Actividad a=this.actividadDAO.buscarPorNombre(this.sesion, nomActividad); // Obtenemos la Actividad con el 'idActividad' introducido por parametro
            
            this.socioDAO.inscribirSocioEnActividad(this.sesion, s, a);
            this.tr.commit();
            this.vMensaje.mensaje(null, "Socio '" + numeroSocio + "' inscrito con éxito de la Actividad '" + nomActividad + "'", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nSocio '" + numeroSocio + "' inscrito con éxito de la Actividad '" + nomActividad + "'\n");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorInscripcion: Error en 'altaSocioActividad()' al inscribir el Socio en una Actividad de la BD -> " + e.getMessage(), "Operacion CRUD 'altaSocioActividad()'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'altaSocioActividad()' al inscribir el Socio en una Actividad de la BD -> " + e.getMessage() + "\n");
        }
        finally
        {
            if(this.sesion != null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
    }

    /**
     * Permite eliminar/borrar a un Socio de una Actividad de la BD
     * @param numeroSocio Es el 'numeroSocio' del Socio del que queremos eliminar de una Actividad de la BD
     * @param nomActividad Es el 'nombre' de la Actividad de la que queremos eliminar al Socio de la BD.
     */
    public void bajaSocioActividad(String numeroSocio, String nomActividad)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            // Eliminamos el Socio de la Actividad seleccionada de la BD
            Socio eliminarSocio = this.sesion.get(Socio.class, numeroSocio);
            Socio s = this.sesion.get(Socio.class, numeroSocio); // Obtenemos el Socio con el 'numeroSocio' introducido por parametro
            Actividad a=this.actividadDAO.buscarPorNombre(this.sesion, nomActividad); // Obtenemos la Actividad con el 'idActividad' introducido por parametro
            
            this.socioDAO.eliminarSocioDeActividad(this.sesion, s, a);
            this.tr.commit();
            this.vMensaje.mensaje(null, "Socio '" + numeroSocio + "' eliminado con éxito de la Actividad '" + nomActividad + "'", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nSocio '" + numeroSocio + "' eliminado con éxito de la Actividad '" + nomActividad + "'\n");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorInscripcion: Error en 'bajaSocioActividad()' al eliminar el Socio de una Actividad de la BD -> " + e.getMessage(), "Operacion CRUD 'bajaSocioActividad()'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorInscripcion: Error en 'bajaSocioActividad()' al eliminar el Socio de una Actividad de la BD -> " + e.getMessage() + "\n");
        }
        finally
        {
            if(this.sesion != null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
    }
    
};
