package cocina_canibal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Libreria {
    
    //Menu Base de Datos
    public static MenuIni menuIni(){
        Scanner teclado=new Scanner(System.in);
        MenuIni[] opciones=MenuIni.values();
        int opcion;
        
        do{
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("   MENU de "+formatString("verde")+"Base de Datos"+formatString("reset")+"  ");
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("1. Base de Datos - Set up");
            System.out.println("2. Base de Datos - Set up + inserts");
            System.out.println("3. Continuar.");
            System.out.print("> ");
            opcion=compInput();
        }while(opcion<1 || opcion>3);
        opcion-=1;
        return opciones[opcion];
    }
    
    //MENU PRINCIPAL
    public static MenuUsuario menuLogin(){
        Scanner teclado=new Scanner(System.in);
        MenuUsuario[] opciones=MenuUsuario.values();
        int opcion;
        
        do{
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("      MENU de "+formatString("verde")+"LOGIN"+formatString("reset")+"      ");
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("1. Registrar Usuario.");
            System.out.println("2. Login.");
            System.out.println("3. Cerrar Sesión.");
            System.out.println("4. Usar sin registrar.");
            System.out.println("5. Ver todos los usuarios.");
            System.out.println("6. SALIR.");
            System.out.print("> ");
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
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("   MENU de \033[34mRecetas"+formatString("reset"));
            System.out.println(formatString("verde")+"=========================="+formatString("reset"));
            System.out.println("1. Crea Receta.");
            System.out.println("2. Busca Receta.");
            System.out.println("3. Elimina Receta.");
            System.out.println("4. Modifica Receta.");            
            System.out.println("5. Puntuar Recetas..");
            System.out.println("6. SALIR.");
            System.out.print(">");
            opcion=compInput();
        }while(opcion<1 || opcion>6);
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
    
    //muestra las recetas - recibe un String búsqueda y un char opc que especifica el modo de búsqueda, si por tags o por nombre 
    public static void muestraRecetasReceta(Conexion con, String busqueda) throws SQLException{
        String creador="", nombre="", desc="", ingre="", pasos="", cod="", tags="";
        int estrellas =0;
        int existe=0, seleccion=-1;
        int[] resultados=new int[20], dummy={0,0,0};;
        int indexResultados=0;
        existe=Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
        int cont=1;
        if(existe==0){
            System.out.println("No se han encontrado coincidencias.");
            System.out.println("Búsqueda: "+busqueda);
        }
        else{
            while(cont<=existe){
                //Character.getNumericValue(con.selectToString("select estrellas from recetas where cod = "+cont).charAt(0));
                cod=con.selectToString("select cod from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                estrellas = Character.getNumericValue(con.selectToString("select estrellas from recetas where cod = "+cont).charAt(0));
                if(!cod.equals("")){
                    nombre=con.selectToString("select nombre from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                    muestraNombreCodEstrellas(estrellas, nombre, cod);
                    resultados[indexResultados]=Integer.parseInt(cod);
                    indexResultados++;
                }
                cont++;
            }
            System.out.println("\nSelecciona la receta que deseas ver:");
            seleccion=compInput(resultados);
            desglosaReceta(con, seleccion,  busqueda,  0,'n', nombre, dummy);

        }
        
    }
    public static void muestraNombreCodEstrellas(int estrellas, String nombre, String cod){
        System.out.println("\n=======================================");
        System.out.print("Código: "+cod+"\nNombre: "+nombre+"\nPuntuacion: ");
        if(estrellas == 0) System.out.println("No tiene valoraciones");
        if(estrellas == 1) System.out.println("\u001B[33m★\u001B[30m");
        if(estrellas == 2) System.out.println("\u001B[33m★★\u001B[30m");
        if(estrellas == 3) System.out.println("\u001B[33m★★★\u001B[30m");
        if(estrellas == 4) System.out.println("\u001B[33m★★★★\u001B[30m");
        if(estrellas == 5) System.out.println("\u001B[33m★★★★★\u001B[30m");
    }
    public static void muestraRecetasNombre(Conexion con, String busqueda, char opc) throws SQLException{
        String creador="", nombre="", desc="", ingre="", pasos="", cod="", tags="";
        int estrellas =0;
        int existe=0, seleccion=-1;
        int[] dummy={0,0,0};
        existe=Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
        int cont=1;
        if(existe==0){
            System.out.println("No se han encontrado coincidencias.");
            System.out.println("Búsqueda: "+busqueda);
        }
        else{
            while(cont<=existe){
                //Character.getNumericValue(con.selectToString("select estrellas from recetas where cod = "+cont).charAt(0));
                cod=con.selectToString("select cod from recetas where owner like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                estrellas = Character.getNumericValue(con.selectToString("select estrellas from recetas where cod = "+cont).charAt(0));
                if(!cod.equals("")){
                    nombre=con.selectToString("select nombre from recetas where owner like'%"+busqueda+"%' and cod="+cont).replaceAll(" - \n", "");
                    muestraNombreCodEstrellas(estrellas, nombre, cod);
                }
                cont++;
            }
            System.out.println("Selecciona la receta que deseas ver:");
            seleccion=compInput();
            
            cod=con.selectToString("select cod from recetas where owner like'%"+busqueda+"%' and cod="+seleccion).replaceAll(" - \n", "");
            nombre=con.selectToString("select nombre from recetas where owner like'%"+busqueda+"%' and cod="+seleccion).replaceAll(" - \n", "");
            int codRec = Integer.parseInt(cod);
            
            desglosaReceta(con, seleccion,  busqueda,  codRec,'u', nombre, dummy);
        }
    }
    public static void busquedaEti(Conexion con, String[] nom_etiquetas, String busqueda, int conEti) throws SQLException{//busca por etiqueta
            String creador="", nombre="", desc="", ingre="", pasos="", cod="", tags="";
            int existe=0, seleccion=-1;
            Etiquetas[] etiqueta=Etiquetas.values();
            existe=Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
            int cont=1;
            boolean etiquetaFound=false;
            int[] cod_etis = new int[nom_etiquetas.length];
            int cont_cod = 0;
            for(int i=1; i<etiqueta.length; i++){// i empieza en 1 para evitar la posición cero, que contiene una entrada dummy. Así se evita que la primera opción aparezca en rojo
                //noMostrar = false; //Reinicio variable
                etiquetaFound = false;
                for(int j=0; j<conEti; j++){
                    //System.out.println(etiqueta[i].toString()+" = "+etiquetas[j]);
                    if(etiqueta[i].toString().contains(nom_etiquetas[j])){
                        cod_etis[cont_cod] = i;
                        System.out.println(etiqueta[i].toString()+" = "+nom_etiquetas[j]);
                        System.out.println("cod_etis["+cont_cod+"] => "+cod_etis[cont_cod]);
                        cont_cod++;
                        break;
                    }
                }
                
            }
            System.out.println("cont_cod => "+cont_cod);
            if(cont_cod > 0){
                if(cont_cod==1){
                    System.out.println("==============================");
                    while(cont<=existe){ 
                        nombre=con.selectToString("select nombre from recetas where cod in (select cod from rec_et where id="+cod_etis[0]+")and cod="+cont).replaceAll(" - \n", "");                    
                        cod=con.selectToString("select cod from recetas where cod ="+cont).replaceAll(" - \n", "");
                        System.out.println("cod => "+cod);
                        if(!nombre.equals("")){
                            System.out.println("Código: "+cont+"\nNombre: "+nombre);
                        }
                            cont++;
                            System.out.println("==============================");
                        }
                }
                if(cont_cod==2){
                    System.out.println("==============================");
                    while(cont<=existe){
                        nombre=con.selectToString("select nombre from recetas where cod in (select cod from rec_et where id="+cod_etis[0]+" and cod in (select distinct cod from rec_et where id="+cod_etis[1]+"))"+cont).replaceAll(" - \n", "");                    
                        cod=con.selectToString("select cod from recetas where cod ="+cont).replaceAll(" - \n", "");
                        if(!nombre.equals("")){
                            System.out.println("Código: "+cont+"\nNombre: "+nombre);
                        }
                            cont++;
                            System.out.println("==============================");
                        }
                }
                if(cont_cod==3){
                    System.out.println("==============================");
                    while(cont<=existe){
                        nombre=con.selectToString("select nombre from recetas where cod in (select cod from rec_et where id="+cod_etis[0]+" and cod in(select distinct cod from rec_et where id="+cod_etis[1]+" and cod in (select distinct cod from rec_et where id="+cod_etis[2]+"))) and cod="+cont).replaceAll(" - \n", "");                    
                        cod=con.selectToString("select cod from recetas where cod ="+cont).replaceAll(" - \n", "");
                        if(!nombre.equals("")){
                            System.out.println("Código: "+cont+"\nNombre: "+nombre);
                        }
                            cont++;
                            System.out.println("==============================");
                    }
                }
                //System.out.println("cod_etis.length: "+cod_etis.length);
                //System.out.println("hola");
                System.out.println("Selecciona la receta que deseas ver:");
                seleccion=compInput();
                int codRec = Integer.parseInt(cod);
                desglosaReceta(con, seleccion,  busqueda,  codRec,'e', nombre, cod_etis);
            }
        }
    
    
    
    
    
    
    //desglosa la búsqueda de una receta seleccionada - //este método se va a llamar cuando ya se ha comprobado que la receta en cuestión existe
    public static void desglosaReceta(Conexion con, int indice, String busqueda, int EtiquetaFound,char opc, String nombre, int[] cod_etis) throws SQLException{
        String creador="", desc="", ingre="", pasos="", tags="", valoracion="";
        int existe=-1, estrellas=0;
        if(opc=='u'){
            tags=con.selectToString("select nom from etiqueta where id in (select id from rec_et where cod="+indice+")").replaceAll(" - \n", ", ");
            creador=con.selectToString("select owner from recetas where lower(owner) like'%"+busqueda+"%'and cod="+indice).replaceAll(" - \n", "");
            desc=con.selectToString("select descripcion from recetas where lower(owner) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "");
            ingre=con.selectToString("select ingredientes from recetas where lower(owner) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "");
            pasos=con.selectToString("select pasos from recetas where lower(owner) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "").replaceAll(" - ", "\n");
            valoracion=con.selectToString("select estrellas from recetas where lower(owner) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "").replaceAll(" - ", "\n");
            nombre = nombre.toUpperCase();
            estrellas = Integer.parseInt(valoracion);
            if(estrellas == 0) System.out.println("No tiene valoraciones");
            if(estrellas == 1) System.out.println("\n======= "+nombre+" \u001B[33m★\u001B[30m =======");
            if(estrellas == 2) System.out.println("\n======= "+nombre+" \u001B[33m★★\u001B[30m =======");
            if(estrellas == 3) System.out.println("\n======= "+nombre+" \u001B[33m★★★\u001B[30m =======");
            if(estrellas == 4) System.out.println("\n======= "+nombre+" \u001B[33m★★★★\u001B[30m =======");
            if(estrellas == 5) System.out.println("\n======= "+nombre+" \u001B[33m★★★★★\u001B[30m =======");
            System.out.println("Tags: "+tags);
            System.out.println("Creador: "+creador);
            System.out.println("Descripcion: "+desc);
            System.out.println("Ingredientes: "+ingre);
            System.out.println("===================");
            System.out.println("Pasos: \n"+pasos);
            System.out.println("===================");
            /* PARTE DE LAS VALORACIONES */
            
        }
        if(opc=='n'){
            tags=con.selectToString("select nom from etiqueta where id in (select id from rec_et where cod="+indice+")").replaceAll(" - \n", ", ");
            creador=con.selectToString("select owner from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "");
            desc=con.selectToString("select descripcion from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "");
            ingre=con.selectToString("select ingredientes from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "");
            pasos=con.selectToString("select pasos from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "").replaceAll(" - ", "\n");
            valoracion=con.selectToString("select estrellas from recetas where lower(nombre) like'%"+busqueda+"%' and cod="+indice).replaceAll(" - \n", "").replaceAll(" - ", "\n");
            nombre = nombre.toUpperCase();
            estrellas = Integer.parseInt(valoracion);
            if(estrellas == 0) System.out.println("No tiene valoraciones");
            if(estrellas == 1) System.out.println("\n======= "+nombre+" \u001B[33m★\u001B[30m =======");
            if(estrellas == 2) System.out.println("\n======= "+nombre+" \u001B[33m★★\u001B[30m =======");
            if(estrellas == 3) System.out.println("\n======= "+nombre+" \u001B[33m★★★\u001B[30m =======");
            if(estrellas == 4) System.out.println("\n======= "+nombre+" \u001B[33m★★★★\u001B[30m =======");
            if(estrellas == 5) System.out.println("\n======= "+nombre+" \u001B[33m★★★★★\u001B[30m =======");
            System.out.println("\n======= "+nombre+" =======");   
            System.out.println("Tags: "+tags);
            System.out.println("Creador: "+creador);
            System.out.println("Descripcion: "+desc);
            System.out.println("Ingredientes: "+ingre);
            System.out.println("===================");
            System.out.println("Pasos: \n"+pasos);
            System.out.println("==================="); 
        }
        if(opc=='e'){
            System.out.println("EtiquetaFound => "+EtiquetaFound);
            System.out.println("indice => "+indice);
            tags=con.selectToString("select nom from etiqueta where id in (select id from rec_et where cod="+indice+")").replaceAll(" - \n", ", ");
            creador=con.selectToString("select owner from recetas where cod in (select cod from rec_et where id in (select id from etiqueta where id ="+indice+"))").replaceAll(" - \n", "");
            desc=con.selectToString("select descripcion from recetas where cod in (select cod from rec_et where cod="+indice+")").replaceAll(" - \n", "");
            ingre=con.selectToString("select ingredientes from recetas where cod in (select cod from rec_et where cod="+indice+")").replaceAll(" - \n", "");
            pasos=con.selectToString("select pasos from recetas where cod in (select cod from rec_et where cod="+indice+")").replaceAll(" - \n", "").replaceAll(" - ", "\n");
            valoracion=con.selectToString("select estrellas from recetas where cod in (select cod from rec_et where id in (select id from etiqueta where id ="+indice+"))").replaceAll(" - \n", "").replaceAll(" - ", "\n");
            nombre = nombre.toUpperCase();
            estrellas = Integer.parseInt(valoracion);
            if(estrellas == 0) System.out.println("No tiene valoraciones");
            if(estrellas == 1) System.out.println("\n======= "+nombre+" \u001B[33m★\u001B[30m =======");
            if(estrellas == 2) System.out.println("\n======= "+nombre+" \u001B[33m★★\u001B[30m =======");
            if(estrellas == 3) System.out.println("\n======= "+nombre+" \u001B[33m★★★\u001B[30m =======");
            if(estrellas == 4) System.out.println("\n======= "+nombre+" \u001B[33m★★★★\u001B[30m =======");
            if(estrellas == 5) System.out.println("\n======= "+nombre+" \u001B[33m★★★★★\u001B[30m =======");
            System.out.println("\n======= "+nombre+" =======");   
            System.out.println("Tags: "+tags);
            System.out.println("Creador: "+creador);
            System.out.println("Descripcion: "+desc);
            System.out.println("Ingredientes: "+ingre);
            System.out.println("===================");
            System.out.println("Pasos: \n"+pasos);
            System.out.println("===================");   
        }
    }
    
    //public static void 
    
    //borrar y/o modificar receta    
    public static void borraModReceta(Conexion con, Usuario login, int chosen, char opc) throws SQLException{
        String creador=con.selectToString("select owner from recetas where cod="+chosen).replaceAll(" - \n", "");
        boolean modif=false;
        boolean borrado=false;
        boolean compOwnership=false;
        if(creador.equals(login.getUsr())) compOwnership=true;
        if(compOwnership || login.getLvl()==2){
            if(opc=='b'){//opc 'b' aniquila la receta
                String deleteDependencias="delete from rec_et where cod="+chosen;
                String deleteRece="delete from recetas where cod="+chosen+" and owner='"+login.getUsr()+"'";
                con.insert(deleteDependencias);
                con.insert(deleteRece);
                borrado=true;
            }
            if(opc=='m'){//opc 'm' para modificar - pregunta qué elemento o elementos modificar, el resto los consulta y los mantiene. Sólo alterará los que le pidamos
                Scanner teclado=new Scanner(System.in, "ISO-8859-2");
                String nombre=con.selectToString("select nombre from recetas where cod="+chosen).replaceAll(" - \n", "");
                String desc=con.selectToString("select descripcion from recetas where cod="+chosen).replaceAll(" - \n", "");
                String ingre=con.selectToString("select ingredientes from recetas where cod="+chosen).replaceAll(" - \n", "");
                String pasos=con.selectToString("select pasos from recetas where cod="+chosen).replaceAll(" - \n", "");
                String tags=con.selectToString("select nom from etiqueta where id in (select id from rec_et where cod="+chosen+")").replaceAll(" - \n", ", ");
                String updateRece="";
                int opcion=-1;
                char end='n';
                        
                //int[] tagsElegidas=new int[3];
                boolean terminado=false;
                do{
                    do{
                        System.out.println("Elije qué vas a modificar:");
                        System.out.println("1. Nombre.");
                        System.out.println("2. Descripción.");
                        System.out.println("3. Ingredientes.");
                        System.out.println("4. Pasos.");
                        System.out.println("5. Etiquetas.");
                        System.out.println("6. Todo.");
                        System.out.println("7. He cambiado de idea. Salir");
                        System.out.print(">");
                    opcion=compInput();
                    }while(opcion<1 || opcion>7);
                    //do{
                        switch(opcion){
                            case 1:
                                teclado.nextLine();
                                System.out.println("Nuevo nombre:");
                                System.out.print(">");
                                nombre=teclado.nextLine();
                                updateRece="update recetas set nombre='"+nombre+"' where cod="+chosen;
                                System.out.println("Modificado con éxito.");
                                modif=true;
                                break;
                            case 2:
                                teclado.nextLine();
                                System.out.println("Nueva Descripción:");
                                System.out.print(">");
                                desc=teclado.nextLine();
                                updateRece="update recetas set descripcion='"+desc+"' where cod="+chosen;
                                System.out.println("Modificado con éxito.");
                                modif=true;
                                break;
                            case 3:
                                teclado.nextLine();
                                ingre=ingredientes();
                                updateRece="update recetas set ingredientes='"+ingre+"' where cod="+chosen;
                                System.out.println("Modificado con éxito.");
                                modif=true;
                                break;
                            case 4:
                                teclado.nextLine();
                                pasos=pasosReceta();
                                updateRece="update recetas set pasos='"+pasos+"' where cod="+chosen;
                                System.out.println("Modificado con éxito.");
                                modif=true;
                                break;
                            case 5:
                                //teclado.nextLine();
                                modifEtiquetas(con, chosen);
                                modif=false;
                                break;
                            case 6://se modifican todo de la receta menos el código y el el creador
                                teclado.nextLine();
                                System.out.println("Nuevo nombre:");
                                System.out.print(">");
                                nombre=teclado.nextLine();
                                System.out.println("Nueva Descripción:");
                                System.out.print(">");
                                desc=teclado.nextLine();
                                System.out.println("Nuevos Ingrecientes:");
                                ingre=ingredientes();
                                System.out.println("Nuevos Pasos:");
                                pasos=pasosReceta();
                                modifEtiquetas(con, chosen);
                                updateRece="update recetas set nombre='"+nombre+"', descripcion='"+desc+"', ingredientes='"+ingre+"', pasos='"+pasos+"' where cod="+chosen;
                                System.out.println("Modificado con éxito.");
                                modif=true;
                                break;
                            case 7:
                                terminado=true;
                                modif=false;
                                break;
                            default:
                                modif=false;
                                break;

                        }
                        System.out.println("\n¿Has terminado? S/N"); 
                        System.out.println(">");
                        end = teclado.next().toLowerCase().charAt(0);
                        if(end=='S') end='s';
                        if(end=='N') end='n';
                        if(end=='s') terminado=true;
                    }while(terminado == false);
                //}while(!terminado);
                if(modif) con.insert(updateRece);
                modif=true;
            }
        }
        else System.out.println("No tienes permisos para borrar/editar esto.");
        if(borrado) System.out.println("Borrado con éxito.");
        if(modif) System.out.println("Edición con éxito.");
    }
    
    //pide ingredientes hasta que el usuario quiera parar, entonces los guarda todos en un String que devuelve
    public static String ingredientes(){
        Scanner sc = new Scanner(System.in, "ISO-8859-2");
        int cont = 0; //Variable que va a ir contabilizando el numero de ingredientes
        String[] ingredientes = new String[25];
        boolean salir = false;
        String res="";
        String nombre;
        char opc;
        //sc.nextLine();
        System.out.println("================================");
        System.out.println("\nVamos a añadir ingresientes..");
        do{
            System.out.println("Introduce el nombre del ingrediente: ");
            System.out.print(">");
            nombre = sc.next();
            ingredientes[cont] = nombre;
            cont++;
            System.out.println("\nDeseas añadir otro ingrediente? S/N");
            System.out.print(">");
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
    
    //pregunta por los pasos de una receta hasta que el usuario quiera parar, entonces los guarda todos en un String que devuelve
    public static String pasosReceta() throws SQLException{
        Scanner sc = new Scanner(System.in, "ISO-8859-2");
        int cont = 0; //Variable que va a ir contabilizando el numero de ingredientes
        int n_paso=1;
        String[] pasos = new String[25];
        boolean salir = false;
        String res="";
        String paso;
        char opc;
        System.out.println("================================");
        System.out.println("\nVamos a enumerar los pasos de esta receta...");
        do{
            sc.nextLine();
            System.out.println("\nPaso "+n_paso+":");
            paso = n_paso+". "+sc.nextLine();
            pasos[cont] = paso;
            n_paso++;
            cont++;
            System.out.println("\nDeseas añadir otro paso? S/N");
            do{
                System.out.print(">");
                opc = sc.next().toLowerCase().charAt(0);
            }while(opc!='s' && opc!='n');
            if(opc == 'n') salir = true;
        }while(cont<25 && !salir);
        
        for(int i=0; i<cont; i++){
            if(i < (cont-1)) res += pasos[i]+" - ";
            else res += pasos[i];
        }
        //System.out.println(res);
        
        return res;
    }
    
    public static int eligeEtiquetas(int[] opcEt){
        Scanner teclado=new Scanner(System.in);
        Etiquetas[] etiqueta=Etiquetas.values();
        int opcion;
        boolean noMostrar = false; //Para que muestrre o no
        do{
            System.out.println("--------------------------");
            System.out.println("------ ETIQUETAS ------");
            System.out.println("--------------------------");
            for(int i=1; i<etiqueta.length; i++){// i empieza en 1 para evitar la posición cero, que contiene una entrada dummy. Así se evita que la primera opción aparezca en rojo
                noMostrar = false; //Reinicio variable
                for(int j=0; j<opcEt.length; j++){
                    if(opcEt[j] == i){
                        noMostrar = true;
                    }
                }
                if(noMostrar) System.out.println("\033[31m"+(i)+".- "+etiqueta[i].toString()+"\033[30m");
                if(!noMostrar) System.out.println("\033[32m"+(i)+".- "+etiqueta[i].toString()+"\033[30m");
            }
            System.out.print("\n>");
            opcion=compInput();
        }while(opcion<1 || opcion>etiqueta.length);
        return opcion;
    }
    
    public static void registraEtiquetas(Conexion con, Usuario login, Receta recetaCrea) throws SQLException{
        int[] opcEt = new int[3];
        int conEt = 0;
        boolean usada = false;
        int eti = 0;
        do{
            int codRec = Character.getNumericValue(con.selectToString("select cod from recetas where recetas.nombre='"+recetaCrea.getNombre()+"' and owner='"+login.getUsr()+"'").charAt(0));
            do{
                usada = false;
                eti=eligeEtiquetas(opcEt);//etiqueta 1 - apañao
                for(int i=0; i<opcEt.length; i++){
                    if(opcEt[i] == eti){
                        usada = true;
                        System.out.println("Ya has usado esta etiqueta!");
                    }
                }
            }while(usada);

            opcEt[conEt] = eti;
            String insertRE = "insert into rec_et (cod, id) values ("+codRec+", "+eti+")";
            con.insert(insertRE);
            conEt++;
        }while(conEt<3);
    }
    
    //borra los tags de la receta con 'cod' que recibe, para luego volver a preguntar por ellas, y las inserta
    public static void modifEtiquetas(Conexion con, int cod_receta) throws SQLException{
        String tags="";
        Etiquetas[] listaTags=Etiquetas.values();
        int[] opcEt = new int[3];
        int conEt = 0;
        boolean usada = false;
        int eti = 0;
        con.insert("delete from rec_et where cod = "+cod_receta+"");
        do{
            do{
                usada = false;
                eti=eligeEtiquetas(opcEt);//etiqueta 1 - apañao
                for(int i=0; i<opcEt.length; i++){
                    if(opcEt[i] == eti){
                        usada = true;
                        System.out.println("Ya has usado esta etiqueta!");
                    }
                }
            }while(usada);

            opcEt[conEt] = eti;
            String insertRE = "insert into rec_et (cod, id) values ("+cod_receta+", "+eti+")";
            con.insert(insertRE);
            conEt++;
        }while(conEt<3);
        
    }
    
    //pregunta al usuario cuántas estrellas asignar a una receta - recibe el int 'cod' de la receta a puntuar - después inserta la puntuación en la BD a la receta indicada
    public static void asignaEstrellas(Conexion con, Usuario login, int cod_receta) throws SQLException{
        //Character.getNumericValue(con.selectToString("select count(*) from recetas").charAt(0));
        String creador=con.selectToString("select owner from recetas where cod="+cod_receta).replaceAll(" - \n", "");
        Scanner teclado=new Scanner(System.in);
        String usr=login.getUsr();
        boolean compOwnership=false;
        String fecha="(trunc(sysdate))";//se pasa sin comillas
        String opinion="";
        int valoracion=0, n_votos=0, stars=-1;
        if(creador.equals(login.getUsr())) compOwnership=true;
        if(compOwnership || login.getLvl()==2){
            teclado.nextLine();
            System.out.println("¿Qué opinas de esta receta?");
            System.out.print(">");
            opinion=teclado.nextLine();
            do{
                System.out.println("¿Qué valoración le das?(1-5):");
                System.out.print(">");
                valoracion=teclado.nextInt();
            }while(valoracion<1 || valoracion>5);
            String insert_estrellas="insert into estrellas (usuario, receta, fecha, opinion, valoracion) values ('"+usr+"',"+cod_receta+", "+fecha+", '"+opinion+"',"+valoracion+")";
            con.insert(insert_estrellas);
            n_votos=Character.getNumericValue(con.selectToString("select count(*) from estrellas where receta="+cod_receta).charAt(0));
            stars=Character.getNumericValue(con.selectToString("select sum(valoracion) from estrellas where receta="+cod_receta).charAt(0));
            stars/=n_votos;
            String update_recetas="update recetas set estrellas="+stars+" where cod="+cod_receta;
            con.insert(update_recetas);
        }
        else System.out.println("No tienes permisos para puntuar esto.");
    }
    
    
    //cifra el pass
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
    
    //valida el String que recibe como un email válido o no
    public static boolean validaCorreo(String email){
        String check="^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(check);
        if(pattern.matches(check, email)) return true;
        else{
            System.out.println("Correo no váildo.");
            return false;
        }
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
    
    //crea las tablas necesarias en en la BD y los usuarios 'admin' y 'base' para que no sea necesario abrir ORACLE
    public static void BDsetUp(Conexion con) throws SQLException{
        String tablaUsu = "create table usuarios (usr 	varchar2(25),pass    varchar2(100) not null,mail    varchar2(50),lvl     number(1) check (lvl between 0 and 2),constraint pk_usuarios primary key (usr))";
        //original - funciona - NO BORRAR- String tablaRece = "create table recetas(cod	    integer generated always as identity (start with 1 increment by 1),owner 	varchar2(25),nombre  varchar2(25),descripcion    varchar2(100) not null,ingredientes varchar2(550), pasos   varchar2(500), constraint pk_recetas primary key (cod),constraint fk_rec_usu foreign key (owner) references usuarios(usr))";
        //nuevo "create table con estrellas"
        String tablaRece = "create table recetas(cod integer generated always as identity (start with 1 increment by 1), owner 	varchar2(25), nombre  varchar2(25), descripcion    varchar2(100) not null, ingredientes varchar2(550), pasos   varchar2(500), estrellas number(1,0) check (estrellas between 0 and 5), constraint pk_recetas primary key (cod), constraint fk_rec_usu foreign key (owner) references usuarios(usr))";
        String tablaEtiq ="create table etiqueta (id integer generated always as identity (start with 1 increment by 1) primary key, nom varchar2(30))";
        String tablaRec_Et="create table rec_et (cod	    number(3), id      integer, constraint pk_rec_et primary key(cod, id),constraint fk_rec_re foreign key (cod) references recetas(cod))";
        String tablaStars="create table estrellas (usuario varchar(25),receta number(3),fecha date, opinion varchar2(500),valoracion number(1)check(valoracion between 1 and 5),constraint pk_estrellas primary key (usuario, receta, fecha), constraint fk_estre_usu foreign key (usuario) references usuarios(usr), constraint fk_estre_rec foreign key (receta) references recetas(cod))";
        String admin = "insert into usuarios values ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin@admin.com', 2)";
        String base = "insert into usuarios values ('base', 'base', 'base', 0)";
        String testUsr="insert into usuarios values ('test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'test@test.com', 1)";//borrar cuando esté la práctica
        
        //las etiquetas
        String etiq="";
        String err="ninguno";
        Etiquetas[]etiqueta=Etiquetas.values();
        try{
            con.insert(tablaUsu);
            err="tablaUsu";
            con.insert(tablaRece);
            err="tablaRece";
            con.insert(tablaEtiq);
            err="tablaEtiq";
            for(int i=1; i<etiqueta.length; i++){
                etiq="insert into etiqueta (nom) values ('"+etiqueta[i].toString()+"')";
                con.insert(etiq);
            }
            err="Etiq insert";
            con.insert(tablaRec_Et);
            err="tablaRec_Et";
            con.insert(tablaStars);
            err="tablaStars";
            con.insert(admin);
            err="admin";
            con.insert(base);
            err="base";
            con.insert(testUsr);
            err="testUsr";
        }catch(Exception e){
            System.out.println("\nError: "+err);
        }
    }
    
    //crea las tablas necesarias en en la BD y los usuarios 'admin' y 'base' para que no sea necesario abrir ORACLE
    public static void BDsetUpIns(Conexion con) throws SQLException{
        String tablaUsu = "create table usuarios (usr 	varchar2(25),pass    varchar2(100) not null,mail    varchar2(50),lvl     number(1) check (lvl between 0 and 2),constraint pk_usuarios primary key (usr))";
        //original - funciona - NO BORRAR- String tablaRece = "create table recetas(cod	    integer generated always as identity (start with 1 increment by 1),owner 	varchar2(25),nombre  varchar2(25),descripcion    varchar2(100) not null,ingredientes varchar2(550), pasos   varchar2(500), constraint pk_recetas primary key (cod),constraint fk_rec_usu foreign key (owner) references usuarios(usr))";
        //nuevo "create table con estrellas"
        String tablaRece = "create table recetas(cod integer generated always as identity (start with 1 increment by 1), owner 	varchar2(25), nombre  varchar2(25), descripcion    varchar2(100) not null, ingredientes varchar2(550), pasos   varchar2(500), estrellas number(1,0) check (estrellas between 0 and 5), constraint pk_recetas primary key (cod), constraint fk_rec_usu foreign key (owner) references usuarios(usr))";
        String tablaEtiq ="create table etiqueta (id integer generated always as identity (start with 1 increment by 1) primary key, nom varchar2(30))";
        String tablaRec_Et="create table rec_et (cod	    number(3), id      integer, constraint pk_rec_et primary key(cod, id),constraint fk_rec_re foreign key (cod) references recetas(cod))";
        String tablaStars="create table estrellas (usuario varchar(25),receta number(3),fecha date, opinion varchar2(500),valoracion number(1)check(valoracion between 1 and 5),constraint pk_estrellas primary key (usuario, receta, fecha), constraint fk_estre_usu foreign key (usuario) references usuarios(usr), constraint fk_estre_rec foreign key (receta) references recetas(cod))";
        String admin = "insert into usuarios values ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin@admin.com', 2)";
        String base = "insert into usuarios values ('base', 'base', 'base', 0)";
        String testUsr="insert into usuarios values ('test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'test@test.com', 1)";//borrar cuando esté la práctica   
        String[] receta={"insert into recetas (owner, nombre, descripcion, ingredientes, pasos, estrellas) values ('test', 'macarrones', 'Un delicioso plato de pasta para salir del paso un domingo por la tarde', 'macarron, tomate, carne, oregano', '1. Ponemos el agua a cocer y echamos los macarrones - 2. Esperamos unos 15 minutos - 3. Los servimos echando el resto de ingredientes', 0)","insert into recetas (owner, nombre, descripcion, ingredientes, pasos, estrellas) values ('test', 'Pizza', 'Un plato tradicional italiano delicioso para disfrutar en familia o con amigos', 'masa, tomate, peperoni, champiñones, queso, jamon, piña', '1. Hacemos la masa de la pizza - 2. Ponemos el tomate como base - 3. Colocamos el resto de ingredientes al gusto - 4. Lo metemos al horno 30 min y listo para servir', 0)","insert into recetas (owner, nombre, descripcion, ingredientes, pasos, estrellas) values ('test', 'Alpujarreño', 'Un plato para la gente mas de campo', 'morcilla, chorizo, patatas, salchichas, pimiento', '1. Prepara cada cosa de manera individual - 2. sirverlo al azar en el plato', 0)"};        
        String[] etiRece={"insert into rec_et (cod, id) values (1, 1)","insert into rec_et (cod, id) values (1, 2)","insert into rec_et (cod, id) values (1, 5)","insert into rec_et (cod, id) values (2, 3)","insert into rec_et (cod, id) values (2, 1)","insert into rec_et (cod, id) values (2, 17)","insert into rec_et (cod, id) values (3, 1)","insert into rec_et (cod, id) values (3, 2)","insert into rec_et (cod, id) values (3, 14)"};
        String F_mRece=("create or replace procedure muestraValor(creador in varchar2, cod in number) is cursor datos is select * from estrellas where usuario = creador and receta = cod; lista datos%rowtype; begin open datos; loop fetch datos into lista; exit when datos%notfound; dbms_output.put_line('USUARIO: '||lista.usuario); dbms_output.put_line('FECHA: '||lista.fecha); dbms_output.put_line('OPINION: '||lista.opinion); end loop; close datos; end muestraValor");
        
        //las etiquetas
        String etiq="";
        String err="ninguno";
        Etiquetas[]etiqueta=Etiquetas.values();
        try{
            con.insert(tablaUsu);
            err="tablaUsu";
            con.insert(tablaRece);
            err="tablaRece";
            con.insert(tablaEtiq);
            err="tablaEtiq";
            for(int i=1; i<etiqueta.length; i++){
                etiq="insert into etiqueta (nom) values ('"+etiqueta[i].toString()+"')";
                con.insert(etiq);
            }
            err="Etiq insert";
            con.insert(tablaRec_Et);
            err="tablaRec_Et";
            con.insert(tablaStars);
            err="tablaStars";
            con.insert(admin);
            err="admin";
            con.insert(base);
            err="base";
            con.insert(testUsr);
            err="testUsr";
            for(int i=0; i<3; i++){
                con.insert(receta[i]);
            }
            
            err="insertRece";
            
            for(int i=0; i<3; i++){
                con.insert(etiRece[i]);
            }
            for(int i=3; i<6; i++){
                con.insert(etiRece[i]);
            }
            for(int i=6; i<9; i++){
                con.insert(etiRece[i]);
            }
            
            con.insert(F_mRece);
            err="muestraValor";
        }catch(Exception e){
            System.out.println("\nError: "+err);
        }
    }
    
    //destruye todo a su paso -CUIDADORRRLL fistro de la pradera
    public static void destroyAll(Conexion con) throws SQLException{
        System.out.println("Destrozando la base de datos!!!");
        String err="ninguno";
        String dropRec_Et="drop table rec_et cascade constraints";
        String dropEtiq="drop table etiqueta cascade constraints";
        String dropReces="drop table recetas cascade constraints";
        String dropUsrs="drop table usuarios cascade constraints";
        String dropStars="drop table estrellas cascade constraints";
        try{
            con.insert(dropRec_Et);
            err="dropRec_Et";
            con.insert(dropEtiq);
            err="dropEtiq";
            con.insert(dropReces);
            err="dropReces";
            con.insert(dropUsrs);
            err="dropUsrs";
            con.insert(dropStars);
            err="dropStars";
        }catch(Exception e){
            System.out.println("Error: "+err);
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
    public static int compInput(int[] cosas){
        Scanner teclado=new Scanner(System.in);
        int aux=-1;
        boolean error = true;
        try{
            do{
                System.out.print("> ");
                error = true;
                aux=compInput();
                for(int i=0; i<cosas.length; i++){
                    if(cosas[i]==aux) error = false;
                }
            }while(error);
        }catch(Exception e){
            System.out.println("Se requiere un número entero.");
        }
        return aux;
        
    }
    
    public static void busca(String[] etiquetas, int indice, int contEti, char tipoBus, String busqueda, Conexion con) throws SQLException{
        Scanner teclado=new Scanner(System.in);
        etiquetas = new String[3];
        etiquetas[0]="";
        etiquetas[1]="";
        etiquetas[2]="";
        indice=-1;
        contEti = 0;
        tipoBus=' ';
        teclado.nextLine();
        System.out.println("======================");
        System.out.println("¿Buscar Receta por Nombre / Etiqueta / Usuario?(n/e/u)");
        //System.out.println("hola");
        do{
            System.out.print(">");
            tipoBus=teclado.next().toLowerCase().charAt(0);
            if(tipoBus=='N')tipoBus='n';
            if(tipoBus=='E')tipoBus='e';
        }while(tipoBus!='n' && tipoBus!='e' && tipoBus!='u');
        System.out.println("TEST - Tipo de búsqueda: "+tipoBus);
        if(tipoBus=='n'){
            System.out.println("Búsqueda por Nombre: ");
            System.out.print(">");
            busqueda=teclado.next().toLowerCase();
            muestraRecetasReceta(con, busqueda);
        }
        if(tipoBus=='e'){
            char opcEti=' ';
            boolean salirBusquedaEtiqueta=false;
            System.out.println("Búsqueda por Etiqueta.");
            do{
                System.out.println("Añade una etiqueda a la búsqueda:");
                System.out.print(">");
                busqueda=teclado.next();
                System.out.println("Desea añadir otra etiqueta? (s/n): ");
                do{
                    System.out.print(">");
                    opcEti=teclado.next().charAt(0);
                    if(opcEti=='S')opcEti='s';
                    if(opcEti=='N')opcEti='n';
                }while(opcEti!='s' && opcEti!='n');
                etiquetas[contEti] = busqueda;
                contEti++;
                if(opcEti=='n') salirBusquedaEtiqueta=true;

            }while(!salirBusquedaEtiqueta && contEti<etiquetas.length);
            if(contEti == 1){
                etiquetas[1] = etiquetas[0];
                etiquetas[2] = etiquetas[0];
            }
            if(contEti == 2) etiquetas[2] = etiquetas[0];
            System.out.println("Cantidad array etiquetas: "+contEti);
            busquedaEti(con, etiquetas, busqueda, contEti);      


        }
        if(tipoBus=='u'){
            System.out.println("Búsqueda por Usuario: ");
            System.out.print(">");
            busqueda=teclado.next();
            muestraRecetasNombre(con, busqueda, tipoBus);
        }
    }
    
}

