package cocina_canibal;
import static cocina_canibal.Libreria.*;
import java.sql.SQLException;
import java.util.Scanner;

public class Cocina_Canibal {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner teclado=new Scanner(System.in, "ISO-8859-2");
        boolean logged=false, salirLogin=false, salir=false, salirReceta=false;
        MenuUsuario opcionLogin;
        MenuReceta opcionReceta;
        
        String usr, pass, mail;//se reutilizan cada vez que se piden datos
        int lvl=-1;
        Usuario usuarioCrea=null;
        Usuario login=null;
        Receta recetaCrea;
        Conexion con=new Conexion();
        do{
            do{
                opcionLogin=menuLogin();
                switch(opcionLogin){
                    case REG_USR:
                        System.out.println(formatString("rojo")+"---REGISTRO DE USUARIO---"+formatString("reset"));
                        System.out.println("Usuario:");
                        System.out.print(">");
                        usr=teclado.next();
                        System.out.println("Pass (10 chars max):");
                        System.out.print(">");
                        pass=teclado.next();
                        System.out.println("eMail:");
                        System.out.print(">");
                        mail=teclado.next();
                        usuarioCrea=new Usuario(con,usr,pass,mail,1, false);//genera el objeto para luego registrarlo en la BD, falso porque crea un usuario, no es un login
                        usuarioCrea.oracleRegistraUsuario(con);//lo registra en la base de datos
                        usuarioCrea=null;//se deja a null porque ya no es necesario el objeto
                        break;

                    case COMP_LOGIN:
                        System.out.println(formatString("rojo")+"---COMPRUEBA USUARIO---"+formatString("reset"));
                        System.out.println("Usuario:");
                        System.out.print(">");
                        usr=teclado.next();
                        System.out.println("Pass (10 chars max):");
                        System.out.print(">");
                        pass=teclado.next();
                        System.out.println("eMail:");
                        System.out.print(">");
                        mail=teclado.next();
                        lvl=Libreria.compruebaPrivilegiosCredenciales(con, usr, pass);//recibe un int correspondiente al lvl de acceso del usuario a comprobar. Si no existe, el lvl es 0. Si es un admin, el lvl es 2, si es un usuario ya registrado, el lvl de acceso es 1
                        if(lvl==0){//no existe
                            login=new Usuario(con, "base","base","no tiene", 0, true);
                            System.out.println("Usuario base.");
                        }
                        if(lvl==1){//ha encontrado coincidencia y crea el objeto login de la clase Usuario con lvl de acceso 1
                            login=new Usuario(con, usr,pass,mail, 1, true);
                            System.out.println("Login exitoso.\n");

                        }
                        if(lvl==2){// credenciales de admin para objeto login de la clase Usuario, lvl de acceso 2
                            login=new Usuario(con, usr,pass,mail, 1, true);
                            System.out.println("Usuario admin.");
                        }
                        logged=true;
                        System.out.println("Login como:\n"+login.toString());//muestra los datos del login
                        salirLogin=true;
                        break;
                    case CERRAR_SESION:
                        if(logged){//requiere login
                            logged=false;
                            login=null;
                        }
                        else System.out.println("Login necesario.");
                        break;

                    case VER_USUARIOS:
                        if(logged){//requiere login
                            if(login.getLvl()==2){//sólo el login con lvl 2 (admin)
                                muestraUsuarios(login, con);//muestra los usuarios existentes
                            }
                            else System.out.println("Solo el administrador puede ver esta función.");
                        }
                        else System.out.println("Login necesario.");
                        break;

                    case SALIR:
                        salirLogin=true;
                        break;
                    default:
                        break;
                }
            }while(!salirLogin);
            do{
                opcionReceta=menuRecetas();
                switch(opcionReceta){
                    case CREA_RECETA:
                        String descripcion="";//Esto es temporal, hasta tener el pdf - sólo para probar los permisos de creación
                        String nom_receta="";
                        if(logged){//requiere login
                            if(login.getLvl()>0){//sólo el login con lvl 1 o 2
                                teclado.nextLine();
                                System.out.println("Nombre de la receta: ");
                                nom_receta=teclado.nextLine();
                                System.out.println("Descripción de la receta: ");
                                descripcion=teclado.nextLine();
                                //System.out.println(login.toString());
                                recetaCrea=new Receta(con, login, nom_receta, descripcion);
                                recetaCrea.oracleRegistraReceta(con);
                                recetaCrea=null;
                            }
                            else{
                                System.out.println("Sólo usuarios registrados.");

                            }
                        }
                        else {
                            System.out.println("Login necesario.");
                        }
                        break;

                    case BUSCA_RECETA:
                        teclado.nextLine();
                        System.out.println("Búsqueda: ");
                        String busqueda=teclado.nextLine();
                        muestraRecetas(con, busqueda, 'u');
                        break;

                    case ELIMINA_RECETA:

                        break;
                    case MOSTRAR_TODAS:
                        if(logged){//requiere login
                            if(login.getLvl()==2){//sólo el admin
                                muestraRecetas(con, "", 'a');
                            }
                            else{
                                System.out.println("Sólo usuarios registrados.");
                            }
                        }
                        else {
                            System.out.println("Login necesario.");
                        }
                        break;
                    case SALIR:
                        salirReceta=true;
                        break;
                    default:
                        break;

                }
            }while(!salirReceta);
        //salir=true;    
        }while(!salir);
        con.cierre();
        
    }
    
}
