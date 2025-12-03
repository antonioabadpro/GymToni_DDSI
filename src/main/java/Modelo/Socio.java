/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
@Entity
@Table(name = "SOCIO")
@XmlRootElement

// Consultas Nombradas HQL
@NamedQueries({
    @NamedQuery(name = "Socio.buscarTodos", query = "SELECT s FROM Socio s"),
    @NamedQuery(name = "Socio.findByNumeroSocio", query = "SELECT s FROM Socio s WHERE s.numeroSocio = :numeroSocio"),
    @NamedQuery(name = "Socio.findByNombre", query = "SELECT s FROM Socio s WHERE s.nombre = :nombre"),
    @NamedQuery(name = "Socio.findByDni", query = "SELECT s FROM Socio s WHERE s.dni = :dni"),
    @NamedQuery(name = "Socio.findByFechaNacimiento", query = "SELECT s FROM Socio s WHERE s.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Socio.findByTelefono", query = "SELECT s FROM Socio s WHERE s.telefono = :telefono"),
    @NamedQuery(name = "Socio.findByCorreo", query = "SELECT s FROM Socio s WHERE s.correo = :correo"),
    @NamedQuery(name = "Socio.findByFechaEntrada", query = "SELECT s FROM Socio s WHERE s.fechaEntrada = :fechaEntrada"),
    @NamedQuery(name = "Socio.findByCategoria", query = "SELECT s FROM Socio s WHERE s.categoria = :categoria")})

// Consultas Nombradas SQL Nativo
@NamedNativeQueries({
    @NamedNativeQuery(name = "Socio.buscarPorCategoriaSQL", query = "SELECT * FROM SOCIO s WHERE s.categoria = :cat", resultClass=Socio.class)})

