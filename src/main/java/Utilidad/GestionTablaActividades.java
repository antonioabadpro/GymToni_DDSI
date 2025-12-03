/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Vista.VistaActividad;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class GestionTablaActividades
{
    private static DefaultTableModel modeloTablaActividades;
    private static int numColumnas;
    
    public GestionTablaActividades()
    {
        numColumnas=7;
        modeloTablaActividades=new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }
    
    /**
     * Inicializa la Tabla de Actividades y hace que las celdas de la Tabla NO sean modificables al hacer doble click sobre ellas
     * @param vActividad Es la Vista en la que queremos mostrar la Tabla ('VistaActividad')
     */
    public static void inicializarTablaActividades(VistaActividad vActividad)
    {
        vActividad.jTable_actividades.setModel(modeloTablaActividades);
    }
    
    /**
     * Escribimos los valores de la Tabla (Cabecera y datos)
     * @param vActividad Es la Vista en la que queremos mostrar la Tabla ('VistaActividad')
     */
    public static void dibujarTablaActividades(VistaActividad vActividad)
    {
        String[] columnas_tabla=new String[numColumnas];
        columnas_tabla[0]="Código";
        columnas_tabla[1]="Nombre";
        columnas_tabla[2]="Día";
        columnas_tabla[3]="Hora";
        columnas_tabla[4]="Descripción";
        columnas_tabla[5]="Precio Base Mes";
        columnas_tabla[6]="Nombre Monitor Responsable";
        
        modeloTablaActividades.setColumnIdentifiers(columnas_tabla); // Escribimos el valor de las Columnas en la Tabla
        
        vActividad.jTable_actividades.getTableHeader().setResizingAllowed(false); // Hacemos que la Tabla NO sea redimensionable
        vActividad.jTable_actividades.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); // Hacemos que la Tabla se redimensione automaticamente segun el ancho de la ultima columna
        
        // Ajustamos el Ancho de cada Columna de la Tabla
        vActividad.jTable_actividades.getColumnModel().getColumn(0).setPreferredWidth(40);
        vActividad.jTable_actividades.getColumnModel().getColumn(1).setPreferredWidth(180);
        vActividad.jTable_actividades.getColumnModel().getColumn(2).setPreferredWidth(70);
        vActividad.jTable_actividades.getColumnModel().getColumn(3).setPreferredWidth(10);
        vActividad.jTable_actividades.getColumnModel().getColumn(4).setPreferredWidth(350);
        vActividad.jTable_actividades.getColumnModel().getColumn(5).setPreferredWidth(100);
        vActividad.jTable_actividades.getColumnModel().getColumn(6).setPreferredWidth(260);
    }
    
    /**
     * Escribe en la Tabla de Actividades lo que contenga el vector 'v_actividades' tras realizar una consulta
     * @param v_actividades Es el vector de tipo Actividad que contiene los datos de los Actividades que queremos mostrar en la Tabla
     */
    public static void escribirTablaActividades(ArrayList<Object[]> v_actividades)
    {
        Object[] filas_tabla=new Object[numColumnas];
        
        for(Object[] a: v_actividades)
        {
            filas_tabla[0]=a[0];
            filas_tabla[1]=a[1];
            filas_tabla[2]=a[2];
            filas_tabla[3]=a[3];
            filas_tabla[4]=a[4];
            filas_tabla[5]=a[5];
            filas_tabla[6]=a[6];
            
            modeloTablaActividades.addRow(filas_tabla); // Añadimos la fila 'i' con los datos del Socio que hemos obtenido en el vector 'filas_tabla'
        }
    }
    
    /**
     * Elimina todas las filas de la Tabla de Actividades
     * Se utiliza antes de escribir en la Tabla de Actividad, ya que los datos deben haber sido borrados previamente antes de escribir
     */
    public static void borrarTablaActividades()
    {
        while(modeloTablaActividades.getRowCount()>0) // Mientras haya filas, las borramos
        {
            modeloTablaActividades.removeRow(0);
        }
    }
    
    /**
     * 
     * @return Devuelve el campo 'idActividad' de la ultima Actividad de la BD
     */
    public static String getCodUltima()
    {
        int numFilas=modeloTablaActividades.getRowCount();
        int fila_numeroSocio=0;
        String cod;
        
        cod=modeloTablaActividades.getValueAt(numFilas-1, fila_numeroSocio).toString();
        
        return cod;
    }
    
};
