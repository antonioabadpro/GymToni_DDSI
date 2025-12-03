/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Utilidad.GestionTablaMonitores;
import Vista.VistaMensaje;
import Vista.VistaMonitor;
import Vista.VistaMonitorCRUD;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class ControladorMonitor implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private SessionFactory sessionFactory=null;
    private Session sesion=null;
    private Transaction tr=null;
    private MonitorDAO monitorDAO=null;
    private VistaMonitor vMonitor=null;
    private VistaMonitorCRUD vCRUD_monitor=null;
    private char operacion; // Insertar ('I'), Actualizar ('A') para saber si debemos comprobar si existe un Monitor con el 'dni' introducido o NO
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************

    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaMonitor' y 'VistaMonitorCRUD' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        // Añadimos los Botones de 'VistaMonitor' para realizar una operacion (Alta, Baja, Modificacion de un Monitor)
        this.vMonitor.jButton_nuevoMonitor.addActionListener(this);
        this.vMonitor.jButton_borrarMonitor.addActionListener(this);
        this.vMonitor.jButton_actualizarMonitor.addActionListener(this);
        // Añadimos los botones de 'VistaMonitorCRUD' para aceptar o cancelar una operacion (Alta, Modificacion)
        this.vCRUD_monitor.jButton_aceptarM.addActionListener(this);
        this.vCRUD_monitor.jButton_cancelarM.addActionListener(this);
    }
    
    /**
     * Metodo privado auxiliar para inicializar los campos del 'JDialog' de 'VistaMonitorCRUD'
     */
    private void inicializarCamposVistaCRUD()
    {
        String codMonitor;
                
        codMonitor=generarCodMonitor();
        // Escribimos el 'codMonitor' generado y dejamos el resto de campos vacios
        this.vCRUD_monitor.jTextField_codigo.setText(codMonitor); // Escribimos el 'codMonitor' en el 'JDialog'
        this.vCRUD_monitor.jTextField_nombre.setText("");
        this.vCRUD_monitor.jTextField_dni.setText("");
        this.vCRUD_monitor.jTextField_telefono.setText("");
        this.vCRUD_monitor.jTextField_correo.setText("");
        this.vCRUD_monitor.jDateChooser_fechaEntrada.setDate(null);
        this.vCRUD_monitor.jTextField_nick.setText("");
    }
    
    /**
     * Metodo privado auxiliar para crear el 'codMonitor' del nuevo Monitor que queremos insertar (Formato -> M001)
     * @return Devuelve el 'codMonitor' del nuevo Monitor que queremos insertar
     */
    private String generarCodMonitor()
    {
        String cod_ultimo; // 'codMonitor' del ultimo Monitor de la Tabla
        int nuevo_codMonitor; // Nº del nuevo 'codMonitor'
        String codMonitor=null; // 'codMonitor' del nuevo Monitor que queremos insertar          

        // Obtenemos el ultimo 'codMonitor' de la BD
        cod_ultimo=GestionTablaMonitores.getCodUltimo();
        if(cod_ultimo==null) // Si NO hay Monitores en la BD
        {
            codMonitor="M001";
        }
        else // Si hay Monitores en la BD
        {
            cod_ultimo=cod_ultimo.substring(1); // Eliminamos el primer caracter del 'cod_ultimo' para solo obtener el numero (M011 -> 011)
            nuevo_codMonitor=Integer.parseInt(cod_ultimo)+1; // Sumamos 1 al ultimo numero obtenido
            codMonitor="M"+String.format("%03d", nuevo_codMonitor); // Convertimos el numero en String con 3 digitos y le concatenamos la 'S'
        }
        return codMonitor;
    }

    /**
     * Metodo privado auxiliar para saber si los datos del Monitor estan introducidos correctamente cumpliendo los campos obligatorios y el formato de cada campo
     * @param codMonitor 'codMonitor' que queremos comprobar (OBLIGATORIO)
     * @param nombre 'nombre' que queremos comprobar (OBLIGATORIO)
     * @param dni 'dni' que queremos comprobar (OBLIGATORIO)
     * @param telefono 'telefono' que queremos comprobar (FORMATO)
     * @param correo 'correo' que queremos comprobar (FORMATO)
     * @param fechaEntrada_date 'fechaEntrada' que queremos comprobar (OBLIGATORIO y FECHA)
     * @return Devuelve 'true' si los datos son correctos y 'false' en caso contrario
     */
    private boolean comprobarDatosMonitor(String codMonitor, String nombre, String dni, String telefono, String correo, Date fechaEntrada_date)
    {
        boolean es_correcto=true;
        
        // Comprobamos que todos los campos obliagatorios estan rellenos
        if(codMonitor.isEmpty()==true || nombre.isEmpty()==true || dni.isEmpty()==true || fechaEntrada_date==null)
        {
            es_correcto=false;
            StringBuilder camposVacios = new StringBuilder();

            if (codMonitor.isEmpty()==true)
            {
                camposVacios.append("'Código'");
            }
            if (nombre.isEmpty()==true)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Nombre'");
            }
            if (dni.isEmpty()==true)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'DNI'");
            }
            if (fechaEntrada_date == null)
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Fecha de Entrada'");
            }

            // Mostramos el mensaje por Pantalla
            this.vMensaje.mensaje(this.vCRUD_monitor, "Los campos " + camposVacios + " son obligatorios", "Restricción Campos Obligatorios", "AVISO");
        }
        else // Comprobamos que el formato del 'dni' sea correcto (8 digitos y una letra mayuscula)
        {
            String formato_dni="\\d{8}[A-Z]";
            if(Pattern.matches(formato_dni, dni)==false) // Si el campo 'dni' NO tiene el formato correcto, mostramos el mensaje por Pantalla
            {
                es_correcto=false;
                this.vMensaje.mensaje(this.vCRUD_monitor, "El formato del 'DNI' NO es correcto", "Restricción Formato DNI", "AVISO");
            }
            else // Comprobamos que el formato del 'correo' sea correcto (xxx@xxx)
            {
                if(correo.isEmpty()==false) // Si el campo 'correo' NO esta vacio
                {
                    String formato_correo="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                    if(Pattern.matches(formato_correo, correo)==false) // Si el campo 'correo' NO tiene el formato correcto, mostramos el mensaje por Pantalla
                    {
                        es_correcto=false;
                        this.vMensaje.mensaje(this.vCRUD_monitor, "El formato del 'Correo' NO es correcto (xxx@xxx)", "Restricción Formato Correo", "AVISO");
                    }
                }
                else // Comprobamos que el formato del 'telefono' sea correcto (9 digitos)
                {
                    if(telefono.isEmpty()==false) // Si el campo 'telefono' NO esta vacio
                    {
                        String formato_telefono="\\d{9}";
                        if(Pattern.matches(formato_telefono, telefono)==false) // Si el campo 'telefono' NO tiene el formato correcto, mostramos el mensaje por Pantalla
                        {
                            es_correcto=false;
                            this.vMensaje.mensaje(this.vCRUD_monitor, "El formato del 'Teléfono' NO es correcto", "Restricción Formato Teléfono", "AVISO");
                        }
                    }
                    else // Comprobamos que el campo 'fechaEntrada' es anterior a la Fecha Actual del Sistema
                    {
                        Date fechaActual_date=new Date();
                        if(fechaEntrada_date.after(fechaActual_date)) // Si el campo 'fechaEntrada' NO tiene el formato correcto (fechaEntrada>fechaActual), mostramos el mensaje por Pantalla
                        {
                            es_correcto=false;
                            this.vMensaje.mensaje(this.vCRUD_monitor, "El campo 'Fecha de Entrada' NO es correcto", "Restricción Formato Fecha de Entrada", "AVISO");
                        }
                    }
                }
            }
        }
        return es_correcto;
    }
    
    /**
     * Meotodo privado auxiliar para convertir una Fecha de tipo 'Date' en una Fecha de tipo 'String' con el formato de la BD (dd/MM/yyyy)
     * @param fecha_date Es la Fecha de tipo 'Date' que queremos convertir en tipo 'String'
     * @return Devuelve la Fecha de tipo 'String' con el formato de la BD (dd/MM/yyyy)
     */
    private String convertirFechaDateToString(Date fecha_date)
    {
        String fecha_string=null;
        SimpleDateFormat formatoFecha=new SimpleDateFormat("dd/MM/yyyy");
        
        // Formateamos la 'fecha_date' con el formato 'dd/MM/yyyy'
        if(fecha_date!=null)
        {
            fecha_string=formatoFecha.format(fecha_date);
        }
        return fecha_string;
    }
    
    /**
     * Metodo privado auxiliar para convertir una Fecha de tipo 'String' con el formato de la BD (dd/MM/yyyy) en una Fecha de tipo 'Date'
     * @param fecha_string Es la Fecha de tipo 'String' que queremos convertir en tipo 'Date'
     * @return Devuelve la Fecha de tipo 'Date' para poder utilizarla en un JDateChooser
     */
    private Date convertirFechaStringToDate(String fecha_string)
    {
        Date fecha_date=null;
        SimpleDateFormat formatoFecha=new SimpleDateFormat("dd/MM/yyyy");
        
        try
        {    
            // Convertimos la 'fecha_string' con el formato 'dd/MM/yyyy' en una Fecha de tipo 'Date'
            if(fecha_string!=null && fecha_string.isEmpty()==false)
            fecha_date=formatoFecha.parse(fecha_string);
        }
        catch (ParseException ex)
        {
            this.vMensaje.mensaje(null, "ControladorMonitor: Error en 'convertirFechaStringToDate()'" + ex.getMessage(), "Error de conversión de Fechas", "ERROR");
            Logger.getLogger(ControladorMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fecha_date;
    }
    
    /**
     * Metodo privado auxiliar para actualizar/refrescar los datos de la Tabla de los Monitores de la BD tras insertar un nuevo Monitor en la BD
     */
    private void refrescarTablaMonitor()
    {
        this.sesion=this.sessionFactory.openSession();
        this.tr=this.sesion.beginTransaction();
        try
        {
            ArrayList<Monitor> v_monitores=pideMonitores();
            GestionTablaMonitores.borrarTablaMonitores();
            GestionTablaMonitores.escribirTablaMonitores(v_monitores);
            this.tr.commit();
        }
        catch(Exception ex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorMonitor: Error en 'refrescarTablaMonitor()'" + ex.getMessage(), "Consulta 'listarTodos()' de 'pideMonitores()'", "ERROR");
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
     * Consutructor de 'ControladorMonitor'
     * @param sesionF Sesion abierta desde el 'actionPerormed' de la Clase 'ControladorConexion'
     * @param vMonitor Objeto de tipo 'VistaMonitor' para poder acceder a los botones de 'VistaMonitor'
     * @param vCRUD_monitor Objeto de tipo 'VistaMonitorCRUD' para poder acceder a los botones de 'VistaMonitorCRUD'
     */
    public ControladorMonitor(SessionFactory sesionF, VistaMonitor vMonitor, VistaMonitorCRUD vCRUD_monitor)
    {
        this.sessionFactory=sesionF;
        this.sesion=this.sessionFactory.openSession();
        this.vMonitor=vMonitor;
        this.vMensaje=new VistaMensaje();
        this.monitorDAO=new MonitorDAO();
        this.operacion=' ';
        // Creamos el JDialog para insertar un Nuevo Monitor
        this.vCRUD_monitor=vCRUD_monitor;
        this.vCRUD_monitor.setLocationRelativeTo(null);
        this.vCRUD_monitor.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.vCRUD_monitor.setResizable(false);
        
        addListeners(); // Añadimos los eventos de los botones de 'VistaMonitor' y 'VistaMonitorCRUD'
    }
    
    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaMonitor' y 'VistaMonitorCRUD'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "NuevoMonitor": // Boton 'jButton_nuevoMonitor' de 'VistaMonitor'
            {
                this.operacion='I';
                inicializarCamposVistaCRUD();
                this.vCRUD_monitor.setTitle("Nuevo Monitor"); // Escribimos el titulo del JDialog
                this.vCRUD_monitor.setVisible(true); // Mostramos el JDialog
                
            }; break;
            
            case "AceptarMonitor": // Boton 'jButton_aceptarM' de 'VistaMonitorCRUD' utilizado para Insertar o Modificar un Monitor
            {
                String codMonitor, nombre, dni, telefono, correo, fechaEntrada, nick;
                Date fechaEntrada_date;
                int opcion_confirmacion=-1; // Valor devuelto tras seleccionar una opcion del 'JOptionPane'
                boolean datos_correctos=false; // Variable que contiene si los datos del nuevo Monitor que vamos a insertar son correctos o NO
                boolean existe_monitor=false;
                
                // Obtenemos el valor de los campos del Nuevo Monitor que queremos insertar introducidos en el 'JDialog'
                codMonitor=this.vCRUD_monitor.jTextField_codigo.getText();
                nombre=this.vCRUD_monitor.jTextField_nombre.getText();
                dni=this.vCRUD_monitor.jTextField_dni.getText();
                telefono=this.vCRUD_monitor.jTextField_telefono.getText();
                correo=this.vCRUD_monitor.jTextField_correo.getText();
                fechaEntrada_date=this.vCRUD_monitor.jDateChooser_fechaEntrada.getDate();
                fechaEntrada=convertirFechaDateToString(fechaEntrada_date); // Convertimos la fecha en 'String' con el formato 'dd/MM/yyyy'
                nick=this.vCRUD_monitor.jTextField_nick.getText();

                // Mostramos una ventana para confirmar si queremos insertar/modificar el Nuevo Monitor en la BD
                opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vMonitor, "¿Estás seguro que deseas insertar/modificar el Monitor (" + nombre + ")?", "Confirmacion Insertar/Modificar Monitor");
                switch(opcion_confirmacion)
                {
                    case 0: // Pulsamos el boton 'YES'
                    {
                        // Comprobamos que el formato de los campos es correcto antes de insertar/modificar el nuevo Monitor en la BD
                        datos_correctos=comprobarDatosMonitor(codMonitor, nombre, dni, telefono, correo, fechaEntrada_date);
                        
                        if(datos_correctos==true)
                        {
                            if(this.operacion=='I') // Si queremos insertar un nuevo Monitor en la BD
                            {
                                // Comprobamos si existe un Monitor con el 'dni' introducido en la BD
                                try
                                {
                                    existe_monitor=compruebaExisteMonitor(dni);
                                    if(existe_monitor==true) // Si existe un Monitor con el 'dni' introducido, mostramos un mensaje de error
                                    {
                                        this.vMensaje.mensaje(this.vMonitor, "Ya existe un Monitor con el DNI introducido '" + dni + "'", "Monitor ya existente", "ERROR");
                                        this.vMensaje.mostrar("\n\nYa existe un Monitor con el DNI introducido '" + dni + "'");
                                    }
                                    else // Si NO existe un Monitor con el 'dni' introducido, insertamos el Monitor en la BD
                                    {
                                        altaMonitor(codMonitor, nombre, dni, telefono, correo, fechaEntrada, nick);
                                        this.vCRUD_monitor.setVisible(false); // Ocultamos el 'JDialog'
                                    }
                                }
                                catch (Exception ex)
                                {
                                    this.vMensaje.mensaje(this.vMonitor, "ControladorMonitor: Error en 'compruebaExisteMonitor()' -> " + ex.getMessage(), "Error en 'existeMonitor()' de 'compruebaExisteMonitor()'", "ERROR");
                                    this.vMensaje.mostrar("\n\n\"ControladorMonitor: Error en 'compruebaExisteMonitor()' ->" + ex.getMessage() + "\n");
                                    Logger.getLogger(ControladorMonitor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if(this.operacion=='A') // Si queremos actualizar los datos de un Monitor de la BD
                            {
                                // Actualizamos los datos del Monitor en la BD
                                altaMonitor(codMonitor, nombre, dni, telefono, correo, fechaEntrada, nick);
                                this.vCRUD_monitor.setVisible(false); // Ocultamos el 'JDialog' 
                            }
                            
                            // Mostramos la Tabla de los Monitores actualizada correctamente
                            refrescarTablaMonitor();
                        }
                    }; break;

                    case 1: // Pulsamos el boton 'NO'
                    {
                        this.vMensaje.mensaje(null, "Operacion CRUD del Monitor CANCELADA", "Operacion Cancelada", "CANCELAR");
                    }; break;

                    case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                    {

                    }; break;
                }
            }; break;
            
            case "CancelarMonitor": // Boton 'jButton_cancelarM' de 'VistaMonitorCRUD' utilizado para Insertar o Modificar un Monitor
            {
                this.vMensaje.mensaje(null, "Operacion CRUD del Monitor CANCELADA", "Operacion Cancelada", "CANCELAR");
                this.vCRUD_monitor.setVisible(false); // Ocultamos el 'JDialog'
            }; break;
            
            case "BorrarMonitor": // Boton 'jButton_borrarMonitor' de 'VistaMonitor'
            {
                int fila_seleccionada;
                int opcion_confirmacion;
                int columna_codMonitor=0;
                String codMonitor_eliminar; // 'codMonitor' del Monitor que queremos eliminar de la BD
                int columna_nomMonitor=1;
                String nomMonitor_eliminar; // 'nombre' del Monitor que queremos eliminar de la BD

                fila_seleccionada=this.vMonitor.jTable_monitores.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Borrar Monitor", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    codMonitor_eliminar=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, columna_codMonitor).toString();
                    nomMonitor_eliminar=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, columna_nomMonitor).toString();
                    
                    // Mostramos una ventana para confirmar si queremos eliminar un Monitor de la BD
                    opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vMonitor, "¿Estás seguro que deseas eliminar el Monitor seleccionado (" + nomMonitor_eliminar + ")?", "Confirmacion Borrar Monitor");
                    switch(opcion_confirmacion)
                    {
                        case 0: // Pulsamos el boton 'YES'
                        {
                            // Eliminamos el Monitor en la BD
                            bajaMonitor(codMonitor_eliminar);
                            this.vCRUD_monitor.setVisible(false); // Ocultamos el 'JDialog'
                            
                            // Mostramos la Tabla de los Monitores actualizada correctamente
                            refrescarTablaMonitor();
                        }; break;

                        case 1: // Pulsamos el boton 'NO'
                        {
                            this.vMensaje.mensaje(null, "Borrado de un Monitor CANCELADA", "Operacion Cancelada", "CANCELAR");
                        }; break;

                        case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                        {

                        }; break;
                    }
                }
            }; break;
            
            case "ActualizarMonitor": // Boton 'jButton_actualizarMonitor' de 'VistaMonitor'
            {
                int fila_seleccionada;
                String codMonitor_modificar; // 'codMonitor' del Monitor que queremos modificar en la BD
                String nombre, dni, telefono, correo, fechaEntrada, nick;
                Date fechaEntrada_date;
                SimpleDateFormat formatoFecha=new SimpleDateFormat("dd/MM/yyyy");
                
                this.operacion='A';
                fila_seleccionada=this.vMonitor.jTable_monitores.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Borrar Monitor", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    // Obtenemos los valores del Monitor que queremos modificar
                    codMonitor_modificar=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 0).toString();
                    nombre=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 1).toString();
                    dni=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 2).toString();
                    telefono=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 3).toString();
                    correo=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 4).toString();
                    fechaEntrada=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 5).toString();
                    fechaEntrada_date=convertirFechaStringToDate(fechaEntrada); // Convertimos la fecha en 'Date'
                    nick=this.vMonitor.jTable_monitores.getValueAt(fila_seleccionada, 6).toString();
                    
                    // Mostramos el JDialog para rellenar los datos del Monitor que queremos modificar
                    this.vCRUD_monitor.jTextField_codigo.setText(codMonitor_modificar);
                    this.vCRUD_monitor.jTextField_nombre.setText(nombre);
                    this.vCRUD_monitor.jTextField_dni.setText(dni);
                    this.vCRUD_monitor.jTextField_telefono.setText(telefono);
                    this.vCRUD_monitor.jTextField_correo.setText(correo);
                    this.vCRUD_monitor.jDateChooser_fechaEntrada.setDate(fechaEntrada_date);
                    this.vCRUD_monitor.jTextField_nick.setText(nick);
                    this.vCRUD_monitor.setTitle("Actualizar Monitor"); // Escribimos el titulo del JDialog
                    this.vCRUD_monitor.setVisible(true); // Mostramos el JDialog
                }
            }; break;
        }
    }
    
    /**
     * Llama al metodo 'listarTodos()' que es una Consulta de 'MonitorDAO'
     * @return Devuelve un array que contiene todos los Monitores de la BD
     * @throws Exception Lanza una excepcion si NO hay ningun Monitor en la BD
     */
    public ArrayList<Monitor> pideMonitores() throws Exception
    {
        this.sesion=this.sessionFactory.openSession();
        
        ArrayList<Monitor> v_monitores=this.monitorDAO.listarTodos(this.sesion);
        return v_monitores;
    }
    
    /**
     * Llama al metodo 'obtenerMonitorPorNombre()' que es una Consulta de 'MonitorDAO'
     * @param nomMonitor Es el 'nombre' del Monitor que queremos obtener
     * @return Devuelve el Monitor con el Nombre introducido por parametro
     * @throws Exception Lanza una excepcion si NO hay ningun Monitor con el 'nombre' introducido en la BD
     */
    public Monitor pideMonitorPorNombre(String nomMonitor) throws Exception
    {
        this.sesion=this.sessionFactory.openSession();
        
        Monitor m=this.monitorDAO.obtenerMonitorPorNombre(this.sesion, nomMonitor);
        return m;
    }
    
    /**
     * Comprueba si existe un Monitor con el 'dni' introducido por parametro
     * @param dniMonitor Es el 'dni' del Monitor que queremos comprobar si existe
     * @return Devuelve 'true' si existe un Monitor con el 'dni' introducido y 'false' si NO existe
     * @throws Exception Lanza una excepcion si se produce algun error
     */
    public boolean compruebaExisteMonitor(String dniMonitor) throws Exception
    {
        boolean existe_monitor=false;
        
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        existe_monitor=this.monitorDAO.existeMonitor(this.sesion, dniMonitor);
        
        return existe_monitor;
    }
    
    /**
     * Permite insertar/modificar a un Monitor en la BD
     * @param codMonitor 'codMonitor' del nuevo Monitor que queremos insertar en la BD
     * @param nombre 'nombre' del nuevo Monitor que queremos insertar en la BD
     * @param dni 'dni' del nuevo Monitor que queremos insertar en la BD
     * @param telefono 'telefono' del nuevo Monitor que queremos insertar en la BD
     * @param correo 'correo' del nuevo Monitor que queremos insertar en la BD
     * @param fechaEntrada 'fechaEntrada' del nuevo Monitor que queremos insertar en la BD
     * @param nick 'nick' del nuevo Monitor que queremos insertar en la BD
     */
    public void altaMonitor(String codMonitor, String nombre, String dni, String telefono, String correo, String fechaEntrada, String nick)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            Monitor nuevoMonitor = new Monitor(codMonitor, nombre, dni, telefono, correo, fechaEntrada, nick);
            this.monitorDAO.insertarMonitor(this.sesion, nuevoMonitor); // Comprueba que los datos sean correctos y sino lanza una Excepcion
            this.tr.commit();
            this.vMensaje.mensaje(null, "Monitor '" + nuevoMonitor.getCodMonitor() + "' insertado/modificado con exito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nMonitor '" + nuevoMonitor.getCodMonitor() + "' insertado/modificado con exito en la BD.");
        }
        catch(PersistenceException pex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "Error al modificar el Monitor, el DNI ya existe en la BD", "Operación CRUD Errónea", "ERROR");
            this.vMensaje.mostrar("\n\nError al modificar el Monitor, el DNI ya existe en la BD\n");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorMonitor: Error en 'altaMonitor()' al insertar/modificar el Monitor en la BD -> " + e.getMessage(), "Operación CRUD Errónea'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorMonitor: Error en 'altaMonitor()' al insertar/modificar el Monitor en la BD -> " + e.getMessage());
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
     * Permite eliminar/borrar a un Monitor de la BD
     * @param codMonitor 'codMonitor' del Monitor que queremos eliminar de la BD
     */
    public void bajaMonitor(String codMonitor)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            // Eliminamos el Monitor de la BD
            Monitor eliminarMonitor = this.sesion.get(Monitor.class, codMonitor);
            this.monitorDAO.eliminarMonitor(this.sesion, eliminarMonitor);
            this.tr.commit();
            this.vMensaje.mensaje(null, "Monitor '" + codMonitor + "' eliminado con éxito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nMonitor '" + eliminarMonitor.getCodMonitor() + "' eliminado con exito de la BD");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorMonitor: Error en 'bajaMonitor()' al eliminar el Monitor de la BD -> " + e.getMessage(), "Operación CRUD Errónea'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorMonitor: Error en 'bajaMonitor()' al eliminar el Monitor de la BD -> " + e.getMessage());
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
