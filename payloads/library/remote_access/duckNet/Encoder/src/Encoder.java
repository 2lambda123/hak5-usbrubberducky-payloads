// File:         Encoder.java
// Created:      8/10/2011
// Original Author:Jason Appelbaum Jason@Hak5.org 
// Author:       Dnucna
// Modified:     8/18/2012
// Modified:	 11/9/2013 midnitesnake "added COMMAND-OPTION"
// Modified:     1/3/2013 midnitesnake "added COMMAND"
// Modified:     1/3/2013 midnitesnake "added REPEAT X"
// Modified:	 2/5/2013 midnitesnake "added ALT-SHIFT"
// Modified:     4/18/2013 midnitesnake "added more user feedback"
// Modified:	 5/2/2013 midnitesnake "added skip over empty lines"
// Modified:     1/12/2014 Benthejunebug "added ALT-TAB"
// Modified:	 9/13/2016 rbeede "added STRING_DELAY n text"

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import java.util.Properties;

public class Encoder {
        /* contains the keyboard configuration */
        private static Properties keyboardProps = new Properties();
        /* contains the language layout */
        private static Properties layoutProps = new Properties();
        private static String version = "2.6.4";
        private static Boolean debug=false;
    
        public static void main(String[] args) {
                String helpStr = "Hak5 Duck Encoder "+version+"\n\n"
                        + "Usage: duckencode -i [file ..]\t\t\tencode specified file\n"
                        + "   or: duckencode -i [file ..] -o [file ..]\tencode to specified file\n\n"
                        + "Arguments:\n"
                        + "   -i [file ..] \t\tInput File\n"
                        + "   -o [file ..] \t\tOutput File\n"
                        + "   -l [file ..] \t\tKeyboard Layout (us/fr/pt or a path to a properties file)\n\n"
                        + "Script Commands:\n"
                        + "   ALT [key name] (ex: ALT F4, ALT SPACE)\n"
                        + "   CTRL | CONTROL [key name] (ex: CTRL ESC)\n"
                        + "   CTRL-ALT [key name] (ex: CTRL-ALT DEL)\n"
                        + "   CTRL-SHIFT [key name] (ex: CTRL-SHIFT ESC)\n"
                        + "   DEFAULT_DELAY | DEFAULTDELAY [Time in millisecond] (change the delay between each command)\n"
                        + "   DELAY [Time in millisecond] (used to overide temporary the default delay)\n"
                        + "   GUI | WINDOWS [key name] (ex: GUI r, GUI l)\n"
                        + "   REM [anything] (used to comment your code, no obligation :) )\n"
                        + "   ALT-SHIFT (swap language)\n"
                        + "   SHIFT [key name] (ex: SHIFT DEL)\n"
                        + "   STRING [any character of your layout]\n"
                        + "   STRING_DELAY [Number] [any character of your layout]	(Number is ms delay between each character)\n"
                        + "   REPEAT [Number] (Repeat last instruction N times)\n"
                        + "   [key name] (anything in the keyboard.properties)";                        

        String inputFile = null;
        String outputFile = null;
        String layoutFile = null;

        if (args.length == 0) {
                System.out.println(helpStr);
                System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
                if ("--gui".equals(args[i]) || "-g".equals(args[i])) {
                        System.out.println("Launch GUI");
                } else if ("--help".equals(args[i]) || "-h".equals(args[i])) {
                        System.out.println(helpStr);
                } else if ("-i".equals(args[i])) {
                        // encode file
                        inputFile = args[++i];
                } else if ("-o".equals(args[i])) {
                        // output file
                        outputFile = args[++i];
                } else if ("-l".equals(args[i])) {
                        // output file
                        layoutFile = args[++i];
                } else if ("-d".equals(args[i])) {
                    // output file
                    debug=true;
                } else {
                        System.out.println(helpStr);
                        break;
                }
        }
            
        System.out.println("Hak5 Duck Encoder "+version+"\n");
        
        if (inputFile != null) {
                String scriptStr = null;

                if (inputFile.contains(".rtf")) {
                        try {
                                FileInputStream stream = new FileInputStream(inputFile);
                                RTFEditorKit kit = new RTFEditorKit();
                                Document doc = kit.createDefaultDocument();
                                kit.read(stream, doc, 0);

                                scriptStr = doc.getText(0, doc.getLength());
                                System.out.println("Loading RTF .....\t\t[ OK ]");
                        } catch (IOException e) {
                                System.out.println("Error with input file!");
                        } catch (BadLocationException e) {
                                System.out.println("Error with input file!");
                        }
                    
                } else {
                        DataInputStream in = null;
                        try {
                                File f = new File(inputFile);
                                byte[] buffer = new byte[(int) f.length()];
                                in = new DataInputStream(new FileInputStream(f));
                                in.readFully(buffer);
                                scriptStr = new String(buffer);
                                System.out.println("Loading File .....\t\t[ OK ]");
                        } catch (IOException e) {
                                System.out.println("Error with input file!");
                        } finally {
                                try {
                                        in.close();
                                } catch (IOException e) { /* ignore it */
                                }
                        }
                }
                loadProperties((layoutFile == null) ? "us" : layoutFile);
                
                encodeToFile(scriptStr, (outputFile == null) ? "inject.bin"
                                : outputFile);
                }
            
        }
        
