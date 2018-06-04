/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ext2.os2;

import static ext2.os2.EXT2OS2.buscarBloquesVaciosDir;
import static ext2.os2.EXT2OS2.clusterSize;
import static ext2.os2.EXT2OS2.fileClusters;
import static ext2.os2.EXT2OS2.fileSystem;
import static ext2.os2.EXT2OS2.offset_bitmapinodos;
import static ext2.os2.EXT2OS2.offset_iniciodatos;
import static ext2.os2.EXT2OS2.offset_tablainodos;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Inti Velasquez
 */
public class WriteFile {

    /**
     * @param args the command line arguments
     */
    public static int clusterSize = 4096;
    public static int fileClusters = 65536;
    public static int offset_iniciodatos = 143360;

    public static void main(String[] args) {
        try{
            File file = new File("./fileSystem");
            if (!file.exists()) {
                RandomAccessFile fileSystem = new RandomAccessFile("fileSystem", "rw");

                for (int i = 0; i < fileClusters; i++) {
                    for (int j = 0; j < clusterSize; j++) {
                        fileSystem.writeByte(0);
                    }
                }
                fileSystem.seek(0);
                for (int i = 0; i < 5; i++) {
                    if (i == 4) {
                        fileSystem.writeByte(240);
                    } else {
                        fileSystem.writeByte(255);
                    }
                }
                fileSystem.seek(offset_iniciodatos);
                for (int i = offset_iniciodatos; i < offset_iniciodatos+4096; i++) {
                    fileSystem.writeByte(0);
                }
                fileSystem.seek(offset_bitmapinodos);
                byte b = fileSystem.readByte();
                b |= (1 << 7);
                fileSystem.seek(offset_bitmapinodos);
                fileSystem.writeByte(b);
                fileSystem.seek(offset_tablainodos);
                Inode inodo = new Inode();
                inodo.setI_size(0);
                //Escritura del Inodo en la tabla de inodos
                //mode, size, links_count, blocks, punteros, create, access, modify, delete
                fileSystem.writeInt(0);
                fileSystem.writeInt(0);                
                fileSystem.writeInt(0);
                fileSystem.writeInt(1);
                for (int i = 0; i < 13; i++) {
                    fileSystem.writeInt(0);
                }
                fileSystem.writeUTF(inodo.getI_ctime());
                fileSystem.writeUTF(inodo.getI_atime());
                fileSystem.writeUTF(inodo.getI_mtime());
                fileSystem.writeUTF(inodo.getI_dtime());
            }
        }catch(IOException ioex){
            
        }
    }
    
}
