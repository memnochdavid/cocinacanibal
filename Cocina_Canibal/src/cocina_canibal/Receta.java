package cocina_canibal;
import java.sql.SQLException;
import java.util.Scanner;
public class Receta {
    private int cod=-1;
    private String owner;
    private String nombre;
    private String desc="";
    private String ingredientes;
    
    public Receta(Conexion con, Usuario login, String nombre, String descripcion, String ingr) throws SQLException{
        cod=updateRecetaID(con)+1;
        owner=login.getUsr();
        this.nombre=nombre;
        desc=descripcion;
        ingredientes = ingr;
    }
    
    
    
    private int updateRecetaID(Conexion con) throws SQLException{//devuelve el max(cod) de la tabla usuarios que no sean 'admin' ni 'base'
        String select="select count(*) from recetas";
        return Character.getNumericValue(con.selectToString(select).charAt(0));
    }
    
    
    
    protected void oracleRegistraReceta(Conexion c) throws ClassNotFoundException, SQLException{
        boolean exito=false;
        String insert="insert into recetas (cod, owner, nombre, descripcion, ingredientes) values("+cod+", '"+owner+"', '"+nombre+"', '"+desc+"', '"+ingredientes+"')";
        
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
}
