package byow.InputDemo;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * Created by hug.
 * Demonstrates how a single interface can be used to provide input
 * from they keyboard, from a random sequence, from a string, or whatever else.
 */
public class DemoInputSource {
    private static final int KEYBOARD = 0;
    private static final int RANDOM = 1;
    private static final int STRING = 2;

    public static void main(String[] args) {
        int inputType = KEYBOARD;

        InputSource inputSource;

        if (inputType == KEYBOARD) {
            inputSource = new KeyboardInputSource();
        } else if (inputType == RANDOM) {
            inputSource = new RandomInputSource(50L);
        } else { // inputType == STRING
            inputSource = new StringInputDevice("HELLO MY FRIEND. QUACK QUACK");
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\cs61b\\sp19-proj3-s458-s950\\proj3\\Magic-Clock-Shop.au").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
        /*
        try {
            File audioFile = new File("\"C:\\cs61b\\sp19-proj3-s458-s950\\proj3\\byow\"");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
        } catch (IOException e) {
            System.out.println("File not found.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Not correct file type.");
        } catch (LineUnavailableException e) {
            System.out.println("No Line.");
        } */

       /* try {
            File yourFile = new File("Magic-Clock-Shop.au");
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception m) {
            System.out.println("No music file found.");
        } */

        int totalCharacters = 0;

        while (inputSource.possibleNextInput()) {
            totalCharacters += 1;
            char c = inputSource.getNextKey();
            if (c == 'M') {
                System.out.println("moo");
            }
            if (c == 'Q') {
                System.out.println("done.");
                break;
            }
        }

        System.out.println("Processed " + totalCharacters + " characters.");
    }
}
