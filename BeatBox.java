import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.event.*;

/**
 * Created by Oleg on 24.11.2019.
 */

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;
    public static final int NOTE_ON = 0x90;


    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
            "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
            "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};


    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    //create GUI

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

//save and load button add

        JButton save = new JButton("Save");
        save.addActionListener(new SaveMenuListener());
        buttonBox.add(save);

        JButton load = new JButton("Load");
        load.addActionListener(new LoadMenuListener());
        buttonBox.add(load);
//end
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        } // end loop

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    } // close method

    //add SAve and Load Menu

    public class LoadMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fileLoad = new JFileChooser();
            fileLoad.showOpenDialog(theFrame);
            loadFile(fileLoad.getSelectedFile());
        }

    }
    private void loadFile(File file) {
        for(JCheckBox ch: checkboxList){
            ch.setSelected(false);
        }
        try {
            sequence = MidiSystem.getSequence(file);

            int trackNumber = 0;
            for (Track track : sequence.getTracks()) {
                trackNumber++;
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    int position = (int)event.getTick();
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;

                        if (sm.getCommand() == NOTE_ON) {
                            int instrument = sm.getData1();
                            for(int n=0; n<instruments.length; n++){
                                if(instruments[n]==instrument){
                                    checkboxList.get(16*(n)+position).setSelected(true);
                                }
                            }

                        }

                    }

                }
                sequence.deleteTrack(track);
            }
        }
             catch(InvalidMidiDataException e){
                e.printStackTrace();
            } catch(IOException e){
                System.out.println("couldn't read the track");
                e.printStackTrace();
            }

        }


    public class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fileSave = new JFileChooser();
            FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Midi files (*.midi)", "midi", "*.midi");
            fileSave.addChoosableFileFilter(fileNameExtensionFilter);
            fileSave.setFileFilter(fileNameExtensionFilter);
            fileSave.showSaveDialog(theFrame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    private void saveFile(File file) {

        try {
            MidiSystem.write(sequence, 0, file);

        } catch (IOException ex) {
            System.out.println("couldn't write the track out");
            ex.printStackTrace();
        }

    }
// ena Save and Load


    //create Sequencer

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 // check ArrayList<JCheckBox> checkboxList, create Message and Event, create track and Run()

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];

            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            } // close inner loop

            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        } // close outer

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 //Start Button Listener

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }

    //Stop Button Listener

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }

    // Tempo Button Listener

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }


    public void makeTracks(int[] list) {

        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}
