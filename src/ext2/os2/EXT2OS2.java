/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ext2.os2;

//import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author ricardo, juany, inti, ariel
 */
public class EXT2OS2 {

    /**
     * @param args the command line arguments
     */
    public static int offset_bitmapdatos = 0;
    public static int offset_bitmapinodos = 8192;
    public static int offset_tablainodos = 12288;
    public static int offset_iniciodatos = 143360;
    public static int clusterSize = 4096;
    public static int fileClusters = 65536;
    public static Stack<Integer> directorioCd = new Stack();    
    public static String route = "/";
    public static RandomAccessFile fileSystem;
    
    public static void main(String[] args) {
        
        directorioCd.push(offset_iniciodatos);
        
        byte test = 9; //00001001
        System.out.println("Binary string: "+Integer.toBinaryString(test));
        System.out.println("Right shift 3rd bit: "+Integer.toBinaryString((test >> 2)));
        System.out.println("AND first and second strings: "+Integer.toBinaryString(1 & (test >> 2)));
        try {
            fileSystem = new RandomAccessFile("fileSystem", "rw");
//            fileSystem.seek(directorioCd.peek());

            
            fileSystem.seek(directorioCd.peek());
            Scanner scanner = new Scanner(System.in);
            boolean read = true;
            while (read) {
                System.out.print(route+" >>");
                String command = scanner.nextLine();
                String[] partes = command.split(" ");
                if (partes[0].equals("cat")) {
                    cat(partes);
                } else if (partes[0].equals("ls")) {
                    ls();
                } else if (partes[0].equals("mkdir")) {
                    mkdir(partes);
                } else if (partes[0].equals("rmdir")) {
                    rmdir(partes);
                } else if (partes[0].equals("rm")) {
                    rm(partes);
                } else if (partes[0].equals("cd")) {
                    cd(partes);
                } else {
                    System.out.println("Command not found");
                }
            }
        } catch (IOException ioex) {
            System.err.println("Error creando Sistema");
        }
    }
    
    public static int[] buscarInodoVacio() throws IOException {
        fileSystem.seek(offset_bitmapinodos);
        for (int i = 0; i < 128; i++) {
            byte currByte = (byte)fileSystem.readUnsignedByte();
            if (currByte < 255) {
                for (int j = 7; j >= 0; j--) {
                    if (((currByte >> j) & 1) == 0) {
                        System.out.println("Agarro inodo vacio byte "+i+", bit "+j);
                        fileSystem.seek(offset_bitmapinodos + i);
                        currByte |= (1 << j);
                        fileSystem.writeByte(currByte);
                        int[] retVal = {i, j};
                        return retVal;
                    }
                }
            }
        }
        int[] fallo = {-1, -1};
        return fallo;
    }
    
    public static void buscarBloquesVaciosDir(String data){
        try{
            fileSystem.seek(offset_bitmapdatos);
            ArrayList<int[]> blocks = new ArrayList();            
            int[] vals = new int[2];
            for (int i = 0; i < offset_bitmapinodos; i++) {
                byte currByte = (byte) fileSystem.readUnsignedByte(); 
                for (int j = 7; j >= 0; j--) {
                    if (((currByte >> j) & 1) == 0) {
                        vals[0] = i;
                        vals[1] = j;
                        i = offset_bitmapinodos;
                        blocks.add(vals);
                        break;
                    }
                }
            }
            fileSystem.seek(vals[0]);
            byte holis = (byte) fileSystem.readUnsignedByte();
            holis |= (1 << vals[1]);
            fileSystem.seek(vals[0]);
            fileSystem.writeByte(holis);
            int inodeForDir = asignarInodo(blocks, 0);
            escribirEntradaDirectorio(blocks, data, inodeForDir);
        }catch(IOException ioex){
            System.out.println("Error en buscar bloques vacios dir");
        }
    }
    
