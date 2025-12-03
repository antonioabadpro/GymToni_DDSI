/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaMensaje;
import org.hibernate.Session;
import org.hibernate.query.Query;
import Vista.VistaActividad;
import java.util.ArrayList;
import javax.persistence.NoResultException;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 * Clase 'Socio' DAO (Data Access Object)
 */
public class SocioDAO
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    private VistaActividad vActividad=null;
    
    // ********************************* METODOS PUBLICOS *********************************
    public SocioDAO()
    {
        this.vMensaje=new VistaMensaje();
        this.vActividad=new VistaActividad();
    }

    /**
     * Inserta un nuevo Socio dado por parametro en la BD
     * Se utiliza en 'ControladorSocio' desde el metodo 'altaSocio()'
     * @param sesion Indica la Sesion en la que queremos insertar el nuevo Socio, NO podemos cerrarla
     * @param socio Es el Nuevo Socio que queremos insertar en la BD
     * @throws Exception Es la excepcion que se lanza si el Socio NO ha sido insertado correctamente en la BD
     */
    public void insertarSocio(Session sesion, Socio socio) throws Exception
    {
        sesion.saveOrUpdate(socio);
    }
    
    /**
     * Elimina un Socio dado por parametro en la BD
     * Se utiliza en 'ControladorSocio' desde el metodo 'bajaSocio()'
     * @param sesion Indica la Sesion en la que queremos eliminar el Socio, NO podemos cerrarla
     * @param socio Es el Socio que queremos eliminar en la BD
     * @throws Exception Es la excepcion que se lanza si el Socio NO ha sido eliminado correctamente en la BD
     */
    public void eliminarSocio(Session sesion, Socio socio) throws Exception
    {
        sesion.delete(socio);
    }
    
    /**
     * Inscribe o da de alta a un Socio en una Actividad
     * Se utiliza en 'ControladorInscricpion' desde 'altaSocioActividad()'
     * @param sesion Indica la Sesion en la que queremos dar de alta al Socio en una Actividad, NO podemos cerrarla
     * @param s Es el Socio que queremos dar de alta en la Actividad
     * @param a Es la Actividad en la que queremos dar de alta al Socio
     */
    public void inscribirSocioEnActividad(Session sesion, Socio s, Actividad a)
    {
        s.getActividadSet().add(a); // Añadimos la Actividad en la lista de Actividades del Socio
        a.getSocioSet().add(s); // Añadimos el Socio en la lista de Socios inscritos en la Actividad

        sesion.saveOrUpdate(s); // Actualizamos la tupla del Socio
        sesion.saveOrUpdate(a); // Actualiza la tupla de la Actividad
    }
    
    /**
     * Elimina o da de baja a un Socio de una Actividad
     * Se utiliza en 'ControladorInscricpion' desde 'bajaSocioActividad()'
     * @param sesion Indica la Sesion en la que queremos dar de baja al Socio de la Actividad, NO podemos cerrarla
     * @param s Es el Socio que queremos dar de baja en la Actividad
     * @param a Es la Actividad en la que queremos dar de baja al Socio
     */
    public void eliminarSocioDeActividad(Session sesion, Socio s, Actividad a)
    {
        s.getActividadSet().remove(a); // Eliminamos la Actividad de la lista de Actividades del Socio
        a.getSocioSet().remove(s); // Eliminamos el Socio de la lista de Socios inscritos en la Actividad

        sesion.saveOrUpdate(s); // Actualizamos la tupla del Socio
        sesion.saveOrUpdate(a); // Actualiza la tupla de la Actividad
    }

    /**
     * Consulta que comprueba si hay un Socio con el 'numSocio' o con el 'dni' introducido en la BD
     * Se utiliza para insertar un Socio en la BD (Boton 'Nuevo Socio' en 'VistaSocio')
     * Consulta HQL
     * @param sesion Indica la Sesion en la que queremos realizar la consulta, NO podemos cerrarla
     * @param numSocio Es el 'numeroSocio' del Socio que queremos comprobar si esta en nuestra BD
     * @param dni Es el 'dni' del Socio que queremos comprobar si esta en nuestra BD
     * @return Devuelve 'true' si existe un Socio con alguno de los 2 campos introducidos y 'false' si NO existe
     */
    public boolean existeSocio(Session sesion, String numSocio, String dni)
    {
        boolean encontrado=false;
        Query consulta;
        ArrayList<Socio> v_socios=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para comprobar que NO exista un Socio en nuestra BD con el 'numSocio' o el 'dni' introducido
            consulta=sesion.createQuery("FROM Socio s WHERE s.numeroSocio=:numSocio OR s.dni=:dniSocio");
            consulta.setParameter("numSocio", numSocio);
            consulta.setParameter("dniSocio", dni);
            v_socios=(ArrayList<Socio>)consulta.getResultList();
            
            if(v_socios.isEmpty()==false)
            {
                encontrado=true;
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("\nError en 'existeSocio()' -> " + e.getMessage() + "\n\n");
        }
        // ¡OJO! -> No podemos cerrar la Sesion, ya que sino cuando volvamos a llamar a este metodo la Sesion estara cerrada y
        // el metodo NO vuelve e abrirla y salta por el catch()
        
        return encontrado;
    }
    
    /**
     * Consulta que comprueba si hay un Socio con el 'numSocio' introducido en la BD
     * Consulta Nombrada HQL
     * @param sesion Indica la Sesion en la que queremos realizar la consulta, NO podemos cerrarla
     * @param numSocio Es el 'numeroSocio' del Socio que queremos comprobar si esta en nuestra BD
     * @return Devuelve 'true' si hay un Socio con el 'numeroSocio' introducido y 'false' si NO existe
     */
    public boolean existeNumSocio(Session sesion, String numSocio)
    {
        boolean encontrado=false;
        Query consulta;
        Socio s=null;
        
        try
        {
            consulta=sesion.createNamedQuery("Socio.findByNumeroSocio", Socio.class);
            consulta.setParameter("numeroSocio", numSocio);
            s=(Socio)consulta.getSingleResult();
            
            if(s!=null)
            {
                encontrado=true;
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("Error en 'existeNumSocio' -> " + e.getMessage());
        }
        return encontrado;
    }
    
    /**
     * Consulta que busca un Socio dado un 'nombre' en la BD
     * Se utiliza en 'ControladorSocio' desde el metodo 'obtenerSocioPorNombre()'
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @param nombre Es el 'nombre' del Socio que queremos buscar
     * @return Devuelve el Socio encontrado y 'null' si NO ha encontrado ningun Socio
     */
    public Socio buscarPorNombre(Session sesion, String nombre)
    {
        Socio s=null;
        Query consulta;
        try
        {
            // Realizamos una Consulta para buscar el socio por su 'nombre'
            consulta = sesion.createQuery("FROM Socio WHERE nombre = :nomSocio");
            consulta.setParameter("nomSocio", nombre);
            
            s=(Socio)consulta.getSingleResult();
        }
        catch (NoResultException e)
        {
            // Si NO se encuentra ningun Socio devolvemos 'null'
            s=null;
        }
        catch (Exception e)
        {
            this.vMensaje.mensaje(null, "SocioDAO: Error en 'buscarPorNombre()' -> " + e.getMessage(), "ERROR", "ERROR");
            this.vMensaje.mostrar("\n\nSocioDAO: Error en 'buscarPorNombre()' -> " + e.getMessage());
        }
        return s;
    }

    /**
     * Consulta que busca un Socio que contenga los caracteres del campo 'nombre' en la BD
     * Busca los Socios que parcialmente contengan el campo 'nombre' mientras escribimos los caracteres en el 'jTextField_buscarSocio'
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @param nombre Es el 'nombre' o parte del campo 'nombre' del Socio que queremos buscar ya que es una Busqueda Parcial
     * @return Devuelve una array con todos los Socios que coinciden parcialmente con el 'nombre' introducido y si NO hay ningun Socio que coincida con el 'nombre' devuelve 'null'
     */
    public ArrayList<Socio> buscarPorNombreParcial(Session sesion, String nombre)
    {
        Query consulta;
        ArrayList<Socio> v_socios=new ArrayList<>();
        try
        {
            consulta = sesion.createQuery("FROM Socio WHERE nombre LIKE :nomSocio");
            consulta.setParameter("nomSocio", "%" + nombre + "%"); // Coincidencia parcial
            v_socios=(ArrayList<Socio>) consulta.getResultList();
        }
        catch (Exception e)
        {
            this.vMensaje.mensaje(null, "Error en 'buscarPorNombreParcial()' -> " + e.getMessage(), "ERROR", "ERROR");
            this.vMensaje.mostrar("\n\nError en 'buscarPorNombreParcial()' -> " + e.getMessage());
        }
        return v_socios;
    }
    
    /**
     * Consulta que muestra todos los Socios que existen en la BD
     * Se utiliza en 'ControladorSocio' desde el metodo 'pideSocios()'
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @return Devuelve un array que contiene todos Socios de la BD
     */
    public ArrayList<Socio> listarTodos(Session sesion)
    {
        Query consulta;
        ArrayList<Socio> v_socios=new ArrayList<Socio>();
        
        try
        {
            // Realizamos la Consulta para mostrar los Socios
            consulta=sesion.createQuery("FROM Socio s"); // Consulta HQL
            v_socios=(ArrayList<Socio>)consulta.getResultList();
            
            if(v_socios.isEmpty()) // Si el array de Socio esta vacio
            {
                this.vMensaje.mensaje(null, "NO hay Socios en la BD", "Información en 'listarTodos()'", "INFO");
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mensaje(null, "Error en 'listarTodos' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return v_socios;
    }
    
    
    
    /**
     * Consulta que muestra las actividades en las que el Socio con 'numSocio' esta inscrito
     * Se utiliza en 'ControladorSocio' desde el metodo 'obtenerActividadesSocio()'
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @param numSocio Es el 'numSocio' del nuevo Socio que queremos mostrar las Actividades en las que esta inscrito
     * @return Devuelve un array con las Actividades en las que esta inscrito el Socio o 'null' si NO esta inscrito en ninguna Actividad
     */
    public ArrayList<Actividad> listadoActividadesSocio(Session sesion, String numSocio)
    {
        Query consulta;
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para mostrar las Actividades que tiene un Socio
            consulta=sesion.createQuery("SELECT a FROM Actividad a JOIN a.socios s WHERE s.numeroSocio=:numeroSocio");
            consulta.setParameter("numeroSocio", numSocio);
            v_actividades=(ArrayList<Actividad>)consulta.getResultList();

            if(v_actividades.isEmpty()) // Si el Socio 'numSocio' NO esta inscrito en ninguna Acividad
            {
                v_actividades=null;
                this.vMensaje.mostrar("\n\nEl Socio '" + numSocio + "' NO esta apuntado a ninguna actividad\n");
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mensaje(null, "Error en 'listadoActividadesSocio()' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return v_actividades;
    }
    
    /**
     * Consulta que muestra las actividades en las que el Socio con 'numSocio' NO esta inscrito
     * Se utiliza en 'ControladorSocio' desde el metodo 'obtenerNOActividadesSocio()'
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @param numSocio Es el 'numSocio' del nuevo Socio que queremos mostrar las Actividades en las que NO esta inscrito
     * @return Devuelve el listado de Actividades en las que NO esta inscrito un Socio o 'null' si esta matriculado en todas las Actividades
     */
    public ArrayList<Actividad> listadoNOActividadesSocio(Session sesion, String numSocio)
    {
        Query consulta;
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para mostrar las Actividades que tiene un Socio
            consulta=sesion.createQuery("SELECT a FROM Actividad a WHERE NOT EXISTS (" +
                                        "SELECT 1 FROM Socio s JOIN s.actividades act WHERE s.numeroSocio = :numeroSocio AND act.idActividad = a.idActividad)", 
                                        Actividad.class);
            consulta.setParameter("numeroSocio", numSocio);
            v_actividades=(ArrayList<Actividad>)consulta.getResultList();

            if(v_actividades.isEmpty()) // Si el Socio 'numSocio' esta inscrito en todas las Actividades
            {
                v_actividades=null;
                this.vMensaje.mostrar("\n\nEl Socio '" + numSocio + "' NO esta apuntado a ninguna actividad\n");
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mensaje(null, "Error en 'listadoNOActividadesSocio()' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return v_actividades;
    }
};
