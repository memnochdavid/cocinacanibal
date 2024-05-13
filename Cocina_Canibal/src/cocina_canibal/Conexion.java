package cocina_canibal;
//import java.io.*;
import java.sql.*;
public class Conexion {
    private String url="jdbc:oracle:thin:@//localhost:1521/xe";
    private String username="casa";
    private String pass="casa";
    private Connection con; 
    
    public Conexion() throws ClassNotFoundException, SQLException {    //constructor
        Class.forName("oracle.jdbc.driver.OracleDriver");        
        con= DriverManager.getConnection(url, username,pass);        
        System.out.println("Conexión con ORACLE extablecida.\n");
    }
    
    public void select(String str) throws SQLException{//Ejecuta una consulta select a partir de un String str
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery(str);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int n_columnas=rsMetaData.getColumnCount();
        while(rs.next()){
            for(int i=1; i<=n_columnas; i++){
                System.out.print(rs.getString(i)+" - ");
            }
            System.out.println("");
        }
    }
    public String selectToString(String str) throws SQLException{ //igual que el método de arriba, pero devuelve la consulta en un String
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery(str);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int n_columnas=rsMetaData.getColumnCount();
        String out="";
        while(rs.next()){
            for(int i=1; i<=n_columnas; i++){
                out+=rs.getString(i)+" - ";
            }
            out+="\n";
        }
        return out;
    }
    
    public void insert(String str) throws SQLException{ //ejecuta un insert o cualquier tipo de acceso de modificación a partir de un String srt
        Statement st=con.createStatement();
        System.out.println("Insertando...");
        st.executeUpdate(str);
    }
    
    public void cierre() throws SQLException{//cierra la conexión
        con.close();
    }
}
