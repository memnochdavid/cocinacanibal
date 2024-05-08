package cocina_canibal;
import java.sql.SQLException;
import java.util.Scanner;
public class Libreria {
    
    //MENU PRINCIPAL
    public static MenuUsuario menuLogin(){
        Scanner teclado=new Scanner(System.in);
        MenuUsuario[] opciones=MenuUsuario.values();
        int opcion;
        do{
            System.out.println(formatString("rojo")+"--------------------------");
            System.out.println(formatString("rojo")+"---MENU de LOGIN---");
            System.out.println(formatString("rojo")+"--------------------------");
            System.out.println(formatString("rojo")+"1. Registrar Usuario.");
            System.out.println(formatString("rojo")+"2. Login.");
            System.out.println(formatString("rojo")+"3. Cerrar Sesión.");
            System.out.println(formatString("rojo")+"4. Ver todos los usuarios.");
            System.out.println(formatString("rojo")+"5. SALIR."+formatString("reset"));
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>5);
        opcion-=1;
        return opciones[opcion];
    }  
    //Menu de Recetas
    public static MenuReceta menuRecetas(){
        Scanner teclado=new Scanner(System.in);
        MenuReceta[] opciones=MenuReceta.values();
        int opcion;
        do{
            System.out.println(formatString("rojo")+"--------------------------");
            System.out.println(formatString("rojo")+"---MENU de Recetas---");
            System.out.println(formatString("rojo")+"--------------------------");
            System.out.println(formatString("rojo")+"1. Crea Receta.");
            System.out.println(formatString("rojo")+"2. Busca Receta.");
            System.out.println(formatString("rojo")+"3. Elimina Receta.");
            System.out.println(formatString("rojo")+"4. Mostrar todas.");            
            System.out.println(formatString("rojo")+"5. SALIR."+formatString("reset"));
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>5);
        opcion-=1;
        return opciones[opcion];
    }
    
    
    
    
    
    
    
    
    //devuelve el nivel de privilegios de un usuario que recibe como argumento
    public static int compruebaPrivilegiosCredenciales(Conexion con, String usr, String pass) throws ClassNotFoundException, SQLException{
        int lvl=0;
        //usuario.oracleCompruebaUser(con, usuario.getUsr(), usuario.getPass());
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
    public static void muestraRecetas(Conexion con, String busqueda, char opc) throws SQLException{
        String creador="";
        String nombre="";
        String desc="";
        int existe=Character.getNumericValue(con.selectToString("select count(*) from recetas where nombre='"+busqueda+"'").charAt(0));
        if(opc=='u'){
            if(existe==0){
                System.out.println("No se han encontrado coincidencias.");
                System.out.println("Búsqueda: "+busqueda);
            }
            else{
                creador=con.selectToString("select owner from recetas where nombre='"+busqueda+"'").replaceAll(" - \n", "");
                nombre=con.selectToString("select nombre from recetas where nombre='"+busqueda+"'").replaceAll(" - \n", "");
                desc=con.selectToString("select descripcion from recetas where nombre='"+busqueda+"'").replaceAll(" - \n", "");
                System.out.println("\nCreador: "+creador+"\nNombre: "+nombre+"\nDescripción:\n"+desc);
            }
        }
        if(opc=='a'){
            existe=Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
            int cont=1;
            if(existe==0){
                System.out.println("No se han encontrado coincidencias.");
                System.out.println("Búsqueda: "+busqueda);
            }
            else{
                while(cont<=existe){
                    creador=con.selectToString("select owner from recetas where cod="+cont).replaceAll(" - \n", "");
                    nombre=con.selectToString("select nombre from recetas where cod="+cont).replaceAll(" - \n", "");
                    desc=con.selectToString("select descripcion from recetas where cod="+cont).replaceAll(" - \n", "");
                    System.out.println("\nCreador: "+creador+"\nNombre: "+nombre+"\nDescripción:\n"+desc);
                    cont++;
                }
                
            }
        }   
    }
    
    //borrar receta
    /*
    public static void borraReceta(Conexion con, Usuario login){
        
    }*/
    
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