    public static int buscarBloquesVaciosFile(String data, String fileName) throws IOException {
        int blockQuantity = (int) Math.ceil((data.length()) / 4096); //cantidad de bloques necesarios para escribir la data
        blockQuantity += 1;
        fileSystem.seek(offset_bitmapdatos);
        ArrayList<int[]> blocks = new ArrayList();  
        System.out.println("Comenzó a buscar en bitmap datos");
        for (int i = 0; i < offset_bitmapinodos; i++) {
            byte currByte =(byte) fileSystem.readUnsignedByte();
            for (int j = 7; j >= 0; j--) {
                //11110000
                if (((currByte >> j) & 1) == 0) {
                    System.out.println("Agarro byte "+i+", bit "+j);
                    int[] vals = {i, j};
                    blocks.add(vals);
                    if (blocks.size() == blockQuantity) {
                        i = offset_bitmapinodos;
                        break;
                    }
                }
            }
        }
        System.out.println("Termino de buscar bloques");
        if (blocks.size() < blockQuantity) {
            System.err.println("no hay suficientes bloques");
        } else {
            for (int i = 0; i < blocks.size(); i++) {
                fileSystem.seek(blocks.get(i)[0]);
                byte curr = fileSystem.readByte();
                curr |= (1 << blocks.get(i)[1]);
                fileSystem.seek(blocks.get(i)[0]);
                fileSystem.writeByte(curr);
            }
            System.out.println("Terminó de reescribir bits en bitmap datos");
            int inodeForDir = asignarInodo(blocks, data.getBytes("UTF-8").length);
            escribirEntradaDirectorio(blocks, fileName, inodeForDir);
            escribirData(blocks, data);
            return inodeForDir;
//            escribirData(blocks, data);                                 //AGREGUE ESTO REVISEN SI ESTA BIEN [@Inti @Ricardo] -JUANY
        }
        return -1;
    }
    
    public static int asignarInodo(ArrayList<int[]> blocks, int size) {
        try{
        if (size == 0) {
            int[] index = buscarInodoVacio();
            if (index[0] != -1) {
                int posicion = ((index[0] * 8) + (7-index[1])) * 128 + offset_tablainodos; //Posicion del inodo en la tabla de inodos
                System.out.println("Posicion de inodo: "+posicion);
                fileSystem.seek(posicion);
                Inode inodo = new Inode();
                inodo.setI_size(0);
                //Escritura del Inodo en la tabla de inodos
                //mode, size, links_count, blocks, punteros, create, access, modify, delete
                fileSystem.writeInt(0);
                fileSystem.writeInt(size);                
                fileSystem.writeInt(0);
                fileSystem.writeInt(1);
                fileSystem.writeInt((blocks.get(0)[0]*8+(7-blocks.get(0)[1]))*clusterSize + offset_iniciodatos);
                for (int i = 1; i < 13; i++) {
                    fileSystem.writeInt(0);
                }
                fileSystem.writeUTF(inodo.getI_ctime());
                fileSystem.writeUTF(inodo.getI_atime());
                fileSystem.writeUTF(inodo.getI_mtime());
                fileSystem.writeUTF(inodo.getI_dtime());
                return posicion;
            }
        } else {
            int[] index = buscarInodoVacio();
            if (index[0] != -1) {
                
                int posicion = ((index[0] * 8) + (7-index[1])) * 128 + offset_tablainodos; //Posicion del inodo en la tabla de inodos
                fileSystem.seek(posicion);
                Inode inodo = new Inode();
                inodo.setI_blocks(blocks.size());
                inodo.setI_size(size);
                int[] punteros = new int[blocks.size()];
                for (int i = 0; i < blocks.size(); i++) {
                    punteros[i] = (index[0] * 8 +(7-index[1])) * clusterSize + offset_iniciodatos;
                }
                inodo.setI_block(punteros);
                //Escritura del Inodo en la tabla de inodos
                //size, links_count, blocks, punteros, create, access, modify, delete
                fileSystem.writeInt(1);
                fileSystem.writeInt(size);
                fileSystem.writeInt(1);
                fileSystem.writeInt(blocks.size());
                for (int i = 0; i < 13; i++) {
                    if (i < punteros.length) {
                        fileSystem.writeInt(punteros[i]);
                    } else {
                        fileSystem.writeInt(0);
                    }
                }
                fileSystem.writeUTF(inodo.getI_ctime());
                fileSystem.writeUTF(inodo.getI_atime());
                fileSystem.writeUTF(inodo.getI_mtime());
                fileSystem.writeUTF(inodo.getI_dtime());
                
                return posicion;
            }
        }
        }catch(IOException ioex){
            System.out.println("Error en asignar inodo");
        }
        return -1;
    }
    
