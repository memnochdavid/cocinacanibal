
//import java.io.*;
import java.sql.*;
public class Conexion {
    private String url="jdbc:oracle:thin:@//localhost:1521/xe";
    private String username="aula";
    private String pass="aula";
    
    public Conexion(String str, char opc) throws ClassNotFoundException, SQLException {    
        Class.forName("oracle.jdbc.driver.OracleDriver");        
        Connection con= DriverManager.getConnection(url, username,pass);        
        System.out.println("Conexión con ORACLE extablecida.\n");
        Statement st=con.createStatement();
        if(opc=='s'){//select
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
        if(opc=='i'){//insert
            System.out.println("Insertando...");
            PreparedStatement insert = con.prepareStatement(str);
            insert.executeUpdate(str);
        }
        con.close();
    }
    
}
