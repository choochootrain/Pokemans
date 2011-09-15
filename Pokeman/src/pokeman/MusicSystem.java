package pokeman;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;
/**<
 * This class controls all music related tasks for: 
 *  ______     _                              
 *  | ___ \   | |                             
 *  | |_/ /__ | | _____ _ __ ___   ___  _ __  
 *  |  __/ _ \| |/ / _ \ '_ ` _ \ / _ \| '_ \ 
 *  | | | (_) |   <  __/ | | | | | (_) | | | |
 *  \_|  \___/|_|\_\___|_| |_| |_|\___/|_| |_|
 *  ________                                   _____                  
 *  ___  __ \_____ _________________ _________ __  /______ ___________
 *  __  /_/ /  __ `/__  /__  /_  __ `__ \  __ `/  __/  __ `/__  /__  /
 *  _  _, _// /_/ /__  /__  /_  / / / / / /_/ // /_ / /_/ /__  /__  /_
 *  /_/ |_| \__,_/ _____/____/_/ /_/ /_/\__,_/ \__/ \__,_/ _____/____/
 * 
 * coming to a HornAttacks center near you
 * 
 * @author Hurshal Patel
 */
public class MusicSystem implements Closeable
{
    
    private File f = null;
    private String name = "";
    private Sequence sequence = null;
    private Sequencer sequencer = null;
    private MidiChannel[] channel = null;
    private boolean stop = false;
            
    /**
     * Currently, this method takes in the exact midi file location as a string.
     * We may later modify this to take in a directory with a file in it that
     * defines loop points.
     * @param filePath the path of the midi file
     */
    public void loadMusic(String filePath){
        if(sequence != null)
            pause();
        f = new File(filePath);
        name = filePath;
        try
        {
            sequence = MidiSystem.getSequence(f);
            sequencer = MidiSystem.getSequencer();
            
            sequencer.setSequence(sequence);
            sequencer.open();
            
            
        }
        catch(InvalidMidiDataException imde) {
            System.out.println("Error type: invalid data");
        } catch(IOException io) {
            System.out.println("Error type: io");
        } catch(MidiUnavailableException mue) {
            System.out.println("Error type: Midi Unavailable");
        }
    }
    
    /**
     * 
     * @param loop a boolean, true if it loops, false if it doesn't
     */
    public void play(boolean loop){

            if (!stop) {
                if (loop) {
                    sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                }
                sequencer.start();
            }
            /*Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel[] channels = synth.getChannels();
            double gain = 0.0;
            for (int i=0; i<channels.length; i++) {
                channels[i].controlChange(7, (int)(gain * 127.0));
            }
            Receiver receiver = MidiSystem.getReceiver();

            ShortMessage volumeMessage= new ShortMessage();
            int volume = 1;
            for (int i = 0; i < 16; i++) {
              volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volume);
              receiver.send(volumeMessage, -1);
            }*/

    }
    
    public void pause(){
        sequencer.stop(); 
    }
    
    public boolean playing(){
        return sequencer.isRunning();
    }
    
    /**
     * Sets the current time
     * @param pos time in milliseconds
     */
    public void goTo(long pos){
        sequencer.setTickPosition(pos);
    }
    
    public void close(){
        pause();
        sequencer.close();
    }
    
    public void stop(){
        stop = true;
    }
    
    public String getName(){
        return name;
    }
}

