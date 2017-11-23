/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6_nfc;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 *
 * @author stefh
 */
public class Assignment6_NFC {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        NFC nfc = null;
        System.out.println("Does this device have NFC? (y/[Anything else])");
        if(in.nextLine().equals("y")) {
            nfc = new NFCMock();
        }
        
        System.out.println("Welcome to this primitive message sender!");
        while(true) {
            System.out.println("Enter a message that you want to send or empty to exit:");
            String message = in.nextLine();
            
            if(message.equals("")) break;

            if(nfc != null) {
                if(nfc.send(message)) {
                    System.out.println("Message successfully sent with NFC.");
                } else {
                    System.out.println("Failed to send message with NFC :(");
                }
            } else {
                System.out.println("NFC not available. Sending with email...");
                Desktop desktop = Desktop.getDesktop();
                String url = "mailto:?body="+encodeURIComponent(message);
                URI uri = URI.create(url);
                desktop.mail(uri);
                System.out.println("Email client opened!");
            }
        }
        in.close();
    }
    
    public static String encodeURIComponent(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }
        return result;
    }
}
