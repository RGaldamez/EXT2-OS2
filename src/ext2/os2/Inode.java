/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ext2.os2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author juany, inti, ricardo, ariel
 */
public class Inode {

    int i_size;
    int i_links_count;
    int i_blocks;
    int[] i_block;
    String i_ctime;
    String i_atime;
    String i_mtime;
    String i_dtime;
    
    public Inode(int i_size, String i_atime, String i_mtime, String i_dtime, String i_ctime, int i_links_count, int i_blocks, int[] i_block) {
        this.i_size = i_size;
        this.i_atime = i_atime;
        this.i_mtime = i_mtime;
        this.i_dtime = i_dtime;
        this.i_ctime = i_ctime;
        this.i_links_count = i_links_count;
        this.i_blocks = i_blocks;
        this.i_block = i_block;
    }
    
    public Inode(){
        this.i_size = 0;
        this.i_links_count = 0;
        this.i_blocks = 0;
        this.i_block = new int[13];
        
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fecha = Calendar.getInstance().getTime();
        this.i_ctime = df.format(fecha);
        this.i_atime = "";
        this.i_mtime = "";
        this.i_dtime = ""; 
    }

    public int getI_size() {
        return i_size;
    }

    public void setI_size(int i_size) {
        this.i_size = i_size;
    }

    public String getI_atime() {
        return i_atime;
    }

    public void setI_atime(String i_atime) {
        this.i_atime = i_atime;
    }

    public String getI_mtime() {
        return i_mtime;
    }

    public void setI_mtime(String i_mtime) {
        this.i_mtime = i_mtime;
    }

    public String getI_dtime() {
        return i_dtime;
    }

    public void setI_dtime(String i_dtime) {
        this.i_dtime = i_dtime;
    }

    public String getI_ctime() {
        return i_ctime;
    }

    public void setI_ctime(String i_ctime) {
        this.i_ctime = i_ctime;
    }

    public int getI_links_count() {
        return i_links_count;
    }

    public void setI_links_count(int i_links_count) {
        this.i_links_count = i_links_count;
    }

    public int getI_blocks() {
        return i_blocks;
    }

    public void setI_blocks(int i_blocks) {
        this.i_blocks = i_blocks;
    }

    public int[] getI_block() {
        return i_block;
    }

    public void setI_block(int[] i_block) {
        this.i_block = i_block;
    }
    
    
    
    
}
