/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Monitor;
import Utilidad.GestionTablaActividades;
import Vista.VistaActividad;
import Vista.VistaActividadCRUD;
import Vista.VistaMensaje;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class ControladorActividad implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private SessionFactory sessionFactory=null;
    private Session sesion=null;
    private Transaction tr=null;
    private ActividadDAO actividadDAO=null;
    private VistaActividad vActividad=null;
    private VistaActividadCRUD vCRUD_actividad=null;
    private ControladorMonitor cMonitor=null;
    private char operacion; // Insertar ('I'), Actualizar ('A') para saber si debemos comprobar si existe una Actividad con el 'nombre' introducido o NO
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************
    
    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaActividad' y 'VistaActividadCRUD' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        // Añadimos los Botones de 'VistaActividad' para realizar una operacion (Alta, Baja, Modificacion de un Socio)
        this.vActividad.jButton_nuevaActividad.addActionListener(this);
        this.vActividad.jButton_borrarActividad.addActionListener(this);
        this.vActividad.jButton_actualizarActividad.addActionListener(this);
        // Añadimos los botones de 'VistaActividadCRUD' para aceptar o cancelar una operacion (Alta, Modificacion)
        this.vCRUD_actividad.jButton_aceptarA.addActionListener(this);
        this.vCRUD_actividad.jButton_cancelarA.addActionListener(this);
    }
    
    /**
     * Metodo privado auxiliar para crear el 'idActividad' de la nueva Actividad que queremos insertar (Formato -> A001)
     * @return Devuelve el 'idActividad' de la nueva Actividad que queremos insertar
     */
    private String generarIdActividad()
    {
        String idActividad_ultimo; // 'idActividad' de la ultima Actividad de la Tabla
        int nuevo_idActividad; // Nº del nuevo 'idActividad'
        String idActividad=null; // 'idActividad' de la nueva Actividad que queremos insertar          

        // Obtenemos el ultimo 'idActividad' de la BD
        idActividad_ultimo=GestionTablaActividades.getCodUltima();
        if(idActividad_ultimo==null) // Si NO hay Actividades en la BD
        {
            idActividad_ultimo="A01";
        }
        else // Si hay Actividades en la BD
        {
            idActividad_ultimo=idActividad_ultimo.substring(2); // Eliminamos los 2 primeros caracteres del 'idActividad_ultimo' para solo obtener el numero (AC01 -> 01)
            nuevo_idActividad=Integer.parseInt(idActividad_ultimo)+1; // Sumamos 1 al ultimo numero obtenido
            idActividad="AC"+String.format("%02d", nuevo_idActividad); // Convertimos el numero en String con 2 digitos y le concatenamos 'AC'
        }
        return idActividad;
    }
    
    /**
     * Metodo privado auxiliar para inicializar los campos del 'JDialog' de 'VistaActividadCRUD'
     */
    private void inicializarCamposVistaCRUD()
    {
        String idActividad;
        ArrayList<Monitor> v_monitores=new ArrayList<>();
                
        idActividad=generarIdActividad();
        // Escribimos el 'idActividad' generado y dejamos el resto de campos vacios
        this.vCRUD_actividad.jTextField_codigo.setText(idActividad);
        this.vCRUD_actividad.jTextField_nombre.setText("");
        // Añadimos los dias al 'jComboBox_dia' (Lunes, Martes, Miércoles, Jueves, Viernes, Sábado, Domingo)
        this.vCRUD_actividad.jComboBox_dia.removeAllItems(); // Eliminamos todos los elementos del 'jComboBox'
        this.vCRUD_actividad.jComboBox_dia.addItem("-");
        this.vCRUD_actividad.jComboBox_dia.addItem("Lunes");
        this.vCRUD_actividad.jComboBox_dia.addItem("Martes");
        this.vCRUD_actividad.jComboBox_dia.addItem("Miércoles");
        this.vCRUD_actividad.jComboBox_dia.addItem("Jueves");
        this.vCRUD_actividad.jComboBox_dia.addItem("Viernes");
        this.vCRUD_actividad.jComboBox_dia.addItem("Sábado");
        this.vCRUD_actividad.jComboBox_dia.addItem("Domingo");
        this.vCRUD_actividad.jComboBox_dia.setSelectedItem("-");
        // Añadimos las horas al 'jComboBox_hora' (7-23)
        this.vCRUD_actividad.jComboBox_hora.removeAllItems(); // Eliminamos todos los elementos del 'jComboBox'
        this.vCRUD_actividad.jComboBox_hora.addItem("-");
        for(Integer i=7; i<=23; i++)
        {
            String valor=i.toString();
            this.vCRUD_actividad.jComboBox_hora.addItem(valor);
        }
        this.vCRUD_actividad.jComboBox_hora.setSelectedItem("-");
        this.vCRUD_actividad.jTextField_precio.setText("");
        this.vCRUD_actividad.jTextArea_descripcion.setText("");
        // Añadimos los distintos Monitores Responsables al 'jComboBox' obtenidos en una consulta
        this.vCRUD_actividad.jComboBox_monitorResponsable.removeAllItems(); // Eliminamos todos los elementos del 'jComboBox'
        this.vCRUD_actividad.jComboBox_monitorResponsable.addItem("-");

        // Obtenemos los Monitores que hay en la BD para mostrarlos como posibles Monitores Responsables de una Actividaad
        this.sesion=this.sessionFactory.openSession();
        this.tr=this.sesion.beginTransaction();
        try
        {
            v_monitores=this.cMonitor.pideMonitores();
            for(Monitor m: v_monitores)
            {
                this.vCRUD_actividad.jComboBox_monitorResponsable.addItem(m.getNombre() + " (" + m.getCodMonitor() + ")");
            }
            this.tr.commit();
        }
        catch(Exception ex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorActividad/NuevaActividad: Error en actionPerformed() -> " + ex.getMessage(), "Consulta 'listarTodos() de pideMonitores()", "ERROR");
        }
        finally
        {
            if(this.sesion!=null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
        this.vCRUD_actividad.jComboBox_monitorResponsable.setSelectedItem("-");
    }
    
    /**
     * Metodo privado auxiliar para saber si los datos de la Actividad estan introducidos correctamente cumpliendo los campos obligatorios y el formato de cada campo
     * @param idActividad 'idActividad' que queremos comprobar (OBLIGATORIO)
     * @param nombre 'nombre' que queremos comprobar (OBLIGATORIO)
     * @param dia 'dia' que queremos comprobar (OBLIGATORIO)
     * @param hora 'hora' que queremos comprobar (OBLIGATORIO)
     * @param descripcion 'descripcion' que queremos comprobar
     * @param precioBaseMes 'precioBaseMes' que queremos comprobar (OBLIGATORIO y PRECIO)
     * @param cod_monitorResponsable 'codigoMonitor' del Monitor Responsable que queremos comprobar (NO puede tener varias actividades el mismo dia y a la misma hora)
     * @return Devuelve 'true' si los datos son correctos y 'false' en caso contrario
     */
    private boolean comprobarDatosActividad(String idActividad, String nombre, String dia, int hora, String descripcion, int precioBaseMes, String cod_monitorResponsable)
    {
        boolean es_correcto=true;
        boolean monitor_ocupado=false;
        
        // Comprobamos que todos los campos obliagatorios estan rellenos
        if(idActividad.isEmpty()==true || nombre.isEmpty()==true || dia.equals("-") || hora==-1 || precioBaseMes==-1)
        {
            es_correcto=false;
            StringBuilder camposVacios = new StringBuilder();

            if (idActividad.isEmpty()==true)
            {
                camposVacios.append("'Código'");
            }
            if (nombre.isEmpty()==true)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Nombre'");
            }
            if (dia.equals("-"))
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Día'");
            }
            if (hora==-1)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Hora'");
            }
            if (precioBaseMes==-1)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Precio'");
            }

            // Mostramos el mensaje por Pantalla
            this.vMensaje.mensaje(this.vCRUD_actividad, "Los campos " + camposVacios + " son obligatorios", "Restricción Campos Obligatorios", "AVISO");
        }
        else
        {
            if(precioBaseMes<0) // Si el 'precioBaseMes' es Negativo
            {
                es_correcto=false;
                this.vMensaje.mensaje(this.vCRUD_actividad, "El Precio NO puede ser negativo", "Restricción Precio Negativo", "AVISO");
            }
            else // Comprobamos que el Monitor Responsable de la Actividad NO tenga varias actividades el mismo dia y a la misma hora
            {
                if(cod_monitorResponsable!=null && cod_monitorResponsable.equals("-")==false)
                {
                    try
                    {
                        monitor_ocupado=obtenerMonitorOcupado(cod_monitorResponsable, dia, hora);
                        if(monitor_ocupado==true)
                        {
                            es_correcto=false;
                            this.vMensaje.mensaje(this.vCRUD_actividad, "El Monitor Responsable '" + cod_monitorResponsable + "' ya tiene una Actividad programada en esa fecha", "Restricción Monitor Responsable", "AVISO");
                        }
                    }
                    catch (Exception ex)
                    {
                        this.vMensaje.mostrar("\n\nControladorActividad: Error en 'obtenerMonitorOcupado()' -> " + ex.getMessage());
                        Logger.getLogger(ControladorActividad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return es_correcto;
    }
    
    /**
     * Metodo privado auxiliar para actualizar/refrescar los datos de la Tabla de las Actividades de la BD tras insertar una nueva Actividad en la BD
     */
    private void refrescarTablaActividad()
    {
        this.sesion=this.sessionFactory.openSession();
        this.tr=this.sesion.beginTransaction();
        try
        {
            ArrayList<Object[]> v_actividades=pideActividades();
            GestionTablaActividades.borrarTablaActividades();
            GestionTablaActividades.escribirTablaActividades(v_actividades);
            this.tr.commit();
        }
        catch(Exception ex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorActividad: Error en 'refrescarTablaActividad()'" + ex.getMessage(), "Consulta 'listarTodos()' de 'pideActividades()'", "ERROR");
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
     * Consutructor de 'ControladorActividad'
     * @param sesionF Sesion abierta desde el 'actionPerormed' de la Clase 'ControladorConexion'
     * @param vActividad Objeto de tipo 'VistaActividad' para poder acceder a los botones de 'VistaActividad'
     * @param vCRUD_actividad Objeto de tipo 'VistaActividadCRUD' para poder acceder a los botones de 'VistaActividadCRUD'
     * @param cMonitor Objeto de tipo 'ControladorMonitor' para poder realizar una Consulta sobre un Monitor
     */
    public ControladorActividad(SessionFactory sesionF, VistaActividad vActividad, VistaActividadCRUD vCRUD_actividad, ControladorMonitor cMonitor)
    {
        this.sessionFactory=sesionF;
        this.sesion=this.sessionFactory.openSession();
        this.vActividad=vActividad;
        this.vMensaje=new VistaMensaje();
        this.actividadDAO=new ActividadDAO();
        this.cMonitor=cMonitor;
        this.operacion=' ';
        // Creamos el JDialog de 'VistaActividadCRUD'
        this.vCRUD_actividad=vCRUD_actividad;
        this.vCRUD_actividad.setLocationRelativeTo(null);
        this.vCRUD_actividad.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.vCRUD_actividad.setResizable(false);
        
        addListeners(); // Añadimos los eventos de los botones de 'VistaActividad' y 'VistaActividadCRUD'
    }

    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaActividad' y 'VistaActividadCRUD'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "NuevaActividad": // Boton 'jButton_nuevaActividad' de 'VistaActividad'
            {
                this.operacion='I';
                inicializarCamposVistaCRUD();
                this.vCRUD_actividad.setTitle("Nueva Actividad"); // Escribimos el titulo del JDialog
                this.vCRUD_actividad.setVisible(true); // Mostramos el JDialog
            }; break;
            
            case "AceptarActividad": // Boton 'jButton_aceptarActividad' de 'VistaActividadCRUD'
            {
                String idActividad, nombre, dia, descripcion, nom_monitorResponsable=null;
                String valor_hora=null, valor_precio=null;
                int hora;
                int precio;
                Monitor monitorResponsable=null;
                String cod_monitorResponsable=null;
                String valor_monitorResponsable; // Contiene el 'nombre' y el 'codMonitor con el siguiente formato -> "nombre (codMonitor)"
                String partes[]; // Contiene la division del array para obtener solo el 'nombre' del Monitor que queremos insertar en el campo 'monitorResponsable'
                int opcion_confirmacion=-1; // Valor devuelto tras seleccionar una opcion del 'JOptionPane'
                boolean datos_correctos=false; // Variable que contiene si los datos de la nueva Actividad que vamos a insertar son correctos o NO
                boolean existe_actividad=false;
                
                // Obtenemos el valor de los campos de la Nueva Actividad que queremos insertar introducidos en el 'JDialog'
                idActividad=this.vCRUD_actividad.jTextField_codigo.getText();
                nombre=this.vCRUD_actividad.jTextField_nombre.getText();
                dia=this.vCRUD_actividad.jComboBox_dia.getSelectedItem().toString();
                valor_hora=this.vCRUD_actividad.jComboBox_hora.getSelectedItem().toString();
                if(valor_hora.equals("-")) // Si NO hemos seleccionado ninguna hora
                {
                    hora=-1;
                }
                else // Si hemos seleccionado una hora
                {
                    hora=Integer.parseInt(valor_hora);
                }
                descripcion=this.vCRUD_actividad.jTextArea_descripcion.getText();
                valor_precio=this.vCRUD_actividad.jTextField_precio.getText();
                if(valor_precio.equals("")) // Si NO hemos escrito ningun Precio, es decir, si el campo 'precioBaseMes' esta vacio
                {
                    precio=-1;
                }
                else // Si hemos escrito un Precio
                {
                    precio=Integer.parseInt(valor_precio);
                }
                valor_monitorResponsable=this.vCRUD_actividad.jComboBox_monitorResponsable.getSelectedItem().toString(); // Contiene el 'nombre' y el 'codMonitor con el siguiente formato -> "nombre (codMonitor)"
                if(valor_monitorResponsable.equals("-")) // Si el campo 'monitorResposable' NO esta relleno
                {
                    nom_monitorResponsable=null;
                }
                else // Si el campo 'monitorResposable' esta relleno, obtenemos el 'nombre' del Monitor y lo almacenamos en la variable 'nom_monitorResponsable'
                {
                    partes = valor_monitorResponsable.split("\\s*\\(\\s*"); // Divide el 'String' hasta antes del paréntesis '('
                    nom_monitorResponsable = partes[0]; // El nombre esta en la primera posicion del array
                    try
                    {
                        // Obtenemos el campo 'monitorResposable' del Monitor cuyo nombre hemos seleccionado en el 'jComboBox_monitorResponsable'
                        monitorResponsable=this.cMonitor.pideMonitorPorNombre(nom_monitorResponsable);
                        cod_monitorResponsable=monitorResponsable.getCodMonitor(); // Obtenemos el 'codMonitor' del Monitor para utilizarlo en 'comprobarDatosActividad()'
                    }
                    catch (Exception ex)
                    {
                        this.vMensaje.mostrar("\n\nControladorActividad: Error en 'pideMonitorPorNombre()' -> " + ex.getMessage());
                        Logger.getLogger(ControladorActividad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Mostramos una ventana para confirmar si queremos insertar/modificar la Nueva Actividad en la BD
                opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vActividad, "¿Estás seguro que deseas insertar/modificar la Actividad (" + nombre + ")?", "Confirmacion Insertar/Modificar Actividad");
                switch(opcion_confirmacion)
                {
                    case 0: // Pulsamos el boton 'YES'
                    {
                        // Comprobamos que el formato de los campos es correcto antes de insertar/modificar la nueva Actividad en la BD
                        datos_correctos=comprobarDatosActividad(idActividad, nombre, dia, hora, descripcion, precio, cod_monitorResponsable);
                        
                        if(datos_correctos==true)
                        {
                            if(this.operacion=='I') // Si queremos insertar una nueva Actividad en la BD
                            {
                                // Comprobamos si existe una Actividad con el 'nombre' introducido en la BD
                                try
                                {
                                    existe_actividad=compruebaExisteActividad(nombre);
                                    if(existe_actividad==true) // Si existe una Actividad con el 'nombre' introducido, mostramos un mensaje de error
                                    {
                                        this.vMensaje.mensaje(this.vActividad, "Ya existe una Actividad con el Nombre introducido '" + nombre + "'", "Actividad ya existente", "ERROR");
                                        this.vMensaje.mostrar("\n\nYa existe una Actividad con el Nombre introducido '" + nombre + "'");
                                    }
                                    else // Si NO existe una Actividad con el 'nombre' introducido, insertamos la Actividad en la BD
                                    {
                                        altaActividad(idActividad, nombre, dia, hora, descripcion, precio, monitorResponsable);
                                        this.vCRUD_actividad.setVisible(false); // Ocultamos el 'JDialog'
                                    }
                                }
                                catch (Exception ex)
                                {
                                    this.vMensaje.mensaje(this.vActividad, "ControladorActividad: Error en 'compruebaExisteActividad()' -> " + ex.getMessage(), "Error en 'existeActividad()' de 'compruebaExisteActividad()'", "ERROR");
                                    this.vMensaje.mostrar("\n\n\"ControladorActividad: Error en 'compruebaExisteActividad()' ->" + ex.getMessage() + "\n");
                                    Logger.getLogger(ControladorActividad.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if(this.operacion=='A') // Si queremos actualizar los datos de una Actividad de la BD
                            {
                                // Actualizamos los datos de la Actividad en la BD
                                altaActividad(idActividad, nombre, dia, hora, descripcion, precio, monitorResponsable);
                                this.vCRUD_actividad.setVisible(false); // Ocultamos el 'JDialog' 
                            }
                            
                            // Mostramos la Tabla de las Actividades actualizada correctamente
                            refrescarTablaActividad();
                        }
                    }; break;

                    case 1: // Pulsamos el boton 'NO'
                    {
                        this.vMensaje.mensaje(null, "Operacion CRUD de la Actividad CANCELADA", "Operacion Cancelada", "CANCELAR");
                    }; break;

                    case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                    {

                    }; break;
                }
            }; break;
            
            case "CancelarActividad": // Boton 'jButton_cancelarActividad' de 'VistaActividadCRUD'
            {
                this.vMensaje.mensaje(null, "Operacion CRUD de la Actividad CANCELADA", "Operacion Cancelada", "CANCELAR");
                this.vCRUD_actividad.setVisible(false); // Ocultamos el 'JDialog'
            }; break;
            
            case "BorrarActividad": // Boton 'jButton_borrarActividad' de 'VistaActividad'
            {
                int fila_seleccionada;
                int opcion_confirmacion;
                int columna_idActividad=0;
                String idActividad_eliminar; // 'idActividad' de la Actividad que queremos eliminar de la BD
                int columna_nomActividad=1;
                String nomActividad_eliminar; // 'nombre' de la Actividad que queremos eliminar de la BD
                
                fila_seleccionada=this.vActividad.jTable_actividades.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Borrar Socio", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    idActividad_eliminar=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, columna_idActividad).toString();
                    nomActividad_eliminar=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, columna_nomActividad).toString();
                    
                    // Mostramos una ventana para confirmar si queremos eliminar una Actividad de la BD
                    opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vActividad, "¿Estás seguro que deseas eliminar la Actividad seleccionada (" + nomActividad_eliminar + ")?", "Confirmacion Borrar Actividad");
                    switch(opcion_confirmacion)
                    {
                        case 0: // Pulsamos el boton 'YES'
                        {
                            // Eliminamos la Actividad en la BD
                            bajaActividad(idActividad_eliminar);
                            this.vCRUD_actividad.setVisible(false); // Ocultamos el 'JDialog'
                            
                            // Mostramos la Tabla de las Actividades actualizada correctamente
                            refrescarTablaActividad();
                        }; break;

                        case 1: // Pulsamos el boton 'NO'
                        {
                            this.vMensaje.mensaje(null, "Borrado de una Actividad CANCELADA", "Operacion Cancelada", "CANCELAR");
                        }; break;

                        case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                        {

                        }; break;
                    }
                }   
            }; break;
            
            case "ActualizarActividad": // Boton 'jButton_actualizarActividad' de 'VistaActividad'
            {
                String idActividad_modificar; // 'idActividad' de la Actividad que queremos modificar en la BD
                String nombre, dia, descripcion=null, precio, nom_monitorResponsable=null, cod_monitorResponsable=null;
                String valor_hora=null;
                Monitor monitorResponsable;
                int hora=-1;
                Object valor_descripcion=null, valor_monitorResponsable=null;
                int fila_seleccionada;
                
                this.operacion='A';
                fila_seleccionada=this.vActividad.jTable_actividades.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Actualizar Actividad", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    // Obtenemos los valores de la Actividad que queremos modificar
                    idActividad_modificar=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 0).toString();
                    nombre=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 1).toString();
                    dia=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 2).toString();
                    valor_hora=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 3).toString();
                    hora=Integer.parseInt(valor_hora);
                    valor_descripcion=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 4);
                    if(valor_descripcion!=null)
                    {
                        descripcion=valor_descripcion.toString();
                    }
                    precio=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 5).toString();
                    valor_monitorResponsable=this.vActividad.jTable_actividades.getValueAt(fila_seleccionada, 6);
                    if(valor_monitorResponsable!=null)
                    {
                        nom_monitorResponsable=valor_monitorResponsable.toString();
                        // Obtenemos el 'codMonitor' del Monitor responsable, ya que el formato en el 'jComboBox_monitorResponsable' es "nombre (codMonitor)"
                        try
                        {
                            // Obtenemos el campo 'monitorResposable' del Monitor cuyo nombre hemos seleccionado en el 'jComboBox_monitorResponsable'
                            monitorResponsable=this.cMonitor.pideMonitorPorNombre(nom_monitorResponsable);
                            cod_monitorResponsable=monitorResponsable.getCodMonitor(); // Obtenemos el 'codMonitor' del Monitor
                        }
                        catch (Exception ex)
                        {
                            this.vMensaje.mostrar("\n\nControladorActividad: Error en 'pideMonitorPorNombre()' -> " + ex.getMessage());
                            Logger.getLogger(ControladorActividad.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    // Mostramos el JDialog para rellenar los datos de la Actividad que queremos modificar
                    inicializarCamposVistaCRUD();
                    this.vCRUD_actividad.jTextField_codigo.setText(idActividad_modificar);
                    this.vCRUD_actividad.jTextField_nombre.setText(nombre);
                    this.vCRUD_actividad.jComboBox_dia.setSelectedItem(dia);
                    this.vCRUD_actividad.jComboBox_hora.setSelectedItem(valor_hora);
                    this.vCRUD_actividad.jTextArea_descripcion.setText(descripcion);
                    this.vCRUD_actividad.jTextField_precio.setText(precio);
                    if(valor_monitorResponsable!=null) // Si hay un Monitor Responsable seleccionado para la actividad
                    {
                        this.vCRUD_actividad.jComboBox_monitorResponsable.setSelectedItem(nom_monitorResponsable + " (" + cod_monitorResponsable + ")");
                    }
                    this.vCRUD_actividad.setTitle("Actualizar Actividad"); // Escribimos el titulo del JDialog
                    this.vCRUD_actividad.setVisible(true); // Mostramos el JDialog
                }
            }; break;
        }
    }
    
    /**
     * Llama al metodo 'listarActividades()' que es una Consulta de 'ActividadDAO'
     * @return Devuelve todas las Actividades que hay en la BD
     * @throws Exception Lanza una excepcion si NO hay ninguna Actividad en la BD
     */
    public ArrayList<Object[]> pideActividades() throws Exception
    {
        this.sesion=this.sessionFactory.openSession();
        
        ArrayList<Object[]> v_actividades=this.actividadDAO.listarActividades(this.sesion);
        return v_actividades;
    }
    
    /**
     * Llama al metodo 'compruebaMonitorResponsable()' que es una Consulta de 'ActividadDAO'
     * @param codMonitor Es el 'monitorResponsable' que queremos comprobar
     * @param dia Es el 'dia' que queremos comprobar
     * @param hora Es la 'hora' que queremos comprobar
     * @return Devuelve 'true' si el Monitor Responsable que hemos introducido tiene alguna actividad programada o 'false' en caso contrario
     * @throws Exception Lanza una excepcion si NO hay ningun Monitor Responsable en el 'dia' y 'hora' introducidos
     */
    public boolean obtenerMonitorOcupado(String codMonitor, String dia, int hora) throws Exception
    {
        boolean monitor_ocupado=false;
        
        this.sesion=this.sessionFactory.openSession();
        
        monitor_ocupado=this.actividadDAO.compruebaMonitorResponsable(this.sesion, codMonitor, dia, hora);
        
        return monitor_ocupado;
    }
    
    /**
     * Comprueba si existe una Actividad con el 'nombre' introducido por parametro
     * @param nomActividad Es el 'nombre' de la Actividad que queremos comprobar si existe
     * @return Devuelve 'true' si existe una Actividad con el 'nombre' introducido y 'false' si NO existe
     * @throws Exception Lanza una excepcion si se produce algun error
     */
    public boolean compruebaExisteActividad(String nomActividad) throws Exception
    {
        boolean existe_actividad=false;
        
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        existe_actividad=this.actividadDAO.existeActividad(this.sesion, nomActividad);
        
        return existe_actividad;
    }
    
    /**
     * Permite insertar/modificar a una Actividad en la BD
     * @param idActividad 'idActividad' de la nueva Actividad que queremos insertar en la BD
     * @param nombre 'nombre' de la nueva Actividad que queremos insertar en la BD
     * @param dia 'dia' de la nueva Actividad que queremos insertar en la BD
     * @param hora 'hora' de la nueva Actividad que queremos insertar en la BD
     * @param descripcion 'descripcion' de la nueva Actividad que queremos insertar en la BD
     * @param precioBaseMes 'precioBaseMes' de la nueva Actividad que queremos insertar en la BD
     * @param monitorResponsable 'monitorResponsable' de la nueva Actividad que queremos insertar en la BD
     */
    public void altaActividad(String idActividad, String nombre, String dia, int hora, String descripcion, int precioBaseMes, Monitor monitorResponsable)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            Actividad nuevaActividad = new Actividad(idActividad, nombre, dia, hora, descripcion, precioBaseMes, monitorResponsable);
            this.actividadDAO.insertaActividad(this.sesion, nuevaActividad); // Comprueba que los datos sean correctos y sino lanza una Excepcion
            this.tr.commit();
            this.vMensaje.mensaje(null, "Actividad '" + nuevaActividad.getIdActividad()+ "' insertada/modificada con éxito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nActividad '" + nuevaActividad.getIdActividad()+ "' insertada/modificada con exito en la BD.");
        }
        catch(PersistenceException pex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "Error al modificar la Actividad, el Nombre ya existe en la BD", "Operación CRUD Errónea", "ERROR");
            this.vMensaje.mostrar("\n\nError al modificar la Actividad, el Nombre ya existe en la BD\n");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorActividad: Error en 'altaActividad()' al insertar/modificar la Actividad en la BD -> " + e.getMessage(), "Operación CRUD Errónea'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorActividad: Error en 'altaActividad()' al insertar/modificar la Actividad en la BD -> " + e.getMessage() + "\n");
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
     * Permite eliminar/borrar una Actividad de la BD
     * @param idActividad 'idActividad' de la Actividad que queremos eliminar de la BD
     */
    public void bajaActividad(String idActividad)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            // Eliminamos la Actividad de la BD
            Actividad eliminarActividad = this.sesion.get(Actividad.class, idActividad);
            this.actividadDAO.eliminarActividad(this.sesion, eliminarActividad);
            this.tr.commit();
            this.vMensaje.mensaje(null, "Actividad '" + idActividad + "' eliminada con éxito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nActividad '" + eliminarActividad.getIdActividad()+ "' eliminada con exito en la BD.");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorActividad: Error en 'bajaActividad()' al eliminar la Actividad de la BD -> " + e.getMessage(), "Operacion CRUD 'bajaActividad()'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorActividad: Error en 'bajaActividad()' al eliminar la Actividad de la BD -> " + e.getMessage() + "\n");
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