        private static void loadProperties (String lang){
                InputStream in;
                ClassLoader loader = ClassLoader.getSystemClassLoader ();
                try {
                        in = loader.getResourceAsStream("keyboard.properties");
                        if(in != null){
                                keyboardProps.load(in);
                                in.close();
                                System.out.println("Loading Keyboard File .....\t[ OK ]");
                        }else{
                                System.out.println("Error with keyboard.properties!");
                                System.exit(0);
                        }
                } catch (IOException e) {
                        System.out.println("Error with keyboard.properties!");
                }
                        
                try {
                        in = loader.getResourceAsStream(lang + ".properties");
                        if(in != null){
                                layoutProps.load(in);
                                in.close();
                                System.out.println("Loading Language File .....\t[ OK ]");
                        }else{
                                if(new File(lang).isFile()){
                                        layoutProps.load(new FileInputStream(lang));
                                        System.out.println("Loading Language File .....\t[ OK ]");
                                } else{
                                        System.out.println("External layout.properties non found!");
                                        System.exit(0);
                                }
                        }
                } catch (IOException e) {
                        System.out.println("Error with layout.properties!");
                        System.exit(0);
                }

        }
        private static void encodeToFile(String inStr, String fileDest) {

                inStr = inStr.replaceAll("\\r", ""); // CRLF Fix
                String[] instructions = inStr.split("\n");
                String[] last_instruction = inStr.split("\n");
                List<Byte> file = new ArrayList<Byte>();
                int defaultDelay = 0;
                int loop =0;
                boolean repeat=false;
                System.out.println("Loading DuckyScript .....\t[ OK ]");
                if(debug) System.out.println("\nParsing Commands:");
                for (int i = 0; i < instructions.length; i++) {
                        try {
                                boolean delayOverride = false;
                                String commentCheck = instructions[i].substring(0, 2);
                                if ("//".equals(commentCheck))
                                        continue;
				if ("\n".equals(instructions[i]))
					continue;
                               String[] instruction = instructions[i].split(" ", 2);
                                
                                if(i>0){
                                		last_instruction=instructions[i-1].split(" ", 2);
                                		last_instruction[0].trim();
                                		if (last_instruction.length == 2) {
                                        		last_instruction[1].trim();
                                		}
                                }else{
                                		last_instruction=instructions[i].split(" ", 2);
                                		last_instruction[0].trim();
                                		if (last_instruction.length == 2) {
                                        		last_instruction[1].trim();
                                		}
                                }

                                instruction[0].trim();

                                if (instruction.length == 2) {
                                        instruction[1].trim();
                                }

								if ("REM".equals(instruction[0])){
									continue;
								}
								if ("REPEAT".equals(instruction[0])){
									loop=Integer.parseInt(instruction[1].trim());
									repeat=true;
								}else{
									repeat=false;
									loop=1;
								}
								while(loop>0){
									if (repeat){
										instruction=last_instruction;
										//System.out.println(Integer.toString(instruction.length));
									}
								if (debug) System.out.println(java.util.Arrays.toString(instruction));
                                	if ("DEFAULT_DELAY".equals(instruction[0])
                                                || "DEFAULTDELAY".equals(instruction[0])) {
                                      	  defaultDelay = Integer.parseInt(instruction[1].trim());
                                       	 delayOverride = true;
                                	} else if ("DELAY".equals(instruction[0])) {
                                        int delay = Integer.parseInt(instruction[1].trim());
                                        while (delay > 0) {
                                                file.add((byte) 0x00);
                                                if (delay > 255) {
                                                        file.add((byte) 0xFF);
                                                        delay = delay - 255;
                                                } else {
                                                        file.add((byte) delay);
                                                        delay = 0;
                                                }
                                        }
                                        delayOverride = true;
                                	} else if ("STRING".equals(instruction[0])) {
                                        for (int j = 0; j < instruction[1].length(); j++) {
                                                char c = instruction[1].charAt(j);
                                                addBytes(file,charToBytes(c));
                                        }
                                	} else if ("STRING_DELAY".equals(instruction[0])) {
                                		final String[] twoOptions = instruction[1].split(" ", 2);
                                		final int delayMillis = Integer.parseInt(twoOptions[0].trim());
                                		final String userText = twoOptions[1].trim();
                                		
                                		if(debug)  System.out.println(delayMillis);
                                		if(debug)  System.out.println(userText);
                                		
                                        for (int j = 0; j < userText.length(); j++) {
                                                char c = userText.charAt(j);
                                                addBytes(file,charToBytes(c));
                                                
                                                // Now insert the delay before the next character (and after the last is provided)
                                                for(int counter = delayMillis; counter > 0; counter -= 0xFF) {
                                                	file.add((byte) 0x00);
                                                	if(counter > 0xFF) {
                                                		file.add((byte) 0xFF);
                                                	} else {
                                                		file.add((byte) counter);  // Last one
                                                	}
                                                }
                                        }
                                	} else if ("CONTROL".equals(instruction[0])
                                                || "CTRL".equals(instruction[0])) {
                                        if (instruction.length != 1){
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_CTRL")));
                                                file.add((byte) 0x00);
                                        }                               
                                	} else if ("ALT".equals(instruction[0])) {
                                        if (instruction.length != 1){
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_ALT")));
                                                file.add((byte) 0x00);
                                        }
                                	} else if ("SHIFT".equals(instruction[0])) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_SHIFT")));
                                                file.add((byte) 0x00);
                                        }
                                	} else if ("CTRL-ALT".equals(instruction[0])) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT"))));
                                        } else {
                                                continue;
                                        }
                                	} else if ("CTRL-SHIFT".equals(instruction[0])) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT"))));
                                        } else {
                                                continue;
                                        }
                                    } else if ("COMMAND-OPTION".equals(instruction[0])) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_KEY_LEFT_GUI"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT"))));
                                        } else {
                                                continue;
                                        }
                                	} else if ("ALT-SHIFT".equals(instruction[0])) {
                                        if (instruction.length != 1) {
                                            file.add(strInstrToByte(instruction[1]));
                                            file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")))
                                                    );
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_ALT")));
                                                                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")))
                                                        );
                                        }
                                	} else if ("ALT-TAB".equals(instruction[0])){
                                        if (instruction.length == 1) {
                                            file.add(strToByte(keyboardProps.getProperty("KEY_TAB")));
                                            file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT")));
                                        } else{
                                            // do something?
                                        }
                                    } else if ("REM".equals(instruction[0])) {
                                        /* no default delay for the comments */
                                        delayOverride = true;
                                        continue;
                                	} else if ("WINDOWS".equals(instruction[0])
                                                || "GUI".equals(instruction[0])) {
                                        if (instruction.length == 1) {
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                                file.add((byte) 0x00);
                                        } else {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                        }
                                	} else if ("COMMAND".equals(instruction[0])){
                                        if (instruction.length == 1) {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_COMMAND")));
                                                file.add((byte) 0x00);
                                        } else {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                        }
                                	}else {
                                        /* treat anything else as a key */
                                        file.add(strInstrToByte(instruction[0]));
                                        file.add((byte) 0x00);
                                	}
                                	loop--;
								}
                                // Default delay
                                if (!delayOverride & defaultDelay > 0) {
                                        int delayCounter = defaultDelay;
                                        while (delayCounter > 0) {
                                                file.add((byte) 0x00);
                                                if (delayCounter > 255) {
                                                        file.add((byte) 0xFF);
                                                        delayCounter = delayCounter - 255;
                                                } else {
                                                        file.add((byte) delayCounter);
                                                        delayCounter = 0;
                                                }
                                        }
                                }
                        }catch (StringIndexOutOfBoundsException e){
				//do nothing
                        }
                        catch (Exception e) {
                                System.out.println("Error on Line: " + (i + 1));
                                e.printStackTrace();
                        }
                }

                // Write byte array to file
                byte[] data = new byte[file.size()];
                for (int i = 0; i < file.size(); i++) {
                        data[i] = file.get(i);
                }
                try {
                        File someFile = new File(fileDest);
                        FileOutputStream fos = new FileOutputStream(someFile);
                        fos.write(data);
                        fos.flush();
                        fos.close();
                        System.out.println("DuckyScript Complete.....\t[ OK ]\n");
                } catch (Exception e) {
                        System.out.print("Failed to write hex file!");
                }
            
        }

        private static void addBytes(List<Byte> file, byte[] byteTab){
                for(int i=0;i<byteTab.length;i++)
                        file.add(byteTab[i]);
                if(byteTab.length % 2 != 0){
                        file.add((byte) 0x00);
                }
        }
        
        private static byte[] charToBytes (char c){
                return codeToBytes(charToCode(c));
        }
        private static String charToCode (char c){
                String code;
                if(c<128){
                code = "ASCII_"+Integer.toHexString(c).toUpperCase();
            }else if (c<256){
                code = "ISO_8859_1_"+Integer.toHexString(c).toUpperCase();
            }else{
                code = "UNICODE_"+Integer.toHexString(c).toUpperCase();
            }
                return code;
        }
        
        private static byte[] codeToBytes (String str){
                if(layoutProps.getProperty(str) != null){
                        String keys[] = layoutProps.getProperty(str).split(",");
                        byte[] byteTab = new byte[keys.length];
                    for(int j=0;j<keys.length;j++){
                        String key = keys[j].trim();
                        if(keyboardProps.getProperty(key) != null){
                                byteTab[j] = strToByte(keyboardProps.getProperty(key).trim());
                        }else if(layoutProps.getProperty(key) != null){
                                byteTab[j] = strToByte(layoutProps.getProperty(key).trim());
                        }else{
                                System.out.println("Key not found:"+key);
                                byteTab[j] = (byte) 0x00;
                        }
                    }
                        return byteTab;
                }else{
                        System.out.println("Char not found:"+str);
                        byte[] byteTab = new byte[1];
                        byteTab[0] = (byte) 0x00;
                        return byteTab;
                }
        }
        private static byte strToByte(String str) {
                if(str.startsWith("0x")){
                        return (byte)Integer.parseInt(str.substring(2),16);
                }else{
                        return (byte)Integer.parseInt(str);
                }
        }
        
        private static byte strInstrToByte(String instruction){
                instruction = instruction.trim();
                if(keyboardProps.getProperty("KEY_"+instruction)!=null)
                        return strToByte(keyboardProps.getProperty("KEY_"+instruction));
                /* instruction different from the key name */
                if("ESCAPE".equals(instruction))
                        return strInstrToByte("ESC");
                if("DEL".equals(instruction))
                        return strInstrToByte("DELETE");
                if("BREAK".equals(instruction))
                        return strInstrToByte("PAUSE");
                if("CONTROL".equals(instruction))
                        return strInstrToByte("CTRL");
                if("DOWNARROW".equals(instruction))
                        return strInstrToByte("DOWN");
                if("UPARROW".equals(instruction))
                        return strInstrToByte("UP");
                if("LEFTARROW".equals(instruction))
                        return strInstrToByte("LEFT");
                if("RIGHTARROW".equals(instruction))
                        return strInstrToByte("RIGHT");
                if("MENU".equals(instruction))
                        return strInstrToByte("APP");
                if("WINDOWS".equals(instruction))
                        return strInstrToByte("GUI");
                if("PLAY".equals(instruction) || "PAUSE".equals(instruction))
                        return strInstrToByte("MEDIA_PLAY_PAUSE");
                if("STOP".equals(instruction))
                        return strInstrToByte("MEDIA_STOP");
                if("MUTE".equals(instruction))
                        return strInstrToByte("MEDIA_MUTE");
                if("VOLUMEUP".equals(instruction))
                        return strInstrToByte("MEDIA_VOLUME_INC");
                if("VOLUMEDOWN".equals(instruction))
                        return strInstrToByte("MEDIA_VOLUME_DEC");
                if("SCROLLLOCK".equals(instruction))
                        return strInstrToByte("SCROLL_LOCK");
                if("NUMLOCK".equals(instruction))
                        return strInstrToByte("NUM_LOCK");
                if("CAPSLOCK".equals(instruction))
                        return strInstrToByte("CAPS_LOCK");
                /* else take first letter */
                return charToBytes(instruction.charAt(0))[0];
        }
}
