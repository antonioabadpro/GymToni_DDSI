/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.Socio;
import Modelo.SocioDAO;
import Utilidad.GestionTablaSocios;
import Vista.VistaMensaje;
import Vista.VistaSocio;
import Vista.VistaSocioCRUD;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
public class ControladorSocio implements ActionListener
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private SessionFactory sessionFactory=null;
    private Session sesion=null;
    private Transaction tr=null;
    private SocioDAO socioDAO=null;
    private VistaSocio vSocio=null;
    private VistaSocioCRUD vCRUD_socio=null;
    private char operacion; // Insertar ('I'), Actualizar ('A') para saber si debemos comprobar si existe un Socio con el 'numSocio' y el 'dni' introducido o NO
    
    // ********************************* METODOS AUXILIARES (PRIVADOS) *********************************
    
    /**
     * Añade los eventos de los botones (ActionEvent) a los botones de 'VistaSocio' y 'VistaSocioCRUD' para poder programar las acciones de cada uno desde el metodo 'actionPerformed()'
     * Los botones deben estar en 'public', ya que sino NO son accesibles
     */
    private void addListeners()
    {
        // Añadimos los Botones de 'VistaSocio' para realizar una operacion (Alta, Baja, Modificacion de un Socio)
        this.vSocio.jButton_nuevoSocio.addActionListener(this);
        this.vSocio.jButton_borrarSocio.addActionListener(this);
        this.vSocio.jButton_actualizarSocio.addActionListener(this);
        // Añadimos los botones de 'VistaSocioCRUD' para aceptar o cancelar una operacion (Alta, Modificacion)
        this.vCRUD_socio.jButton_aceptarS.addActionListener(this);
        this.vCRUD_socio.jButton_cancelarS.addActionListener(this);
        // Añadimos el campo de texto para Buscar a un Socio por nombre
        this.vSocio.jTextField_buscarSocio.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
        {
            /**
             * Cada vez que insertamos un caracter en 'jTextField_buscarSocio' llamamos a 'filtrarSocio()'
             */
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e)
            {
                filtrarSocio();
            }

            /**
             * Cada vez que borramos un caracter de 'jTextField_buscarSocio' llamamos a 'filtrarSocio()'
             */
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e)
            {
                filtrarSocio();
            }

            /**
             * Cada vez que cambiamos un caracter en 'jTextField_buscarSocio' llamamos a 'filtrarSocio()'
             */
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e)
            {
                filtrarSocio();
            }
        });
    }
    
    /**
     * Metodo privado auxiliar para crear el 'numeroSocio' del nuevo Socio que queremos insertar (Formato -> S001)
     * @return Devuelve el 'numeroSocio' del nuevo Socio que queremos insertar
     */
    private String generarNumeroSocio()
    {
        String numS_ultimo; // 'numeroSocio' del ultimo Socio de la Tabla
        int nuevo_numeroSocio; // Nº del nuevo 'numeroSocio'
        String numeroSocio=null; // 'numeroSocio' del nuevo Socio que queremos insertar          

        // Obtenemos el ultimo 'numeroSocio' de la BD
        numS_ultimo=GestionTablaSocios.getCodUltimo();
        if(numS_ultimo==null) // Si NO hay Socios en la BD
        {
            numS_ultimo="S001";
        }
        else // Si hay Socio en la BD
        {
            numS_ultimo=numS_ultimo.substring(1); // Eliminamos el primer caracter del 'numS_ultimo' para solo obtener el numero (S011 -> 011)
            nuevo_numeroSocio=Integer.parseInt(numS_ultimo)+1; // Sumamos 1 al ultimo numero obtenido
            numeroSocio="S"+String.format("%03d", nuevo_numeroSocio); // Convertimos el numero en String con 3 digitos y le concatenamos la 'S'
        }
        return numeroSocio;
    }
    
    /**
     * Metodo privado auxiliar para inicializar los campos del 'JDialog' de 'VistaSocioCRUD'
     */
    private void inicializarCamposVistaCRUD()
    {
        String numeroSocio;
        
        numeroSocio=generarNumeroSocio();
        // Escribimos el 'numeroSocio' generado y dejamos el resto de campos vacios
        this.vCRUD_socio.jTextField_codigo.setText(numeroSocio);
        this.vCRUD_socio.jTextField_nombre.setText("");
        this.vCRUD_socio.jTextField_dni.setText("");
        this.vCRUD_socio.jDateChooser_fechaNac.setDate(null);
        this.vCRUD_socio.jTextField_telefono.setText("");
        this.vCRUD_socio.jTextField_correo.setText("");
        this.vCRUD_socio.jDateChooser_fechaEntrada.setDate(null);
        this.vCRUD_socio.jComboBox_categoria.removeAllItems(); // Eliminamos todos los elementos del 'jComboBox'
        // Añadimos los distintos tipos de categorias al 'jComboBox_categoria' (A, B, C, D, E)
        this.vCRUD_socio.jComboBox_categoria.addItem("-");
        this.vCRUD_socio.jComboBox_categoria.addItem("A");
        this.vCRUD_socio.jComboBox_categoria.addItem("B");
        this.vCRUD_socio.jComboBox_categoria.addItem("C");
        this.vCRUD_socio.jComboBox_categoria.addItem("D");
        this.vCRUD_socio.jComboBox_categoria.addItem("E");
        this.vCRUD_socio.jComboBox_categoria.setSelectedItem("-");
    }
    
    /**
     * Metodo privado auxiliar para saber si los datos del Socio estan introducidos correctamente cumpliendo los campos obligatorios y el formato de cada campo
     * @param numeroSocio 'numeroSocio' que queremos comprobar (OBLIGATORIO)
     * @param nombre 'nombre' que queremos comprobar (OBLIGATORIO)
     * @param dni 'dni' que queremos comprobar (OBLIGATORIO)
     * @param telefono 'telefono' que queremos comprobar (FORMATO)
     * @param correo 'correo' que queremos comprobar (FORMATO)
     * @param fechaNacimiento_date 'fechaNacimiento_date' que queremos comprobar (FECHA)
     * @param fechaEntrada_date 'fechaEntrada_date' que queremos comprobar (OBLIGATORIO y FECHA)
     * @param categoria 'categoria' que queremos comprobar (OBLIGATORIO)
     * @return Devuelve 'true' si los datos son correctos y 'false' en caso contrario
     */
    private boolean comprobarDatosSocio(String numeroSocio, String nombre, String dni, String telefono, String correo, Date fechaNacimiento_date, Date fechaEntrada_date, Character categoria)
    {
        boolean es_correcto=true;
        
        // Comprobamos que todos los campos obliagatorios estan rellenos
        if(numeroSocio.isEmpty()==true || nombre.isEmpty()==true || dni.isEmpty()==true || fechaEntrada_date==null || categoria.equals('-'))
        {
            es_correcto=false;
            StringBuilder camposVacios = new StringBuilder();

            if (numeroSocio.isEmpty()==true)
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
            if (categoria.equals('-'))
            {
                if (camposVacios.length() > 0) camposVacios.append(", ");
                camposVacios.append("'Categoria'");
            }

            // Mostramos el mensaje por Pantalla
            this.vMensaje.mensaje(this.vCRUD_socio, "Los campos " + camposVacios + " son obligatorios", "Restricción Campos Obligatorios", "AVISO");
        }
        else // Comprobamos que el formato del 'dni' sea correcto (8 digitos y una letra mayuscula)
        {
            String formato_dni="\\d{8}[A-Z]";
            if(Pattern.matches(formato_dni, dni)==false) // Si el campo 'dni' NO tiene el formato correcto, mostramos el mensaje por Pantalla
            {
                es_correcto=false;
                this.vMensaje.mensaje(this.vCRUD_socio, "El formato del 'DNI' NO es correcto", "Restricción Formato DNI", "AVISO");
            }
            else // Comprobamos que el formato del 'correo' sea correcto (xxx@xxx)
            {
                if(correo.isEmpty()==false) // Si el campo 'correo' NO esta vacio
                {
                    String formato_correo="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                    if(Pattern.matches(formato_correo, correo)==false) // Si el campo 'correo' NO tiene el formato correcto, mostramos el mensaje por Pantalla
                    {
                        es_correcto=false;
                        this.vMensaje.mensaje(this.vCRUD_socio, "El formato del 'Correo' NO es correcto (xxx@xxx)", "Restricción Formato Correo", "AVISO");
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
                            this.vMensaje.mensaje(this.vCRUD_socio, "El formato del 'Teléfono' NO es correcto", "Restricción Formato Teléfono", "AVISO");
                        }
                    }
                    else // Comprobamos que el campo 'fechaEntrada' es anterior a la Fecha Actual del Sistema
                    {
                        Date fechaActual_date=new Date();
                        if(fechaEntrada_date.after(fechaActual_date)) // Si el campo 'fechaEntrada' NO tiene el formato correcto (fechaEntrada>fechaActual), mostramos el mensaje por Pantalla
                        {
                            es_correcto=false;
                            this.vMensaje.mensaje(this.vCRUD_socio, "El campo 'Fecha de Entrada' NO es correcto", "Restricción Formato Fecha de Entrada", "AVISO");
                        }
                        else // Comprobamos que el campo 'fechaNacimiento' es anterior a la Fecha Actual del Sistema
                        {
                            if(fechaNacimiento_date!=null && fechaNacimiento_date.after(fechaActual_date)) // Si el campo 'fechaNacimiento' NO tiene el formato correcto (fechaNacimiento>fechaActual), mostramos el mensaje por Pantalla
                            {
                                es_correcto=false;
                                this.vMensaje.mensaje(this.vCRUD_socio, "El campo 'Fecha de Nacimiento' NO es correcto", "Restricción Formato Fecha de Nacimiento", "AVISO");
                            }
                            else // Comprobamos que el Socio es mayor de edad (>=18 años)
                            {
                                int edad;
                                
                                if(fechaNacimiento_date!=null)
                                {
                                    edad=calcularEdad(fechaNacimiento_date);
                                    if(edad<18)
                                    {
                                        es_correcto=false;
                                        this.vMensaje.mensaje(this.vCRUD_socio, "El Socio es menor de edad", "Restricción Mayoría de Edad", "AVISO");
                                    }
                                }
                            }
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
     * Meotodo privado auxiliar para convertir una Fecha de tipo 'String' con el formato de la BD (dd/MM/yyyy) en una Fecha de tipo 'Date'
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
            if(fecha_string.isEmpty()==false && fecha_string!=null)
            {
                fecha_date=formatoFecha.parse(fecha_string);
            }
        }
        catch (ParseException ex)
        {
            this.vMensaje.mensaje(null, "ControladorSocio: Error en 'convertirFechaStringToDate()'" + ex.getMessage(), "Error de conversión de Fechas", "ERROR");
            Logger.getLogger(ControladorMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fecha_date;
    }
    
    /**
     * Metodo privado auxiliar para calcular la Edad de un Socio
     * @param fechaNacimiento Es la 'fechaNacimiento' del Socio del que queremos saber su edad
     * @return Devuelve la edad del Socio
     */
    private int calcularEdad(Date fechaNacimiento)
    {
        Integer edad=null;
        LocalDate fechaActual=LocalDate.now();
        
        if(fechaNacimiento!=null) // Si el Socio tiene 'fechaNacimiento'
        {
            LocalDate fechaNac=fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            edad=Period.between(fechaNac, fechaActual).getYears();
        }
        
        return edad;
    }
    
    /**
     * Metodo privado auxiliar para actualizar/refrescar los datos de la Tabla de los Socios de la BD tras insertar un nuevo Socio en la BD
     */
    private void refrescarTablaSocio()
    {
        this.sesion=this.sessionFactory.openSession();
        this.tr=this.sesion.beginTransaction();
        try
        {
            ArrayList<Socio> v_socios=pideSocios();
            GestionTablaSocios.borrarTablaSocios();
            GestionTablaSocios.escribirTablaSocios(v_socios);
            this.tr.commit();
        }
        catch(Exception ex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorSocio: Error en 'refrescarTablaSocio()'" + ex.getMessage(), "Consulta 'listarTodos()' de 'pideSocios()'", "ERROR");
        }
        finally
        {
            if(this.sesion!=null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
    }
    
    /**
     * Metodo privado auxiliar para Buscar a un Socio por su 'nombre' en 'VistaSocio'
     */
    private void filtrarSocio()
    {
        try
        {
            String filtro_nombre=null;
            ArrayList<Socio> v_socios=new ArrayList<>();
            this.sesion=this.sessionFactory.openSession();
            
            filtro_nombre=this.vSocio.jTextField_buscarSocio.getText().trim();

            if (filtro_nombre.isEmpty()) // Si NO hay filtro, obtenemos todos los Socios de la BD
            {
                v_socios=this.socioDAO.listarTodos(this.sesion);
            }
            else // Si hay filtro, obtenemos los Socios que coincidan con el filtro, es decir, con el Socio que estamos buscando
            {
                v_socios=this.socioDAO.buscarPorNombreParcial(this.sesion, filtro_nombre);
            }

            // Actualizamos la tabla de Socios con los Socios obtenidos en la Consulta realizada
            GestionTablaSocios.borrarTablaSocios();
            GestionTablaSocios.escribirTablaSocios(v_socios);
        }
        catch (Exception e)
        {
            this.vMensaje.mensaje(null, "ControladorSocio: Error en 'filtrarSocio()'" + e.getMessage(), "Consulta 'listarTodos()' o 'buscarPorNombreParcial()'", "ERROR");
        }
        finally
        {
            if(this.sesion != null && this.sesion.isOpen())
            {
                this.sesion.close();
            }
        }
    }
    
    // ********************************* METODOS PUBLICOS *********************************
    
    /**
     * Consutructor de 'ControladorSocio'
     * @param sesionF Sesion abierta desde el 'actionPerormed' de la Clase 'ControladorConexion'
     * @param vSocio Objeto de tipo 'VistaSocio' para poder acceder a los botones de 'VistaSocio'
     * @param vCRUD_socio Objeto de tipo 'VistaSocioCRUD' para poder acceder a los botones de 'VistaSocioCRUD'
     */
    public ControladorSocio(SessionFactory sesionF, VistaSocio vSocio, VistaSocioCRUD vCRUD_socio)
    {
        this.sessionFactory=sesionF;
        this.sesion=this.sessionFactory.openSession();
        this.vSocio=vSocio;
        this.vMensaje=new VistaMensaje();
        this.socioDAO=new SocioDAO();
        this.operacion=' ';
        // Creamos el JDialog de 'VistaSocioCRUD'
        this.vCRUD_socio=vCRUD_socio;
        this.vCRUD_socio.setLocationRelativeTo(null);
        this.vCRUD_socio.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.vCRUD_socio.setResizable(false);
        
        addListeners(); // Añadimos los eventos de los botones de 'VistaSocio' y 'VistaSocioCRUD'
    }

    /**
     * Metodo para programar las acciones de los botones (listeners) añadidos desde 'VistaSocio' y 'VistaSocioCRUD'
     * @param e Nombre del 'actionCommand' del boton que hemos tenido que cambiar en sus propiedades
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {       
            case "NuevoSocio": // Boton 'jButton_nuevoSocio' de 'VistaSocio'
            {
                this.operacion='I';
                inicializarCamposVistaCRUD();
                this.vCRUD_socio.setTitle("Nuevo Socio"); // Escribimos el titulo del JDialog
                this.vCRUD_socio.setVisible(true); // Mostramos el JDialog
                
            }; break;
            
            case "AceptarSocio": // Boton 'jButton_aceptarS' de 'VistaSocioCRUD' utilizado para Insertar o Modificar un Socio
            {
                String numeroSocio, nombre, dni, fechaNac, telefono, correo, fechaEntrada, categoria_string;
                Character categoria;
                Date fechaNac_date, fechaEntrada_date;
                int opcion_confirmacion=-1; // Valor devuelto tras seleccionar una opcion del 'JOptionPane'
                boolean datos_correctos=false; // Variable que contiene si los datos del nuevo Socio que vamos a insertar son correctos o NO
                boolean existe_socio=false;
                
                // Obtenemos el valor de los campos del Nuevo Socio que queremos insertar introducidos en el 'JDialog'
                numeroSocio=this.vCRUD_socio.jTextField_codigo.getText();
                nombre=this.vCRUD_socio.jTextField_nombre.getText();
                dni=this.vCRUD_socio.jTextField_dni.getText();
                fechaNac_date=this.vCRUD_socio.jDateChooser_fechaNac.getDate();
                fechaNac=convertirFechaDateToString(fechaNac_date); // Convertimos la fecha en 'String' con el formato 'dd/MM/yyyy'
                telefono=this.vCRUD_socio.jTextField_telefono.getText();
                correo=this.vCRUD_socio.jTextField_correo.getText();
                fechaEntrada_date=this.vCRUD_socio.jDateChooser_fechaEntrada.getDate();
                fechaEntrada=convertirFechaDateToString(fechaEntrada_date); // Convertimos la fecha en 'String' con el formato 'dd/MM/yyyy'
                categoria_string=this.vCRUD_socio.jComboBox_categoria.getSelectedItem().toString();
                categoria=categoria_string.charAt(0); // Convertimos el 'String' en 'Character'

                // Mostramos una ventana para confirmar si queremos insertar/modificar el Nuevo Socio en la BD
                opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vSocio, "¿Estás seguro que deseas insertar/modificar el Socio (" + nombre + ")?", "Confirmacion Insertar/Modificar Socio");
                switch(opcion_confirmacion)
                {
                    case 0: // Pulsamos el boton 'YES'
                    {
                        // Comprobamos que el formato de los campos es correcto antes de insertar/modificar el nuevo Socio en la BD
                        datos_correctos=comprobarDatosSocio(numeroSocio, nombre, dni, telefono, correo, fechaNac_date, fechaEntrada_date, categoria);
                        
                        if(datos_correctos==true)
                        {
                            if(this.operacion=='I') // Si queremos insertar un nuevo Socio en la BD
                            {
                                // Comprobamos si existe un Socio con el 'numSocio' o el 'dni' introducido en la BD
                                try
                                {
                                    existe_socio=compruebaExisteSocio(numeroSocio, dni);
                                    if(existe_socio==true) // Si existe un Socio con el DNI introducido, mostramos un mensaje de error
                                    {
                                        this.vMensaje.mensaje(this.vSocio, "Ya existe un Socio con el DNI introducido '" + dni + "'", "Socio ya existente", "ERROR");
                                        this.vMensaje.mostrar("\n\nYa existe un Socio con el DNI introducido '" + dni + "'");
                                    }
                                    else // Si NO existe un Socio con el 'numSocio' o el 'dni' introducido, insertamos el Socio en la BD
                                    {
                                        altaSocio(numeroSocio, nombre, dni, fechaNac, telefono, correo, fechaEntrada, categoria);
                                        this.vCRUD_socio.setVisible(false); // Ocultamos el 'JDialog'
                                    }
                                }
                                catch (Exception ex)
                                {
                                    this.vMensaje.mensaje(this.vSocio, "ControladorSocio: Error en 'compruebaExisteSocio()' -> " + ex.getMessage(), "Error en 'existeSocio()' de 'compruebaExisteSocio()'", "ERROR");
                                    this.vMensaje.mostrar("\n\n\"ControladorSocio: Error en 'compruebaExisteSocio()' ->" + ex.getMessage() + "\n");
                                    Logger.getLogger(ControladorSocio.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if(this.operacion=='A') // Si queremos actualizar los datos de un Socio de la BD
                            {
                                // Actualizamos los datos del Socio en la BD
                                altaSocio(numeroSocio, nombre, dni, fechaNac, telefono, correo, fechaEntrada, categoria);
                                this.vCRUD_socio.setVisible(false); // Ocultamos el 'JDialog' 
                            }
                            
                            // Mostramos la Tabla de los Socios actualizada correctamente
                            refrescarTablaSocio();
                        }
                    }; break;

                    case 1: // Pulsamos el boton 'NO'
                    {
                        this.vMensaje.mensaje(null, "Operacion CRUD del Socio CANCELADA", "Operacion Cancelada", "CANCELAR");
                    }; break;

                    case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                    {

                    }; break;
                }
            }; break;
            
            case "CancelarSocio": // Boton 'jButton_cancelarS' de 'VistaSocioCRUD' utilizado para Insertar o Modificar un Socio
            {
                this.vMensaje.mensaje(null, "Operación CRUD del Socio CANCELADA", "Operacion Cancelada", "CANCELAR");
                this.vCRUD_socio.setVisible(false); // Ocultamos el 'JDialog'
            }; break;
            
            case "BorrarSocio": // Boton 'jButton_borrarSocio' de 'VistaSocio'
            {
                int fila_seleccionada;
                int opcion_confirmacion;
                int columna_numeroSocio=0;
                String numeroSocio_eliminar; // 'numeroSocio' del Socio que queremos eliminar de la BD
                int columna_nomSocio=1;
                String nomSocio_eliminar; // 'nombre' del Socio que queremos eliminar de la BD
                
                fila_seleccionada=this.vSocio.jTable_socios.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Borrar Socio", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    numeroSocio_eliminar=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, columna_numeroSocio).toString();
                    nomSocio_eliminar=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, columna_nomSocio).toString();
                    
                    // Mostramos una ventana para confirmar si queremos eliminar un Socio de la BD
                    opcion_confirmacion=this.vMensaje.mensajeConfirmacion(this.vSocio, "¿Estás seguro que deseas eliminar el Socio seleccionado (" + nomSocio_eliminar + ")?", "Confirmacion Borrar Socio");
                    switch(opcion_confirmacion)
                    {
                        case 0: // Pulsamos el boton 'YES'
                        {
                            // Eliminamos el Socio en la BD
                            bajaSocio(numeroSocio_eliminar);                            
                            this.vCRUD_socio.setVisible(false); // Ocultamos el 'JDialog'
                            
                            // Mostramos la Tabla de los Socios actualizada correctamente
                            refrescarTablaSocio();
                        }; break;

                        case 1: // Pulsamos el boton 'NO'
                        {
                            this.vMensaje.mensaje(null, "Borrado de un Socio CANCELADA", "Operacion Cancelada", "CANCELAR");
                        }; break;

                        case -1: // NO pulsamos ningun boton o le damos a la 'X' para cerrar la ventana
                        {

                        }; break;
                    }
                }   
            }; break;
            
            case "ActualizarSocio": // Boton 'jButton_actualizarSocio' de 'VistaSocio'
            {
                String numeroSocio_modificar; // 'numeroSocio' del Socio que queremos modificar en la BD
                String nombre, dni, fechaNac, telefono, correo, fechaEntrada, categoria_string;
                Character categoria;
                Object valor_fechaNac;
                Date fechaNac_date=null, fechaEntrada_date;
                
                SimpleDateFormat formatoFecha=new SimpleDateFormat("dd/MM/yyyy");
                int fila_seleccionada;
                
                this.operacion='A';
                fila_seleccionada=this.vSocio.jTable_socios.getSelectedRow();
                
                if(fila_seleccionada==-1) // Si NO hemos seleccionado ninguna fila de la Tabla
                {
                    this.vMensaje.mensaje(null, "NO has seleccionado ninguna fila de la Tabla", "Advertencia de Actualizar Socio", "AVISO");
                }
                else // Si hemos seleccionado alguna fila de la Tabla
                {
                    // Obtenemos los valores del Socio que queremos modificar
                    numeroSocio_modificar=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 0).toString();
                    nombre=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 1).toString();
                    dni=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 2).toString();
                    valor_fechaNac=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 3);
                    if(valor_fechaNac!=null)
                    {
                        fechaNac=valor_fechaNac.toString();
                        fechaNac_date=convertirFechaStringToDate(fechaNac); // Convertimos la fecha en 'Date'
                    }
                    telefono=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 4).toString();
                    correo=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 5).toString();
                    fechaEntrada=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 6).toString();
                    fechaEntrada_date=convertirFechaStringToDate(fechaEntrada); // Convertimos la fecha en 'Date'
                    categoria_string=this.vSocio.jTable_socios.getValueAt(fila_seleccionada, 7).toString();
                    
                    // Mostramos el JDialog para rellenar los datos del Socio que queremos modificar
                    this.vCRUD_socio.jTextField_codigo.setText(numeroSocio_modificar);
                    this.vCRUD_socio.jTextField_nombre.setText(nombre);
                    this.vCRUD_socio.jTextField_dni.setText(dni);
                    this.vCRUD_socio.jDateChooser_fechaNac.setDate(fechaNac_date);
                    this.vCRUD_socio.jTextField_telefono.setText(telefono);
                    this.vCRUD_socio.jTextField_correo.setText(correo);
                    this.vCRUD_socio.jDateChooser_fechaEntrada.setDate(fechaEntrada_date);
                    this.vCRUD_socio.jComboBox_categoria.setSelectedItem(categoria_string);
                    this.vCRUD_socio.setTitle("Actualizar Socio"); // Escribimos el titulo del JDialog
                    this.vCRUD_socio.setVisible(true); // Mostramos el JDialog
                }   
            }; break;
        }
    }
    
    /**
     * Llama al metodo 'listarTodos()' que es una Consulta de 'SocioDAO'
     * @return Devuelve un array que contiene todos los Socios de la BD
     * @throws Exception Lanza una excepcion si NO hay ningun Socio en la BD
     */
    public ArrayList<Socio> pideSocios() throws Exception
    {
        this.sesion=this.sessionFactory.openSession();
        
        ArrayList<Socio> v_socios=this.socioDAO.listarTodos(this.sesion);
        
        return v_socios;
    }
    
    /**
     * Llama al metodo 'listadoActividadesSocio()' que es una Consulta de 'SocioDAO'
     * @param numSocio Es el 'numeroSocio' del Socio que queremos obtener las Actividades
     * @return Devuelve un array con las Actividades en las que esta inscrito el Socio o 'null' si NO esta inscrito en ninguna Actividad
     * @throws Exception Lanza una excepcion si NO existe el Socio con el 'numeroSocio' introducido
     */
    public ArrayList<Actividad> obtenerActividadesSocio(String numSocio) throws Exception
    {
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        v_actividades=this.socioDAO.listadoActividadesSocio(this.sesion, numSocio);
        
        return v_actividades;
    }
    
    /**
     * Llama al metodo 'listadoNOActividadesSocio()' que es una Consulta de 'SocioDAO'
     * @param numSocio Es el 'numeroSocio' del Socio que queremos obtener las Actividades en la que NO esta inscrito
     * @return Devuelve un array con las Actividades en las que NO esta inscrito el Socio introducido por parametro o 'null' si NO existe el Socio introducido
     * @throws Exception Lanza una excepcion si NO existe el Socio con el 'numeroSocio' introducido
     */
    public ArrayList<Actividad> obtenerNOActividadesSocio(String numSocio) throws Exception
    {
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        v_actividades=this.socioDAO.listadoNOActividadesSocio(this.sesion, numSocio);
        
        return v_actividades;
    }
    
    /**
     * Llama al metodo 'buscarPorNombre()' que es una Consulta de 'SocioDAO'
     * @param nombre Es el 'nombre' del Socio que queremos obtener
     * @return Devuelve el Socio con el 'nombre' introducido
     * @throws Exception Lanza una excepcion si NO existe el Socio con el 'nombre' introducido
     */
    public Socio obtenerSocioPorNombre(String nombre) throws Exception
    {
        Socio s;
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        s=this.socioDAO.buscarPorNombre(this.sesion, nombre);
        
        return s;
    }
    
    /**
     * Comprueba si existe un Socio con el 'numeroSocio' o 'dni' introducido por parametro
     * @param numeroSocio Es el 'numeroSocio' del Socio que queremos comprobar si existe
     * @param dniSocio Es el 'dni' del Socio que queremos comprobar si existe
     * @return Devuelve 'true' si existe un Socio con alguno de los 2 campos introducidos y 'false' si NO existe
     * @throws Exception Lanza una excepcion si se produce algun error
     */
    public boolean compruebaExisteSocio(String numeroSocio, String dniSocio) throws Exception
    {
        boolean existe_socio=false;
        
        // Variables para realizar la transaccion
        this.sesion = this.sessionFactory.openSession();
        
        existe_socio=this.socioDAO.existeSocio(this.sesion, numeroSocio, dniSocio);
        
        return existe_socio;
    }
    
    /**
     * Permite insertar/modificar a un Socio en la BD
     * @param numeroSocio 'numSocio' del nuevo Socio que queremos insertar en la BD
     * @param nombre 'nombre' del nuevo Socio que queremos insertar en la BD
     * @param dni 'dni' del nuevo Socio que queremos insertar en la BD
     * @param fechaNacimiento 'fechaNacimiento' del nuevo Socio que queremos insertar en la BD
     * @param telefono 'telefono' del nuevo Socio que queremos insertar en la BD
     * @param correo 'correo' del nuevo Socio que queremos insertar en la BD
     * @param fechaEntrada 'fechaEntrada' del nuevo Socio que queremos insertar en la BD
     * @param categoria 'categoria' del nuevo Socio que queremos insertar en la BD
     */
    public void altaSocio(String numeroSocio, String nombre, String dni, String fechaNacimiento, String telefono, String correo, String fechaEntrada, Character categoria)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            Socio nuevoSocio = new Socio(numeroSocio, nombre, dni, fechaNacimiento, telefono, correo, fechaEntrada, categoria);
            this.socioDAO.insertarSocio(this.sesion, nuevoSocio); // Comprueba que los datos sean correctos y sino lanza una Excepcion
            this.tr.commit();
            this.vMensaje.mensaje(null, "Socio '" + nuevoSocio.getNumeroSocio()+ "' insertado/modificado con éxito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nSocio '" + nuevoSocio.getNumeroSocio()+ "' insertado/modificado con exito en la BD.");
        }
        catch(PersistenceException pex)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "Error al insertar/modificar el Socio, el DNI ya existe en la BD", "Operación CRUD Errónea", "ERROR");
            this.vMensaje.mostrar("\n\nError al insertar/modificar el Socio, el DNI ya existe en la BD\n");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorSocio: Error en 'altaSocio()' al insertar/modificar el Socio en la BD -> " + e.getMessage(), "Operación CRUD Errónea", "ERROR");
            this.vMensaje.mostrar("\n\nControladorSocio: Error en 'altaSocio()' al insertar/modificar el Socio en la BD -> " + e.getMessage() + "\n");
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
     * Permite eliminar/borrar a un Socio de la BD
     * @param numeroSocio 'numeroSocio' del Socio que queremos eliminar de la BD
     */
    public void bajaSocio(String numeroSocio)
    {
        // Variables para realizar la transaccion
        this.sesion=this.sessionFactory.openSession();
        this.tr = this.sesion.beginTransaction();
        
        try
        {
            // Eliminamos el Socio de la BD
            Socio s = this.sesion.get(Socio.class, numeroSocio);
            this.socioDAO.eliminarSocio(this.sesion, s);
            this.tr.commit();
            this.vMensaje.mensaje(null, "Socio '" + numeroSocio + "' eliminado con éxito", "Operación CRUD Realizada", "INFO");
            this.vMensaje.mostrar("\n\nSocio '" + s.getNumeroSocio() + "' eliminado con exito en la BD.");
        }
        catch(Exception e)
        {
            this.tr.rollback();
            this.vMensaje.mensaje(null, "ControladorSocio: Error en 'bajaSocio()' al eliminar el Socio de la BD -> " + e.getMessage(), "Operacion CRUD 'bajaSocio()'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorSocio: Error en 'bajaSocio()' al eliminar el Socio de la BD -> " + e.getMessage() + "\n");
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
     * Calcula la Cuota mensual de un Socio en base a la categoria que tiene el Socio introducido por parametro
     * La 'cuota' es un atributo derivado de la Tabla REALIZA
     * @param s Es el Socio del que queremos saber la cuota que debe pagar cada mes
     * @return Devuelve la cuota que tiene que pagar el Socio cada mes en base a su Categoria
     */
    public int obtenerCuotaSocio(Socio s)
    {
        int precio_total=0; // Precio total de la Suma de todas las actividades en las que esta matriculado el Socio 's' sin tener en cuenta el descuento por el tipo de categoria
        int cuota=0; // Cuota total que debe pagar al mes el Socio 's' teniendo en cuenta el descuento por el tipo de categoria
        int descuento=0;
        int precio_descuento=0;
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        
        try
        {
            v_actividades=obtenerActividadesSocio(s.getNumeroSocio());
            
            if(v_actividades!=null) // Si el Socio introducido tiene actividades
            {
                for(Actividad a: v_actividades)
                {
                    int precio_actividad=a.getPrecioBaseMes();
                    precio_total=precio_total+precio_actividad;
                }
            }
        }
        catch (Exception ex)
        {
            this.vMensaje.mensaje(this.vSocio, "ControladorSocio: Error en 'obtenerCuotaSocio() -> " + ex.getMessage(), "Error en 'obtenerCuotaSocio()'", "ERROR");
            this.vMensaje.mostrar("\n\nControladorSocio: Error en 'obtenerCuotaSocio() -> " + ex.getMessage() + "\n");
            Logger.getLogger(ControladorSocio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch(s.getCategoria())
        {
            case 'A': // Categoria A (Sin Descuento)
            {
                descuento=0;
                precio_descuento=1-(descuento/100);
                
            }; break;
            
            case 'B': // Categoria B (10% de descuento)
            {
                descuento=10;
                precio_descuento=1-(descuento/100);
                
            }; break;
            
            case 'C': // Categoria C (20% de descuento)
            {
                descuento=20;
                precio_descuento=1-(descuento/100);
                
            }; break;
            
            case 'D': // Categoria D (30% de descuento)
            {
                
                descuento=30;
                precio_descuento=1-(descuento/100);
                
            }; break;
            
            case 'E': // Categoria E (40% de descuento)
            {
                descuento=40;
                precio_descuento=1-(descuento/100);
                
            }; break;
        }
        
        cuota=precio_total*precio_descuento;
        
        return cuota;
    }
    
};
