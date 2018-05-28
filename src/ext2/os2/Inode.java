/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ext2.os2;

/**
 *
 * @author juany
 */
public class Inode {
    int i_size;
    char[] i_ctime;
    char[] i_blocks;
    int[] i_block;
    /*
    4   2   8   60
    
    */
    public Inode() {
        this.i_size = 0;
        this.i_ctime = new char[19];//2016/11/16 12:08:43
        this.i_blocks = new char[4];
        this.i_block = new int[15];
    }

    public Inode(int i_size, char[] i_ctime, char[] i_blocks, int[] i_block) {
        this.i_size = i_size;
        this.i_ctime = i_ctime;
        this.i_blocks = i_blocks;
        this.i_block = i_block;
    }


    public int getI_size() {
        return i_size;
    }

    public void setI_size(int i_size) {
        this.i_size = i_size;
    }
    
    public char[] getI_ctime() {
        return i_ctime;
    }

    public void setI_ctime(char[] i_ctime) {
        this.i_ctime = i_ctime;
    }
    
    public char[] getI_blocks() {
        return i_blocks;
    }

    public void setI_blocks(char[] i_blocks) {
        this.i_blocks = i_blocks;
    }

    public int[] getBlock() {
        return i_block;
    }

    public void setBlock(int[] i_block) {
        this.i_block = i_block;
    }
    
    
    
    
}
