/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Modelo.Socio;
import Vista.VistaInscripcion;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class GestionTablaInscripcion
{
    private static DefaultTableModel modeloTablaInscripcion;
    private static int numColumnas;
    
    public GestionTablaInscripcion()
    {
        numColumnas=8;
        modeloTablaInscripcion=new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }
    
    /**
     * Inicializa la Tabla de Socios y hace que las celdas de la Tabla NO sean modificables al hacer doble click sobre ellas
     * @param vInscripcion Es la Vista en la que queremos mostrar la Tabla ('VistaInscripcion')
     */
    public static void inicializarTablaInscripcion(VistaInscripcion vInscripcion)
    {
        vInscripcion.jTable_inscripcionSocios.setModel(modeloTablaInscripcion);
    }
    
    /**
     * Escribimos los valores de la Tabla (Cabecera y datos)
     * @param vInscripcion Es la Vista en la que queremos mostrar la Tabla ('VistaInscripcion')
     */
    public static void dibujarTablaInscripcion(VistaInscripcion vInscripcion)
    {
        String[] columnas_tabla=new String[numColumnas];
        columnas_tabla[0]="Número Socio";
        columnas_tabla[1]="Nombre";
        columnas_tabla[2]="DNI";
        columnas_tabla[3]="Fecha Nacimiento";
        columnas_tabla[4]="Teléfono";
        columnas_tabla[5]="Correo";
        columnas_tabla[6]="Fecha Incorporación";
        columnas_tabla[7]="Categoría";
        
        modeloTablaInscripcion.setColumnIdentifiers(columnas_tabla); // Escribimos el valor de las Columnas en la Tabla
        
        vInscripcion.jTable_inscripcionSocios.getTableHeader().setResizingAllowed(false); // Hacemos que la Tabla NO sea redimensionable
        vInscripcion.jTable_inscripcionSocios.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); // Hacemos que la Tabla se redimensione automaticamente segun el ancho de la ultima columna
        
        // Ajustamos el Ancho de cada Columna de la Tabla
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(0).setPreferredWidth(40);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(1).setPreferredWidth(240);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(2).setPreferredWidth(70);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(3).setPreferredWidth(70);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(4).setPreferredWidth(100);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(5).setPreferredWidth(150);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(6).setPreferredWidth(60);
        vInscripcion.jTable_inscripcionSocios.getColumnModel().getColumn(7).setPreferredWidth(30);
    }
    
    /**
     * Escribe en la Tabla de Socios lo que contenga el vector 'v_socios' tras realizar una consulta
     * @param v_socios Es el vector de tipo Socio que contiene los datos de los Socios que queremos mostrar en la Tabla
     */
    public static void escribirTablaInscripcion(ArrayList<Socio> v_socios)
    {
        Object[] filas_tabla=new Object[numColumnas];
        
        for(Socio s: v_socios)
        {
            filas_tabla[0]=s.getNumeroSocio();
            filas_tabla[1]=s.getNombre();
            filas_tabla[2]=s.getDni();
            filas_tabla[3]=s.getFechaNacimiento();
            filas_tabla[4]=s.getTelefono();
            filas_tabla[5]=s.getCorreo();
            filas_tabla[6]=s.getFechaEntrada();
            filas_tabla[7]=s.getCategoria();
            modeloTablaInscripcion.addRow(filas_tabla); // Añadimos la fila 'i' con los datos del Socio que hemos obtenido en el vector 'filas_tabla'
        }
    }
    
    /**
     * Elimina todas las filas de la Tabla de Socios
     * Se utiliza antes de escribir en la Tabla de Socios, ya que los datos deben haber sido borrados previamente antes de escribir
     */
    public static void borrarTablaInscripcion()
    {
        while(modeloTablaInscripcion.getRowCount()>0) // Mientras haya filas, las borramos
        {
            modeloTablaInscripcion.removeRow(0);
        }
    }
    
    /**
     * 
     * @return Devuelve el campo 'numeroSocio' del ultimo Socio de la BD
     */
    public static String getCodUltimo()
    {
        int numFilas=modeloTablaInscripcion.getRowCount();
        int fila_numeroSocio=0;
        String cod;
        
        cod=modeloTablaInscripcion.getValueAt(numFilas-1, fila_numeroSocio).toString();
        
        return cod;
    }
    
};
