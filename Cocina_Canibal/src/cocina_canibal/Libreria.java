package cocina_canibal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;
public class Libreria {
    
    //Menu Base de Datos
    public static MenuIni menuIni(){
        Scanner teclado=new Scanner(System.in);
        MenuIni[] opciones=MenuIni.values();
        int opcion;
        
        do{
            System.out.println("--------------------------");
            System.out.println("----- MENU de "+formatString("verde")+"Base de Datos"+formatString("reset")+" ------");
            System.out.println("--------------------------");
            System.out.println("1. Base de Datos - Set up");
            System.out.println("2. Continuar.");
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>2);
        opcion-=1;
        return opciones[opcion];
    }
    
    //MENU PRINCIPAL
    public static MenuUsuario menuLogin(){
        Scanner teclado=new Scanner(System.in);
        MenuUsuario[] opciones=MenuUsuario.values();
        int opcion;
        
        do{
            System.out.println("--------------------------");
            System.out.println("----- MENU de "+formatString("verde")+"LOGIN"+formatString("reset")+" ------");
            System.out.println("--------------------------");
            System.out.println("1. Registrar Usuario.");
            System.out.println("2. Login.");
            System.out.println("3. Cerrar Sesión.");
            System.out.println("4. Usar sin registrar.");
            System.out.println("5. Ver todos los usuarios.");
            System.out.println("6. SALIR.");
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>6);
        opcion-=1;
        return opciones[opcion];
    }  
    //Menu de Recetas
    public static MenuReceta menuRecetas(){
        Scanner teclado=new Scanner(System.in);
        MenuReceta[] opciones=MenuReceta.values();
        int opcion;
        do{
            System.out.println("--------------------------");
            System.out.println("---MENU de \033[34mRecetas"+formatString("reset")+"---");
            System.out.println("--------------------------");
            System.out.println("1. Crea Receta.");
            System.out.println("2. Busca Receta.");
            System.out.println("3. Elimina Receta.");
            System.out.println("4. Mostrar todas.");            
            System.out.println("5. SALIR.");
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>5);
        opcion-=1;
        return opciones[opcion];
    }
    
    
    
    
    
    //comprueba si existe algún usuario con el nombre que recibe por el String
    public static boolean checkUsuario(String usr, Conexion con) throws SQLException{
        String select="select count(*) from usuarios where usr='"+usr+"'";
        int check=Character.getNumericValue(con.selectToString(select).charAt(0));
        if(check>0)return true;
        else return false;
    }
    
    //devuelve el nivel de privilegios de un usuario que recibe como argumento
    public static int compruebaPrivilegiosCredenciales(Conexion con, String usr, String pass) throws ClassNotFoundException, SQLException{
        int lvl=0;
        //pass=cifrar(pass, 'd');//se descifra
        
        String select="select lvl from usuarios where usr='"+usr+"' and pass='"+pass+"'";
        try{
            lvl= Character.getNumericValue(con.selectToString(select).charAt(0));  //guarda como int el primer char que devuelve con.selectToString(consulta), que es el lvl de permiso
        }catch(Exception e){
            System.out.println("El usuario no existe. Sesión básica.");
        }        
        return lvl;
    }
    
    //muestra todos los usuarios - SOLO ADMIN
    public static void muestraUsuarios(Usuario login, Conexion con) throws SQLException{
        String select="";
        if(login.getLvl()==2){//sólo permite al admin
            select="select * from usuarios";
            con.select(select);
        }
        else System.out.println("Sólo para admin.");
    }
    