public class Socio implements Serializable
{

    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "numeroSocio")
    private String numeroSocio;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "dni")
    private String dni;
    @Column(name = "fechaNacimiento")
    private String fechaNacimiento;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "correo")
    private String correo;
    @Basic(optional = false)
    @Column(name = "fechaEntrada")
    private String fechaEntrada;
    @Basic(optional = false)
    @Column(name = "categoria")
    private Character categoria;
    @ManyToMany(mappedBy = "socios")
    private Set<Actividad> actividades=new HashSet<Actividad>();

    // ********************************* METODOS PUBLICOS *********************************
    /**
     * Constructor sin parametros
     */
    public Socio()
    {
        
    }
    
    /**
     * Constructor con la Clave Primaria (PK)
     * @param numeroSocio Es la PK de la Tabla Socio
     */
    public Socio(String numeroSocio)
    {
        this.numeroSocio = numeroSocio;
    }

    /**
     * Constructor con todos los parametros de la Tabla Socio
     * @param numeroSocio Es el Codigo del Socio (Clave Primaria)
     * @param nombre Es el Nombre del Socio (NOT NULL)
     * @param dni Es el DNI del Socio (Campo Unico, NOT NULL)
     * @param fechaNacimiento Es la Fecha de Nacimiento del Socio (Puede ser NULL)
     * @param telefono Es el Numero de Telefono del Socio
     * @param correo Es el Correo Electronico del Socio
     * @param fechaEntrada Es la Fecha de Entrada del Monitor en el Gimnasio (NOT NULL)
     * @param categoria Es la Categoria del Socio e indica el descuento al mes (NOT NULL)
     */
    public Socio(String numeroSocio, String nombre, String dni, String fechaNacimiento, String telefono, String correo, String fechaEntrada, Character categoria)
    {
        this.numeroSocio = numeroSocio;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento=fechaNacimiento;
        this.telefono=telefono;
        this.correo=correo;
        this.fechaEntrada = fechaEntrada;
        this.categoria = categoria;
    }

    /**
     * 
     * @return Devuelve el 'numeroSocio' del Socio
     */
    public String getNumeroSocio()
    {
        return numeroSocio;
    }

    /**
     * Establece el 'numeroSocio' de un Socio
     * @param numeroSocio Es el codigo del Socio que queremos establecer
     */
    public void setNumeroSocio(String numeroSocio)
    {
        this.numeroSocio = numeroSocio;
    }

    /**
     * 
     * @return Devuelve el 'nombre' del Socio
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Establece el 'nombre' de un Socio
     * @param nombre Es el Nombre del Socio que queremos establecer
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * 
     * @return Devuelve el 'dni' del Socio
     */
    public String getDni()
    {
        return dni;
    }

    /**
     * Establece el 'dni' de un Socio
     * @param dni Es el DNI del Socio que queremos establecer
     */
    public void setDni(String dni)
    {
        this.dni = dni;
    }

    /**
     * 
     * @return Devuelve la 'fechaNacimiento' del Socio
     */
    public String getFechaNacimiento()
    {
        return fechaNacimiento;
    }

    /**
     * Establece la 'fechaNacimiento' de un Socio
     * @param fechaNacimiento Es la Fecha de Nacimiento del Socio que queremos establecer
     */
    public void setFechaNacimiento(String fechaNacimiento)
    {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * 
     * @return Devuelve el 'telefono' de un Socio
     */
    public String getTelefono()
    {
        return telefono;
    }

    /**
     * Establece el 'telefono' de un Socio
     * @param telefono Es el Numero de Telefono del Socio que queremos establecer
     */
    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    /**
     * 
     * @return Devuelve el 'correo' del Socio
     */
    public String getCorreo()
    {
        return correo;
    }

    /**
     * Establece el 'correo' de un Socio
     * @param correo Es el Correo Electronico del Socio que queremos establecer
     */
    public void setCorreo(String correo)
    {
        this.correo = correo;
    }
    
    /**
     * 
     * @return Devuelve la 'fechaEntrada' del Socio
     */
    public String getFechaEntrada()
    {
        return fechaEntrada;
    }

    /**
     * Establece la 'fechaEntrada' de un Socio
     * @param fechaEntrada Es la Fecha de Entrada al Gimnasio del Socio que queremos establecer
     */
    public void setFechaEntrada(String fechaEntrada)
    {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * 
     * @return Devuelve la 'categoria' del Socio
     */
    public Character getCategoria()
    {
        return categoria;
    }

    /**
     * Establece la 'categoria' de un Socio
     * @param categoria Es la Categoria del Socio que queremos establecer
     */
    public void setCategoria(Character categoria)
    {
        this.categoria = categoria;
    }

    /**
     * 
     * @return Devuelve el Conjunto de Actividades en las que el Socio esta inscrito
     */
    @XmlTransient
    public Set<Actividad> getActividadSet()
    {
        return actividades;
    }

    /**
     * Establece el Conjunto de Actividades en las que el Socio esta inscrito
     * ¡OJO! -> NO añade una actividad al Conjunto de Actividades del Socio, sino que lo establece desde 0
     * @param actividades Es el Conjunto de Actividades que queremos asignar a un Socio para que este inscrito en ellas
     */
    public void setActividadSet(Set<Actividad> actividades)
    {
        this.actividades = actividades;
    }

    /**
     * Calcula el código hash del Socio basado en el 'numeroSocio'
     * @return Devuelve el valor del codigo Hash del Socio
     */
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (numeroSocio != null ? numeroSocio.hashCode() : 0);
        return hash;
    }

    /**
     * Compara si 2 Socios son iguales en base a su Clave Primaria 'numeroSocio'
     * @param object Es el objeto que queremos comprobar para ver si es igual que el Socio
     * @return Devuelve 'true' si ambos Objetos son iguales y 'false' en caso contrario
     */
    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Socio))
        {
            return false;
        }
        Socio other = (Socio) object;
        if ((this.numeroSocio == null && other.numeroSocio != null) || (this.numeroSocio != null && !this.numeroSocio.equals(other.numeroSocio)))
        {
            return false;
        }
        return true;
    }

    /**
     * Transforma un Objeto que NO es de tipo 'String' en un 'String' con el formato "Modelo.Socio[ numeroSocio=" + numeroSocio + " ]"
     * @return Devuelve una cadena de caracteres
     */
    @Override
    public String toString()
    {
        return "Modelo.Socio[ numeroSocio=" + numeroSocio + " ]";
    }
    
};