    public static void escribirData(ArrayList<int[]> blocks, String data) throws IOException {
        int posicion;
        System.out.println("blocks size: " +blocks.size());
        System.out.println("Data a escribir: "+data);
        for (int i = 0; i < blocks.size(); i++) {
            posicion = (blocks.get(i)[0] * 8 + (7-blocks.get(i)[1])) * clusterSize;
            System.out.println("Data escrita a bloque "+posicion);
            fileSystem.seek(posicion);
            if (data.length() >= 4096) {
                String output = data.substring(0, 4096);
                data = data.substring(4096, data.length());
                for (int j = 0; j < output.length(); j++) {
                    fileSystem.writeChar(output.charAt(j));
                }
            } else {
                for (int j = 0; j < data.length(); j++) {
                    System.out.println("Caracter a escribir: "+data.charAt(j));
                    fileSystem.writeChar(data.charAt(j));
                }
            }
        }
    }
    
    public static void escribirEntradaDirectorio(ArrayList<int[]> blocks, String filename, int inodo) {
        try{
            int posicion;
            //Entrada de directorio
            //index inodo, tamaño del nombre, nombre 
            posicion = directorioCd.peek();
            fileSystem.seek(posicion);       
            //64Bytes total
            //int inodo
            int inode, reclen;
            int rec_len =  12+filename.getBytes("UTF-8").length;
            int direntries = 0;
            while(true){
                if(direntries < 4096){
                    
//                    fileSystem.seek(directorioCd.peek() + direntries);
                    reclen = fileSystem.readInt();
                    
                    if(reclen == 0){
                        long posactual = fileSystem.getFilePointer() - 4;
                        fileSystem.seek(posactual);
                        fileSystem.writeInt(rec_len+1);
                        fileSystem.writeInt(inodo);
                        int l = filename.getBytes("UTF-8").length;
                        fileSystem.writeInt(l);
                        fileSystem.writeUTF(filename);
                        break;
                    }else{
                        direntries += reclen + 1;
                        inode = fileSystem.readInt();
                        int length = fileSystem.readInt();
                        String asd = fileSystem.readUTF();
//                        fileSystem.seek(fileSystem.getFilePointer() + reclen - 8);
                        
                    }
                }else{
                    System.out.println("Ya no hay espacio para entradas de directorios en este directorio");
                    break;
                }
            }
//        for (int i = 0; i < 4096; i++) {
//            num = fileSystem.readInt();
//            if(num == 0){
//                fileSystem.seek(fileSystem.getFilePointer()-4);
//                fileSystem.writeInt(rec_len);
//                fileSystem.writeInt(inodo);
//                fileSystem.writeInt(data.length()*2);
//                fileSystem.writeUTF(data);
//            }else{
//                fileSystem.seek(fileSystem.getFilePointer()-4);
//                while(true){
//                    
//                    int size = fileSystem.readInt();
//                    System.out.println("Size: "+size);
//                    int inode = fileSystem.readInt();
//                    System.out.println("Inodo: "+inode);
//                    if ( inode == 0  ){
//                        fileSystem.seek(fileSystem.getFilePointer() - 8);
//                        fileSystem.writeInt(rec_len);
//                        fileSystem.writeInt(inodo);
//                        fileSystem.writeInt(data.length()*2);
//                        fileSystem.writeUTF(data);
//                        i = 4096;
//                        break;
//                    }else{
//                        fileSystem.readInt();
//                        fileSystem.readUTF();
//                    }
//                }
//            }
//        }
        }catch(IOException ioex){
            System.out.println("Error en escribir entrada dir");
            ioex.printStackTrace();
        }
    }
    
