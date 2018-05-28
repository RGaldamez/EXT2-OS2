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
public class directoryEntry {
    int inodeIndex;
    int nameLength;
    String nombre;

    public directoryEntry() {
    }

    public directoryEntry(int inodeIndex, int nameLength, String nombre) {
        this.inodeIndex = inodeIndex;
        this.nameLength = nameLength;
        this.nombre = nombre;
    }

    public int getInodeIndex() {
        return inodeIndex;
    }

    public void setInodeIndex(int inodeIndex) {
        this.inodeIndex = inodeIndex;
    }

    public int getNameLength() {
        return nameLength;
    }

    public void setNameLength(int nameLength) {
        this.nameLength = nameLength;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    
}
