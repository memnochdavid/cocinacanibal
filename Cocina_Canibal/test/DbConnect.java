/*PRACTICANDO BASE DE DATOS*/

///import java.io.*;
//import Cocina_Canibal.*;
import java.sql.*;
import java.util.Scanner;
public class DbConnect {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner teclado=new Scanner(System.in, "ISO-8859-2");
        boolean exito=false;
        Conexion casa=null;
        char opc=' ';
        System.out.println("Insert o Select?");
        opc=teclado.next().charAt(0);
        teclado.nextLine();
        System.out.println("Escribe Consulta:");
        String consulta=teclado.nextLine();
        //consulta= consulta.substring(0, consulta.length() - 1);
        try{
            casa=new Conexion(consulta, opc);
            exito=true;
        }catch(Exception e){
            System.out.println(consulta);
            //System.out.println("No se ha insertado.");
        }finally{
            //if(exito) System.out.println("Se ha insertado con �xito.");
        }
        
    }
    
    
    
}
//select * from usuarios
//delete from usuarios where usr<>'admin' and usr<>'base' and usr<>'dummy'