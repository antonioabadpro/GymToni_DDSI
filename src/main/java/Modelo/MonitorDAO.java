/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaActividad;
import Vista.VistaMensaje;
import java.util.ArrayList;
import javax.persistence.Query;
import org.hibernate.Session;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 * Clase 'Monitor' DAO (Data Access Object)
 */
public class MonitorDAO
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private VistaActividad vActividad=null;
    
    // ********************************* METODOS PUBLICOS *********************************
    public MonitorDAO()
    {
        this.vMensaje=new VistaMensaje();
        this.vActividad=new VistaActividad();
    }
    
    /**
     * Inserta un nuevo Monitor dado por parametro en la BD
     * Se utiliza en 'ControladorMonitor' desde el metodo 'altaMonitor()'
     * @param sesion Indica la Sesion en la que queremos insertar el nuevo Monitor, NO podemos cerrarla
     * @param nuevoMonitor Es el Nuevo Monitor que queremos insertar en la BD
     * @throws Exception Es la excepcion que se lanza si el Monitor NO ha sido insertado correctamente en la BD
     */
    public void insertarMonitor(Session sesion, Monitor nuevoMonitor) throws Exception
    {
        sesion.saveOrUpdate(nuevoMonitor);
    }
    
    /**
     * Elimina un Monitor dado por parametro en la BD
     * Se utiliza en 'ControladorMonitor' desde el metodo 'bajaMonitor()'
     * @param sesion Indica la Sesion en la que queremos eliminar el Monitor, NO podemos cerrarla
     * @param eliminarMonitor Es el Monitor que queremos eliminar en la BD
     * @throws Exception Es la excepcion que se lanza si el Monitor NO ha sido eliminado correctamente en la BD
     */
    public void eliminarMonitor(Session sesion, Monitor eliminarMonitor) throws Exception
    {
        sesion.delete(eliminarMonitor);
    }
    
    /**
     * Consulta que comprueba si existe un Monitor con el 'dni' introducido en la BD
     * Se utiliza para insertar un Monitor en la BD (Boton 'Nuevo Monitor' en 'VistaMonitor')
     * Consulta Nombrada HQL
     * @param sesion Indica la Sesion en la que queremos comprobar si existe el Monitor, NO podemos cerrarla
     * @param dni Es el 'dni' del Monitor que queremos comprobar si esta en nuestra BD
     * @return Devuelve 'true' si existe un Monitor con el 'dni' introducido y 'false' si NO existe
     */
    public boolean existeMonitor(Session sesion, String dni)
    {
        boolean encontrado=false;
        Query consulta;
        
        try
        {
            // Realizamos la Consulta para comprobar si existe un Monitor con el 'dni' introducido en nuestra BD -> HQL Nombrada
            consulta=sesion.createNamedQuery("Monitor.findByDni", Monitor.class);
            consulta.setParameter("dni", dni);
            Monitor m=(Monitor)consulta.getSingleResult();
            
            if(m!=null)
            {
                encontrado=true;
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("\nMonitorDAO: Error en 'existeMonitor()' -> " + e.getMessage() + "\n\n");
        }
        return encontrado;
    }
    
    /**
     * Consulta que muestra las Actividades de las que un Monitor cuyo 'dni' introducido por parametro es responsable
     * Consulta HQL
     * @param sesion Indica la Sesion en la que queremos mostrar las actividades del Monitor, NO podemos cerrarla
     * @param nomMonitor Es el 'nombre' del Monitor del que queremos mostrar sus Actvidades
     * @return Deuelve un array con las Actividades en las que el Monitor es responsable
     */
    public ArrayList<Actividad> nombreActividadesMonitor(Session sesion, String nomMonitor)
    {
        Query consulta;
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para mostrar las Actividades que tiene un Monitor
            consulta=sesion.createQuery("SELECT a FROM Actividad a JOIN a.monitorResponsable m WHERE m.nombre=:nombreMonitor");
            // consulta=sesion.createNativeQuery("SELECT * FROM ACTIVIDAD a INNER JOIN MONITOR m ON a.monitorResponsable=m.codMonitor WHERE m.dni=:dni"); // Consulta SQL Nativo
            consulta.setParameter("nombreMonitor", nomMonitor);
            v_actividades=(ArrayList<Actividad>)consulta.getResultList();
            
            if(v_actividades.isEmpty()) // Si el array de Actividades esta vacio
            {
                this.vMensaje.mostrar("El Monitor con el DNI introducido NO tiene actividades\n");
            }
            else // Si el array de Actividades NO esta vacio
            {
                // Mostramos por Consola el 'codMonitor' del Monitor que queremos mostrar sus actividades
                obtenerMonitorPorNombre(sesion, nomMonitor);
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("\nMonitorDAO: Error en 'nombreActividadesMonitor()' -> " + e.getMessage() + "\n\n");
        }
        return v_actividades;
    }

    /**
     * Consulta que muestra todos los Monitores que existen en la BD
     * Consulta HQL
     * @param sesion Indica la Sesion en la que queremos mostrar los Monitores, NO podemos cerrarla
     * @return Devuelve un array que contiene todos los Monitores de la BD
     */
    public ArrayList<Monitor> listarTodos(Session sesion)
    {
        Query consulta;
        ArrayList<Monitor> v_monitores=new ArrayList<Monitor>();
        
        try
        {
            // Realizamos la Consulta para mostrar las Actividades que tiene un Monitor
            consulta=sesion.createQuery("FROM Monitor m"); // Consulta HQL
            v_monitores=(ArrayList<Monitor>)consulta.getResultList();
            
            if(v_monitores.isEmpty()) // Si el array de Monitores esta vacio
            {
                this.vMensaje.mensaje(null, "NO hay Monitores en la BD", "Información en 'listarTodos()'", "INFO");
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mensaje(null, "MonitorDAO: Error en 'listarTodos()' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return v_monitores;
    }
    
    /**
     * Consulta que muestra el Monitor con el 'nombre' introducido por parametro
     * Se utiliza en 'ControladorActividad' para obtener el campo 'monitorResponsable' para insertar una nueva Actividad
     * Consulta HQL
     * @param sesion Indica la Sesion de la que queremos obtener el Monitor, NO podemos cerrarla
     * @param nomMonitor Es el 'nombre' del Monitor que queremos obtener
     * @return Devuelve un Monitor de la BD
     */
    public Monitor obtenerMonitorPorNombre(Session sesion, String nomMonitor)
    {
        Query consulta;
        Monitor m=null;

        try
        {
            if(nomMonitor.isEmpty()==false && nomMonitor!=null && nomMonitor!="")
            {
                consulta=sesion.createQuery("SELECT m FROM Monitor m WHERE m.nombre=:nomMonitor", Monitor.class); // Consulta HQL
                consulta.setParameter("nomMonitor", nomMonitor);
                m=(Monitor)consulta.getSingleResult();

                if(m==null) // Si NO ningun Monitor con el Nombre introducido
                {
                    this.vMensaje.mensaje(null, "NO hay ningun Monitor con el nombre introducido en la BD", "Información en 'obtenerMonitorPorNombre()'", "INFO");
                }
            }
        }
        catch(Exception ex)
        {
            this.vMensaje.mostrar("\n\nMonitorDAO: Error en 'obtenerMonitorPorNombre()'" + ex.getMessage() + "\n");
        }
        finally
        {
            if(sesion != null && sesion.isOpen())
            {
                sesion.close();
            }
        }
        return m;
    }
};
