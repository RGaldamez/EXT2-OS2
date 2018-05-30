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

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Scanner;

/**
 *
 * @author ricar
 */
public class EXT2OS2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("./fileSystem");
        if (!file.exists()) {
            try {
                RandomAccessFile fileSystem = new RandomAccessFile("fileSystem","rw");
                
                byte initial = 0;
                char division = '|';
                
                int fileClusters = 65536;
                int clusterSize = 4096;
                
                for (int i = 0; i < fileClusters ; i++) {
                    
                    for (int j = 0; j < clusterSize; j++) {
                        fileSystem.writeByte(initial);
                    }
                    fileSystem.writeChar(division);
                }
                
            } catch (IOException ioex) {
                System.err.println("Error creando Sistema");
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