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

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Scanner;

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

    public static RandomAccessFile fileSystem;

    public static void main(String[] args) {
        try {
            File file = new File("./fileSystem");
            fileSystem =  new RandomAccessFile("fileSystem","rw");
            if (!file.exists()) {
                for (int i = 0; i < fileClusters ; i++) {
                    for (int j = 0; j < clusterSize; j++) {
                        fileSystem.writeByte(0);
                    }
                }
            }else{
                fileSystem.seek(0);
                for (int i = 0; i < 5; i++) {
                    if(i == 4) {
                        fileSystem.writeByte(170);
                    }else{
                        fileSystem.writeByte(255);
                    }
                }
            }
        } catch (IOException ioex) {
                System.err.println("Error creando Sistema");
        }
}
    
public int[] buscarInodoVacio() throws IOException {
    fileSystem.seek(offset_bitmapinodos);
    for(int i = 0; i < 128; i++){
        byte currByte = fileSystem.readByte();
        if(currByte < 127){
            for (int j = 7; j >= 0; j--) {
                if(((currByte >> j) & 0) == 0){
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

public void buscarBloquesVacios(String data) throws IOException{
    int blockQuantity = (int) Math.ceil((data.length()*2)/4096); //cantidad de bloques necesarios para escribir la data
    fileSystem.seek(offset_bitmapdatos);
    ArrayList<int[]> blocks = new ArrayList(); 
    for (int i = 4; i < offset_bitmapinodos; i++) {
        byte currByte = fileSystem.readByte();
        for (int j = 7; j >= 0; j++) {
            if(((currByte >> j) & 0) == 0){
                int[] vals = {i, j};
                blocks.add(vals);
            }
        }
        if(blocks.size() == blockQuantity){
            break;
        }
    }
    if(blocks.size() < blockQuantity){
        System.err.println("no hay suficientes bloques");
    }else{
        for (int i = 0; i < blocks.size(); i++) {
            fileSystem.seek(blocks.get(i)[0]);
            byte curr = fileSystem.readByte();
            curr |= (1 << blocks.get(i)[1]);
            fileSystem.seek(blocks.get(i)[0]);
            fileSystem.writeByte(curr);
        }

        asignarInodo(blocks, data.length()*2);
        escribirData(blocks, data);
    }
}

public void asignarInodo(ArrayList<int[]> blocks, int size) throws IOException {
    int[] index = buscarInodoVacio();
    if(index[0] != -1){
        int posicion = ((index[0]*8)+index[1])*128+offset_tablainodos; //Posicion del inodo en la tabla de inodos
        fileSystem.seek(posicion);
        Inode inodo = new Inode();
        inodo.setI_blocks(blocks.size());
        inodo.setI_size(size);
        int[] punteros = new int[blocks.size()];
        for(int i = 0; i< blocks.size(); i++) {
            punteros[i] = (index[0]*8+index[1])*clusterSize;
        }
        inodo.setI_block(punteros);
        //Escritura del Inodo en la tabla de inodos
        //size, links_count, blocks, punteros, create, access, modify, delete
        fileSystem.writeInt(size);
        fileSystem.writeInt(1);
        fileSystem.writeInt(blocks.size());
        for (int i = 0; i < 13; i++) {
            if(i < punteros.length){
                fileSystem.writeInt(punteros[i]);
            }else{
                fileSystem.writeInt(0);
            }
        }
        fileSystem.writeUTF(inodo.getI_ctime());
        fileSystem.writeUTF(inodo.getI_atime());
        fileSystem.writeUTF(inodo.getI_mtime());
        fileSystem.writeUTF(inodo.getI_dtime());
    }
}
    
public void escribirData(ArrayList<int[]> blocks, String data) throws IOException{
    int posicion;
    for (int i = 0; i < blocks.size(); i++) {
        posicion = (blocks.get(i)[0]*8+blocks.get(i)[1])*4096;
        fileSystem.seek(posicion);
        if(data.length() >= 2047){
            String output = data.substring(0, 2047);
            data = data.substring(2047, data.length());
            fileSystem.writeUTF(output);
        }else{
            fileSystem.writeUTF(data);
        }
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