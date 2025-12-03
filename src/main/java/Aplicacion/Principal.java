/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aplicacion;

import Controlador.ControladorConexion;
import Vista.VistaMensaje;
import java.io.UnsupportedEncodingException;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class Principal
{   
    /**
     * Es el 'main()' del Proyecto
     * Se realiza la llamada a 'ControladorConexion' para mostrar la pantalla de Inicio de Sesion
     * @param args
     * @throws UnsupportedEncodingException 
     */
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        VistaMensaje vMensaje=new VistaMensaje();
        try
        {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        }
        catch(Exception e)
        {
            vMensaje.mensaje(null, "Principal: Error al cargar Look & Feel", "ERROR Look & Feel", "ERROR");
            vMensaje.mostrar("\n\nPrincipal: Error al cargar Look & Feel\n");
        }
        ControladorConexion cConexion=new ControladorConexion();
    }
};
