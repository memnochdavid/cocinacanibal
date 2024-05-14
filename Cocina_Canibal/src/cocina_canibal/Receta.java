package cocina_canibal;
import java.sql.SQLException;
import java.util.Scanner;
public class Receta {
    private int cod=-1;
    private String owner;
    private String nombre;
    private String desc="";
    private String ingredientes;
    private String pasos;
    
    public Receta(Conexion con, Usuario login, String nombre, String descripcion, String ingr, String steps) throws SQLException{
        owner=login.getUsr();
        this.nombre=nombre;
        desc=descripcion;
        ingredientes = ingr;
        pasos=steps;
    }
    
    protected void oracleRegistraReceta(Conexion c) throws ClassNotFoundException, SQLException{//la inserta en la BD
        boolean exito=false;
        String insert="insert into recetas (owner, nombre, descripcion, ingredientes, pasos) values('"+owner+"', '"+nombre+"', '"+desc+"', '"+ingredientes+"','"+pasos+"')";
        
        try{
            c.insert(insert);
            exito=true;
        }catch(Exception e){
            System.out.println(insert);
            System.out.println("No se ha podido insertar.");
            e.printStackTrace(System.out);
        }finally{
            if(exito) System.out.println("Inserts con éxito.");
        }
        
    }

    public String getNombre() {
        return nombre;
    }
    
}
