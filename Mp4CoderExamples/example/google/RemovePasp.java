package me.zuichu.mp4coder.example.google;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.zuichu.mp4coder.Box;
import me.zuichu.mp4coder.IsoFile;
import me.zuichu.mp4coder.boxes.sampleentry.VisualSampleEntry;
import me.zuichu.mp4coder.tools.Path;

public class RemovePasp {
    public static void main(String[] args) throws IOException {
        IsoFile i = new IsoFile("C:\\Users\\sannies\\Downloads\\CF2.0_1920x1080_8000.mp4");

        VisualSampleEntry sampleEntry = Path.getPath(i, "/moov[0]/trak[0]/mdia[0]/minf[0]/stbl[0]/stsd[0]/avc1[0]");
        List<Box> nuBoxes = new ArrayList<Box>();
        assert sampleEntry != null;
        for (Box box : sampleEntry.getBoxes()) {
            if (!box.getType().equals("pasp")) {
                nuBoxes.add(box);
            }
        }
        sampleEntry.setBoxes(nuBoxes);


        i.writeContainer(new FileOutputStream("C:\\Users\\sannies\\Downloads\\CF2.0_1920x1080_8000-without-pasp.mp4").getChannel());
    }

}