    public static void cat(String[] command) throws IOException {
        if (command[1].equals(">")) {
            Scanner scanner = new Scanner(System.in);
            String text = "";
            String linea = "";
            System.out.print(">");
            while(!(linea = scanner.nextLine()).equals("::q")){
                System.out.print(">");
                text+= linea+"\n";
            }
            buscarBloquesVaciosFile(text, command[2]);
            //Escribe el texto que se le agregue hasta encontrar ::q
            //--Crea el nuevo archivo en la entrada de directorio
            //--Escribir los datos del archivo
        } else {
            fileSystem.seek(directorioCd.peek());
            int reclen;
            while(true) {
                reclen = fileSystem.readInt();
                if(reclen == 0){
                    break;
                }else{
                    int inodo = fileSystem.readInt();
                    int l = fileSystem.readInt();
                    String name = fileSystem.readUTF();
                    if(name.equals(command[1])){
                        fileSystem.seek(inodo);
                        fileSystem.readInt(); //mode
                        int size = fileSystem.readInt();
                        fileSystem.readInt(); //links
                        int blocks = fileSystem.readInt();
                        long currDir = fileSystem.getFilePointer();
                        
                        String text = "";
                        for (int i = 0; i < blocks; i++) {
                            int ptr = fileSystem.readInt();
                            currDir += 4;
                            fileSystem.seek(ptr);
                            if(size > 4096){
                                for (int k = 0; k < 4096; k++) {
                                    text += fileSystem.readChar();
                                }
                                size-=4096;
                                fileSystem.seek(currDir);
                            }else{
                                for (int k = 0; k < size; k++) {
                                    text+= fileSystem.readChar();
                                }
                                size = 0;
                                fileSystem.seek(currDir);
                                break;
                            }
                        }
                        fileSystem.seek(directorioCd.peek());
                        System.out.println(text);
                        break;
                    }
                }

        }
            //imprime el contenido de un archivo
            //--Entra al inodo del archivo y retorna lo que hay en los bloques del inodo
        }
    }
    
    public static void ls(){
        //Imprime los directorios y archivos
        //-- imprime el contenido los datos de la entrada de directorio
        //-- -l busca la metadata con el indice del inodo
        try{
        fileSystem.seek(directorioCd.peek());
        int reclen;
        String names = "";
        while(true) {
            reclen = fileSystem.readInt();
            if(reclen == 0){
                break;
            }else{
                int inodo = fileSystem.readInt();
                int l = fileSystem.readInt();
                String name = fileSystem.readUTF();
                System.out.println(name+", inodo: "+inodo);
            }
            
        }
        System.out.println(names);
        fileSystem.seek(directorioCd.peek());
        }catch(IOException ioex){
            System.err.println("Aqui te juiste perro");
            ioex.printStackTrace();
        }
    }
    
    public static void mkdir(String[] command) throws IOException {
        //crea una nueva entrada de directorio en el directorio actual
        System.out.println(command[0] + " " + command[1]);
        buscarBloquesVaciosDir(command[1]);
        
    }
    
    public static void rmdir(String[] command) {
        
        System.out.println(command[0] + " " + command[1]);
    }
    
    public static void rm(String[] command) {
        //escribe 0 en los bloques utilizados, del bitmap
        //lo elimina de la entrada de directorio
        System.out.println(command[0] + " " + command[1]);
    }
    
