package cocina_canibal;
import java.sql.SQLException;
public  class Usuario {
    //private int cod;
    private String usr="";
    private String pass="";
    private String mail="";
    private int lvl=0;
    
    public Usuario(Conexion con, String u, String p, String m, int lvl, boolean login) throws SQLException{//el booleano indica si se va a crear un usuario nuevo, o se va a llamar uno creado desde la base de datos
        if(!login){//REVISAR
            usr=u;
            pass=p;
            mail=m;
            this.lvl=lvl;
        }
        if(login){
            usr=con.selectToString("select usr from usuarios where usr='"+u+"'").replaceAll(" - \n", "");
            mail=con.selectToString("select mail from usuarios where usr='"+u+"'").replaceAll(" - \n", "");
            this.lvl=Character.getNumericValue(con.selectToString("select lvl from usuarios where usr='"+u+"'").charAt(0));
        }
    }    
    
    protected void oracleRegistraUsuario(Conexion c) throws ClassNotFoundException, SQLException{
        boolean exito=false;
        String insert="insert into usuarios (usr, pass, mail, lvl) values('"+usr+"', '"+pass+"', '"+mail+"', "+1+")";
        
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
    
    protected void oracleCompruebaUser(Conexion c, String u, String p) throws ClassNotFoundException, SQLException{
        boolean exito=false;
        String select="select lvl from usuarios where usr='"+u+"' and pass='"+p+"'";
        try{
            c.select(select);
            exito=true;
        }catch(Exception e){
            System.out.println(select);
            System.out.println("No se ha podido acceder.");
            e.printStackTrace(System.out);
        }finally{
            if(exito) System.out.println("Acceso con éxito.");
        }
    }
    /*
    private int updateUserCod(Conexion con) throws SQLException{//devuelve el max(cod) de la tabla usuarios que no sean 'admin' ni 'base'
        String select="select max(cod) from usuarios where usr<>'admin' and usr<>'base'";
        String countNumUsuarios="select count(*) from usuarios";
        int totalUsuarios=Character.getNumericValue(con.selectToString(countNumUsuarios).charAt(0));
        if(totalUsuarios==2) return 1;
        else return Character.getNumericValue(con.selectToString(select).charAt(0)+1);//creo que sólo funciona con menos de 10 usuarios! OJO
    }
    
    */
    @Override
    public String toString(){
        String out="";
        out="Usuario: "+usr+"\neMail: "+mail+"\nNivel de acceso: "+lvl;
        return out;
    }

    public int getLvl() {
        return lvl;
    }
    public String getUsr() {
        return usr;
    }

    public String getPass() {
        return pass;
    }
    
    
}
