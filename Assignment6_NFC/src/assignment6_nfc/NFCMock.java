/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6_nfc;

/**
 *
 * @author stefh
 */
public class NFCMock implements NFC {

    @Override
    public boolean send(String msg) {
        return true;
    }
    
}
