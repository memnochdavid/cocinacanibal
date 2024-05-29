package cocina_canibal;
import static cocina_canibal.Libreria.*; //Asi no hay que crear una clase libreria y estar llamando como lib.metodo()
import java.sql.SQLException;
import java.util.Scanner;

public class Cocina_Canibal {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner teclado=new Scanner(System.in, "ISO-8859-2");
        boolean logged=false, salirLogin=false, salir=false, salirReceta=false, salirIni=false;
        MenuIni opcionIni;
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
                opcionIni=menuIni();
                do{
                    System.out.println("opcionIni => "+opcionIni);
                    switch(opcionIni){
                        case SET_BD:
                            destroyAll(con); //ANDAR CON OJETE
                            BDsetUp(con);//crea todo lo necesario para usar la apli sin necesidad de usar ORACLE
                            salirIni=true;
                            break;
                        case SET_BD_INS:
                            destroyAll(con); //ANDAR CON OJETE
                            BDsetUpIns(con);//crea todo lo necesario para usar la apli sin necesidad de usar ORACLE e inserta tablas a test
                            salirIni=true;
                            break;
                        case SALIR:
                            salirIni=true;
                            break;
                    }
                }while(!salirIni);        
                opcionLogin=menuLogin();
                switch(opcionLogin){
                    case REG_USR:
                        System.out.println("\033[34m---REGISTRO DE USUARIO---"+formatString("reset"));
                        System.out.println("\u001B[35mUsuario:\033[30m");
                        System.out.print(">");
                        usr=teclado.next();
                        boolean validaContr; //variable creado para comprobar la fuerza de la contraseña y si es valida
                        if(!checkUsuario(usr, con)){
                            do{
                                System.out.println("\u001B[35mPassword (10 chars max):\033[30m");
                                System.out.print(">");
                                pass=teclado.next();
                                validaContr = validaContraseña(pass, usr);
                            }while(!validaContr);                           
                            pass=cifrarContrasena(pass);
                            System.out.println("\u001B[35meMail:\033[30m");
                            do{
                                System.out.print(">");
                                mail=teclado.next();
                            }while(!validaCorreo(mail));
                            usuarioCrea=new Usuario(con,usr,pass,mail,1, false);//genera el objeto para luego registrarlo en la BD, falso porque crea un usuario, no es un login
                            usuarioCrea.oracleRegistraUsuario(con);//lo registra en la base de datos
                            usuarioCrea=null;//se deja a null porque ya no es necesario el objeto
                        }
                        else System.out.println("El usuario ya existe.");
                        break;

                    case COMP_LOGIN: //inicia sesión
                        System.out.println("\033[34m======================="+formatString("reset"));
                        System.out.println("\033[34m   COMPRUEBA USUARIO"+formatString("reset"));
                        System.out.println("\u001B[35mUsuario:\033[30m");
                        System.out.print(">");
                        usr=teclado.next();
                        System.out.println("\u001B[35mPassword:\033[30m"); 
                        System.out.print(">");
                        pass=teclado.next();
                        pass=cifrarContrasena(pass);//se cifra
                        System.out.println("\033[34m======================="+formatString("reset"));
                        lvl=compruebaPrivilegiosCredenciales(con, usr, pass);//recibe un int correspondiente al lvl de acceso del usuario a comprobar. Si no existe, el lvl es 0. Si es un admin, el lvl es 2, si es un usuario ya registrado, el lvl de acceso es 1
                        if(lvl==0){//no existe
                            login=new Usuario(con, "base","","base", 0, true);
                            System.out.println("Usuario base.");
                        }
                        if(lvl==1){//ha encontrado coincidencia y crea el objeto login de la clase Usuario con lvl de acceso 1
                            login=new Usuario(con, usr,pass,"", 1, true);
                            System.out.println("Login exitoso.\n");

                        }
                        if(lvl==2){// credenciales de admin para objeto login de la clase Usuario, lvl de acceso 2
                            login=new Usuario(con, usr,pass,"", 2, true);
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
                    case USAR_SIN_LOGIN:
                        login=new Usuario(con, "base","base","base", 0, true);
                        logged=true;
                        salirLogin=true;
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
            
            /*VARIABLES DE BUSQUEDA*/
            
            String busqueda="";
            String[] etiquetas = new String[3];
            etiquetas[0]="";
            etiquetas[1]="";
            etiquetas[2]="";
            int indice=-1;
            int contEti = 0;
            char tipoBus=' ';
            
            /*---------------------------*/
            
            do{
                opcionReceta=menuRecetas();
                switch(opcionReceta){
                    case CREA_RECETA:
                        String descripcion="";//Esto es temporal, hasta tener el pdf - sólo para probar los permisos de creación
                        String nom_receta="";
                        String ingredientes="";
                        String pasos="";
                        if(logged){//requiere login
                            if(login.getLvl()>0){//sólo el login con lvl 1 o 2
                                teclado.nextLine();
                                int existe = 0;
                                System.out.println("======================");
                                do{
                                    System.out.println("Nombre de la receta: ");
                                    nom_receta=teclado.nextLine();
                                    existe = Character.getNumericValue(con.selectToString("select count(*) from recetas where recetas.nombre='"+nom_receta+"' and owner='"+login.getUsr()+"'").charAt(0));
                                }while(existe>0);
                                System.out.println("Descripción de la receta: ");
                                descripcion=teclado.nextLine();
                                ingredientes = ingredientes();//guarda todos los ingredientes en un String, separados por coma
                                pasos=pasosReceta();
                                recetaCrea=new Receta(con, login, nom_receta, descripcion, ingredientes, pasos);
                                recetaCrea.oracleRegistraReceta(con);//guarda en la BD
                                registraEtiquetas(con, login, recetaCrea);
                                recetaCrea=null;//destruye el objeto para volver a ser usado cuando haga falta
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
  
                        busca(etiquetas, indice, contEti, tipoBus, busqueda, con);

                        break;

                    case ELIMINA_RECETA:
                        if(logged){//requiere login
                            System.out.println("======================");
                            if(login.getLvl()>0){//sólo el login con lvl 1 o 2
                                int recetaElegida=-1;
                                System.out.println("Eliminar Receta.");
                                
                                /*--------- BUSCA RECETA ---------*/                              
                                busca(etiquetas, indice, contEti, tipoBus, busqueda, con);
                                
                                System.out.println("De entre los resultados, indica el índice de la receta que quieres borrar:");
                                recetaElegida=compInput();
                                borraModReceta(con, login, recetaElegida, 'b');//opción 'b' para borrar
                            }
                            else{
                                System.out.println("Sólo usuarios registrados.");
                            }
                        }
                        else {
                            System.out.println("Login necesario.");
                        }
                        break;
                    case MODIFICAR://en obras                        
                        if(logged){//requiere login
                            if(login.getLvl()!=0){
                                System.out.println("Modificar Recetas.");
                                int recetaElegida=-1;

                                /*--------- BUSCA RECETA ---------*/                              
                                busca(etiquetas, indice, contEti, tipoBus, busqueda, con);

                                System.out.println("Indica el índice de la receta que quieres editar para confirmar:");
                                System.out.print(">");
                                recetaElegida=compInput();
                                borraModReceta(con, login, recetaElegida, 'm');//opción 'b' para borrar
                            }
                            else{
                                System.out.println("No tienes permisos para modificar recetas");
                            }
                                
                        }
                        else {
                            System.out.println("Login necesario.");
                        }
                        break;
                    case PUNTUAR:
                        if(logged){//requiere login
                            if(login.getLvl()!=0){
                                System.out.println("Puntuar Recetas.");
                                int recetaElegida=-1;

                                /*--------- BUSCA RECETA ---------*/                              
                                busca(etiquetas, indice, contEti, tipoBus, busqueda, con);

                                System.out.println("Indica el índice de la receta que quieres puntuar para confirmar:");
                                System.out.print(">");
                                recetaElegida=compInput();
                                asignaEstrellas(con, login, recetaElegida);//en obras
                            }
                            else System.out.println("No tienes permisos para puntuar recetas");
                            
                        }
                        else {
                            System.out.println("Login necesario.");
                        }
                        break;
                    case SALIR:
                        salirReceta=true;
                        salir = true; //ESTA AQUI POR AHORA PARA PODER CERRAR EL PROGRAMA
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