    //muestra recetas - opc u(una) - opc a(all)
    public static void muestraRecetas(Conexion con, String busqueda) throws SQLException{
        String creador="";
        String nombre="";
        String desc="";
        String ingre="";
        int existe=Character.getNumericValue(con.selectToString("select count(*) from recetas where nombre='"+busqueda+"'").charAt(0));
        existe=Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
        int cont=1;
        if(existe==0){
            System.out.println("No se han encontrado coincidencias.");
            System.out.println("Búsqueda: "+busqueda);
        }
        else{
            while(cont<=existe){
                creador=con.selectToString("select owner from recetas where nombre like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                nombre=con.selectToString("select nombre from recetas where nombre like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                desc=con.selectToString("select descripcion from recetas where nombre like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                ingre=con.selectToString("select ingredientes from recetas where nombre like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                System.out.println("\nCreador: "+creador+"\nNombre: "+nombre+"\nDescripción:\n"+desc+"\nIngredientes: "+ingre);
                cont++;
            }
        }
    }
    
    //borrar receta
    /*
    public static void borraReceta(Conexion con, Usuario login){
        String creador=
    }*/
    
    
    public static String ingredientes(Conexion con) throws SQLException{//pide e inserta los ingredientes en un varchar2 en la BD
        Scanner sc = new Scanner(System.in);
        int cont = 0; //Variable que va a ir contabilizando el numero de ingredientes
        String[] ingredientes = new String[25];
        boolean salir = false;
        String res="";
        String nombre;
        char opc;
        
        do{
            System.out.println("\nVamos a añadir ingresientes..");
            System.out.println("\nIntroduce el nombre del ingrediente: ");
            nombre = sc.next();
            ingredientes[cont] = nombre;
            cont++;
            System.out.println("\nDeseas añadir otro ingrediente? S/N");
            opc = sc.next().toLowerCase().charAt(0);
            if(opc == 's'|| opc=='S') salir = true;
            else if(opc == 'n' || opc=='N') salir = false;
        }while(cont<25 && salir == true);
        
        for(int i=0; i<cont; i++){
            if(i < (cont-1)) res += ingredientes[i]+", ";
            else res += ingredientes[i];
        }
        System.out.println(res);
        
        return res;
    }
    public static String cifrarContrasena(String contrasena){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
        }
        
        return null;
    }
    
    //Generación automática de números
    public static int random(int tope){//el tope indica el número máximo generado, siendo en el rango [0-tope]
        double roll=Math.random();
        roll*=tope;
        int trun=(int)roll;
        return trun;
    }
    
    
    public static boolean validaContraseña(String contraseña, String usuario){
        boolean valida = false;
        boolean mayus = false;
        boolean numeros = false;
        boolean pro = false;
        boolean media = false;
        boolean facil = false;
        int cont = 0;
        
        for(int i=0; i<contraseña.length(); i++){
            int letra = contraseña.charAt(i);
            if(letra >= 65 && letra <= 90){
                mayus = true;
            }
        }
        
        for(int i=0; i<contraseña.length(); i++){
            if(contraseña.charAt(i) == '1' || contraseña.charAt(i) == '2' || contraseña.charAt(i) == '3' || contraseña.charAt(i) == '4' || contraseña.charAt(i) == '5' || contraseña.charAt(i) == '6' || contraseña.charAt(i) == '7' || contraseña.charAt(i) == '8' || contraseña.charAt(i) == '9' || contraseña.charAt(i) == '0'){
                numeros = true;
            }
        }
        
        if(contraseña.length()<8){
            System.out.println("\033[31mContraseña demasiado corta!\033[30m");
        }
        if(contraseña.length()>20){
            System.out.println("\033[31mContraseña demasiado larga!\033[30m");
        }
        
        if(contraseña.length()>=8 ||contraseña.length()<=20){
            facil = true;
            valida = true;
            if(mayus){
                facil = false;
                media = true;
            }
            if(numeros){
                facil = false;
                media = true;
            }
            if(mayus && numeros){
                media = false;
                pro = true;
            }
        }
        
        if(facil) System.out.println("\u001B[41m  \u001B[0m     \033[31mCONTRASEÑA DEBIL\033[30m");
        if(media) System.out.println("\u001B[43m    \u001B[0m   \u001B[33mCONTRASEÑA INTERMEDIA\033[30m");
        if(pro) System.out.println("\u001B[42m      \u001B[0m \033[32mCONTRASEÑA FUERTE\033[30m");
        
        return valida;
    }
    
    public static void creaTablas(Conexion con) throws SQLException{//crea las tablas necesarias en en la BD y los usuarios 'admin' y 'base' para que no sea necesario abrir ORACLE
        String tablaUsu = "create table usuarios (usr 	varchar2(25),pass    varchar2(100) not null,mail    varchar2(50),lvl     number(1) check (lvl between 0 and 2),constraint pk_usuarios primary key (usr))";
        String tablaRece = "create table recetas(cod	    number(3),owner 	varchar2(25),nombre  varchar2(25),descripcion    varchar2(100) not null,ingredientes varchar2(550),constraint pk_recetas primary key (cod),constraint fk_rec_usu foreign key (owner) references usuarios(usr))";
        String admin = "insert into usuarios values ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin@admin.com', 2)";
        String base = "insert into usuarios values ('base', 'base', 'base', 0)";
        try{
            con.insert(tablaUsu);
            con.insert(tablaRece);
            con.insert(admin);
            con.insert(base);
        }catch(Exception e){
            System.out.println("\nLas tablas ya existen");
        }
    }
    
    public static void destroyAll(Conexion con) throws SQLException{//destruye todo a su paso -CUIDADORRRLL
        String deleteReces="delete from recetas";
        String dropReces="drop table recetas cascade constraints";
        String deleteUsrs="delete from usuarios";
        String dropUsrs="drop table usuarios cascade constraints";
        try{
            con.insert(deleteReces);
            con.insert(dropReces);
            con.insert(deleteUsrs);
            con.insert(dropUsrs);
        }catch(Exception e){
        }finally{
            System.out.println("Todo OK.");
        }
        
    }
    //Cambia los colores de cualquier printline
    public static String formatString(String format_color){
        /*este método recibe un String con un nombre de color. Luego se buscará ese nombre en la constante String[] COLOR.
        Si encuentra coincidencia, coge su posición contigua y la pasa a otro String output, que será lo que se devuelva.
        Lo cual permitirá formatear el color de un println*/
        format_color=format_color.toUpperCase();
        final String[] COLOR={"RESET","\u001B[0m","NEGRO","\u001B[30m","ROJO","\u001B[31m","VERDE","\u001B[32m","AMARILLO","\u001B[33m","AZUL","\u001B[34m","LILA","\u001B[35m","BLANCO","cian","\u001B[96m","\u001B[37m"};
        int tope=COLOR.length;
        String output="";
        for(int i=0; i<tope; i++){
            if(format_color.contains(COLOR[i])){
                output=COLOR[i+1];
            }            
        }
        return output;
    }
    
    //evita crash si tipo de dato incorrecto
    public static int compInput(){
        Scanner teclado=new Scanner(System.in);
        int aux=-1;
        try{
            aux=teclado.nextInt();
        }catch(Exception e){
            System.out.println("Se requiere un número entero.");
        }
        return aux;
        
    }
    
}