    public static void cd(String[] command) {
        //entra a un directorio
        //revisa si el nombre ingresado es de un directorio, revisando si tiene inodo o no
        try{
            if(command[1].equals("..")){
                directorioCd.pop();
                fileSystem.seek(directorioCd.peek());
                int index = route.lastIndexOf('/');
                route = route.substring(0, index);
                index = route.lastIndexOf('/');
                route = route.substring(0, index+1);
            }else{
                fileSystem.seek(directorioCd.peek());
                int reclen;
                while(true){
                    reclen = fileSystem.readInt();
                    if(reclen == 0){
                        break;
                    }else{
                        int inodo = fileSystem.readInt();
                        int l = fileSystem.readInt();
                        String name = fileSystem.readUTF();
                        if(name.equals(command[1])){
                            fileSystem.seek(inodo);
                            for (int i = 0; i < 4; i++) {
                                fileSystem.readInt();
                            }
                            int blockptr = fileSystem.readInt();
                            fileSystem.seek(blockptr);
                            directorioCd.push(blockptr);
                            route += name +"/";
                            break;
                        }
                    }
                }
            }
        }catch(IOException ioex){
            System.err.println("Error en cd");
            ioex.printStackTrace();
        }
    }

//        String file = "./boot.start";
//        writeStructure(file);
//        int menu = 1;
//        Scanner sc = new Scanner(System.in);
//        String nombre;
//        String texto;
//        while(menu != 3){
//            System.out.println("1. Crear nuevo archivo:");
//            System.out.println("2. Crear nuevo directorio:");
//            System.out.println("3. Salir");
//            menu = sc.nextInt();
//            if(menu==1){
//                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                Date date = new Date();
//                System.out.println(); //2016/11/16 12:08:43
//                System.out.println("Ingresar Nombre del archivo");
//                nombre = sc.next();
//                System.out.println("Ingresar texto");
//                texto = sc.next();
//                int i_size = texto.length()*2;
//                char[] i_ctime = (dateFormat.format(date)).toCharArray();;
//                char[] i_blocks = new char[4];
//                int[] i_block = new int[15];
//                Inode inodo = createInode(i_size, i_ctime ,i_blocks,i_block);
//                writeTextandInode(file, " ", inodo, texto);
//                
//            }else if(menu==2){
//                System.out.println("Ingresar Nombre del directorio");
//                nombre = sc.next();
//            }
//        }
}

//    public static void writeStructure(String file){
//        boolean [] inodeBitmap = new boolean[1024];
//        boolean [] blockBitmap = new boolean[262144];
//        for (int i = 0; i < inodeBitmap.length; i++) {
//            inodeBitmap[i] = false;
//        }
//        for (int i = 0; i < blockBitmap.length; i++) {
//            inodeBitmap[i] = false;
//        }
//        Inode [] inodeTable = new Inode[1024];
//        directoryEntry [] directoryEntries = new directoryEntry[1024];
//        try {
//            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
//            for (int i = 0; i < inodeBitmap.length; i++) {
//                os.writeBoolean(inodeBitmap[i]);
//            }
//            for (int i = 0; i < blockBitmap.length; i++) {
//                os.writeBoolean(inodeBitmap[i]);
//            }
//            os.close();
//        } catch (IOException r) {
//            System.out.println("IOERROR:  " + r.getMessage() + "\n");
//        }   
//    }
//    
//    public static void writeTextandInode(String file, String file2, Inode inodo, String texto) {
//        //revisar los bloques vacios y llenar el tamaño en el bitmap
//        //revisar los inodos vacios y agregar nuevo inodo a la tabla de inodos
//        try {
//            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
//            int i_size = inodo.getI_size();
//            char[] i_blocks = inodo.getI_blocks();
//            int[] i_block = inodo.getBlock();
//            char[] i_ctime = inodo.getI_ctime();
//            for (int i = 0; i < i_ctime.length; i++) {
//                os.writeChar(i_ctime[i]);
//            }
//            os.writeInt(i_size);
//            for (int i = 0; i < i_blocks.length; i++) {
//                os.writeChar(i_blocks[i]);
//            }
//            for (int i = 0; i < i_block.length; i++) {
//                os.writeInt(i_block[i]);
//            }
//            os.close();
//        } catch (IOException r) {
//            System.out.println("IOERROR:  " + r.getMessage() + "\n");
//        }   
//    }
//    
//    public static Inode createInode(int i_size, char[] i_ctime, char[] i_blocks, int[] block) {
//        Inode nuevo = new Inode();
//        nuevo.setI_size(i_size);
//        nuevo.setI_ctime(i_ctime);
//        nuevo.setI_blocks(i_blocks);
//        nuevo.setBlock(block);    
//        return nuevo;
//    }
